package com.zaafoohpl.zaafoo.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;

import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.yayandroid.locationmanager.LocationBaseActivity;
import com.yayandroid.locationmanager.LocationConfiguration;
import com.yayandroid.locationmanager.constants.ProviderType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.mateware.snacky.Snacky;
import io.paperdb.Paper;


public class SplashActivity extends LocationBaseActivity {


    SessionManagement sm = new SessionManagement(this);
    HashMap<String,String> cities;
    JSONObject city;
    List<Address> addresses;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AndroidNetworking.initialize(this);
        cities=new HashMap<>();
        Paper.init(this);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        TextView name = (TextView) findViewById(R.id.textView5);
        name.setText("Zaafoo");
        name.setTypeface(face);



        TextView slogan = (TextView) findViewById(R.id.textView6);
        slogan.setText("Your Wish,Your Choice");
        slogan.setTypeface(face);

        getLocation();
        getLocationFromZaafoo();

        Handler h=new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {


                goToHome();
            }
        }, 4000);


    }

    // Go to Next Activity
    public void goToHome(){

        if(sm.checkToken())
            startActivity(new Intent(SplashActivity.this,GetLocation.class));
        else
            startActivity(new Intent(SplashActivity.this,MainActivity.class));

        startService(new Intent(this,BookingService.class));
        finish();

    }


    // Get Location From Zaafoo
    public void getLocationFromZaafoo(){

        AndroidNetworking.get("http://zaafoo.in/publicrest/cities/")
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "3")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {

                        for(int i=0;i<response.length();i++) {
                            try {
                                city = response.getJSONObject(i);
                                String id=city.getString("id");
                                String c=city.getString("city_name");
                                cities.put(id,c);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        Paper.book().write("cities",cities);
                    }

                    @Override
                    public void onError(ANError anError) {
                    }
                });
    }


    @Override
    public LocationConfiguration getLocationConfiguration() {
        return new LocationConfiguration()
                .keepTracking(false)
                .askForGooglePlayServices(true)
                .setMinAccuracy(200.0f)
                .setWaitPeriod(ProviderType.GOOGLE_PLAY_SERVICES, 5 * 1000)
                .setWaitPeriod(ProviderType.GPS, 10 * 1000)
                .setWaitPeriod(ProviderType.NETWORK, 5 * 1000)
                .setGPSMessage("Would you mind to turn GPS on?")
                .setRationalMessage("Provide the permission!");
    }

    @Override
    public void onLocationFailed(int failType) {
        Snacky.builder()
                .setActivty(SplashActivity.this)
                .setText("Location Could Not Be Traced")
                .centerText()
                .setDuration(Snacky.LENGTH_SHORT)
                .error()
                .show();
        Paper.book().write("gpsLocation","xyz");

    }

    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            Log.e("Longitude & Latitude", location.getLongitude() + " " + location.getLatitude());
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(addresses!=null) {
                String cityName = addresses.get(0).getLocality();
                Log.e("city", cityName);
                Paper.book().write("gpsLocation", cityName);
            }
        }
    }
}
