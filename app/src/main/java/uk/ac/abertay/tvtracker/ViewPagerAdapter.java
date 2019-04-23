package uk.ac.abertay.tvtracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class ViewPagerAdapter extends FragmentPagerAdapter {
    private final Series series;

    public ViewPagerAdapter(FragmentManager fm, Series series) {
        super(fm);
        this.series = series;
    }

    @Override
    public Fragment getItem(int i) {
        if(i == 0) {
            SeriesInfoFragment sif = new SeriesInfoFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", i);
            bundle.putInt("series_id", series.get_id());
            bundle.putString("name", series.get_name());
            bundle.putString("poster", series.get_poster_path());
            bundle.putString("status", series.get_status());
            bundle.putString("network", series.get_network());
            bundle.putString("first_aired", series.get_first_aired());
            bundle.putString("content_rating", series.get_content_rating());
            bundle.putDouble("site_rating", series.get_site_rating());
            bundle.putString("overview", series.get_overview());


            sif.setArguments(bundle);
            return sif;
        } else {
            SeriesSeasonFragment ssf = new SeriesSeasonFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("series_id", series.get_id());
            bundle.putString("series_name", series.get_name());
            ssf.setArguments(bundle);
            return ssf;
        }

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
