package com.kido.ucmaindemo;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.kido.ucmaindemo.adapter.TagFragmentAdapter;
import com.kido.ucmaindemo.behavior.UcNewsHeaderPagerBehavior;
import com.kido.ucmaindemo.widget.CustomViewPager;
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
    private View titlebarFLayout;
    private CustomViewPager mViewPager;
    private TabLayout mTabLayout;

    private List<NewsTagFragment> mFragments;
    private UcNewsHeaderPagerBehavior mPagerBehavior;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        initTitleLayout();
        initTabsPager();
        initRefreshLayout();
        initBehavior();
    }

    private void bindViews() {

        mRefreshLayout = (KSwipeRefreshLayout) findViewById(R.id.root_refresh_layout);
        titlebarFLayout = findViewById(R.id.titlebar_fLayout);
        mViewPager = (CustomViewPager) findViewById(R.id.news_viewPager);
        mTabLayout = (TabLayout) findViewById(R.id.news_tabLayout);

    }

    private void initTitleLayout() {
        titlebarFLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragments.get(mViewPager.getCurrentItem()).getRecyclerView().smoothScrollToPosition(0);
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
                public void onTerminal() {
                    if (mPagerBehavior != null && mPagerBehavior.isClosed()) {
                        mPagerBehavior.openPager();
                    }
                }
            });
            mFragments.add(fragment);
        }

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mViewPager.setAdapter(new TagFragmentAdapter(getSupportFragmentManager(), mFragments));
        mViewPager.setPagingEnabled(false);

        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
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
        mRefreshLayout.setOnRefreshListener(new KSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.setRefreshing(false);
                    }
                }, 1500);
            }

            @Override
            public void onTerminal() {
                if (mPagerBehavior != null && !mPagerBehavior.isClosed()) {
                    mPagerBehavior.closePager();
                }
            }
        });
    }

    private void initBehavior() {
        mPagerBehavior = (UcNewsHeaderPagerBehavior) ((CoordinatorLayout.LayoutParams) findViewById(R.id.news_header_fLayout).getLayoutParams()).getBehavior();
        if (mPagerBehavior != null) {
            mPagerBehavior.setPagerStateListener(new UcNewsHeaderPagerBehavior.OnPagerStateListener() {

                @Override
                public void onPagerClosed() {
                    mViewPager.setPagingEnabled(true);
                    mFragments.get(0).setRefreshEnable(true);
                    mRefreshLayout.setEnabled(false);
                }

                @Override
                public void onPagerOpened() {
                    mViewPager.setCurrentItem(0, false);
                    mViewPager.setPagingEnabled(false);
                    mFragments.get(0).scrollToTop();
                    mFragments.get(0).setRefreshEnable(false);
                    mRefreshLayout.setEnabled(true);

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (mPagerBehavior != null && mPagerBehavior.isClosed()) {
            mPagerBehavior.openPager();
        } else {
            super.onBackPressed();
        }
    }
}
