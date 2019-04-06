package uk.ac.abertay.tvtracker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import uk.ac.abertay.tvtracker.TheTVDB.TVDB_API;

public class EpisodeActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private int series_id;
    private int episode_id;
    private String series_name;
    private Series series;
    private Episode episode;
    private DatabaseHelper db;
    private Toolbar toolbar;

    private TextView episode_name;
    private TextView episode_date;
    private ImageView episode_image;
    private CheckBox episode_watched;
    private CheckBox episode_notification;
    private TextView episode_overview;

    private TVDB_API API = TVDB_API.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode);

        episode_name = findViewById(R.id.episode_name);
        episode_date = findViewById(R.id.episode_date);
        episode_image = findViewById(R.id.episode_image);
        episode_watched = findViewById(R.id.episode_watched);
        episode_notification = findViewById(R.id.episode_notification);
        episode_overview = findViewById(R.id.episode_overview);

        episode_watched.setOnCheckedChangeListener(this);
        episode_notification.setOnCheckedChangeListener(this);

        Intent intent = getIntent();
        Bundle args = intent.getExtras();
        series_id = args.getInt("series_id");
        episode_id = args.getInt("episode_id");
        series_name = args.getString("series_name");

        db = new DatabaseHelper(this);
        series = db.get_series(series_id);
        episode = db.get_episode(episode_id);

        if(episode != null) {
            set_up_toolbar();
            show_episode_details();
        } else {
            Toast.makeText(this, "Episode doesn't exist", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void set_up_toolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(series_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        String sn = "Season " + (episode.get_season_number() < 10 ? "0" + episode.get_season_number() : episode.get_season_number());
        String en = "Episode " + (episode.get_episode_number() < 10 ? "0" + episode.get_episode_number() : episode.get_episode_number());
        getSupportActionBar().setSubtitle(episode.get_season_number() == 0 ? "Special " + en : sn + " " + en);
    }

    private void show_episode_details() {
        episode_name.setText(episode.get_name().equals("null") ? "TBA" : episode.get_name());
        episode_date.setText(episode.get_aired());
        episode_watched.setChecked(episode.is_watched());
        episode_notification.setChecked(episode.is_notification());
        if(episode.get_overview().equals("") || episode.get_overview().equals("null")) {
            episode_overview.setText("No description available at the moment.");
        } else {
            episode_overview.setText(episode.get_overview());
        }

        Bitmap image = episode.get_image();
        if(image == null) { // No local file exists, fetch image from TVDB
            API.get_image(episode.get_file_name(), this);
        } else { // Local image exists, show it
            episode_image.setImageBitmap(image);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(!buttonView.isPressed()) return;
        switch(buttonView.getId()) {
            case R.id.episode_watched:
                toggle_watch_status(isChecked);
                break;
            case R.id.episode_notification:
                toggle_notification(isChecked);
                break;
        }
    }

    private void toggle_watch_status(boolean checked) {
        db.mark_episode_as_watched(episode_id, checked);
        Toast.makeText(this, "Episode marked as " + (checked ? "" : "un") + "watched", Toast.LENGTH_LONG).show();

    }

    private void toggle_notification(boolean checked) {

    }

    public void update_image(Bitmap image) {
        episode_image.setImageBitmap(image);
    }
}