package com.example.nasa_image_search;

import static com.example.nasa_image_search.CustomOpener.COL_DATE;
import static com.example.nasa_image_search.CustomOpener.COL_ID;
import static com.example.nasa_image_search.CustomOpener.COL_IMAGE;
import static com.example.nasa_image_search.CustomOpener.TABLE_NAME;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

        photos = loadItemsFromDatabase();

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
                        sqLiteDatabase.delete(TABLE_NAME, COL_ID + " = " + adapter.getItemId(pos), null);
                        photos.remove(pos);
                        adapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("No", (click1, arg) -> {
                    })
                    .setView(inflate)
                    .create().show();
            return true;
        });
    }

    private List<SavedPhoto> loadItemsFromDatabase() {
        CustomOpener dbOpener = new CustomOpener(this);
        ArrayList<SavedPhoto> retrievedItems = new ArrayList();
        sqLiteDatabase = dbOpener.getWritableDatabase();

        //get all rows from the to-do-list table
        Cursor photoList = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        int dateIndex = photoList.getColumnIndex(COL_DATE);
        int photoIndex = photoList.getColumnIndex(COL_IMAGE);
        int idIndex = photoList.getColumnIndex(COL_ID);

        // iterate over the results
        while (photoList.moveToNext()) {
            String date = photoList.getString(dateIndex);
            String photo = photoList.getString(photoIndex);
            long id = photoList.getLong(idIndex);

            // add retrieved item to the ArrayList for displaying
            retrievedItems.add(new SavedPhoto(id, photo, date, null));
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

            TextView dateTextView = newView.findViewById(R.id.date);
            dateTextView.setText("Date: " + ((SavedPhoto) getItem(position)).getDate());

            TextView photoTextView = newView.findViewById(R.id.photo);
            photoTextView.setText("Photo: " + ((SavedPhoto) getItem(position)).getUrl());

            return newView;
        }
    }
}