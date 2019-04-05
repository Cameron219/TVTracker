package uk.ac.abertay.tvtracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class EpisodeActivity extends AppCompatActivity {
    private int series_id;
    private int episode_id;
    private String series_name;
    private Episode episode;
    private DatabaseHelper db;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode);

        Intent intent = getIntent();
        Bundle args = intent.getExtras();
        series_id = args.getInt("series_id");
        episode_id = args.getInt("episode_id");
        series_name = args.getString("series_name");

        db = new DatabaseHelper(this);
        episode = db.get_episode(episode_id);

        if(episode != null) {
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(series_name);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            String sn = "Season " + (episode.get_season_number() < 10 ? "0" + episode.get_season_number() : episode.get_season_number());
            String en = "Episode " + (episode.get_episode_number() < 10 ? "0" + episode.get_episode_number() : episode.get_episode_number());
            getSupportActionBar().setSubtitle(episode.get_season_number() == 0 ? "Special " + en : sn + " " + en);
        } else {
            Toast.makeText(this, "Episode doesn't exist", Toast.LENGTH_LONG).show();
            finish();
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
}
