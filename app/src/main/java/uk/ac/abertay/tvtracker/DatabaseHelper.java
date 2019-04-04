package uk.ac.abertay.tvtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import uk.ac.abertay.tvtracker.TheTVDB.TVDB_API;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tv";

    private static final String SERIES_TABLE_NAME = "series";
    private static final String[] SERIES_COLUMN_NAMES = {"seriesId", "name", "poster", "banner", "status", "network", "firstAired", "overview", "lastUpdated", "rating", "imdbId", "siteRating", "slug"};
    private static final String[] SERIES_COLUMN_TYPES = {"INTEGER", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "INTEGER", "TEXT", "TEXT", "REAL", "TEXT"};

    private static final String EPISODE_TABLE_NAME = "episode";
    private static final String[] EPISODE_COLUMN_NAMES = {"episodeId", "seriesId", "season", "episode", "name", "aired", "overview", "lastUpdated", "fileName", "imdbId", "siteRating", "watched"};
    private static final String[] EPISODE_COLUMN_TYPES = {"INTEGER", "INTEGER", "INTEGER", "INTEGER", "TEXT", "TEXT", "TEXT", "INTEGER", "TEXT", "TEXT", "REAL", "INTEGER"};

    private TVDB_API API = TVDB_API.getInstance();

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String contacts_table_create = "CREATE TABLE " + SERIES_TABLE_NAME + " (";
        for(int i = 0; i < SERIES_COLUMN_NAMES.length; i++) {
            contacts_table_create += SERIES_COLUMN_NAMES[i] + " " + SERIES_COLUMN_TYPES[i] + (i < SERIES_COLUMN_NAMES.length - 1 ? "," : ");");
        }
        db.execSQL(contacts_table_create);

        String episodes_table_create = "CREATE TABLE " + EPISODE_TABLE_NAME + " (";
        for(int i = 0; i < EPISODE_COLUMN_NAMES.length; i++ ) {
            episodes_table_create += EPISODE_COLUMN_NAMES[i] + " " + EPISODE_COLUMN_TYPES[i] + (i < EPISODE_COLUMN_NAMES.length - 1 ? "," : ");");
        }
        db.execSQL(episodes_table_create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean series_exist(int series_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT seriesId, name FROM " + SERIES_TABLE_NAME + " WHERE seriesId = ?";
        Cursor cursor = db.rawQuery(query, new String[] {"" + series_id});
        if(cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public void insert_series(JSONObject series) {
        try {
            if(!series_exist(series.getInt("id"))) {
                ContentValues row = new ContentValues();
                row.put("seriesId", series.getInt("id"));
                row.put("name", series.getString("seriesName"));
                row.put("poster", series.getString("slug") + ".png");
                row.put("banner", series.getString("banner"));
                row.put("status", series.getString("status"));
                row.put("network", series.getString("network"));
                row.put("firstAired", series.getString("firstAired"));
                row.put("overview", series.getString("overview"));
                row.put("lastUpdated", series.getInt("lastUpdated"));
                row.put("rating", series.getString("rating"));
                row.put("imdbId", series.getString("imdbId"));
                row.put("siteRating", series.getDouble("siteRating"));
                row.put("slug", series.getString("slug"));

                SQLiteDatabase db = this.getWritableDatabase();
                db.insert(SERIES_TABLE_NAME, null, row);
                db.close();

                Log.d("DB", "Added: " + series.getString("seriesName"));
            } else {
                Log.d("DB", "Didn't Add Duplicate: " + series.getString("seriesName"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void insert_episodes(JSONArray episodes) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT INTO " + EPISODE_TABLE_NAME + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        SQLiteStatement statement = db.compileStatement(sql);
        db.beginTransaction();
        try {
            //ContentValues row = new ContentValues();
            for(int i = 0; i < episodes.length(); i++ ) {
                statement.clearBindings();
                try {
                    JSONObject episode = episodes.getJSONObject(i);
                    statement.bindLong(1, episode.getInt("id"));
                    statement.bindLong(2, episode.getInt("seriesId"));
                    statement.bindLong(3, episode.getInt("airedSeason"));
                    statement.bindLong(4, episode.getInt("airedEpisodeNumber"));
                    statement.bindString(5, episode.getString("episodeName"));
                    statement.bindString(6, episode.getString("firstAired"));
                    statement.bindString(7, episode.getString("overview"));
                    statement.bindLong(8, episode.getInt("lastUpdated"));
                    statement.bindString(9, episode.getString("filename"));
                    statement.bindString(10, episode.getString("imdbId"));
                    statement.bindDouble(11, episode.getDouble("siteRating"));
                    statement.bindLong(12, 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                statement.execute();
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

    public void insert_episode(JSONObject episode) {
        try {
            if(!episode_exist(episode.getInt("id"))) {
                ContentValues row = new ContentValues();
                row.put("episodeId", episode.getInt("id"));
                row.put("seriesId", episode.getInt("seriesId"));
                row.put("season", episode.getInt("airedSeason"));
                row.put("episode", episode.getInt("airedEpisodeNumber"));
                row.put("name", episode.getString("episodeName"));
                row.put("aired", episode.getString("firstAired"));
                row.put("overview", episode.getString("overview"));
                row.put("lastUpdated", episode.getInt("lastUpdated"));
                row.put("filename", episode.getString("filename"));
                row.put("imdbId", episode.getString("imdbId"));
                row.put("siteRating", episode.getDouble("siteRating"));
                row.put("watched", 0);

                SQLiteDatabase db = this.getWritableDatabase();
                db.insert(EPISODE_TABLE_NAME, null, row);
                db.close();

                Log.d("DB", "Added: S" + episode.getInt("airedSeason") + "E" + episode.getInt("airedEpisodeNumber"));
            } else {
                Log.d("DB", "Didn't Add Duplicate: S" + episode.getInt("airedSeason") + "E" + episode.getInt("airedEpisodeNumber"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean episode_exist(int episode_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT seriesId, name FROM " + EPISODE_TABLE_NAME + " WHERE seriesId = ?";
        Cursor cursor = db.rawQuery(query, new String[] {"" + episode_id});

        int episode_count = cursor.getCount();
        cursor.close();
        db.close();

        return episode_count > 0;
    }

    public ArrayList<Series> get_series_names() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor result = db.query(SERIES_TABLE_NAME, new String[] {"seriesId", "name", "poster"}, null, null, null, null, "name", null);

        ArrayList<Series> series_list = new ArrayList<>();

        for(int i = 0; i < result.getCount(); i++ ) {
            result.moveToPosition(i);
            Series series = new Series(result.getInt(0), result.getString(1), result.getString(2));
            series_list.add(series);
        }

        result.close();
        db.close();

        return series_list;
    }

    public ArrayList<Season> get_seasons(int series_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Season> seasons = new ArrayList<>();

        Cursor result = db.query(EPISODE_TABLE_NAME, null, "seriesId = ?", new String[] {"" + series_id}, null, null, null);

        //TODO: Change this, rather hacky and can probably be done much better
        for(int i = 0; i < result.getCount(); i++) {
            result.moveToPosition(i);
            int season_index = -1;
            Episode episode = new Episode(result);
            for(int j = 0; j < seasons.size(); j++) {
                if(seasons.get(j).get_season_number() == episode.get_season_number()) {
                    season_index = j;
                    break;
                }
            }
            if(season_index == -1) {
                Season season = new Season(episode.get_season_number());
                seasons.add(season);
                season_index = seasons.size() - 1;
            }
            seasons.get(season_index).add_episode(episode);
        }
        result.close();
        db.close();

        Collections.sort(seasons);
        if(seasons.size() > 0 && seasons.get(0).get_season_number() == 0) {
            Season season = seasons.get(0);
            seasons.remove(0);
            seasons.add(season);
        }
        return seasons;
    }

    public Series get_series(int series_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Series series = null;

        Cursor result = db.query(SERIES_TABLE_NAME, null, "seriesId = ?", new String[] {"" + series_id}, null, null, null, null);
        result.moveToFirst();
        if(result.getCount() > 0) {
            series = new Series(result);
        }
        result.close();
        db.close();
        return series;
    }

    public ArrayList<Episode> get_episodes(int series_id, int season_num) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Episode> episodes = new ArrayList<>();

        Cursor result = db.query(EPISODE_TABLE_NAME, null, "seriesId = ? AND season = ?", new String[] {"" + series_id, "" + season_num}, null, null, "episode", null);
        for(int i = 0; i < result.getCount(); i++) {
            result.moveToPosition(i);
            Episode ep = new Episode(result);
            episodes.add(ep);
        }
        result.close();
        db.close();

        return episodes;
    }

    public void mark_all_as_watched(int series_id, boolean watched) {
        mark_all_as_watched(series_id, -1, watched);
    }

    public void mark_all_as_watched(int series_id, int season_num, boolean watched) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues content = new ContentValues();
        content.put("watched", watched ? 1 : 0);

        if(season_num == -1) {
            db.update(EPISODE_TABLE_NAME, content, "seriesId = ? ", new String[] {"" + series_id});
        } else {
            db.update(EPISODE_TABLE_NAME, content, "seriesId = ? AND season = ?", new String[] {"" + series_id, "" + season_num});
        }
        db.close();
    }

    public void delete_series(int series_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(EPISODE_TABLE_NAME, "seriesId = ?", new String[] {"" + series_id});
        db.delete(SERIES_TABLE_NAME, "seriesId = ?", new String[] {"" + series_id});
        db.close();
    }
}
