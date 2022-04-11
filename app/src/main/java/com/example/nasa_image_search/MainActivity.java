package com.example.nasa_image_search;

import static com.example.nasa_image_search.ApiSetting.API_KEY;
import static com.example.nasa_image_search.ApiSetting.BASE_URL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static LocalDate date;

    public void setDate(LocalDate newDate) {
        date = newDate;
        // Force reload the activity
        finish();
        startActivity(getIntent());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // API request
        NasaImages request = new NasaImages();
        if (date == null) {
            request.execute(BASE_URL.getValue() + API_KEY.getValue() + "&date=" + LocalDate.now().toString());
        } else {
            request.execute(BASE_URL.getValue() + API_KEY.getValue() + "&date=" + date.toString());
        }
    }

    // Display date picker
    public void showDatePicker(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
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