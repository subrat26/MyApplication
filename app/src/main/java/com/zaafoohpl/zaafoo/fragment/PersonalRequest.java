 package com.zaafoohpl.zaafoo.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.zaafoohpl.zaafoo.myapplication.FinalActivity;
import com.zaafoohpl.zaafoo.myapplication.R;

import io.paperdb.Paper;

 /**
 * A simple {@link Fragment} subclass.
 */
public class PersonalRequest extends Fragment {


    EditText request;
    public PersonalRequest() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_personal_request, container, false);
        getActivity().setTitle("Personal Request");
        Paper.init(getActivity());
        request=(EditText)v.findViewById(R.id.editText3);
        Button b=(Button)v.findViewById(R.id.button10);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String req=request.getText().toString();
                Paper.book().write("personalRequest",req);
                startActivity(new Intent(getActivity(), FinalActivity.class));
            }
        });
        return v;
    }

}
