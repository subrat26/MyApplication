package com.zaafoohpl.zaafoo.myapplication;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;
import com.zaafoohpl.zaafoo.fragment.CartFragment;
import com.zaafoohpl.zaafoo.fragment.EditProfileFragment;
import com.zaafoohpl.zaafoo.fragment.MainFragment;
import com.zaafoohpl.zaafoo.fragment.PastBooking;
import com.zaafoohpl.zaafoo.fragment.PreorderFood;
import com.roger.catloadinglibrary.CatLoadingView;
import com.zaafoohpl.zaafoo.fragment.PrivacyPolicy;
import com.zaafoohpl.zaafoo.fragment.RestList;
import com.zaafoohpl.zaafoo.fragment.Table;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import berlin.volders.badger.BadgeShape;
import berlin.volders.badger.Badger;
import berlin.volders.badger.CountBadge;
import cn.carbs.android.avatarimageview.library.AvatarImageView;
import io.paperdb.Paper;

public class HomeDrawar extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    JSONArray array;
    JSONObject jsobj;
    String res;
    CatLoadingView cat;
    ArrayList<String> restaurants;
    private TextView tv;
    ZaafooUser user;
    CountBadge.Factory circleFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_drawar);
        Paper.init(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        user=Paper.book().read("user");

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);

        View hView =  navigationView.getHeaderView(0);
        TextView tv=(TextView)hView.findViewById(R.id.nav_user_name);
        tv.setText(user.getName());
        AvatarImageView aiv = (AvatarImageView) hView.findViewById(R.id.item_avatar);
        aiv.setImageResource(R.drawable.userheader);
        String image=Paper.book().read("image");
        Log.e("image",image);

        if(!image.equalsIgnoreCase("no")) {
            Picasso.with(this).load(image).into(aiv);
        }

        navigationView.setNavigationItemSelectedListener(this);
        android.app.FragmentManager fm=getFragmentManager();
        fm.beginTransaction().replace(R.id.content_home_drawar,new MainFragment()).commit();

        circleFactory = new CountBadge.Factory(this, BadgeShape.circle(.5f, Gravity.END | Gravity.TOP));


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            Paper.book().write("cart_items",new ArrayList<com.zaafoohpl.zaafoo.fragment.Menu>());

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_drawar, menu);
        Badger.sett(menu.findItem(R.id.cart_navigation), circleFactory).setCount(0);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(R.id.cart_navigation==id)
            getFragmentManager().beginTransaction().replace(R.id.content_home_drawar,new CartFragment()).commit();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        FragmentTransaction ft=getFragmentManager().beginTransaction();

        int id = item.getItemId();

        if (id == R.id.nav_edit_profile) {
            //ft.addToBackStack("editProfile");
            ft.replace(R.id.content_home_drawar,new EditProfileFragment()).commit();
        } else if (id == R.id.nav_pre_order) {
           // ft.addToBackStack("preOrderFood");
            ft.replace(R.id.content_home_drawar, new MainFragment()).commit();
        }
        else if (id == R.id.nav_past_booking) {
            // ft.addToBackStack("cart");
            ft.replace(R.id.content_home_drawar,new PastBooking()).commit();
        }
        else if(id==R.id.nav_logout)
        {
            Paper.book().write("user",new ZaafooUser());
            FacebookSdk.sdkInitialize(this);
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(HomeDrawar.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else if (id == R.id.nav_privacy_policy) {
            ft.replace(R.id.content_home_drawar,new PrivacyPolicy()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



}
