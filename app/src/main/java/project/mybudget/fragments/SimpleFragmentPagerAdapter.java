package project.mybudget.fragments;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import project.mybudget.ReportsActivity;

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    Context context;

    public SimpleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new ReportsActivity();
        } else if (position == 1) {
            return new ReportsActivity();
        } else if (position == 2) {
            return new ReportsActivity();
        } else {
            return new ReportsActivity();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "test1";
        } else if (position == 1) {
            return "test2";
        } else if (position == 2) {
            return "test3";
        } else {
            return "test4";
        }
    }
}

