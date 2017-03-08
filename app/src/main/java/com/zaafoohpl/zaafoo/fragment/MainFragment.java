package com.zaafoohpl.zaafoo.fragment;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.zaafoohpl.zaafoo.myapplication.DateChooser;
import com.zaafoohpl.zaafoo.myapplication.NoOfPersons;
import com.zaafoohpl.zaafoo.myapplication.R;
import com.zaafoohpl.zaafoo.myapplication.ZaafooUser;

import java.util.Calendar;

import io.paperdb.Paper;

/**
 * Created by SUB on 1/16/2017.
 */

public class MainFragment extends Fragment {

    TextView userName;
    ZaafooUser user;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        getActivity().setTitle("Pre-Order");
        Paper.init(getActivity());
        user=Paper.book().read("user");
        userName=(TextView)rootView.findViewById(R.id.textView35);

        //Google Admob Code
        MobileAds.initialize(getActivity(), "ca-app-pub-1614786112628995~9416871261");
        AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Greet User
        Calendar c=Calendar.getInstance();
        int hour=c.get(Calendar.HOUR_OF_DAY);
        String greeting=null;
        if(hour>=0&&hour<12)
            greeting="Good Morning";
        else if(hour>=12&&hour<16)
            greeting="Good Afternoon";
        else if(hour>=16&&hour<24)
            greeting="Good Evening";

        Typeface face= Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Medium.ttf");
        userName.setTypeface(face);
        userName.setText(greeting+" , "+user.getName().toUpperCase());


        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ft.replace(R.id.rest,new RestList()).commit();
        getFragmentManager().beginTransaction().replace(R.id.person_frame,new NoOfPersons()).commit();
        getFragmentManager().beginTransaction().replace(R.id.date_time_frame,new DateChooser()).commit();

        return rootView;
    }


}


