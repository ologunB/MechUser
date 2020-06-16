package com.mechanics.mechapp.customer;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class CarouselAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> frag = new ArrayList();
    private final List<String> title = new ArrayList();

    CarouselAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return frag.get(position);
    }

    @Override
    public int getCount() {
        return frag.size();
    }

    void addFrag(Fragment f, String s) {
        frag.add(f);
        title.add(s);
    }

    @Override
    public CharSequence getPageTitle(int position) {
                return title.get(position);
    }

}