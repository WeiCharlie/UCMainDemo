package com.kido.ucmaindemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kido.ucmaindemo.R;

import java.util.List;

/**
 * @author Kido
 */

public class ListViewAdapter extends BaseAdapter {

    private List<String> mItems;
    private Context mContext;
    private LayoutInflater mInflater;


    public ListViewAdapter(Context context, List<String> items) {
        this.mContext = context;
        this.mItems = items;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems == null ? null : mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_news, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(mItems.get(position));

        return convertView;
    }

    public class ViewHolder {
        public TextView title;

        public ViewHolder(View rootView) {
            title = (TextView) rootView.findViewById(R.id.title);
        }

    }
}
