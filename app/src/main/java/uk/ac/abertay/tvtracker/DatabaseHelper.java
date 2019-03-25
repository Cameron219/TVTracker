package uk.ac.abertay.tvtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONObject;

import java.util.ArrayList;

import uk.ac.abertay.tvtracker.TheTVDB.TVDB_API;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tv";

    private static final String SERIES_TABLE_NAME = "series";
    private static final String[] SERIES_COLUMN_NAMES = {"seriesId", "name", "poster", "status", "network", "firstAired", "overview", "lastUpdated", "rating", "imdbId", "siteRating", "slug"};
    private static final String[] SERIES_COLUMN_TYPES = {"INTEGER", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "INTEGER", "REAL", "TEXT"};

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

    public void add_series(int series_id) {

    }

    public void insert_series(JSONObject series) {

    }
}
