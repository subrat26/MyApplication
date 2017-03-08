package com.zaafoohpl.zaafoo.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zaafoohpl.zaafoo.myapplication.R;
import com.zaafoohpl.zaafoo.myapplication.SessionManagement;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

/**
 * Created by SUB on 1/30/2017.
 */

public class MenuDetailsFragment extends Fragment {

    Menu m;
    List<Menu> menus;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.menu_details_cart, container, false);
        TextView t1=(TextView)rootView.findViewById(R.id.textView11);
        TextView t2=(TextView)rootView.findViewById(R.id.textView20);
        TextView t3=(TextView)rootView.findViewById(R.id.textView21);
        Button b=(Button)rootView.findViewById(R.id.button7);
        Spinner spinner=(Spinner)rootView.findViewById(R.id.spinner3);
        m=new SessionManagement(getActivity()).getMenuObject();
        t1.setText(m.getName());
        t2.setText("Rs."+m.getPrice());
        t3.setText(m.getDesc());


        ArrayList<String> itemNo=new ArrayList<>();
        for(int i=1;i<=10;i++){
            itemNo.add(String.valueOf(i));
        }

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Added to cart",Toast.LENGTH_SHORT).show();
                new SessionManagement(getActivity()).addCartItems(m);

            }
        });

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,itemNo);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    Paper.book().write("no_of_items", parent.getItemAtPosition(position).toString());
                    m.setAmount(Paper.book().read("no_of_items").toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return rootView;
    }
}
