package com.example.naddi.wifip2p2tesi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Myadapter extends ArrayAdapter<String> {


    public Myadapter(@NonNull Context context, @NonNull String[] objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String c = getItem(position);

        if(c.length() > 3 && c.substring(0,3).equals("me:")){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.mymex, null);
            TextView titolo = (TextView)convertView.findViewById(R.id.message_body);
            titolo.setText(c);

        }else {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.hismex, null);
            TextView titolo = (TextView)convertView.findViewById(R.id.message_body);
            titolo.setText(c);
        }
        return convertView;
    }
}
