package uk.ac.abertay.tvtracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class EpisodeListActivity extends AppCompatActivity implements EpisodeAdapter.ItemClickListener {
    private int series_id;
    private int season_num;
    private String series_name;
    private DatabaseHelper db;
    private RecyclerView recycler_view;
    private EpisodeAdapter adapter;
    private RecyclerView.LayoutManager layout_manager;
    private ArrayList<Episode> episodes;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_list);

        Intent intent = getIntent();
        Bundle args = intent.getExtras();

        series_id = args.getInt("series_id");
        season_num = args.getInt("season_num");
        series_name = args.getString("series_name");

        LinearLayout layout = findViewById(R.id.linear_layout);
        db = new DatabaseHelper(this);

        episodes = db.get_episodes(series_id, season_num);

        recycler_view = findViewById(R.id.recycler_episodes);
        recycler_view.setHasFixedSize(true);

        layout_manager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(layout_manager);

        adapter = new EpisodeAdapter(this, episodes);
        adapter.set_click_listener(this);
        recycler_view.setAdapter(adapter);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(series_name);
        getSupportActionBar().setSubtitle(season_num == 0 ? "Special Episodes" : "Season " + (season_num < 10 ? "0" + season_num : season_num));
    }

    @Override
    public void onItemClick(View view, int position) {
        Episode episode = adapter.get_item(position);
        Intent intent = new Intent(this, EpisodeActivity.class);
        intent.putExtra("series_id", series_id);
        intent.putExtra("series_name", series_name);
        intent.putExtra("episode_id", episode.get_episode_id());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_season, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.season_mark_as_watched:
                confirm_mark_as_watched();
                return true;
            case R.id.season_mark_as_unwatched:
                confirm_mark_as_unwatched();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirm_mark_as_watched() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm mark as watched")
                .setMessage("Do you really want to mark all episodes of season " + season_num + " as watched?")
                .setIcon(R.drawable.ic_check_all)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        update_watch_status(true);
                    }
                })
                .setNegativeButton("No", null).show();
    }

    private void confirm_mark_as_unwatched() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm mark as unwatched")
                .setMessage("Do you really want to mark all episodes of season " + season_num + " as unwatched?")
                .setIcon(R.drawable.ic_check_all)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        update_watch_status(false);
                    }
                })
                .setNegativeButton("No", null).show();
    }
    private void update_watch_status(boolean watched) {
        //TODO: Implement mark as watched
        //Toast.makeText(this, "Marked as watched", Toast.LENGTH_LONG).show();
        db.mark_all_as_watched(series_id, season_num, watched);
        adapter.notifyDataSetChanged();
    }

}
