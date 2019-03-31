package uk.ac.abertay.tvtracker;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SeriesInfoFragment extends Fragment {
    private DatabaseHelper db;
    private LinearLayout info_layout;
    private ImageView poster;
    private TextView overview;
    private int series_id;
    private Series series;


    public SeriesInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_series_info, container, false);
        Bundle args = getArguments();

        this.series_id = args.getInt("series_id");

        db = new DatabaseHelper(getActivity());

        info_layout = view.findViewById(R.id.info_layout);
        poster = view.findViewById(R.id.series_poster);
        overview = view.findViewById(R.id.series_overview);
        overview.setMovementMethod(new ScrollingMovementMethod());

        series = db.get_series(series_id);

        if(series != null) {
            //getSupportActionBar().setTitle(series.get_name()); TODO: Fix this, allow fragment to talk to activity
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

        return view;
    }

    private void add_info(String txt_title, String txt_value) {
        TextView title = new TextView(getActivity());
        title.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        title.setText(txt_title);
        title.setTextAppearance(R.style.TextAppearance_AppCompat_Subhead);
        info_layout.addView(title);

        TextView value = new TextView(getActivity());
        value.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        value.setText(txt_value);
        info_layout.addView(value);
    }
}
