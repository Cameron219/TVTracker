package uk.ac.abertay.tvtracker.TheTVDB;

import android.graphics.Bitmap;

public class Banner {
    private Bitmap banner;
    private int id;

    public Banner() {
        this.banner = null;
        this.id = -1;
    }

    public void set_banner(Bitmap banner) {
        this.banner = banner;
    }

    public Bitmap get_banner() {
        return this.banner;
    }

    public void set_id(int id) {
        this.id = id;
    }

    public int get_id() {
        return this.id;
    }
}
