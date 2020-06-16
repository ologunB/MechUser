package com.mechanics.mechapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mechanics.mechapp.customer.Help_and_NotificationModel;

import java.util.ArrayList;

public class HelpFragmentAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<Help_and_NotificationModel> values;
    private final int image;

    public HelpFragmentAdapter(Context context, ArrayList<Help_and_NotificationModel> values, int image) {
        this.context = context;
        this.values = values;
        this.image = image;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.helpnoti_item, parent, false);
        TextView message = rowView.findViewById(R.id.eachMessage);
        TextView time = rowView.findViewById(R.id.eachTime);
        View divi = rowView.findViewById(R.id.divi1);
        ImageView imageIcon = rowView.findViewById(R.id.imageIcon);
        message.setText(values.get(position).getItem_message());
        time.setText(values.get(position).getItem_time_value());
        imageIcon.setImageResource(image);
        if(image == R.drawable.ic_sync_black_24dp){
            time.setVisibility(View.GONE);
            divi.setVisibility(View.GONE);
        }

        return rowView;
    }
}