package uk.ac.abertay.tvtracker;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RecyclerViewAdapter.ItemClickListener {
    private static final String GITHUB_SOURCE_URL = "https://github.com/Cameron219/TVTracker";
    private static final int PERMISSION_REQUEST_CODE = 1;

    private RecyclerViewAdapter adapter;
    private TextView empty_view;
    private RecyclerView recyclerView;
    private ArrayList<Series> series_list;
    private DatabaseHelper db;
    private DrawerLayout drawer_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        request_permissions();

        db = new DatabaseHelper(this);

        series_list = db.get_series_names();

        recyclerView = findViewById(R.id.seriesList);
        empty_view = findViewById(R.id.empty_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapter(this, series_list);
        adapter.set_click_listener(this);
        recyclerView.setAdapter(adapter);

        check_if_empty();

        drawer_layout = findViewById(R.id.drawer_layout);

        NavigationView nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        menuItem.setChecked(false);
                        drawer_layout.closeDrawers();

                        switch(menuItem.getItemId()) {
                            case R.id.nav_search:
                                open_search_activity();
                                break;
                            case R.id.nav_source:
                                open_source();
                                break;
                        }
                        return true;
                    }
                }
        );

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar action_bar = getSupportActionBar();
        action_bar.setDisplayHomeAsUpEnabled(true);
        action_bar.setHomeAsUpIndicator(R.drawable.ic_menu);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    private void request_permissions() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "This app requires storage permission. Please allow it", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.fab) {
            open_search_activity();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        series_list.clear();
        ArrayList<Series> new_list = db.get_series_names();
        series_list.addAll(new_list);
        adapter.notifyDataSetChanged();

        check_if_empty();
    }

    private void open_search_activity() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    private void check_if_empty() {
        //TODO: Add button to empty view screen that opens up search page
        if(series_list.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            empty_view.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            empty_view.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
//        Toast.makeText(this, "You clicked " + adapter.get_item(position).get_name() + "\n" + adapter.get_item(position).get_id(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, SeriesActivity.class);
        intent.putExtra("series_id", adapter.get_item(position).get_id());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            drawer_layout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void open_source() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(GITHUB_SOURCE_URL));
        startActivity(intent);
    }
}
