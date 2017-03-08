package com.zaafoohpl.zaafoo.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.zaafoohpl.zaafoo.myapplication.FinalActivity;
import com.zaafoohpl.zaafoo.myapplication.R;
import com.zaafoohpl.zaafoo.myapplication.SessionManagement;
import com.zaafoohpl.zaafoo.myapplication.ZaafooUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import berlin.volders.badger.BadgeShape;
import berlin.volders.badger.Badger;
import berlin.volders.badger.CountBadge;
import de.mateware.snacky.Snacky;
import io.paperdb.Paper;


public class MenuFragment extends Fragment {


    ListView lv;
    ArrayList<Menu> menu_list;
    ProgressBar pbar;
    public Menu m;
    public Menu myMenu;
    Button skip_menu;
    ZaafooUser user;
    SearchView menuSearch;
    MenuAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_menu, container, false);
        getActivity().setTitle("Menu");
        Paper.init(getActivity());
        user=new ZaafooUser();
        user=Paper.book().read("user",new ZaafooUser());

        skip_menu=(Button)rootView.findViewById(R.id.skip_menu);
        lv=(ListView)rootView.findViewById(R.id.menu_list);
        pbar=(ProgressBar)rootView.findViewById(R.id.progressBar4);
        pbar.setVisibility(View.VISIBLE);
        menu_list=new ArrayList<>();
        String restid=user.getRestaurant();

        menuSearch=(SearchView)rootView.findViewById(R.id.menu_search);
        menuSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                ArrayList<Menu> menuList1=new ArrayList<Menu>();
                if(!menu_list.isEmpty())
                for(Menu m:menu_list)
                {
                    String menu=m.getName().toLowerCase();
                    String query=newText.toLowerCase();
                    if(menu.contains(query)) {
                        Log.e("rest name", m.getName());
                        menuList1.add(m);
                    }
                }

                adapter=new MenuAdapter(getActivity(),R.layout.menu_row_view,menuList1);
                lv.setAdapter(adapter);
                return true;
            }
        });

        AndroidNetworking.post("http://zaafoo.in/cusresview/")
                .addBodyParameter("restid",restid)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                                     @Override
                                     public void onResponse(JSONObject response) {


                                         JSONArray menuArray=null;
                                         JSONArray ratingArray=null;
                                         JSONArray imageArray=null;
                                         JSONObject objrate=null;
                                         JSONObject objimage=null;
                                         try {
                                             menuArray=response.getJSONArray("menus");
                                             String menuName=null;
                                             String menuPrice=null;
                                             String menuType=null;
                                             String menuCuisine=null;
                                             String menuTime=null;
                                             String menuDesc=null;
                                             String id=null;



                                             for(int i=0;i<menuArray.length();i++) {
                                                 JSONObject objrest = menuArray.getJSONObject(i);
                                                 menuName = objrest.getString("name");
                                                 menuPrice = objrest.getString("price");
                                                 menuType=objrest.getString("Veg");
                                                 id = objrest.getString("id");
                                                 menuCuisine= objrest.getString("cuisine");
                                                 menuTime = objrest.getString("food_timings");
                                                 menuDesc=objrest.getString("description");

                                                 m = new Menu();
                                                 m.setName(menuName);
                                                 m.setPrice(menuPrice);
                                                 m.setMenu_type(menuType);
                                                 m.setId(id);
                                                 m.setFood_timing(menuTime);
                                                 m.setDesc(menuDesc);
                                                 m.setCuisine(menuCuisine);

                                                 menu_list.add(m);
                                             }

                                             adapter=new MenuAdapter(getActivity(),R.layout.menu_row_view,menu_list);
                                             lv.setScrollingCacheEnabled(false);
                                             lv.setAdapter(adapter);
                                             pbar.setVisibility(View.GONE);
                                         } catch (JSONException e) {
                                             pbar.setVisibility(View.GONE);
                                         }

                                     }

                                     @Override
                                     public void onError(ANError anError) {
                                         pbar.setVisibility(View.GONE);

                                     }
                                 }
                );


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                myMenu=(Menu)parent.getItemAtPosition(position);
                showAddToCartDialog();
            }
        });

        skip_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Paper.book().write("cart_items",new ArrayList<Menu>());
                startActivity(new Intent(getActivity(), FinalActivity.class));
            }
        });
        return rootView;
    }

    public void showAddToCartDialog(){

        AlertDialog.Builder alert=new AlertDialog.Builder(getActivity());
        alert.setCancelable(false);
        alert.setTitle("Zaafoo");
        alert.setMessage("Wana Add this Item To Cart?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showSelectAmountDialog();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
            }
        });

        AlertDialog dialog=alert.create();
        dialog.show();

    }

    private void showSelectAmountDialog() {

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());
        View mView = layoutInflaterAndroid.inflate(R.layout.select_menu_amount, null);

        ArrayList<String> itemNo=new ArrayList<String>();
        for(int i=1;i<=10;i++){
            itemNo.add(String.valueOf(i));
        }

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,itemNo);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner=(Spinner)mView.findViewById(R.id.spinner4);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String amount=parent.getItemAtPosition(position).toString();
                    myMenu.setAmount(amount);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        AlertDialog.Builder alert=new AlertDialog.Builder(getActivity());
        alert.setCancelable(false);
        alert.setView(mView);

        alert.setPositiveButton("Add To Cart", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                boolean present=false;
                ArrayList<Menu> xyz=new SessionManagement(getActivity()).loadCartItems();
                for(Menu m:xyz){
                    if(m.getId().equalsIgnoreCase(myMenu.getId()))
                        present=true;
                }

                if(present){
                    Snacky.builder()
                            .setActivty(getActivity())
                            .setText("Item Already Present In Cart")
                            .centerText()
                            .setDuration(Snacky.LENGTH_SHORT)
                            .error()
                            .show();
                }
                else{
                    new SessionManagement(getActivity()).addCartItems(myMenu);
                    Snacky.builder()
                            .setActivty(getActivity())
                            .setText("Item Added To Cart")
                            .centerText()
                            .setDuration(Snacky.LENGTH_SHORT)
                            .success()
                            .show();
                }



            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog=alert.create();
        dialog.show();



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
