package com.example.newsgateway;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ColorArrayAdapter extends ArrayAdapter {

    private MainActivity mainActivity;
    private ArrayList<DrawerObject> drawer_list;
    private int res;


    public ColorArrayAdapter(@NonNull MainActivity mainA, int resource, @NonNull ArrayList<DrawerObject> drawer_l) {
        super(mainA, resource, drawer_l);
        mainActivity = mainA;
        res = resource;
        drawer_list = drawer_l;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mainActivity).inflate(R.layout.drawer_list_items, parent,  false);
        DrawerObject currentDrawer = drawer_list.get(position);

        //set colors and shit
        TextView text = listItem.findViewById(R.id.drawer_text);
        text.setText(currentDrawer.getText());
        text.setTextColor(currentDrawer.getColor());
        return listItem;
    }
}

