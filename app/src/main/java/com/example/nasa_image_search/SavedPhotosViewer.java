package com.example.nasa_image_search;

import static com.example.nasa_image_search.CustomOpener.COL_DATE;
import static com.example.nasa_image_search.CustomOpener.COL_ID;
import static com.example.nasa_image_search.CustomOpener.COL_IMAGE;
import static com.example.nasa_image_search.CustomOpener.COL_NAME;
import static com.example.nasa_image_search.CustomOpener.NASA_IMAGES;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.nasa_image_search.models.Photo;

import java.util.ArrayList;
import java.util.List;

public class SavedPhotosViewer extends ActivityHeaderCreator {
    private List<Photo> photos;
    private SQLiteDatabase sqLiteDatabase;
    private Intent nextPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_photos_viewer);
        createActivityHeader();

        photos = loadItemsFromDatabase("SELECT * FROM " + NASA_IMAGES);

        Button slideshowButton = findViewById(R.id.slideshowButton);
        slideshowButton.setOnClickListener(slideshowClick -> {
            nextPage = new Intent(this, SlideshowActivity.class);
            startActivity(nextPage);
        });

        ListView listView = findViewById(R.id.savedPhotos);
        SavedPhotosViewer.CustomListAdapter adapter = new SavedPhotosViewer.CustomListAdapter();

        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener((p, b, pos, id) -> {
            View inflate = getLayoutInflater().inflate(R.layout.saved_photos_layout, null);
            TextView date = inflate.findViewById(R.id.date);
            date.setText(photos.get(pos).getDate());
            ImageView photo = inflate.findViewById(R.id.photo);
            Bitmap bitmap = BitmapFactory
                    .decodeByteArray(photos.get(pos).getImage(), 0, photos.get(pos).getImage().length);
            photo.setImageBitmap(bitmap);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Would you like to delete this entry?")
                    .setMessage("The selected row is " + pos)
                    .setPositiveButton("Yes", (click1, arg) -> {
                        sqLiteDatabase.delete(NASA_IMAGES, COL_ID + " = " + adapter.getItemId(pos), null);
                        photos.remove(pos);
                        adapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("No", (click1, arg) -> {
                    })
                    .setView(inflate)
                    .create().show();
            return true;
        });

        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(click -> {
            EditText typedSearch = findViewById(R.id.searchText);
            String searchText = typedSearch.getText().toString();
            String searchQuery = "SELECT * FROM " + NASA_IMAGES + " WHERE " + COL_NAME + " LIKE '%" + searchText + "%'";
            photos = loadItemsFromDatabase(searchQuery);
            adapter.notifyDataSetChanged();
        });
    }

    private List<Photo> loadItemsFromDatabase(String query) {
        CustomOpener dbOpener = new CustomOpener(this);
        ArrayList<Photo> retrievedItems = new ArrayList();
        sqLiteDatabase = dbOpener.getWritableDatabase();

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

    private class CustomListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public Object getItem(int position) {
            return photos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return ((Photo)getItem(position)).getId();
        }

        @Override
        public View getView(int position, View oldView, ViewGroup parent) {
            View newView = oldView;

            LayoutInflater inflater = getLayoutInflater();

            if (newView == null) {
                newView = inflater.inflate(R.layout.saved_photos_layout, parent, false);
            }

            TextView titleTextView = newView.findViewById(R.id.photoName);
            titleTextView.setText(getResources().getString(R.string.photo_name)
                    + ": " + ((Photo) getItem(position)).getTitle());

            TextView dateTextView = newView.findViewById(R.id.date);
            dateTextView.setText(getResources().getString(R.string.date)
                    + ": " + ((Photo) getItem(position)).getDate());

            ImageView photoImageView = newView.findViewById(R.id.photo);
            Bitmap bitmap = BitmapFactory
                    .decodeByteArray(((Photo) getItem(position)).getImage(), 0,
                            ((Photo) getItem(position)).getImage().length);
            photoImageView.setImageBitmap(bitmap);

            return newView;
        }
    }
}