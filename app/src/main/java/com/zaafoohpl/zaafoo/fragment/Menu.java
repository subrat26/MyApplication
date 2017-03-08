package com.zaafoohpl.zaafoo.fragment;


import android.os.Parcel;
import android.os.Parcelable;

public class Menu implements Parcelable {

    String id;
    String name;
    String restaurant_name;
    String price;
    String cuisine;
    String food_timing;
    String desc;
    String menu_type;
    String amount;


    public Menu(String name, String price) {
        this.name = name;
        this.price = price;
    }

    public Menu(){

    }

    protected Menu(Parcel in) {
        id = in.readString();
        name = in.readString();
        restaurant_name = in.readString();
        price = in.readString();
        cuisine = in.readString();
        food_timing = in.readString();
        desc = in.readString();
        menu_type = in.readString();
        amount = in.readString();
    }

    public static final Creator<Menu> CREATOR = new Creator<Menu>() {
        @Override
        public Menu createFromParcel(Parcel in) {
            return new Menu(in);
        }

        @Override
        public Menu[] newArray(int size) {
            return new Menu[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(restaurant_name);
        dest.writeString(price);
        dest.writeString(cuisine);
        dest.writeString(food_timing);
        dest.writeString(desc);
        dest.writeString(menu_type);
        dest.writeString(amount);
    }




    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMenu_type() {
        return menu_type;
    }

    public void setMenu_type(String menu_type) {
        this.menu_type = menu_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public void setRestaurant_name(String restaurant_name) {
        this.restaurant_name = restaurant_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getFood_timing() {
        return food_timing;
    }

    public void setFood_timing(String food_timing) {
        this.food_timing = food_timing;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
