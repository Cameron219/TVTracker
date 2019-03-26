package uk.ac.abertay.tvtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import uk.ac.abertay.tvtracker.TheTVDB.TVDB_API;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tv";

    private static final String SERIES_TABLE_NAME = "series";
    private static final String[] SERIES_COLUMN_NAMES = {"seriesId", "name", "poster", "status", "network", "firstAired", "overview", "lastUpdated", "rating", "imdbId", "siteRating", "slug"};
    private static final String[] SERIES_COLUMN_TYPES = {"INTEGER", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "INTEGER", "TEXT", "INTEGER", "REAL", "TEXT"};

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
            ContentValues row = new ContentValues();
            row.put("seriesId", series.getInt("id"));
            row.put("name", series.getString("seriesName"));
            row.put("poster", series.getString("slug") + ".png");
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
