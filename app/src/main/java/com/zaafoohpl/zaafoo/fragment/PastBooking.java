package com.zaafoohpl.zaafoo.fragment;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

/**
 * A simple {@link Fragment} subclass.
 */
public class PastBooking extends Fragment {


    ArrayList<Past> bookings;
    RecyclerView recyclerView;
    ProgressDialog pd;
    String restaurant_name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_past_booking, container, false);
        getActivity().setTitle("Past Booking");
        bookings=new ArrayList<>();
        Paper.init(getActivity());
        recyclerView = (RecyclerView)v.findViewById(R.id.booking_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        getPastBookings();
        return v;
    }

    public void getPastBookings() {

        ZaafooUser user= Paper.book().read("user",new ZaafooUser());
        pd=new ProgressDialog(getActivity());
        pd.setCancelable(false);
        pd.setMessage("Loading Past Bookings");
        pd.setTitle("Zaafoo");
        pd.show();
        AndroidNetworking.post("http://zaafoo.in/mytransactionsview/")
                .addHeaders("Authorization","Token "+user.getToken())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response",response.toString());
                        try {
                            JSONArray array=response.getJSONArray("bookings");
                            for(int i=0;i<array.length();i++){
                                JSONObject obj=array.getJSONObject(i);
                                String restid=obj.getString("restaurant");
                                String restname=getRestaurantName(restid);
                                String total=obj.getString("total");
                                String advance=obj.getString("advance");
                                Past p=new Past();
                                p.setRestid(restid);
                                p.setRestname(restname);
                                p.setTotal(total);
                                p.setAdvance(advance);
                                bookings.add(p);
                            }
                            RecyclerView.Adapter adapter = new PastBookingAdapter(bookings,getActivity());
                            recyclerView.setAdapter(adapter);
                            pd.dismiss();
                        } catch (JSONException e) {
                            Log.e("error",e.toString());
                            pd.dismiss();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.e("networking error",error.getErrorDetail());
                        pd.dismiss();
                    }
                });




    }

    private String getRestaurantName(String restid) {

        AndroidNetworking.post("http://zaafoo.in/cusresview/")
                .addBodyParameter("restid", restid)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray menu_array=response.getJSONArray("menus");
                            JSONObject obj=menu_array.getJSONObject(0);
                            restaurant_name=obj.getString("restaurant");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.e("error",error.getErrorDetail());
                    }
                });

        return "Lal Qila";
    }

}
