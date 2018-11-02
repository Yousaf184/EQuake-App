package com.example.yousafkhan.equake;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    private static final String apiURL =
       "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2018-01-01&limit=30";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress_bar);

        new ApiRequest(this).execute(apiURL);
    }

    private ArrayList<EarthQuake> parseJSON(String jsonString) {

        ArrayList<EarthQuake> earthQuakesInfoList = new ArrayList<>();

        double magnitude = 0;
        String location = "";
        long time = 0;
        String detailUrl = "";

        try {
            // convert JSON string in to JSON object
            JSONObject jsonObj = new JSONObject(jsonString);
            // get the features array from the JSON object
            JSONArray jsonArr = jsonObj.getJSONArray("features");

            // iterate over the features array and extract the required information
            for (int i=0; i<jsonArr.length(); i++) {
                // get the JSON object on the current index of the features array
                // instead of declaring new JSONObject, already declared jsonObj is reused here
                jsonObj = jsonArr.getJSONObject(i);

                // get the properties object from the current JSON object
                jsonObj = jsonObj.getJSONObject("properties");

                // get the required information in local variables
                magnitude = Double.parseDouble(jsonObj.getString("mag"));
                location = jsonObj.getString("place");
                time = Long.parseLong(jsonObj.getString("time"));
                detailUrl = jsonObj.getString("url");

                // create EarthQuake object
                EarthQuake eq = new EarthQuake(magnitude, location, time, detailUrl);

                // add the newly created EarthQuake object to array list
                earthQuakesInfoList.add(eq);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return earthQuakesInfoList;
    }

    private void populateListView(ArrayList<EarthQuake> list) {
        ListView listView = findViewById(R.id.listview);
        EarthQuakeAdapter adapter = new EarthQuakeAdapter(this, list);
        listView.setAdapter(adapter);
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

                // parse the json string
                ArrayList<EarthQuake> list = mainActivity.get().parseJSON(strBuilder.toString());

                // hide the progress bar
                mainActivity.get().progressBar.setVisibility(View.GONE);

                // populate the listview
                mainActivity.get().populateListView(list);
            }
        }
    }

}
