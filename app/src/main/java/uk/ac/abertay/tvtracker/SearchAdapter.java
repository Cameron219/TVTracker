package uk.ac.abertay.tvtracker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private final ArrayList<Series> results;
    private final LayoutInflater inflater;
    private ItemClickListener click_listener;

    public SearchAdapter(Context context, ArrayList<Series> results) {
        this.inflater = LayoutInflater.from(context);
        this.results = results;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int view_type) {
        View view = inflater.inflate(R.layout.recycler_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Series series = results.get(position);
        holder.banner.setId(series.get_id());
        holder.name.setText(series.get_name());
        holder.overview.setText(series.get_overview());
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView banner;
        private final TextView name;
        private final TextView overview;

        ViewHolder(View item_view) {
            super(item_view);
            banner = item_view.findViewById(R.id.search_banner);
            name = item_view.findViewById(R.id.search_name);
            overview = item_view.findViewById(R.id.search_overview);
            item_view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(click_listener != null) {
                click_listener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    public Series get_result(int id) {
        return results.get(id);
    }

    public void set_click_listener(ItemClickListener item_click_listener) {
        this.click_listener = item_click_listener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
