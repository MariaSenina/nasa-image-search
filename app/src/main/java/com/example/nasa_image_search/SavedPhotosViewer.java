package com.example.nasa_image_search;

import static com.example.nasa_image_search.CustomOpener.COL_DATE;
import static com.example.nasa_image_search.CustomOpener.COL_ID;
import static com.example.nasa_image_search.CustomOpener.COL_IMAGE;
import static com.example.nasa_image_search.CustomOpener.COL_NAME;
import static com.example.nasa_image_search.CustomOpener.NASA_IMAGES;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.nasa_image_search.models.SavedPhoto;

import java.util.ArrayList;
import java.util.List;

public class SavedPhotosViewer extends ActivityHeaderCreator {
    private List<SavedPhoto> photos;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_photos_viewer);
        createActivityHeader();

        photos = loadItemsFromDatabase("SELECT * FROM " + NASA_IMAGES);

        ListView listView = findViewById(R.id.savedPhotos);
        SavedPhotosViewer.CustomListAdapter adapter = new SavedPhotosViewer.CustomListAdapter();

        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener((p, b, pos, id) -> {
            View inflate = getLayoutInflater().inflate(R.layout.saved_photos_layout, null);
            TextView date = inflate.findViewById(R.id.date);
            date.setText(photos.get(pos).getDate());
            TextView photo = inflate.findViewById(R.id.photo);
            photo.setText(photos.get(pos).getUrl());

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

    private List<SavedPhoto> loadItemsFromDatabase(String query) {
        CustomOpener dbOpener = new CustomOpener(this);
        ArrayList<SavedPhoto> retrievedItems = new ArrayList();
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
            String photo = photoList.getString(photoIndex);
            long id = photoList.getLong(idIndex);

            // add retrieved item to the ArrayList for displaying
            retrievedItems.add(new SavedPhoto(id, photo, date, null, name));
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
            return ((SavedPhoto)getItem(position)).getId();
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
                    + ": " + ((SavedPhoto) getItem(position)).getTitle());

            TextView dateTextView = newView.findViewById(R.id.date);
            dateTextView.setText(getResources().getString(R.string.date)
                    + ": " + ((SavedPhoto) getItem(position)).getDate());

            TextView photoTextView = newView.findViewById(R.id.photo);
            photoTextView.setText(getResources().getString(R.string.photo)
                    + ": " + ((SavedPhoto) getItem(position)).getUrl());

            return newView;
        }
    }
}