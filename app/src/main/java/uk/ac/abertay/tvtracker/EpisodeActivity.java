package uk.ac.abertay.tvtracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class EpisodeActivity extends AppCompatActivity implements EpisodeAdapter.ItemClickListener {
    private int series_id;
    private int season_num;
    private DatabaseHelper db;
    private RecyclerView recycler_view;
    private EpisodeAdapter adapter;
    private RecyclerView.LayoutManager layout_manager;
    private ArrayList<Episode> episodes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode);

        Intent intent = getIntent();
        Bundle args = intent.getExtras();

        series_id = args.getInt("series_id");
        season_num = args.getInt("season_num");

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
    }

    @Override
    public void onItemClick(View view, int position) {
        Episode episode = adapter.get_item(position);
        Toast.makeText(this, "Episode " + episode.get_name(), Toast.LENGTH_LONG).show();
    }
}
