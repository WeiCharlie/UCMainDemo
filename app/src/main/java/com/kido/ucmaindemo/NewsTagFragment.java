package com.kido.ucmaindemo;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kido.ucmaindemo.adapter.ListViewAdapter;
import com.kido.ucmaindemo.widget.refresh.KSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 新闻标签对应的fragment
 *
 * @author Kido
 */
public class NewsTagFragment extends Fragment {
    private static final String KEY_TITLE = "title";

    private NestedScrollView mNestedScrollView;
    private ListView mListView;
    private KSwipeRefreshLayout mRefreshLayout;

    private String mTitle = "";
    private List<KSwipeRefreshLayout.OnRefreshListener> mOnRefreshListeners = new ArrayList<>();

    final ArrayList<String> dataList = new ArrayList<>();
    ListViewAdapter adapter;

    public static NewsTagFragment newInstance() {
        return newInstance("");
    }

    public static NewsTagFragment newInstance(String title) {
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        NewsTagFragment fragment = new NewsTagFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_news_tag, container, false);
        initView(rootView);
        initData();
        return rootView;
    }

    private void initView(View rootView) {
        mTitle = getArguments().getString(KEY_TITLE);
        mNestedScrollView = (NestedScrollView) rootView.findViewById(R.id.nested_scrollView);
        mListView = (ListView) rootView.findViewById(R.id.recyclerView);
        mRefreshLayout = (KSwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
//        mListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRefreshLayout.setOnRefreshListener(new KSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                triggerOnRefresh();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.setRefreshing(false);
                    }
                }, 1500); //模拟下拉耗时
            }

            @Override
            public void onTerminal() {
                triggerOnTerminal();
            }
        });
    }

    public KSwipeRefreshLayout getRereshLayout() {
        return mRefreshLayout;
    }

    public ListView getRecyclerView() {
        return mListView;
    }

    public void setRefreshEnable(boolean refreshEnable) {
        if (mRefreshLayout != null) {
            mRefreshLayout.setEnabled(refreshEnable);
        }
    }

    public void scrollToTop() {
        if (mNestedScrollView != null) {
            mNestedScrollView.scrollTo(0, 0);
            mListView.setSelection(0);
        }
    }

    public void addOnRefreshListener(KSwipeRefreshLayout.OnRefreshListener listener) {
        mOnRefreshListeners.add(listener);
    }

    private void triggerOnRefresh() {
        for (KSwipeRefreshLayout.OnRefreshListener listener : mOnRefreshListeners) {
            if (listener != null) {
                listener.onRefresh();
            }
        }
    }

    private void triggerOnTerminal() {
        for (KSwipeRefreshLayout.OnRefreshListener listener : mOnRefreshListeners) {
            if (listener != null) {
                listener.onTerminal();
            }
        }
    }

    private void initData() {

        for (int i = 0; i < 40; i++) {
            dataList.add("This is the title. (" + mTitle + i + ")");
        }
//        RecyclerViewAdapter adapter = new RecyclerViewAdapter(dataList);
//        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                Toast.makeText(getContext(), dataList.get(position), Toast.LENGTH_LONG).show();
//            }
//        });
        adapter = new ListViewAdapter(getContext(), dataList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addData();
            }
        });

    }

    public void addData() {

        for (int i = 0; i < 5; i++) {
            dataList.add("This is the title. (" + mTitle + i + ")");
        }

        adapter.notifyDataSetChanged();

    }

}
