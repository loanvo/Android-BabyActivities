package cs175.babysactivities;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mach on 12/7/17.
 */

public class DownloadTask extends AsyncTask<String, Void, String>{

    @Override
    protected String doInBackground(String... urls) {
        String result ="";
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(urls[0]);
            urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = urlConnection.getInputStream();

            InputStreamReader reader = new InputStreamReader(in);

            int data = reader.read();

            while(data != -1){
                char current = (char) data;
                result += current;
                data = reader.read();
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject weatherData = new JSONObject(jsonObject.getString("main"));

            double tempInt = Double.parseDouble(weatherData.getString("temp"));
            //convert temp to Farienheit
            int tempIn = (int) (tempInt*1.8-459.67);

            String temp = String.valueOf(tempIn);
            String city = jsonObject.getString("name");
            WalkActivity.tempView.setText(temp);
           // WalkActivity.cityName.setText(city);


        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
