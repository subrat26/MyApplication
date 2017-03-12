package com.zaafoohpl.zaafoo.myapplication;



import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.roger.catloadinglibrary.CatLoadingView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.paperdb.Paper;


public class GetLocation extends AppCompatActivity {


    Spinner s1;
    ArrayList<String> city;
    HashMap<String,String> cities;
    String cityId;
    Button goToHome;
    CatLoadingView cat;
    ZaafooUser user;
    ArrayList<String> localityName;
    ArrayList<String> localityId;
    TextView currentLocation;
    int x,y;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);
        setTitle("Location");
        Paper.init(this);
        Typeface face= Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        TextView tv=(TextView)findViewById(R.id.textView3);
        TextView tv1=(TextView)findViewById(R.id.textView33);
        currentLocation=(TextView)findViewById(R.id.textView32);
        tv.setTypeface(face);
        tv1.setTypeface(face);
        currentLocation.setTypeface(face);
        String gpsLocation=Paper.book().read("gpsLocation","xyz");
        getUserEmail();


        localityName=new ArrayList<>();
        localityId=new ArrayList<>();

        cities=Paper.book().read("cities",new HashMap<String, String>());

        s1=(Spinner)findViewById(R.id.spinner);
        city=new ArrayList<>();
        user=new ZaafooUser();

        goToHome=(Button)findViewById(R.id.button6);
        goToHome.setClickable(false);
        goToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x==1&&y==1)
                startActivity(new Intent(GetLocation.this,HomeDrawar.class));

                else
                    Toast.makeText(GetLocation.this,"Select Both City & Location",Toast.LENGTH_SHORT).show();
            }
        });


        for(String key:cities.keySet()){
            city.add(cities.get(key).toString());
        }


        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,city);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(adapter);

        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String city=parent.getItemAtPosition(position).toString();
                for(String key:cities.keySet()){
                    if(((cities.get(key).toString()).equalsIgnoreCase(city))){
                        user= Paper.book().read("user");
                        user.setCity(key);
                        getLocality(key);
                        localityId.clear();
                        localityName.clear();
                        Paper.book().write("user",user);
                        x=1;
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(gpsLocation.equalsIgnoreCase("xyz")){
            currentLocation.setText("Unable To Trace Current Location");

        }
        else{
            int i=0;
            for(String key:city){
                if(key.equalsIgnoreCase(gpsLocation))
                    s1.setSelection(i,true);
                i++;
            }
            currentLocation.setText("You are at "+gpsLocation);
        }


    }

    // Get User Email
    private void getUserEmail() {

       user=Paper.book().read("user",new ZaafooUser());
        AndroidNetworking.post("http://zaafoo.in/useremailview/")
                .addHeaders("Authorization","Token "+user.getToken())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String email=response.getString("email");
                            user.setEmail(email);
                            Paper.book().write("user",user);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                    }
                });


    }


    private void getLocality(String key) {

        final ProgressDialog pd=new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setTitle("Zaafoo");
        pd.setMessage("Fetching Locality...Please Wait");
        pd.show();

        AndroidNetworking.post("http://zaafoo.in/localityview/")
                .addBodyParameter("cityid",key)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {
                            JSONArray locality=response.getJSONArray("locailities");
                            JSONObject local;
                            for(int i=0;i<locality.length();i++){
                                local=locality.getJSONObject(i);
                                localityId.add(local.getString("id"));
                                localityName.add(local.getString("name"));
                            }
                            populateLocalitySpinner(localityName);
                            pd.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            pd.dismiss();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        pd.dismiss();

                    }
                });
    }

    private void populateLocalitySpinner(ArrayList<String> locality) {

        Spinner sp=(Spinner)findViewById(R.id.spinner7);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,locality);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.e("position",localityName.get(position));
                Paper.book().write("locality",localityId.get(position));
                goToHome.setClickable(true);
                y=1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }



}




