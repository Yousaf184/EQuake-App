package com.example.yousafkhan.equake;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    private static final String apiURL =
       "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2018-01-01&endtime=2018-06-01&limit=10";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress_bar);

        new ApiRequest(this).execute(apiURL);
    }

    // async task class
    private static class ApiRequest extends AsyncTask<String, Void, InputStream> {

        private WeakReference<MainActivity> mainActivity;

        ApiRequest(MainActivity context) {
            mainActivity = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected InputStream doInBackground(String... strings) {

            String apiURL = strings[0];
            URL url = null;
            HttpURLConnection httpConn = null;
            InputStream inputStream = null;

            try {
                url = new URL(apiURL);
                httpConn = (HttpURLConnection) url.openConnection();
                httpConn.setRequestMethod("GET");
                inputStream = httpConn.getInputStream();

            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if(httpConn != null) {
                    httpConn.disconnect();
                }
            }

            return inputStream;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(InputStream ins) {

            mainActivity.get().progressBar.setVisibility(View.GONE);

            if(ins != null) {

                String line = "";
                StringBuilder strBuilder = new StringBuilder();

                InputStreamReader inputStreamReader = new InputStreamReader(ins);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                try {
                    line = bufferedReader.readLine();

                    while (line != null) {
                        strBuilder.append(line);
                        line = bufferedReader.readLine();
                    }

                    bufferedReader.close();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
    }

}
