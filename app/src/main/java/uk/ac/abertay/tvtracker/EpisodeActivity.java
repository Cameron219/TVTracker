package uk.ac.abertay.tvtracker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import uk.ac.abertay.tvtracker.TheTVDB.TVDB_API;

/**
 * Activity that shows Episode information
 */
public class EpisodeActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    // Private variables
    private int episode_id;
    private String series_name;
    private Series series;
    private Episode episode;
    private DatabaseHelper db;

    // View elements
    private TextView episode_name;
    private TextView episode_date;
    private TextView episode_network;
    private ImageView episode_image;
    private CheckBox episode_watched;
    private TextView episode_overview;
    private ProgressBar spinner;

    private final TVDB_API API = TVDB_API.getInstance();

    /**
     * Initializes activity
     * @param savedInstanceState Saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode);

        episode_name = findViewById(R.id.episode_name);
        episode_date = findViewById(R.id.episode_date);
        episode_network = findViewById(R.id.episode_network);
        episode_image = findViewById(R.id.episode_image);
        episode_watched = findViewById(R.id.episode_watched);
        episode_overview = findViewById(R.id.episode_overview);
        spinner = findViewById(R.id.spinner);

        episode_watched.setOnCheckedChangeListener(this);

        Intent intent = getIntent();
        Bundle args = intent.getExtras();
        int series_id = args.getInt("series_id");
        episode_id = args.getInt("episode_id");
        series_name = args.getString("series_name");

        db = new DatabaseHelper(this);
        series = db.get_series(series_id);
        episode = db.get_episode(episode_id);

        if(episode != null) {
            set_up_toolbar();
            show_episode_details();
        } else {
            // Shouldn't ever happen
            Toast.makeText(this, "Episode doesn't exist", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * Initialize the toolbar
     * Sets the title, as well displays back button
     */
    private void set_up_toolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(series_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        String sn = "Season " + (episode.get_season_number() < 10 ? "0" + episode.get_season_number() : episode.get_season_number());
        String en = "Episode " + (episode.get_episode_number() < 10 ? "0" + episode.get_episode_number() : episode.get_episode_number());
        getSupportActionBar().setSubtitle(episode.get_season_number() == 0 ? "Special " + en : sn + " " + en);
    }

    private void show_episode_details() {
        episode_name.setText(episode.get_name());
        episode_date.setText(episode.get_aired() + " " + (series.get_airstime().equals("null") ? "" : series.get_airstime()));
        episode_network.setText(series.get_network());
        episode_watched.setChecked(episode.is_watched());
        if(episode.get_overview().equals("") || episode.get_overview().equals("null")) {
            episode_overview.setText(R.string.no_description);
        } else {
            episode_overview.setText(episode.get_overview());
        }

        //TODO: Insert placeholder image when no image is available
        Bitmap image = episode.get_image();
        if(image == null) { // No local file exists, fetch image from TVDB
            API.get_image(episode.get_file_name(), this, spinner);
        } else { // Local image exists, show it
            episode_image.setImageBitmap(image);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(!buttonView.isPressed()) return;
        if(buttonView.getId() == R.id.episode_watched) {
            toggle_watch_status(isChecked);
        }
    }

    private void toggle_watch_status(boolean checked) {
        db.mark_episode_as_watched(episode_id, checked);
        Toast.makeText(this, "Episode marked as " + (checked ? "" : "un") + "watched", Toast.LENGTH_LONG).show();

    }

    public void update_image(Bitmap image) {
        if(image != null){
            episode_image.setImageBitmap(image);
        }
    }
}
