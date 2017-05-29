package com.kido.ucmaindemo;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.kido.ucmaindemo.adapter.TagFragmentAdapter;
import com.kido.ucmaindemo.widget.main.UcNewsContentPager;
import com.kido.ucmaindemo.widget.main.UcNewsBarLayout;
import com.kido.ucmaindemo.widget.main.UcNewsTabLayout;
import com.kido.ucmaindemo.widget.main.UcNewsTitleLayout;
import com.kido.ucmaindemo.widget.refresh.KSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 仿UC首页
 *
 * @author Kido
 */

public class MainActivity extends AppCompatActivity {

    private KSwipeRefreshLayout mRefreshLayout;
    private UcNewsTitleLayout mTitleLayout;
    private UcNewsBarLayout mBarLayout;
    private UcNewsTabLayout mTabLayout;
    private UcNewsContentPager mContentPager;

    private List<NewsTagFragment> mFragments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        initTitleAndHeader();
        initTabsPager();
        initRefreshLayout();
    }

    private void bindViews() {

        mRefreshLayout = (KSwipeRefreshLayout) findViewById(R.id.root_refresh_layout);
        mTitleLayout = (UcNewsTitleLayout) findViewById(R.id.titlebar_layout);
        mBarLayout = (UcNewsBarLayout) findViewById(R.id.news_header_layout);
        mContentPager = (UcNewsContentPager) findViewById(R.id.news_viewPager);
        mTabLayout = (UcNewsTabLayout) findViewById(R.id.news_tabLayout);

    }

    private void initTitleAndHeader() {
        mTitleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragments.get(mContentPager.getCurrentItem()).getRecyclerView().smoothScrollToPosition(0);
            }
        });
        mBarLayout.setBarStateListener(new UcNewsBarLayout.OnBarStateListener() {
            @Override
            public void onBarStartClosing() {
                mContentPager.setPagingEnabled(true);
                mFragments.get(0).setRefreshEnable(true);
                mRefreshLayout.setEnabled(false);
            }

            @Override
            public void onBarStartOpening() {
                mContentPager.setCurrentItem(0, false);
                mContentPager.setPagingEnabled(false);
                mFragments.get(0).scrollToTop();
                mFragments.get(0).setRefreshEnable(false);
                mRefreshLayout.setEnabled(true);
            }

            @Override
            public void onBarClosed() {

            }

            @Override
            public void onBarOpened() {

            }
        });
    }


    private void initTabsPager() {
        String[] newsTabTitles = getResources().getStringArray(R.array.news_tab_titles);
        mFragments = new ArrayList<>(newsTabTitles.length);
        for (String title : newsTabTitles) {
            mTabLayout.addTab(mTabLayout.newTab().setText(title));
            NewsTagFragment fragment = NewsTagFragment.newInstance(title);
            fragment.addOnRefreshListener(new KSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // do nothing, because something has been done inside the fragment.
                }

                @Override
                public void onTerminal() { // open header to go back home
                    mBarLayout.openBar();
                }
            });
            mFragments.add(fragment);
        }

        mContentPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mContentPager.setAdapter(new TagFragmentAdapter(getSupportFragmentManager(), mFragments));
        mContentPager.setPagingEnabled(false);

        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mContentPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initRefreshLayout() {
        mRefreshLayout.setTerminalRate(1.5f);
        mRefreshLayout.setOnRefreshListener(new KSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onTerminal() { // close header to go to news list
                mBarLayout.closeBar();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mBarLayout.isClosed()) {
            mBarLayout.openBar();
        } else {
            super.onBackPressed();
        }
    }
}
