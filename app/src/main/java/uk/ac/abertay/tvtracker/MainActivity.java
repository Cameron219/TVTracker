package uk.ac.abertay.tvtracker;

import android.Manifest;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RecyclerViewAdapter.ItemClickListener {
    private static final int PERMISSION_REQUEST_CODE = 1;

    private Button btn_open_search_activity;
    private RecyclerViewAdapter adapter;
    private TextView empty_view;
    private RecyclerView recyclerView;
    private ArrayList<Series> series_list;
    private DatabaseHelper db;

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

        btn_open_search_activity = (Button) findViewById(R.id.btn_open_search_activity);
        btn_open_search_activity.setOnClickListener(this);
    }

    private void request_permissions() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "External Storage permissions are required to store posters. Please allow this", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open_search_activity:
                open_search_activity();
                break;
        }
    }

    @Override
    public void onResume() {
        Log.d("RESUME", "Activity has resumed");
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
        Toast.makeText(this, "You clicked " + adapter.get_item(position).get_name() + " on row number " + position, Toast.LENGTH_LONG).show();
    }
}
