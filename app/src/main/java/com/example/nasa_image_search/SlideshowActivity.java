package com.example.nasa_image_search;

import static com.example.nasa_image_search.CustomOpener.COL_DATE;
import static com.example.nasa_image_search.CustomOpener.COL_ID;
import static com.example.nasa_image_search.CustomOpener.COL_IMAGE;
import static com.example.nasa_image_search.CustomOpener.COL_NAME;
import static com.example.nasa_image_search.CustomOpener.NASA_IMAGES;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.nasa_image_search.models.Photo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SlideshowActivity extends AppCompatActivity {
    private List<Photo> photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);

        photos = loadItemsFromDatabase("SELECT * FROM " + NASA_IMAGES);

        Slideshow slideshow = new Slideshow(this);
        slideshow.execute(photos);
    }

    private List<Photo> loadItemsFromDatabase(String query) {
        CustomOpener dbOpener = new CustomOpener(this);
        ArrayList<Photo> retrievedItems = new ArrayList();
        SQLiteDatabase sqLiteDatabase = dbOpener.getWritableDatabase();

        //get all rows from the to-do-list table
        Cursor photoList = sqLiteDatabase.rawQuery(query, null);

        int nameIndex = photoList.getColumnIndex(COL_NAME);
        int dateIndex = photoList.getColumnIndex(COL_DATE);
        int photoIndex = photoList.getColumnIndex(COL_IMAGE);
        int idIndex = photoList.getColumnIndex(COL_ID);

        // iterate over the results
        while (photoList.moveToNext()) {
            String name = photoList.getString(nameIndex);
            String date = photoList.getString(dateIndex);
            byte[] photo = photoList.getBlob(photoIndex);
            long id = photoList.getLong(idIndex);

            // add retrieved item to the ArrayList for displaying
            retrievedItems.add(new Photo(id, photo, date, null, name));
        }

        return retrievedItems;
    }

    private class Slideshow extends AsyncTask<List<Photo>, Integer, String> {
        private boolean newImageSelected;
        private Bitmap currentPicture;
        private ProgressBar progressBar;
        private Context context;
        private ImageView imageView;

        public Slideshow(Context context) {
            this.context = context;
            progressBar = findViewById(R.id.progressBar);
            imageView = findViewById(R.id.imageView);
        }

        @Override
        protected String doInBackground(List<Photo>... photos) {
            AtomicBoolean isWatching = new AtomicBoolean(true);
            int position = 0;

            while (isWatching.get()) {
                currentPicture = BitmapFactory
                        .decodeByteArray(photos[0].get(position).getImage(), 0,
                                photos[0].get(position).getImage().length);

                newImageSelected = true;
                for (int i = 0; i < 100; i++) {
                    try {
                        publishProgress(i);
                        Thread.sleep(20);
                        newImageSelected = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Button backButton = findViewById(R.id.backButton);
                backButton.setOnClickListener(clickBack -> {
                    isWatching.set(false);
                    Intent nextPage = new Intent(context, SavedPhotosViewer.class);
                    startActivity(nextPage);
                });

                if (position < (photos[0].size()-1) ) {
                    position++;
                } else {
                    position = 0;
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (newImageSelected) {
                imageView.setImageBitmap(currentPicture);
            }

            progressBar.setProgress(values[0]);
        }
    }
}