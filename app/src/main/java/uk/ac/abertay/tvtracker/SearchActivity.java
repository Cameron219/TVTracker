package uk.ac.abertay.tvtracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.abertay.tvtracker.TheTVDB.Banner;
import uk.ac.abertay.tvtracker.TheTVDB.Response;
import uk.ac.abertay.tvtracker.TheTVDB.TVDB_API;


public class SearchActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener, SearchAdapter.ItemClickListener {
    private TextView input_search;
    private SearchAdapter adapter;
    private ArrayList<Series> results;
    private final TVDB_API API = TVDB_API.getInstance();
    private int series_id = -1;

    private ArrayList<AsyncTask> task_queue;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        results = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search");

        db = new DatabaseHelper(this);

        RecyclerView recycler_view = findViewById(R.id.recycler_search);

        RecyclerView.LayoutManager layout_manager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(layout_manager);

        adapter = new SearchAdapter(this, results);
        adapter.set_click_listener(this);
        recycler_view.setAdapter(adapter);

        task_queue = new ArrayList<>();
        input_search = findViewById(R.id.input_search);
        Button btn_search = findViewById(R.id.btn_search);

        btn_search.setOnClickListener(this);
        input_search.setOnKeyListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_search) {
            search();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        clear_task_queue();
    }

    private void clear_task_queue() {
        for (int i = task_queue.size() - 1; i >= 0; i--) {
            AsyncTask task = task_queue.get(i);
            if (task.getStatus() == AsyncTask.Status.RUNNING || task.getStatus() == AsyncTask.Status.PENDING) {
                task.cancel(true);
            }
            task_queue.remove(task);
        }
    }

    private void search() {
        clear_task_queue();
        String search_term = input_search.getText().toString();
        // TODO: Check if token is present (and validate it? maybe.)
        AsyncTask search_task = API.search_for_series(search_term, this);
        task_queue.add(search_task);
    }


    public void display_results(Response response) {
        if(response.get_response_code() == 200) {
            try {
                JSONObject json = new JSONObject(response.get_response());
                JSONArray data = json.getJSONArray("data");
                results.clear();
                for(int i = 0; i < data.length(); i++) {
                    Series show = new Series((JSONObject) data.get(i));
                    results.add(show);

                    if(!show.get_banner_path().isEmpty()) {
                        AsyncTask banner_task = API.get_banner("https://www.thetvdb.com/banners/" + show.get_banner_path(), show.get_id(), this);
                        task_queue.add(banner_task);
                    }
                }
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e("JSON", "Unable to parse response");
                e.printStackTrace();
            }
        }
    }

    public void insert_series(JSONObject series) {
        if(db.insert_series(series)){
            if(series_id > 0) {
                Intent intent = new Intent(this, SeriesActivity.class);
                intent.putExtra("series_id", series_id);
                intent.putExtra("fetch_episodes", true);
                startActivity(intent);
            }
        } else {
            Toast.makeText(this, "Error adding Series. Please check internet and try again", Toast.LENGTH_LONG).show();
        }
    }

    public void update_banner(Banner banner) {
        ImageView img = findViewById(banner.get_id());
        if(img == null) {
            Log.e("BANNER", "Unable to find ImageView for show " + banner.get_id());
        } else {
            if(banner.get_banner() != null) {
                img.setImageBitmap(banner.get_banner());
                //img.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(v.getId() == R.id.input_search) {
            if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                search();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onItemClick(View view, int position) {
        final Series series = adapter.get_result(position);
        if(!db.series_exist(series.get_id())) {
            new AlertDialog.Builder(this)
                    .setTitle("Track?")
                    .setMessage("Would you like to track " + series.get_name() + "?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int button) {
                            add_series(series);
                        }
                    })
                    .setNegativeButton("No", null).show();
        } else {
            Intent intent = new Intent(this, SeriesActivity.class);
            intent.putExtra("series_id", series.get_id());
            startActivity(intent);
        }
    }

    private void add_series(Series series) {
        series_id = series.get_id();
        API.get_poster(series.get_id(), series.get_slug());
        API.get_series(series.get_id(), SearchActivity.this);
    }
}
