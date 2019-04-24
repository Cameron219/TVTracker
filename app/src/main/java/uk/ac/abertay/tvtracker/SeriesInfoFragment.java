package uk.ac.abertay.tvtracker;


import android.graphics.Bitmap;
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
    private Series series;

    private ImageView poster;
    private TextView overview;

    private TextView status_title;
    private TextView network_title;
    private TextView first_aired_title;
    private TextView content_rating_title;
    private TextView tvdb_rating_title;

    private TextView status;
    private TextView network;
    private TextView first_aired;
    private TextView content_rating;
    private TextView tvdb_rating;




    public SeriesInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_series_info, container, false);
        Bundle args = getArguments();

        int series_id = args.getInt("series_id");
        series = new Series(series_id);
        series.set_name(args.getString("name"));
        series.set_poster(args.getString("poster"));
        series.set_status(args.getString("status"));
        series.set_network(args.getString("network"));
        series.set_first_aired(args.getString("first_aired"));
        series.set_content_rating(args.getString("content_rating"));
        series.set_site_rating(args.getDouble("site_rating"));
        series.set_overview(args.getString("overview"));

        status_title = view.findViewById(R.id.episode_status_title);
        network_title = view.findViewById(R.id.episode_network_title);
        first_aired_title = view.findViewById(R.id.episode_first_aired_title);
        content_rating_title = view.findViewById(R.id.episode_content_rating_title);
        tvdb_rating_title = view.findViewById(R.id.episode_tvdb_rating_title);

        status = view.findViewById(R.id.episode_status);
        network = view.findViewById(R.id.episode_network);
        first_aired = view.findViewById(R.id.episode_first_aired);
        content_rating = view.findViewById(R.id.episode_content_rating);
        tvdb_rating = view.findViewById(R.id.episode_tvdb_rating);

        poster = view.findViewById(R.id.series_poster);
        overview = view.findViewById(R.id.series_overview);

        set_info();

        return view;
    }

    private void set_info() {
        overview.setText(series.get_overview().equals("null") ? getActivity().getString(R.string.no_description) : series.get_overview());
        overview.setMovementMethod(new ScrollingMovementMethod());
        Bitmap bp = series.get_poster();
        if(bp != null) poster.setImageBitmap(bp);

        if(series.get_status().isEmpty()) {
            status_title.setVisibility(View.GONE);
            status.setVisibility(View.GONE);
        } else {
            status.setText(series.get_status());
        }

        if(series.get_network().isEmpty()) {
            network_title.setVisibility(View.GONE);
            network.setVisibility(View.GONE);
        } else {
            network.setText(series.get_network());
        }

        if(series.get_first_aired().isEmpty()) {
            first_aired_title.setVisibility(View.GONE);
            first_aired.setVisibility(View.GONE);
        } else {
            first_aired.setText(series.get_first_aired());
        }

        if(series.get_content_rating().isEmpty()) {
            content_rating_title.setVisibility(View.GONE);
            content_rating.setVisibility(View.GONE);
        } else {
            content_rating.setText(series.get_content_rating());
        }

        if(series.get_site_rating() == 0) {
            tvdb_rating_title.setVisibility(View.GONE);
            tvdb_rating.setVisibility(View.GONE);
        } else {
            tvdb_rating.setText(series.get_site_rating() + " / 10");
        }
    }
}
