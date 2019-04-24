package uk.ac.abertay.tvtracker;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SeriesSeasonFragment extends Fragment implements SeasonAdapter.ItemClickListener {
    private int series_id;
    private String series_name;
    private RecyclerView recycler_view;
    private TextView no_seasons;
    private SeasonAdapter adapter;
    private DatabaseHelper db;
    private ArrayList<Season> seasons;

    /**
     * Required empty constructor
     */
    public SeriesSeasonFragment() {

    }


    /**
     * Initialize the fragment
     * @param inflater Inflater
     * @param container Container
     * @param savedInstanceState Saved Instance State
     * @return Fragment View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_series_season, container, false);

        Bundle args = getArguments();
        this.series_id = args.getInt("series_id");
        this.series_name = args.getString("series_name");


        db = new DatabaseHelper(getActivity());

        seasons = db.get_seasons(series_id);

        recycler_view = view.findViewById(R.id.recycler_seasons);
        no_seasons = view.findViewById(R.id.no_seasons);
        recycler_view.setHasFixedSize(true);

        RecyclerView.LayoutManager layout_manager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(layout_manager);

        adapter = new SeasonAdapter(getActivity(), seasons);
        adapter.set_click_listener(this);
        recycler_view.setAdapter(adapter);

        toggle_view();

        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        Season season = adapter.get_item(position);
        Intent intent = new Intent(getActivity(), EpisodeListActivity.class);
        intent.putExtra("series_id", series_id);
        intent.putExtra("series_name", series_name);
        intent.putExtra("season_num", season.get_season_number());
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        update_view();
    }

    private void update_view() {
        seasons.clear();
        ArrayList<Season> new_list = db.get_seasons(series_id);
        seasons.addAll(new_list);
        adapter.notifyDataSetChanged();

        toggle_view();
    }

    private void toggle_view() {
        if(adapter.getItemCount() == 0) {
            no_seasons.setVisibility(View.VISIBLE);
            recycler_view.setVisibility(View.INVISIBLE);
        } else {
            no_seasons.setVisibility(View.INVISIBLE);
            recycler_view.setVisibility(View.VISIBLE);
        }
    }
}
