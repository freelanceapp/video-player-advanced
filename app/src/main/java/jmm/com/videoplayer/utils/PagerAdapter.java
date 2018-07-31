package jmm.com.videoplayer.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import jmm.com.videoplayer.fragment.CloudFragment;
import jmm.com.videoplayer.fragment.DeviceFragment;
import jmm.com.videoplayer.fragment.FvrtFragment;

public class PagerAdapter  extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                DeviceFragment deviceFragment = new DeviceFragment();
                return deviceFragment;
            case 1:
                FvrtFragment fvrtFragment = new FvrtFragment();
                return fvrtFragment;
            case 2:
                CloudFragment cloudFragment = new CloudFragment();
                return cloudFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}