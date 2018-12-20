package com.turing.turingsdksample.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.turing.authority.util.LogUtil;
import com.turing.turingsdksample.R;
import com.turing.turingsdksample.bean.ContentBean;
import com.turing.turingsdksample.constants.EmotionConstants;
import com.turing.turingsdksample.constants.FunctionConstants;

import java.util.ArrayList;

/**
 * @author：licheng@uzoo.com
 */

public class ContentAdapter extends BaseAdapter {
    private static final String TAG = ContentAdapter.class.getSimpleName();
    private ArrayList<ContentBean> lists;
    private Context context;

    public ContentAdapter(Context context, ArrayList<ContentBean> lists) {
        this.context = context;
        this.lists = lists;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_listview_content, null);
            holder = new ViewHolder();
            holder.tv_category = (TextView) convertView.findViewById(R.id.tv_category);
            holder.tv_value = (TextView) convertView.findViewById(R.id.tv_value);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == 0) {
            holder.tv_category.setText(R.string.content_text);
            holder.tv_value.setText(lists.get(position).getText());
        } else if (position == 1) {
            holder.tv_category.setText(R.string.content_emotion);
            try {
                int code = lists.get(position).getEmotion();
                String strEmotion = EmotionConstants.emotionMap.get(code);
                holder.tv_value.setText(strEmotion);
            } catch (Exception e) {
                holder.tv_value.setText("");
            }

        } else if (position == 2) {
            holder.tv_category.setText(R.string.content_code);
            try {
                int code = lists.get(position).getCode();
                LogUtil.d(TAG, "exception before");
                if (FunctionConstants.codeMap != null && FunctionConstants.codeMap.size() > 0) {
                    String strFunction = FunctionConstants.codeMap.get(code);
                    LogUtil.d(TAG, "exception after" + strFunction);
                    if ( holder.tv_value != null) {
                        holder.tv_value.setText(strFunction);
                    }
                }

            } catch (Exception e) {
                holder.tv_value.setText("");
                e.printStackTrace();
            }
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView tv_category, tv_value;
    }
}
