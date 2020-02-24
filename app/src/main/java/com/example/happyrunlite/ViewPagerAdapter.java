package com.example.happyrunlite;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private int pageCount;

    public ViewPagerAdapter(androidx.fragment.app.FragmentManager mgr, int pageCount) {
        super(mgr, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.pageCount = pageCount;

    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new RunningFragment();
            case 1:
                return new RecordFragment();
            case 2:
                return new SettingFragment();
            default:
                return null;
        }
    }
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return pageCount;
    }
}