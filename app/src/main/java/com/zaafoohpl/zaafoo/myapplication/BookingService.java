package com.zaafoohpl.zaafoo.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONObject;

public class BookingService extends Service {

    boolean cont=true;

    public BookingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Service","Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("Service","started");
        return super.onStartCommand(intent, flags, startId);
    }

    private void checkForCustomerArrivalAndLeftStatus() {
        AndroidNetworking.post("http://zaafoo.in/mytransactionsview/")
                .addHeaders("Authorization","Token d2a10afd0759021fd50e584982ddd918d1f1d915")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener(){
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.e("Booking Response",response.toString());

                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }
}
