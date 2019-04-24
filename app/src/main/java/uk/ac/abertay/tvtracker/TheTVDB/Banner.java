package uk.ac.abertay.tvtracker.TheTVDB;

import android.graphics.Bitmap;

/**
 * Banner class
 * Holds the banner image within a bitmap and the ID of the show it corresponds to.
 */
public class Banner {
    private Bitmap banner;
    private int id;

    /**
     * Default Constructor
     * Sets properties to default values
     */
    public Banner() {
        this.banner = null;
        this.id = -1;
    }

    /**
     * Set the banner image bitmap
     * @param banner Banner image
     */
    public void set_banner(Bitmap banner) {
        this.banner = banner;
    }

    /**
     * Get the banner image
     * @return Banner image or null, if there isn't one.
     */
    public Bitmap get_banner() {
        return this.banner;
    }

    /**
     * Set the series ID
     * @param id ID of the series
     */
    public void set_id(int id) {
        this.id = id;
    }

    /**
     * Get the ID of the series.
     * @return Series ID
     */
    public int get_id() {
        return this.id;
    }
}
