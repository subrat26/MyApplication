package com.zaafoohpl.zaafoo.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.roger.catloadinglibrary.CatLoadingView;

import org.json.JSONException;
import org.json.JSONObject;

import io.paperdb.Paper;


@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {

    Button register;
    EditText name_field, email_field, pass_field;
    TextView tv;
    String name, email, password;
    CatLoadingView cat_view;
    ZaafooUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Paper.init(this);
        AndroidNetworking.initialize(this);
        cat_view=new CatLoadingView();
        cat_view.setCancelable(false);

        user=new ZaafooUser();
        user=Paper.book().read("user",new ZaafooUser());

        // Check if use data already exists
        if(user.getToken()!=null) {
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        }


        // Reference to the XML views
        name_field = (EditText) findViewById(R.id.name_field);
        email_field = (EditText) findViewById(R.id.email_field);
        pass_field = (EditText) findViewById(R.id.pass_field);
        register = (Button) findViewById(R.id.create_acc);


    }


    // Already Have Account Method
    public void haveAccount(View v) {
        startActivity(new Intent(MainActivity.this, Login.class));
        finish();
    }


    //On click method to create account

    public void createAccount(View v) {


        cat_view.show(getSupportFragmentManager(),"");
        name = name_field.getText().toString();
        email = email_field.getText().toString();
        password = pass_field.getText().toString();

        AndroidNetworking.post("http://zaafoo.in/registrationrest/")
                .addBodyParameter("user", name)
                .addBodyParameter("email", email)
                .addBodyParameter("password", password)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        cat_view.dismiss();
                        try {
                            String res=response.getString("detail");
                            if(res.equalsIgnoreCase("POST answer")){

                                user.setEmail(email);
                                user.setName(name);
                                user.setPassword(password);
                                Paper.book().write("user",user);
                                Toast.makeText(MainActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this,Login.class));
                            }
                            else{
                                Toast.makeText(MainActivity.this,"Invalid Credentials",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        cat_view.dismiss();
                        Toast.makeText(MainActivity.this,"Invalid Credentials",Toast.LENGTH_SHORT).show();

                    }
                });

    }


}

















