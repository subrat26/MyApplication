package com.zaafoohpl.zaafoo.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.zaafoohpl.zaafoo.myapplication.R;

import java.util.ArrayList;

/**
 * Created by SUB on 1/27/2017.
 */

public class MenuAdapter extends ArrayAdapter<Menu> {


    ArrayList<Menu> menuObjects;
    Context context;

    public MenuAdapter(Context context, int resource, ArrayList<Menu> menuObjects) {
        super(context, resource, menuObjects);
        this.menuObjects=menuObjects;
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Menu m=getItem(position);

        if(convertView==null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.menu_row_view, parent, false);


        TextView menu_name=(TextView)convertView.findViewById(R.id.textView);
        TextView price=(TextView)convertView.findViewById(R.id.textView8);
       /* TextView cuisine=(TextView)convertView.findViewById(R.id.textView17);
        TextView time=(TextView)convertView.findViewById(R.id.textView18);
        TextView description=(TextView)convertView.findViewById(R.id.textView19);*/

        Typeface face= Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");
        menu_name.setTypeface(face);
        menu_name.setText(m.getName());
        price.setTypeface(face);
        price.setText("Rs."+m.getPrice());
/*
        cuisine.setText(m.getCuisine());
        time.setText(m.getFood_timing());
        description.setText(m.getDesc());
*/


        return convertView;
    }

    @Nullable
    @Override
    public Menu getItem(int position) {
        return menuObjects.get(position);
    }
}
