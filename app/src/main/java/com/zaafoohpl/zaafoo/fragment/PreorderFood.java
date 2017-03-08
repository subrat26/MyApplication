package com.zaafoohpl.zaafoo.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zaafoohpl.zaafoo.myapplication.NoOfPersons;
import com.zaafoohpl.zaafoo.myapplication.R;

/**
 * Created by SUB on 1/17/2017.
 */

public class PreorderFood extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_pre_order,container,false);
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());
        View mView = layoutInflaterAndroid.inflate(R.layout.invalid_credentials, null);

        getActivity().getFragmentManager().beginTransaction().replace(R.id.content_home_drawar,new NoOfPersons()).commit();

        return rootView;
    }
}
