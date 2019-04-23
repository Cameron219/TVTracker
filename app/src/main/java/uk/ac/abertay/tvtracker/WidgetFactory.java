package uk.ac.abertay.tvtracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

class WidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context context;
    private ArrayList<Episode> episodes;
    private DatabaseHelper db;

    public WidgetFactory(Context context, Intent intent) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        episodes = new ArrayList<>();
        db = new DatabaseHelper(context);
    }

    @Override
    public void onDataSetChanged() {
        episodes = db.get_upcoming_episodes(10);
        Log.d("WIDGET", "Episodes: " + episodes.toString());
    }

    @Override
    public void onDestroy() {
        episodes.clear();
    }

    @Override
    public int getCount() {
        return episodes.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.upcoming_widget_row);
        Episode episode = episodes.get(position);
        Series series = db.get_series(episode.get_series_id());
        rv.setTextViewText(R.id.row_series_name, series.get_name());
        rv.setTextViewText(R.id.row_series_episode, episode.get_season_episode() + " " + episode.get_name());
        rv.setTextViewText(R.id.row_series_date, episode.get_aired());

        Bundle bundle = new Bundle();
        bundle.putInt("series_id", episode.get_series_id());
        bundle.putInt("episode_id", episode.get_episode_id());

        Intent intent = new Intent();
        intent.putExtras(bundle);
        rv.setOnClickFillInIntent(R.id.widget_row, intent);
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
