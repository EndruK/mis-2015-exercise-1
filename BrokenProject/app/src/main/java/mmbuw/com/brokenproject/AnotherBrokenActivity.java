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
import android.widget.EditText;
import android.widget.TextView;
import android.content.Context;

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

        System.out.println(stringUrl);
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {


            WebHandler handler = new WebHandler();
            String result = "";
            try {
                result = handler.execute(stringUrl).get();
            } catch(Exception e) {
                result = "error";
            }
            myTextView.setText(result);

        } else {
            myTextView.setText("No network connection available.");
        }


    }
    private class WebHandler extends AsyncTask<String, Void, String> {

        @Override
        public String doInBackground(String... urls) {
            try {
                return htmlConnector(urls[0]);
            } catch(IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }

        }

        public String htmlConnector(String urlIn) throws IOException {
            //Beginning of helper code for HTTP Request.

            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(new HttpGet(urlIn));
            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                response.getEntity().writeTo(outStream);
                String responseAsString = outStream.toString();
                //System.out.println("Response string: " + responseAsString);
                return responseAsString;
            } else {
                //Well, this didn't work.
                response.getEntity().getContent().close();
                throw new IOException(status.getReasonPhrase());

            }
            //End of helper code!
        }
    }
}
