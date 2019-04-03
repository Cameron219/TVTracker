package uk.ac.abertay.tvtracker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class SeasonAdapter extends RecyclerView.Adapter<SeasonAdapter.ViewHolder> {
    private ArrayList<Season> seasons;
    private LayoutInflater inflater;
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        Season season = seasons.get(position);
        holder.season_number.setText("Season " + season.get_season_number());
    }

    @Override
    public int getItemCount() {
        return seasons.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView season_number;

        public ViewHolder(View item_view) {
            super(item_view);
            season_number = itemView.findViewById(R.id.season_numner);
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
