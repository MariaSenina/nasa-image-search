package com.example.nasa_image_search;

import static com.example.nasa_image_search.ApiSetting.API_KEY;
import static com.example.nasa_image_search.ApiSetting.BASE_URL;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.example.nasa_image_search.models.ApiResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NasaImages request = new NasaImages();
        request.execute(BASE_URL.getValue() + API_KEY.getValue() + "&date=2021-02-01");
    }

    public class NasaImages extends AsyncTask<String, Integer, ApiResponse> {

        @Override
        protected ApiResponse doInBackground(String... args) {
            ApiResponse result = new ApiResponse();

            try {
                InputStream response = makeHttpRequest(args[0]);
                String jsonStr = parseJson(response);

                JSONObject nasaImage = new JSONObject(jsonStr);
                result.setUrl(nasaImage.getString("url"));
                result.setDate(nasaImage.getString("date"));
                result.setHdUrl(nasaImage.getString("hdurl"));
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return result;
        }

        protected InputStream makeHttpRequest(String address) throws IOException {
            URL url = new URL(address);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            return urlConnection.getInputStream();
        }

        @Override
        protected void onPostExecute(ApiResponse apiResponse) {
            TextView urlText = findViewById(R.id.url);
            urlText.setText(apiResponse.getUrl());

            TextView dateText = findViewById(R.id.date);
            dateText.setText(apiResponse.getDate());

            TextView hdUrlText = findViewById(R.id.hd_url);
            hdUrlText.setText(apiResponse.getHdUrl());
        }

        protected String parseJson(InputStream response) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            return stringBuilder.toString();
        }
    }
}