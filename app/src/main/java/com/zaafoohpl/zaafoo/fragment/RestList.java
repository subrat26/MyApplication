package com.zaafoohpl.zaafoo.fragment;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.zaafoohpl.zaafoo.myapplication.R;
import com.zaafoohpl.zaafoo.myapplication.ZaafooUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.paperdb.Paper;


public class RestList extends Fragment {

    ArrayList<Rest> restaurantList;
    ZaafooUser user;
    JSONArray restaurant,images,ratings;
    Rest r;
    String id,name,address,image,rating;
    RecyclerView recyclerView;
    ProgressDialog pd;
    SearchView search;
    RecyclerView.Adapter adapter;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restaurantList=new ArrayList<>();
        Paper.init(getActivity());
        user=Paper.book().read("user",new ZaafooUser());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_rest_list, container, false);
        search= (SearchView) rootView.findViewById(R.id.rest_search);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(layoutManager);
        getRestaurantList();
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                ArrayList<Rest> restList1=new ArrayList<Rest>();
                if(!restaurantList.isEmpty())
                for(Rest r:restaurantList)
                {
                    String rest=r.getRest_name().toLowerCase();
                    String query=newText.toLowerCase();
                    if(rest.contains(query)) {
                        Log.e("rest name", r.getRest_name());
                        restList1.add(r);
                    }
                }

                adapter = new RestAdapter(restList1,getActivity());
                recyclerView.setAdapter(adapter);
                return true;
            }
        });
        return rootView;
    }

    public void getRestaurantList(){
        pd=new ProgressDialog(getActivity());
        pd.setCancelable(false);
        pd.setTitle("Zaafoo");
        pd.setMessage("Loading Restaurants..Please Wait");
        pd.show();
        String local=Paper.book().read("locality");
        AndroidNetworking.post("http://zaafoo.in/restlocal/")
                .addBodyParameter("localityid",local)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                                     @Override
                                     public void onResponse(JSONObject response) {
                                        JSONObject obj,objimage,objrate;
                                         try {
                                             restaurant=response.getJSONArray("rests");
                                             images=response.getJSONArray("restimages");
                                             ratings=response.getJSONArray("ratings");
                                             for(int i=0;i<restaurant.length();i++) {
                                                 obj = restaurant.getJSONObject(i);
                                                 id = obj.getString("id");
                                                 name = obj.getString("rname");
                                                 address = obj.getString("street_address");

                                                 for (int j = 0; j < images.length(); j++) {
                                                     objimage = images.getJSONObject(j);
                                                     if (objimage.getString("restid") == id)
                                                         image = objimage.getString("imagecontent");
                                                 }

                                                 for(int k=0;k<ratings.length();k++){
                                                     objrate=ratings.getJSONObject(k);
                                                     if(objrate.getString("restid")==id)
                                                        rating=objrate.getString("rating");
                                                 }

                                                 r = new Rest();
                                                 r.setRest_id(id);
                                                 r.setRest_name(name);
                                                 r.setRest_address(address);
                                                 r.setRest_image_url(image);
                                                 r.setRest_rating(rating);
                                                 restaurantList.add(r);
                                             }
                                             adapter = new RestAdapter(restaurantList,getActivity());
                                             recyclerView.setAdapter(adapter);
                                             pd.dismiss();

                                         } catch (JSONException e) {
                                             e.printStackTrace();
                                             if(pd.isShowing())
                                             pd.dismiss();
                                         }

                                     }

                                     @Override
                                     public void onError(ANError anError) {
                                         if(pd.isShowing())
                                            pd.dismiss();
                                     }
                                 }
                );
    }

}
