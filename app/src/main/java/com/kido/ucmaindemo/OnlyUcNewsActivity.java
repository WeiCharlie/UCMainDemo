package com.kido.ucmaindemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.kido.ucmaindemo.widget.main.UcNewsContentPager;
import com.kido.ucmaindemo.widget.main.UcNewsTabLayout;
import com.kido.ucmaindemo.widget.main.UcNewsTitleLayout;

import java.util.List;

/**
 * @author Kido
 */

public class OnlyUcNewsActivity extends AppCompatActivity {

    private UcNewsTitleLayout mTitleLayout;
    private UcNewsTabLayout mTabLayout;
    private UcNewsContentPager mContentPager;

    private List<NewsTagFragment> mFragments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onlyucnews);
//        bindViews();
//        initTitleAndHeader();
//        initTabsAndPager();
    }
//
//    private void bindViews() {
//
//        mTitleLayout = (UcNewsTitleLayout) findViewById(R.id.titlebar_layout);
//        mContentPager = (UcNewsContentPager) findViewById(R.id.news_viewPager);
//        mTabLayout = (UcNewsTabLayout) findViewById(R.id.news_tabLayout);
//
//    }
//
//    private void initTitleAndHeader() {
//        mTitleLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mFragments.get(mContentPager.getCurrentItem()).getRecyclerView().smoothScrollToPosition(0);
//            }
//        });
//    }
//
//
//    private void initTabsAndPager() {
//        String[] newsTabTitles = getResources().getStringArray(R.array.news_tab_titles);
//        mFragments = new ArrayList<>(newsTabTitles.length);
//        for (String title : newsTabTitles) {
//            mTabLayout.addTab(mTabLayout.newTab().setText(title));
//            NewsTagFragment fragment = NewsTagFragment.newInstance(title);
//            fragment.addOnRefreshListener(new KSwipeRefreshLayout.OnRefreshListener() {
//                @Override
//                public void onRefresh() {
//                    // do nothing, because something has been done inside the fragment.
//                }
//
//                @Override
//                public void onTerminal() { // open header to go back home
//                }
//            });
//            mFragments.add(fragment);
//        }
//
//        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
//        mContentPager.setupTabLayout(mTabLayout);
//        mContentPager.setAdapter(new TagFragmentAdapter(getSupportFragmentManager(), mFragments));
//
//    }
}

