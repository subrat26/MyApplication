package com.zaafoohpl.zaafoo.myapplication;


import android.content.Context;
import android.content.SharedPreferences;

import com.zaafoohpl.zaafoo.fragment.Menu;
import com.google.gson.Gson;
import com.zaafoohpl.zaafoo.fragment.Table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.paperdb.Paper;

/**
 * Created by SUB on 1/14/2017.
 */

public class SessionManagement {

    SharedPreferences sp;
    Context context;


    public SessionManagement(Context con){
        context=con;
    }

    public void insertToSharedPrefToken(String token){
        sp=context.getSharedPreferences("myPref",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("token",token);
        editor.commit();
    }

    public boolean checkToken(){

        sp=context.getSharedPreferences("myPref",context.MODE_PRIVATE);
        if(sp.contains("token"))
            return true;
        return false;
    }

    public String getToken(){
        String token;
        sp=context.getSharedPreferences("myPref",context.MODE_PRIVATE);
        token=sp.getString("token","error");
        return token;

    }

    public void insertToSharedPrefLocation(String location){
        sp=context.getSharedPreferences("myPref",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("location",location);
        editor.commit();
    }

    public String getLocation(){
        String location;
        sp=context.getSharedPreferences("myPref",context.MODE_PRIVATE);
        location=sp.getString("location","error");
        return location;

    }

    public void insertToSharedPrefEmail(String email){
        sp=context.getSharedPreferences("myPref",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("email",email);
        editor.commit();
    }

    public String getEmail() {
        String location;
        sp = context.getSharedPreferences("myPref", context.MODE_PRIVATE);
        location = sp.getString("email", "error");
        return location;
    }


    public void insertToSharedPrefName(String name){
        sp=context.getSharedPreferences("myPref",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("name",name);
        editor.commit();
    }

    public String getName(){
        String location;
        sp=context.getSharedPreferences("myPref",context.MODE_PRIVATE);
        location=sp.getString("name","error");
        return location;}



    public void insertMenuObject(Menu m){
        sp=context.getSharedPreferences("myPref",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(m);
        editor.putString("menu_object",json);
        editor.commit();
    }

    public Menu getMenuObject(){
        sp=context.getSharedPreferences("myPref",context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp.getString("menu_object", "");
        Menu obj = gson.fromJson(json, Menu.class);
        return obj;}


    public void addCartItems(Menu m){
        ArrayList<Menu> menus=loadCartItems();
        if(loadCartItems().isEmpty()){
            menus=new ArrayList<>();
            menus.add(m);
        }
        else
            menus.add(m);


        storeCartItems(menus);
    }


    public ArrayList<Menu> loadCartItems(){
        ArrayList<Menu> menus=Paper.book().read("cart_items",new ArrayList<Menu>());
        return menus;
    }



    public void storeCartItems(ArrayList<Menu> x){

        Paper.book().write("cart_items", x);
    }

    public void removeCartItem(Menu m){
        ArrayList<Menu> menus=loadCartItems();
        menus.remove(m);
        storeCartItems(menus);
    }

    public void removeAllCartItems(){
        Paper.book().write("cart_items", new ArrayList<Menu>());
    }

    public void insertTableList(ArrayList<Table> tableList){
        sp=context.getSharedPreferences("myPref",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String tables=gson.toJson(tableList);
        editor.putString("tableList",tables);
        editor.commit();

    }

    public ArrayList<Table> loadTables(){
        sp=context.getSharedPreferences("myPref",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        List<Table> tableList;
        if(sp.contains("tableList")){
            String json=sp.getString("tableList",null);
            Gson gson = new Gson();
            Table[] mytables=gson.fromJson(json,Table[].class);
            tableList= Arrays.asList(mytables);
            tableList=new ArrayList<>(tableList);
        }
        else
            return new ArrayList<Table>();
        return (ArrayList)tableList;

    }
}
