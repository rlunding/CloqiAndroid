package com.cloqi.gui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloqi.R;

import java.util.ArrayList;

/**
 *
 * Created by Lunding on 18/04/15.
 */
public class NavDrawerListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;

    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems) {
        this.context = context;
        this.navDrawerItems = navDrawerItems;
    }

    @Override
    public int getCount(){
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position){
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.drawer_list_item, null);
        }
        //Initialize components
        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        TextView txtCount = (TextView) convertView.findViewById(R.id.counter);

        //Get item and fill in values to the components
        NavDrawerItem item = navDrawerItems.get(position);
        imgIcon.setImageResource(item.getIcon());
        txtTitle.setText(item.getTitle());
        if(item.isCounterVisible()){
            txtCount.setText(item.getCount());
        } else {
            txtCount.setVisibility(View.GONE);
        }
        return convertView;
    }
}
