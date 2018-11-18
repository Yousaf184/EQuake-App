package com.example.yousafkhan.equake;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class EarthQuakeVModel extends AndroidViewModel {

    private static MutableLiveData<ArrayList<EarthQuake>> earthQuakesInfoList;
//    private static final String apiURL =
//            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2018-01-01&limit=30";

    private static final String apiURL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?";

    public EarthQuakeVModel(@NonNull Application application) {
        super(application);

    }

    public MutableLiveData<ArrayList<EarthQuake>> getDataList() {

        if(earthQuakesInfoList == null) {
            // load data only for the first time
            loadData();
            earthQuakesInfoList = new MutableLiveData<>();
        }

        return earthQuakesInfoList;
    }

    public EarthQuake getItemAtPosition(int position) {
        return earthQuakesInfoList.getValue().get(position);
    }

    public void loadData() {
        String url = buildURL(apiURL);
        new ApiRequest().execute(url);
    }

    // construct appropriate api url by reading minimum magnitude from preferences
    private String buildURL(String url) {

        // application context
        Context context = getApplication().getApplicationContext();

        // read minimum magnitude preference value
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String minMagnitude = sharedPrefs.getString(context.getString(
                                                        R.string.settings_min_magnitude_key),
                                                        context.getString(R.string.settings_min_magnitude_default)
                                                   );

        // build url
        Uri baseURL = Uri.parse(url);
        Uri.Builder uriBuilder = baseURL.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("starttime", "2018-01-01");
        uriBuilder.appendQueryParameter("limit", "30");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);

        return uriBuilder.toString();
    }

    private static void parseJSON(String jsonString) {

        ArrayList<EarthQuake> list = new ArrayList<>();

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
                list.add(eq);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // update live data
        earthQuakesInfoList.postValue(list);
    }

    // async task class
    private static class ApiRequest extends AsyncTask<String, Void, InputStream> {

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
                httpConn.setReadTimeout(15000);
                httpConn.setConnectTimeout(15000);
                httpConn.connect();
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
                EarthQuakeVModel.parseJSON(strBuilder.toString());
            }
        }
    }
}
