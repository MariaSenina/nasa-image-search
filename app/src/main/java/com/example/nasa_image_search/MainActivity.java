package com.example.nasa_image_search;

import static com.example.nasa_image_search.CustomOpener.COL_DATE;
import static com.example.nasa_image_search.CustomOpener.COL_IMAGE;
import static com.example.nasa_image_search.CustomOpener.COL_NAME;
import static com.example.nasa_image_search.CustomOpener.NASA_IMAGES;
import static com.example.nasa_image_search.enums.ApiSetting.API_KEY;
import static com.example.nasa_image_search.enums.ApiSetting.BASE_URL;

import androidx.fragment.app.DialogFragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nasa_image_search.models.ApiResponse;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Blob;
import java.time.LocalDate;

public class MainActivity extends ActivityHeaderCreator {
    private static LocalDate date;
    private ApiResponse response;
    private SQLiteDatabase sqLiteDatabase;

    /**
     * Reloads activity if new date is set by user.
     * @param newDate New date selected
     */
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

        Intent sentData = getIntent();
        String nameReceived = sentData.getStringExtra("name");
        TextView welcomeText = findViewById(R.id.greetingText);

        SharedPreferences preferences = getSharedPreferences("SavedPreferences", Context.MODE_PRIVATE);
        String savedString = preferences.getString("ReserveName", "");

        String originalWelcome = getResources().getString(R.string.welcome);
        String welcome = originalWelcome.substring(0, originalWelcome.length() - 1);

        String newWelcomeMessage = "";

        if(nameReceived == null && savedString != null) {
            newWelcomeMessage = welcome + " " + savedString + "!";
        } else {
            newWelcomeMessage = welcome + " " + nameReceived + "!";
        }
        welcomeText.setText(newWelcomeMessage);

        Button datePickerButton = findViewById(R.id.showDatePickerButton);
        datePickerButton.setOnClickListener(click -> {
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getSupportFragmentManager(), "datePicker");
        });

        // API request
        NasaImages request = new NasaImages();
        if (date == null) {
            // Set today's date as the query parameter for the API call
            request.execute(BASE_URL.getValue() + API_KEY.getValue() + "&date=" + LocalDate.now().toString());
        } else {
            // Set user-picked date as the query parameter for the API call
            request.execute(BASE_URL.getValue() + API_KEY.getValue() + "&date=" + date.toString());
        }

        Button saveImageButton = findViewById(R.id.saveImageButton);
        // Add photo to the database on button click
        saveImageButton.setOnClickListener(click -> {
            String selectQuery = "SELECT " + COL_DATE + " FROM " + NASA_IMAGES + " WHERE " + COL_DATE + "='" + response.getDate() + "'";

            if (sqLiteDatabase.rawQuery(selectQuery, null).getCount() == 0) {
                ContentValues newRowValues = new ContentValues();
                newRowValues.put(COL_NAME, response.getTitle());
                newRowValues.put(COL_DATE, response.getDate());
                newRowValues.put(COL_IMAGE, response.getImage());
                sqLiteDatabase.insert(NASA_IMAGES, null, newRowValues);
                Snackbar
                        .make(saveImageButton,
                                getResources().getString(R.string.image_saved),
                                Snackbar.LENGTH_LONG)
                        .setAction( getResources().getString(R.string.undo) , clickUndo -> {
                            sqLiteDatabase.delete(NASA_IMAGES, COL_DATE + " = '" + date.toString() + "'", null);
                        })
                        .show();
            } else {
                Toast.makeText(this,
                        getResources().getString(R.string.image_exists), Toast.LENGTH_LONG)
                        .show();
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

                // Receive JSON object
                JSONObject nasaImage = new JSONObject(jsonStr);

                // get byte[] from a NASA image by following image url received in JSON
                InputStream photoInputStream = makeHttpRequest(nasaImage.getString("url"));
                Bitmap image = BitmapFactory.decodeStream(photoInputStream);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] imageByteArray = stream.toByteArray();

                // Assign received values to the attributes of the POJO responsible for the ApiResponse
                response.setTitle(nasaImage.getString("title"));
                response.setImage(imageByteArray);
                response.setDate(nasaImage.getString("date"));
                response.setHdUrl(nasaImage.getString("hdurl"));
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return response;
        }

        /**
         * Takes in a URL in the form of a String and executes an HTTP request.
         * @param address URL in the form of a String.
         * @return InputStream containing the result from the HTTP request.
         * @throws IOException
         */
        protected InputStream makeHttpRequest(String address) throws IOException {
            URL url = new URL(address);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            return urlConnection.getInputStream();
        }

        @Override
        protected void onPostExecute(ApiResponse apiResponse) {
            TextView title = findViewById(R.id.photoTitle);
            title.setText(getResources().getString(R.string.photo_name) + " " + apiResponse.getTitle());

            ImageView image = findViewById(R.id.url);
            Bitmap bitmap = BitmapFactory
                    .decodeByteArray(apiResponse.getImage(), 0, apiResponse.getImage().length);
            image.setImageBitmap(bitmap);

            TextView dateText = findViewById(R.id.date);
            dateText.setText(getResources().getString(R.string.image_date) + " " + apiResponse.getDate());

            TextView hdUrlText = findViewById(R.id.hd_url);
            hdUrlText.setText(getResources().getString(R.string.image_hd_url) + " " + apiResponse.getHdUrl());
        }

        /**
         * Parses an InputStream into a String.
         * @param response InputStream to be parsed.
         * @return A String containing the InputStream content.
         * @throws IOException
         */
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