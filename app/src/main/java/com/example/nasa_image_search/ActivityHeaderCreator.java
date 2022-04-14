package com.example.nasa_image_search;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class ActivityHeaderCreator extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public void createActivityHeader() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent nextActivity = null;
        LinearLayout placeholderLayout = findViewById(R.id.placeholderLayout);

        switch( item.getItemId() ) {
            case R.id.homeOption:
                if (this.getClass() != MainActivity.class) {
                    nextActivity = new Intent(this, MainActivity.class);
                    getLayoutInflater().inflate(R.layout.activity_main, (DrawerLayout)placeholderLayout.getParent(), false);
                    startActivity(nextActivity);
                }
                break;
            case R.id.savedPhotos:
                if (this.getClass() != SavedPhotosViewer.class) {
                    nextActivity = new Intent(this, SavedPhotosViewer.class);
                    getLayoutInflater().inflate(R.layout.saved_photos_layout, (DrawerLayout)placeholderLayout.getParent(), false);
                    startActivity(nextActivity);
                }
                break;
            case R.id.exit:
                this.finishAffinity();
                break;
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu_layout, menu);

        return true;
    }
}
