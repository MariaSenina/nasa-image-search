package com.example.nasa_image_search;

import static com.example.nasa_image_search.CustomOpener.COL_DATE;
import static com.example.nasa_image_search.CustomOpener.COL_IMAGE;
import static com.example.nasa_image_search.CustomOpener.TABLE_NAME;
import static com.example.nasa_image_search.enums.ApiSetting.API_KEY;
import static com.example.nasa_image_search.enums.ApiSetting.BASE_URL;

import androidx.fragment.app.DialogFragment;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.nasa_image_search.models.ApiResponse;
import com.example.nasa_image_search.models.SavedPhoto;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;

public class MainActivity extends ActivityHeaderCreator {
    private List<SavedPhoto> photos;
    private static LocalDate date;
    private ApiResponse response;
    private SQLiteDatabase sqLiteDatabase;

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
        CustomOpener dbOpener = new CustomOpener(this);
        sqLiteDatabase = dbOpener.getWritableDatabase();

        createActivityHeader();

        Button datePickerButton = findViewById(R.id.showDatePickerButton);
        datePickerButton.setOnClickListener(click -> {
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getSupportFragmentManager(), "datePicker");
        });

        // API request
        NasaImages request = new NasaImages();
        if (date == null) {
            request.execute(BASE_URL.getValue() + API_KEY.getValue() + "&date=" + LocalDate.now().toString());
        } else {
            request.execute(BASE_URL.getValue() + API_KEY.getValue() + "&date=" + date.toString());
        }

        Button saveImageButton = findViewById(R.id.saveImageButton);
        // Add photo to the database on button click
        saveImageButton.setOnClickListener(click -> {
            String selectQuery = "SELECT " + COL_DATE + " FROM " + TABLE_NAME + " WHERE " + COL_DATE + "='" + response.getDate() + "'";

            if (sqLiteDatabase.rawQuery(selectQuery, null).getCount() == 0) {
                ContentValues newRowValues = new ContentValues();
                newRowValues.put(COL_DATE, response.getDate());
                newRowValues.put(COL_IMAGE, response.getUrl());
                sqLiteDatabase.insert(TABLE_NAME, null, newRowValues);
                System.out.println("IMAGE SAVED");
            } else {
                System.out.println("IMAGE ALREADY EXISTS");
            }
        });
    }

    public class NasaImages extends AsyncTask<String, Integer, ApiResponse> {

        @Override
        protected ApiResponse doInBackground(String... args) {
            response = new ApiResponse();

            try {
                InputStream inputStream = makeHttpRequest(args[0]);
                String jsonStr = parseJson(inputStream);

                JSONObject nasaImage = new JSONObject(jsonStr);
                response.setUrl(nasaImage.getString("url"));
                response.setDate(nasaImage.getString("date"));
                response.setHdUrl(nasaImage.getString("hdurl"));
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return response;
        }

        protected InputStream makeHttpRequest(String address) throws IOException {
            URL url = new URL(address);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            return urlConnection.getInputStream();
        }

        @Override
        protected void onPostExecute(ApiResponse apiResponse) {
            TextView urlText = findViewById(R.id.url);
            urlText.setText("Image URL: " + apiResponse.getUrl());

            TextView dateText = findViewById(R.id.date);
            dateText.setText("Image date: " + apiResponse.getDate());

            TextView hdUrlText = findViewById(R.id.hd_url);
            hdUrlText.setText("Image HD URL: " + apiResponse.getHdUrl());
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