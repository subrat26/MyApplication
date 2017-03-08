package com.zaafoohpl.zaafoo.myapplication;


import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.zaafoohpl.zaafoo.fragment.BookTable;

import java.util.ArrayList;

import io.paperdb.Paper;


/**
 * A simple {@link Fragment} subclass.
 */
public class NoOfPersons extends Fragment {


    public NoOfPersons() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_no_of_persons, container, false);

        Typeface face= Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Medium.ttf");
        TextView tv=(TextView)rootView.findViewById(R.id.textView22);
        tv.setTypeface(face);

        Spinner spinner=(Spinner)rootView.findViewById(R.id.spinner2);
        Paper.init(getActivity());
        ArrayList<String> noOfPersons=new ArrayList<>();

        for(int i=1;i<=20;i++){
            noOfPersons.add(String.valueOf(i));
        }


        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,noOfPersons);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    Paper.book().write("persons", parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return rootView;
    }

}
