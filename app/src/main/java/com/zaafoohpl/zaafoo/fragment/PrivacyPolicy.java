package com.zaafoohpl.zaafoo.fragment;


import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zaafoohpl.zaafoo.myapplication.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * A simple {@link Fragment} subclass.
 */
public class PrivacyPolicy extends Fragment {

    TextView policy;

    public PrivacyPolicy() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_privacy_policy, container, false);
        Typeface face= Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Medium.ttf");
        policy=(TextView)v.findViewById(R.id.textView37);
        policy.setTypeface(face);
        getActivity().setTitle("Privacy Policy");
        AssetManager manager=getActivity().getAssets();
        try {
            String str = "";
            StringBuffer buffer = new StringBuffer();
            InputStream is=manager.open("policy.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            if (is != null) {
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
            }
            policy.setText(buffer.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return v;
    }

}
