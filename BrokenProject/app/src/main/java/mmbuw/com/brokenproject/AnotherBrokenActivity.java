package mmbuw.com.brokenproject;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AnotherBrokenActivity extends Activity {
    private String message;

    private EditText url;
    private TextView myTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_broken);

        Intent intent = getIntent();
        message = intent.getStringExtra(BrokenActivity.EXTRA_MESSAGE);
        url = (EditText)findViewById(R.id.edittext2);
        myTextView = (TextView) findViewById(R.id.myTextView);
        //What happens here? What is this? It feels like this is wrong.
        //Maybe the weird programmer who wrote this forgot to do something?

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.another_broken, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void fetchHTML(View view) throws IOException {

        //According to the exercise, you will need to add a button and an EditText first.
        //Then, use this function to call your http requests
        //Following hints:
        //Android might not enjoy if you do Networking on the main thread, but who am I to judge?
        //An app might not be allowed to access the internet without the right (*hinthint*) permissions
        //Below, you find a staring point for your HTTP Requests - this code is in the wrong place and lacks the allowance to do what it wants
        //It will crash if you just un-comment it.



        // Gets the URL from the UI's text field.
        String stringUrl = url.getText().toString();
        if(!stringUrl.startsWith("http://")) {
            stringUrl = "http://" + stringUrl;
        }

        System.out.println(stringUrl);
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            WebHandler handler = new WebHandler();
            String result = "";
            try {
                result = handler.execute(stringUrl).get();
                if(result.startsWith("Error")) {
                    Toast toast = Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG);
                    toast.show();
                }
                else {
                    RadioButton rb = (RadioButton) findViewById(R.id.radioButton1);
                    if(!rb.isChecked()) {
                        myTextView.setText(result);
                    }
                    else {
                        WebView webview = new WebView(this);
                        setContentView(webview);
                        webview.loadUrl(stringUrl);
                    }
                }
            } catch(Exception e) {
                result = "error";
                e.printStackTrace();
            }

        } else {
            myTextView.setText("No network connection available.");
        }


    }
    private class WebHandler extends AsyncTask<String, Void, String> {

        @Override
        public String doInBackground(String... urls) {
            try {
                return httpConnector(urls[0]);
            } catch(IOException e) {
                //return "Unable to retrieve web page. URL may be invalid.";
                return e.getMessage();
            }

        }
//bonusaufgabe webview benutzen
        public String httpConnector(String urlIn) throws IOException {
            //Beginning of helper code for HTTP Request.

            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(new HttpGet(urlIn));
            StatusLine status = response.getStatusLine();
            System.out.println(status.toString());
            if (status.getStatusCode() == HttpStatus.SC_OK) { //200
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                response.getEntity().writeTo(outStream);
                String responseAsString = outStream.toString();
                //System.out.println("Response string: " + responseAsString);
                return responseAsString;
            }
            else {
                //Well, this didn't work.
                response.getEntity().getContent().close();
                String test = "Error: " + Integer.toString(status.getStatusCode())+ " " + status.getReasonPhrase();
                throw new IOException(test);
            }
            //End of helper code!
        }
    }
}
