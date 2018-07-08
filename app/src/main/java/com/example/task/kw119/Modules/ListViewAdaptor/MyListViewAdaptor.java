package com.example.task.kw119.Modules.ListViewAdaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.task.kw119.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MyListViewAdaptor extends BaseAdapter {
    Context context;
    ArrayList<ListView_item> itemArrayList; //아이템 리스트
    ViewHolder viewHolder;

    public MyListViewAdaptor(Context context, ArrayList<ListView_item> itemArrayList) {
        this.context = context;
        this.itemArrayList = itemArrayList;
    }

    @Override
    public int getCount() {
        return this.itemArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.itemArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.itemArrayList.get(position).getTopicId();
    }

    /**
     * using holder patten
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_view_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = (TextView)convertView.findViewById(R.id.tvItemTitle);
            viewHolder.tvAuthor = (TextView)convertView.findViewById(R.id.tvItemAuthor);
            viewHolder.tvKind = (TextView)convertView.findViewById(R.id.tvItemKind);
            viewHolder.tvLocation = (TextView)convertView.findViewById(R.id.tvItemLocation);
            viewHolder.tvDate = (TextView)convertView.findViewById(R.id.tvItemDate);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvTitle.setText(itemArrayList.get(position).getTitle());
        viewHolder.tvAuthor.setText(itemArrayList.get(position).getAuthor());
        viewHolder.tvKind.setText(itemArrayList.get(position).getKind());
        viewHolder.tvLocation.setText(itemArrayList.get(position).getLocation());
        viewHolder.tvDate.setText(itemArrayList.get(position).getDate());

        return convertView;
    }

    class ViewHolder{
        TextView tvTitle;
        TextView tvAuthor;
        TextView tvKind;
        TextView tvLocation;
        TextView tvDate;
    }
}
