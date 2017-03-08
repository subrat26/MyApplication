package com.zaafoohpl.zaafoo.fragment;

import android.app.Fragment;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.zaafoohpl.zaafoo.myapplication.R;
import com.zaafoohpl.zaafoo.myapplication.ZaafooUser;

import org.json.JSONArray;

import io.paperdb.Paper;

/**
 * Created by SUB on 1/17/2017.
 */

public class EditProfileFragment extends Fragment {


    Button confirm;
    EditText pass;
    String newpassword,oldpassword,token;
    ZaafooUser user;
    TextView tv;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_edit_profile,container,false);
        getActivity().setTitle("Edit Profile");
        Typeface face= Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Medium.ttf");
        tv=(TextView)rootView.findViewById(R.id.textView12);
        tv.setTypeface(face);


        confirm=(Button)rootView.findViewById(R.id.button4);
        pass=(EditText)rootView.findViewById(R.id.editText2);
        user=new ZaafooUser();
        user= Paper.book().read("user");
        newpassword=pass.getText().toString();
        token=user.getToken();
        oldpassword=user.getPassword();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidNetworking.post("http://zaafoo.in/changepasswordview/")
                        .addHeaders("Authorization","Token "+token)
                        .addBodyParameter("oldpassword", oldpassword)
                        .addBodyParameter("newpassword", newpassword)
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONArray(new JSONArrayRequestListener() {
                            @Override
                            public void onResponse(JSONArray response) {
                                // do anything with response
                                Toast.makeText(getActivity(),response.toString(),Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onError(ANError error) {
                                // handle error
                                Toast.makeText(getActivity(),error.getErrorDetail(),Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });





        return rootView;
    }
}
