package uk.ac.abertay.tvtracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    int series_id;

    public ViewPagerAdapter(FragmentManager fm, int series_id) {
        super(fm);
        this.series_id = series_id;
    }

    @Override
    public Fragment getItem(int i) {
        if(i == 0) {
            SeriesInfoFragment sif = new SeriesInfoFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", i);
            bundle.putInt("series_id", series_id);
            sif.setArguments(bundle);
            return sif;
        } else {
            SeriesSeasonFragment ssf = new SeriesSeasonFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("series_id", series_id);
            ssf.setArguments(bundle);
            return ssf;
        }

    }

    @Override
    public int getCount() {
        return 2;
    }
}
