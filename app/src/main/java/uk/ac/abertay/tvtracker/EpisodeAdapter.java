package uk.ac.abertay.tvtracker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.ViewHolder> {
    private final ArrayList<Episode> episodes;
    private final LayoutInflater inflater;
    private ItemClickListener click_listener;

    public EpisodeAdapter(Context context, ArrayList<Episode> episodes) {
        this.inflater = LayoutInflater.from(context);
        this.episodes = episodes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int view_type) {
        View view = inflater.inflate(R.layout.recycler_episode, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Episode episode = episodes.get(position);
        holder.episode_name.setText((position + 1) + ". " + episode.get_name());
        holder.episode_date.setText(episode.get_aired());
        holder.episode_check.setVisibility(episode.is_watched() ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView episode_name;
        final TextView episode_date;
        final ImageView episode_check;

        ViewHolder(View item_view) {
            super(item_view);
            episode_name = item_view.findViewById(R.id.episode_name);
            episode_date = item_view.findViewById(R.id.episode_date);
            episode_check = item_view.findViewById(R.id.episode_check);
            item_view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(click_listener != null) {
                click_listener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    public Episode get_item(int id) {
        return episodes.get(id);
    }

    public void set_click_listener(ItemClickListener item_click_listener) {
        this.click_listener = item_click_listener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
