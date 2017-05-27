package com.kido.ucmaindemo;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.kido.ucmaindemo.adapter.TagFragmentAdapter;
import com.kido.ucmaindemo.widget.main.UcNewsContentPager;
import com.kido.ucmaindemo.widget.main.UcNewsHeaderLayout;
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
    private UcNewsTitleLayout titleLayout;
    private UcNewsHeaderLayout mHeaderLayout;
    private UcNewsContentPager mContentPager;
    private UcNewsTabLayout mTabLayout;

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
        titleLayout = (UcNewsTitleLayout) findViewById(R.id.titlebar_layout);
        mHeaderLayout = (UcNewsHeaderLayout) findViewById(R.id.news_header_layout);
        mContentPager = (UcNewsContentPager) findViewById(R.id.news_viewPager);
        mTabLayout = (UcNewsTabLayout) findViewById(R.id.news_tabLayout);

    }

    private void initTitleAndHeader() {
        titleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragments.get(mContentPager.getCurrentItem()).getRecyclerView().smoothScrollToPosition(0);
            }
        });
        mHeaderLayout.setHeaderStateListener(new UcNewsHeaderLayout.OnHeaderStateListener() {
            @Override
            public void onHeaderClosed() {
                mContentPager.setPagingEnabled(true);
                mFragments.get(0).setRefreshEnable(true);
                mRefreshLayout.setEnabled(false);
            }

            @Override
            public void onHeaderOpened() {
                mContentPager.setCurrentItem(0, false);
                mContentPager.setPagingEnabled(false);
                mFragments.get(0).scrollToTop();
                mFragments.get(0).setRefreshEnable(false);
                mRefreshLayout.setEnabled(true);
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
                    mHeaderLayout.openHeader();
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
            public void onTerminal() { // close header to go to news list
                mHeaderLayout.closeHeader();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mHeaderLayout.isClosed()) {
            mHeaderLayout.openHeader();
        } else {
            super.onBackPressed();
        }
    }
}
