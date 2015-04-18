package com.cloqi.gui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloqi.R;
import com.cloqi.app.SQLiteHandler;
import com.cloqi.youpayframework.YouPayEvent;

import java.util.ArrayList;

/**
 *
 * Created by Lunding on 18/04/15.
 */
public class EventAdapter extends BaseAdapter{

    //Constants
    private static final String TAG = EventAdapter.class.getSimpleName();

    //Fields
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<YouPayEvent> items = new ArrayList<>();

    public EventAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);
        items = new SQLiteHandler(context.getApplicationContext()).getEvents();
    }

    @Override
    public int getCount(){
        return items.size();
    }

    public Object getItem(int position) {
        return items.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ImageView picture;
        TextView name;

        if(v == null) {
            v = inflater.inflate(R.layout.imageview_squareimage, parent, false);
            v.setTag(R.id.picture, v.findViewById(R.id.picture));
            v.setTag(R.id.text, v.findViewById(R.id.text));
        }

        picture = (ImageView)v.getTag(R.id.picture);
        name = (TextView)v.getTag(R.id.text);

        YouPayEvent item = (YouPayEvent) getItem(position);
        Log.d(TAG, "Event to be displayed: " + item);
        int color;
        try {
            color = Color.parseColor(item.getColor());
        } catch (IllegalArgumentException e){
            Log.d(TAG, "Invalid color:" + item.getColor());
            color = Color.parseColor("#ffffff");
        }
        picture.setImageDrawable(new ColorDrawable(color));
        name.setText(item.getName());

        return v;
    }
}
