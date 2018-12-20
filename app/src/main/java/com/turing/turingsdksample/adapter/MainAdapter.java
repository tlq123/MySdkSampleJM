package com.turing.turingsdksample.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.turing.turingsdksample.R;

import java.util.ArrayList;

/**
 * 作者：Administrator on 2017/1/19 0019 15:46
 * 邮箱：licheng@uzoo.com
 */

public class MainAdapter extends BaseAdapter {
    private ArrayList<MainBean> lists;
    private Context context;

    public MainAdapter(ArrayList<MainBean> lists, Context context) {
        this.lists = lists;
        this.context = context;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int i) {
        return lists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_adapter, null);
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.text.setText(lists.get(position).getName());
        return convertView;
    }

  private  static class ViewHolder {
        TextView text;
    }
}
