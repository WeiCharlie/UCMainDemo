package com.kido.ucmaindemo.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.kido.ucmaindemo.NewsTagFragment;

import java.util.List;

/**
 * 新闻标签fragment适配器
 *
 * @author Kido
 */

public class TagFragmentAdapter extends FragmentStatePagerAdapter {

    private List<NewsTagFragment> mFragments;

    public TagFragmentAdapter(FragmentManager fm, List<NewsTagFragment> fragments) {
        super(fm);
        this.mFragments = fragments;
    }


    @Override
    public NewsTagFragment getItem(int position) {
        return mFragments == null ? null : mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments == null ? 0 : mFragments.size();
    }
}
