package uk.ac.abertay.tvtracker;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.drawable.BitmapDrawable;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(this);

        info_layout = findViewById(R.id.info_layout);

        poster = findViewById(R.id.series_poster);
        overview = findViewById(R.id.series_overview);

        overview.setMovementMethod(new ScrollingMovementMethod());

        Intent intent = getIntent();
        Bundle data = intent.getExtras();

        int series_id = data.getInt("series_id");
        series = db.get_series(series_id);

        if(series != null) {
            getSupportActionBar().setTitle(series.get_name());
            overview.setText(series.get_overview());
            poster.setImageBitmap(series.get_poster());
            if(!series.get_status().isEmpty()) {
                add_info("Status", series.get_status());
            }
            if(!series.get_network().isEmpty()) {
                add_info("Network", series.get_network());
            }
            if(!series.get_first_aired().isEmpty()) {
                add_info("First Aired", series.get_first_aired());
            }
            if(!series.get_content_rating().isEmpty()) {
                add_info("Content Rating", series.get_content_rating());
            }
            if(series.get_site_rating() > 0) {
                add_info("TVDB Rating", series.get_site_rating() + " / 10");
            }
        } else {
            Log.e("SERIES", "Series " + series_id + " doesn't exist");
        }

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

    private void add_info(String txt_title, String txt_value) {
        TextView title = new TextView(this);
        title.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        title.setText(txt_title);
        title.setTextAppearance(R.style.TextAppearance_AppCompat_Subhead);
        info_layout.addView(title);

        TextView value = new TextView(this);
        value.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        value.setText(txt_value);
        info_layout.addView(value);
    }
}
