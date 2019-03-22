package uk.ac.abertay.tvtracker.TheTVDB;

import android.graphics.Bitmap;

public class Poster {
    private Bitmap bitmap;
    private int id;

    public Poster() {
        this.bitmap = null;
        this.id = -1;
    }

    public void set_bitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap get_bitmap() {
        return bitmap;
    }

    public void set_id(int id) {
        this.id = id;
    }

    public int get_id() {
        return id;
    }
}
