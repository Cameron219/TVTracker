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

/**
 * A helper class to manage all SQLite Database operations.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // Version of the database
    private static final int DATABASE_VERSION = 1;
    // Database name
    private static final String DATABASE_NAME = "tv";

    /**
     * Database Structure for the table named series
     * ---------------------
     * seriesId:    Integer (Primary key)
     * name:        Text
     * poster:      Text
     * banner:      Text
     * status:      Text
     * network:     Text
     * firstAired:  Text
     * overview:    Text
     * lastUpdated: Integer
     * rating:      Text
     * imdbId:      Text
     * siteRating:  Real (Double)
     * slug:        Text
     * airsTime:    Text
     * runtime:     Text
     */
    private static final String SERIES_TABLE_NAME = "series";
    private static final String[] SERIES_COLUMN_NAMES = {"seriesId", "name", "poster", "banner", "status", "network", "firstAired", "overview", "lastUpdated", "rating", "imdbId", "siteRating", "slug", "airsTime", "runtime"};
    private static final String[] SERIES_COLUMN_TYPES = {"INTEGER", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "INTEGER", "TEXT", "TEXT", "REAL", "TEXT", "TEXT", "TEXT"};

    /**
     * Database structure for the table named episode
     * -------------------
     * episodeId:       Integer (Primary Key)
     * seriesId:        Integer (Foreign Key)
     * season:          Integer
     * episode:         Integer
     * name:            Text
     * aired:           Text
     * overview:        Text
     * lastUpdated:     Integer
     * fileName:        Text
     * imdbId:          Text
     * siteRating:      Real (Double)
     * watched:         Integer (Boolean 0/1)
     */
    private static final String EPISODE_TABLE_NAME = "episode";
    private static final String[] EPISODE_COLUMN_NAMES = {"episodeId", "seriesId", "season", "episode", "name", "aired", "overview", "lastUpdated", "fileName", "imdbId", "siteRating", "watched"};
    private static final String[] EPISODE_COLUMN_TYPES = {"INTEGER", "INTEGER", "INTEGER", "INTEGER", "TEXT", "TEXT", "TEXT", "INTEGER", "TEXT", "TEXT", "REAL", "INTEGER"};

    /**
     * Constructor method. Requires the context (current state of the application)
     * @param context Current context
     */
    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created.
     * This is NOT called on initializing of the DatabaseHelper object.
     * It is called the first time a database operation is attempted to be performed.
     * In this case, when either getReadableDatabase, or getWritableDatabase is called.
     * It creates the database by looping through each column and building the CREATE TABLE string, then executing it.
     * @param db The database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Loops through each column and builds the CREATE TABLE query for each table
        StringBuilder contacts_table_create = new StringBuilder("CREATE TABLE ").append(SERIES_TABLE_NAME).append(" (");
        for(int i = 0; i < SERIES_COLUMN_NAMES.length; i++) {
            contacts_table_create.append(SERIES_COLUMN_NAMES[i]).append(" ").append(SERIES_COLUMN_TYPES[i]).append(i < SERIES_COLUMN_NAMES.length - 1 ? "," : ");");
        }
        db.execSQL(contacts_table_create.toString());

        StringBuilder episodes_table_create = new StringBuilder("CREATE TABLE ").append(EPISODE_TABLE_NAME).append(" (");
        for(int i = 0; i < EPISODE_COLUMN_NAMES.length; i++ ) {
            episodes_table_create.append(EPISODE_COLUMN_NAMES[i]).append(" ").append(EPISODE_COLUMN_TYPES[i]).append(i < EPISODE_COLUMN_NAMES.length - 1 ? "," : ");");
        }
        db.execSQL(episodes_table_create.toString());
    }

    /**
     * Required but not used in the applications current state.
     * Would allow for updates to be made to the structure of the database
     * @param db The database
     * @param oldVersion Old version number
     * @param newVersion New version number
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    /**
     * Checks if the series exists within the database.
     * @param series_id ID of the series
     * @return True if the series exists, false otherwise.
     */
    public boolean series_exist(int series_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT seriesId, name FROM " + SERIES_TABLE_NAME + " WHERE seriesId = ?";
        Cursor cursor = db.rawQuery(query, new String[] {"" + series_id});
        if(cursor.getCount() > 0) {
            cursor.close(); // Making sure to close the cursor regardless of result
            return true;
        }
        cursor.close();
        return false;
    }

    /**
     * Insert a series into the database.
     * Designed to take the output of the ASyncTask SeriesTask, which queries the TVDB API.
     * @param series JSONObject containing the attributes of the series.
     * @return Returns true if the operation was successful, false otherwise.
     */
    public boolean insert_series(JSONObject series) {
        try {
            // Only add the series if it doesn't already exist in the database.
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
                row.put("airsTime", series.getString("airsTime"));
                row.put("runtime", series.getString("runtime"));

                SQLiteDatabase db = this.getWritableDatabase();
                db.insert(SERIES_TABLE_NAME, null, row);
                db.close();
            } else {
                Log.d("DB", "Didn't Add Duplicate: " + series.getString("seriesName"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Inserts episodes into the database.
     * Designed to take the output of the ASyncTask EpisodeTask, which queries the TVDB API for episodes.
     * Makes use of the transactions, which greatly reduces the time to add a large amount of recores to the table.
     * As does the use of SQLiteStatement, as opposed to the normal exec call.
     * @param episodes JSONArray containing all the episodes
     * @return True if successful, false otherwise
     */
    public boolean insert_episodes(JSONArray episodes) {
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
        } catch(Exception e) {
            e.printStackTrace();
            db.endTransaction();
            return false;
        } finally {
            db.endTransaction();
        }

        return true;
    }

    /**
     * Fetches the name, seriesId and poster path for each series in the database.
     * Also fetches the next episode to watch of each series found.
     * @return An ArrayList of Series, that contains all series in the DB.
     */
    public ArrayList<Series> get_series_names() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor result = db.query(SERIES_TABLE_NAME, new String[] {"seriesId", "name", "poster"}, null, null, null, null, "name", null);

        ArrayList<Series> series_list = new ArrayList<>();

        for(int i = 0; i < result.getCount(); i++ ) {
            result.moveToPosition(i);
            Series series = new Series(result.getInt(0), result.getString(1), result.getString(2));
            Episode next = get_next_episode(result.getInt(0));
            series.set_next_episode(next);
            series_list.add(series);
        }

        result.close();
        db.close();

        return series_list;
    }

    /**
     * Get all Seasons of a specified TV show, ordered by season in Ascending order (With the exception of season 0, which is the last item if present)
     * @param series_id ID of the series.
     * @return An ArrayList of all Seasons of a given series. An empty ArrayList will be returned if there are no seasons.
     */
    public ArrayList<Season> get_seasons(int series_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Season> seasons = new ArrayList<>();

        Cursor result = db.query(EPISODE_TABLE_NAME, null, "seriesId = ?", new String[] {"" + series_id}, null, null, null);

        // This is rather hacky, but is required to make sure that the application supports series that have missing season gaps (due to incomplete data returned from the API)
        // It creates an arraylist of seasons and adds each episode found to the appropriate season in the ArrayList.
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

        // Sort the seasons into order, and move season 0 to the end if present
        Collections.sort(seasons);
        if(seasons.size() > 0 && seasons.get(0).get_season_number() == 0) {
            Season season = seasons.get(0);
            seasons.remove(0);
            seasons.add(season);
        }

        return seasons;
    }

    /**
     * Get all information about a Series
     * @param series_id ID of the series
     * @return Series object if found, null otherwise
     */
    public Series get_series(int series_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Series series = null;

        Cursor result = db.query(SERIES_TABLE_NAME, null, "seriesId = ?", new String[] {"" + series_id}, null, null, null, null);
        if(result.getCount() > 0) {
            result.moveToFirst();
            series = new Series(result);
        }
        result.close();
        db.close();

        return series;
    }

    /**
     * Get all episodes of a specific season
     * @param series_id ID of the series
     * @param season_num Number of the season
     * @return ArrayList of Episodes if found, empty arraylist otherwise.
     */
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

    /**
     * Returns the next episode of a series to be watched.
     * Filters special episodes out (Season 0).
     * Also filters out watched episodes
     * @param series_id ID of the series
     * @return The next episode that the user hasn't watched. If no next episode exists, returns null
     */
    private Episode get_next_episode(int series_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Episode episode = null;

        // Selecting where aired is not null or blank, as sometimes the API can return blank dates and the date() method will cause them to be first
        Cursor result = db.query(EPISODE_TABLE_NAME, null, "seriesId = ? AND watched = 0 AND season > 0", new String[] {"" + series_id}, null, null, "season, episode", null);
        if(result.getCount() > 0) {
            result.moveToFirst();
            episode = new Episode(result);
        }
        result.close();
        return episode;
    }

    /**
     * Get information about a specific episode
     * @param episode_id ID of the episode
     * @return Episode object if found, null otherwise
     */
    public Episode get_episode(int episode_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Episode episode = null;

        Cursor result = db.query(EPISODE_TABLE_NAME, null, "episodeId = ?", new String[] {"" + episode_id}, null, null, null, null);
        if(result.getCount() > 0) {
            result.moveToFirst();
            episode = new Episode(result);
        }
        result.close();
        db.close();
        return episode;

    }

    /**
     * Mark all episodes of a specific series as watched or unwatched
     * @param series_id ID of the series
     * @param watched State you wish the watched field to be in, true for watched, false for unwatched
     */
    public void mark_all_as_watched(int series_id, boolean watched) {
        mark_all_as_watched(series_id, -1, watched);
    }

    /**
     * Marks all episodes of a specifis series and season number as watched or unwatched
     * @param series_id ID of the series
     * @param season_num Season number, -1 for all seasons
     * @param watched State you wish the watched field to be in, true for watched, false for unwatched
     */
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

    /**
     * Mark a single episode as watched or unwatched
     * @param episode_id ID of the episode
     * @param watched State you wish the watched field to be in, true for watched, false for unwatched
     */
    public void mark_episode_as_watched(int episode_id, boolean watched) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues content = new ContentValues();
        content.put("watched", watched ? 1 : 0);

        db.update(EPISODE_TABLE_NAME, content, "episodeId = ?", new String[] {"" + episode_id});
        db.close();
    }

    /**
     * Delete the series and all accompanying episodes from the database.
     * @param series_id ID of the series
     */
    public void delete_series(int series_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(EPISODE_TABLE_NAME, "seriesId = ?", new String[] {"" + series_id});
        db.delete(SERIES_TABLE_NAME, "seriesId = ?", new String[] {"" + series_id});
        db.close();
    }

    /**
     * Returns episodes that air today or later
     * @param limit Limit number of episodes to return
     * @return ArrayList of Episodes
     */
    public ArrayList<Episode> get_upcoming_episodes(int limit) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Episode> episodes = new ArrayList<>();

        Cursor result = db.query(EPISODE_TABLE_NAME, null, "season > 0 AND date(aired) >= date('now')", null, null, null, "date(aired)", "" + limit);
        for(int i = 0; i < result.getCount(); i++) {
            result.moveToPosition(i);
            Episode ep = new Episode(result);
            episodes.add(ep);
        }
        result.close();
        db.close();

        return episodes;
    }
}
