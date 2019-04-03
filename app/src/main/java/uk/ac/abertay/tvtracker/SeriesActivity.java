package uk.ac.abertay.tvtracker;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SeriesActivity extends AppCompatActivity {
    private Series series;
    private ImageView poster;
    private TextView overview;
    private DatabaseHelper db;
    private Toolbar toolbar;
    private LinearLayout info_layout;
    private ViewPager view_pager;
    private ViewPagerAdapter adapter;
    private TabLayout tab_layout;
    private TabItem tab_series;
    private TabItem tab_season;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series);

        //TODO: Pass series into fragments, as toolbar needs names

        Intent intent = getIntent();
        Bundle data = intent.getExtras();

        int series_id = data.getInt("series_id");

        db = new DatabaseHelper(this);
        series = db.get_series(series_id);

        tab_layout = findViewById(R.id.tab_layout);
        tab_series = findViewById(R.id.tab_series);
        tab_season = findViewById(R.id.tab_season);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(series.get_name());

        view_pager = findViewById(R.id.pager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), series);
        view_pager.setAdapter(adapter);
        view_pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab_layout));

        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                view_pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.series_delete:
                confirm_delete();
                return true;
            case R.id.series_mark_as_watched:
                confirm_mark_as_watched();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirm_delete() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Do you really want to delete " + series.get_name() + "?")
                .setIcon(R.drawable.ic_delete)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button){
                        delete();
                    }})
                .setNegativeButton("No", null).show();
    }

    private void delete() {
        //TODO: Implement delete
        Toast.makeText(SeriesActivity.this, "Deleted", Toast.LENGTH_LONG).show();
    }

    private void confirm_mark_as_watched() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm mark as watched")
                .setMessage("Do you really want to mark all episodes of " + series.get_name() + " as watched?")
                .setIcon(R.drawable.ic_check_all)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mark_as_watched();
                    }
                })
                .setNegativeButton("No", null).show();
    }

    private void mark_as_watched() {
        //TODO: Implement mark as watched
        Toast.makeText(this, "Marked as watched", Toast.LENGTH_LONG).show();
    }
}
