package uk.ac.abertay.tvtracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<Series> data;
    private LayoutInflater inflater;
    private ItemClickListener click_listener;

    RecyclerViewAdapter(Context context, List<Series> data) {
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int view_type) {
        View view = inflater.inflate(R.layout.recycle_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Series series = data.get(position);
        holder.series_name.setText(series.get_name());
        Bitmap bp = series.get_poster();
        if(bp != null) holder.series_poster.setImageBitmap(series.get_poster());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView series_name;
        ImageView series_poster;

        ViewHolder(View item_view) {
            super(item_view);
            series_name = item_view.findViewById(R.id.series_name);
            series_poster = item_view.findViewById(R.id.series_poster);
            item_view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(click_listener != null) {
                click_listener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    Series get_item(int id) {
        return data.get(id);
    }

    void set_click_listener(ItemClickListener item_click_listener) {
        this.click_listener = item_click_listener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
