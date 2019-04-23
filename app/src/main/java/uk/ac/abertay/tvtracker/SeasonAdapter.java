package uk.ac.abertay.tvtracker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class SeasonAdapter extends RecyclerView.Adapter<SeasonAdapter.ViewHolder> {
    private final ArrayList<Season> seasons;
    private final LayoutInflater inflater;
    private ItemClickListener click_listener;

    public SeasonAdapter(Context context, ArrayList<Season> seasons) {
        this.inflater = LayoutInflater.from(context);
        this.seasons = seasons;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int view_type) {
        View view = inflater.inflate(R.layout.recycler_season, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Season season = seasons.get(position);
        holder.season_number.setText(season.get_season_number() == 0 ? "Special Episodes" : "Season " + season.get_season_number());
        holder.episode_count.setText(season.get_number_of_watched_episodes() + "/" + season.get_number_of_episodes() + " episodes watched.");
        double percentage = ((double) season.get_number_of_watched_episodes() / (double) season.get_number_of_episodes()) * 100;
        holder.progress_bar.setProgress((int) percentage);
    }

    @Override
    public int getItemCount() {
        return seasons.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView season_number;
        final TextView episode_count;
        final ProgressBar progress_bar;

        ViewHolder(View item_view) {
            super(item_view);
            season_number = item_view.findViewById(R.id.season_number);
            episode_count = item_view.findViewById(R.id.episode_count);
            progress_bar = item_view.findViewById(R.id.progress_bar);
            item_view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(click_listener != null) {
                click_listener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    public Season get_item(int id) {
        return seasons.get(id);
    }

    void set_click_listener(ItemClickListener item_click_listener) {
        this.click_listener = item_click_listener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
