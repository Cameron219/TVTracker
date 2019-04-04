package uk.ac.abertay.tvtracker;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.abertay.tvtracker.TheTVDB.Banner;
import uk.ac.abertay.tvtracker.TheTVDB.Poster;
import uk.ac.abertay.tvtracker.TheTVDB.Response;
import uk.ac.abertay.tvtracker.TheTVDB.TVDB_API;


public class SearchActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {
    private TextView input_search;
    private Button btn_search;
    private ScrollView scroll_view;
    private LinearLayout layout_results;
    private Toolbar toolbar;
    private TVDB_API API = TVDB_API.getInstance();
    int series_id = -1;

    private ArrayList<AsyncTask> task_queue;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search");

        db = new DatabaseHelper(this);



        task_queue = new ArrayList<AsyncTask>();
        input_search = (TextView) findViewById(R.id.input_search);
        btn_search = (Button) findViewById(R.id.btn_search);
        scroll_view = (ScrollView) findViewById(R.id.scroll_results);
        layout_results = (LinearLayout) findViewById(R.id.layout_results);

        btn_search.setOnClickListener(this);
        input_search.setOnKeyListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                search();
                break;
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
        AsyncTask search_task = API.search_for_show(search_term, this);
        task_queue.add(search_task);
    }

    public void display_results(Response response) {
        //Log.d("SEARCH", "Response: " + response.get_response());
        //Log.d("SEARCH", "Response Code: " + response.get_response_code());

        if(layout_results.getChildCount() > 0) {
            scroll_view.setFocusable(false); //TODO: Fix scrolling to top on clear
            layout_results.removeAllViews();
        }

        if(response.get_response_code() == 200) {
            try {
                JSONObject json = new JSONObject(response.get_response());
                JSONArray data = json.getJSONArray("data");

                for(int i = 0; i < data.length(); i++) {
                    JSONObject show = (JSONObject) data.get(i);

                    CardView cv = new CardView(this);
                    LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    lp.setMargins(10, 10, 10, 10);
                    cv.setLayoutParams(lp);
                    cv.setRadius(8);
                    cv.setTag(show);

                    LinearLayout card_layout = new LinearLayout(this);
                    card_layout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    card_layout.setOrientation(LinearLayout.VERTICAL);

                    ImageView img = new ImageView(this);
                    img.setId(show.getInt("id"));
                    img.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    img.setAdjustViewBounds(true);
                    img.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    card_layout.addView(img);

                    TextView tv_name = new TextView(this);
                    tv_name.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                    tv_name.setText(show.getString("seriesName"));
                    tv_name.setTextColor(0xff000000);
                    tv_name.setTextSize(26);
                    tv_name.setPadding(20, 10, 10, 10);
                    card_layout.addView(tv_name);

                    TextView tv_desc = new TextView(this);
                    tv_desc.setText(show.isNull("overview") ? "No description available" : show.getString("overview"));
                    tv_desc.setTextColor(0xff888888);
                    tv_desc.setPadding(20, 10, 10, 10);
                    tv_desc.setMaxHeight(500);
                    card_layout.addView(tv_desc);

                    Button btn_add = new Button(this);
                    btn_add.setText("Add Show");
                    btn_add.setBackgroundColor(0x11000000);
                    btn_add.setTextColor(0xff000000);
                    btn_add.setTextSize(20);
                    btn_add.setTag(show);
                    btn_add.setPadding(20, 10, 10, 10);
                    btn_add.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                                v.setBackgroundColor(0x33000000);
                            } else {
                                v.setBackgroundColor(0x11000000);
                            }
                            return false;
                        }
                    });
                    btn_add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                JSONObject show = (JSONObject) v.getTag(); //TODO: Change tag so it just stores the ID
                                ((Button) v).setVisibility(View.INVISIBLE);

                                if(!db.series_exist(show.getInt("id"))) {
                                    series_id = show.getInt("id");
                                    API.get_poster(show.getInt("id"), show.getString("slug"), SearchActivity.this);
                                    API.get_series(show.getInt("id"), SearchActivity.this);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    card_layout.addView(btn_add);

                    cv.addView(card_layout);
                    layout_results.addView(cv);

                    if(!show.isNull("banner") && !show.getString("banner").equals("")) {
                        AsyncTask banner_task = API.get_banner("https://www.thetvdb.com/banners/" + show.getString("banner"), show.getInt("id"), this);
                        task_queue.add(banner_task);
                    }
                }
            } catch (JSONException e) {
                Log.e("JSON", "Unable to parse response");
                e.printStackTrace();
            }
        } else {
            TextView tv_error = new TextView(this);
            tv_error.setText("No results found");
            layout_results.addView(tv_error);
        }
    }

    public void update_poster(Poster poster) {
        ImageView img = findViewById(poster.get_id());
        if(img == null) {
            Log.e("POSTER", "Unable to find ImageView for show " + poster.get_id());
        } else {
            if(poster.get_bitmap() != null) {
                img.setImageBitmap(poster.get_bitmap());
            }
        }
    }

    public void insert_series(JSONObject series) {
        db.insert_series(series);
        if(series_id > 0) {
            Intent intent = new Intent(this, SeriesActivity.class);
            intent.putExtra("series_id", series_id);
            intent.putExtra("fetch_episodes", true);
            startActivity(intent);
        }
    }

    public void update_banner(Banner banner) {
        ImageView img = findViewById(banner.get_id());
        if(img == null) {
            Log.e("POSTER", "Unable to find ImageView for show " + banner.get_id());
        } else {
            if(banner.get_banner() != null) {
                img.setImageBitmap(banner.get_banner());
                //img.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch(v.getId()) {
            case R.id.input_search:
                if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    search();
                    return true;
                }
        }
        return false;
    }
}
