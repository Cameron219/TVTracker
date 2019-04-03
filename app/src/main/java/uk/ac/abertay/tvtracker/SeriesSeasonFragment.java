package uk.ac.abertay.tvtracker;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SeriesSeasonFragment extends Fragment implements SeasonAdapter.ItemClickListener {
    private int series_id;
    private RecyclerView recycler_view;
    private SeasonAdapter adapter;
    private RecyclerView.LayoutManager layout_manager;
    private DatabaseHelper db;
    private ArrayList<Season> seasons;


    public SeriesSeasonFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_series_season, container, false);

        Bundle args = getArguments();
        this.series_id = args.getInt("series_id");

        db = new DatabaseHelper(getActivity());

        seasons = db.get_seasons(series_id);

        recycler_view = view.findViewById(R.id.recycler_seasons);
        recycler_view.setHasFixedSize(true);

        layout_manager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(layout_manager);

        adapter = new SeasonAdapter(getActivity(), seasons);
        recycler_view.setAdapter(adapter);

        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(getActivity(),"Season " + adapter.get_item(position).get_season_number(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        seasons.clear();
        ArrayList<Season> new_list = db.get_seasons(series_id);
        seasons.addAll(new_list);
        adapter.notifyDataSetChanged();
    }
}
