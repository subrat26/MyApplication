package com.zaafoohpl.zaafoo.fragment;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
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

import de.mateware.snacky.Snacky;
import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuNewFragment extends Fragment {

    ArrayList<Cuisine> cuisineList;
    ArrayList<String> cuisineNameList;
    ArrayList<Menu> menuList;
    ZaafooUser user;
    JSONArray menu_array;
    JSONObject menu_object;
    MenuExpandableAdapter myadapter;
    ExpandableListView listView;
    ProgressDialog pd;
    public Menu myMenu;
    Button skip_menu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View v=inflater.inflate(R.layout.fragment_menu_new, container, false);
        listView=(ExpandableListView)v.findViewById(R.id.menu_expandable);
        skip_menu=(Button)v.findViewById(R.id.skip_menu);
        Paper.init(getActivity());
        cuisineList=new ArrayList<>();
        menuList=new ArrayList<>();
        cuisineNameList=new ArrayList<>();
        user= Paper.book().read("user",new ZaafooUser());
        new DownloadMenuData().execute();
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                myMenu= (Menu) parent.getExpandableListAdapter().getChild(groupPosition,childPosition);
                Log.e("menu",myMenu.getName());
                showAddToCartDialog();
                return true;
            }
        });

        skip_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Paper.book().write("cart_items",new ArrayList<Menu>());
                startActivity(new Intent(getActivity(), FinalActivity.class));
            }
        });
        return v;
    }




    class DownloadMenuData extends AsyncTask<Void,Void,Void>{


        @Override
        protected Void doInBackground(Void... params) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pd=new ProgressDialog(getActivity());
                    pd.setMessage("Loading Menu..");
                    pd.show();
                }
            });
            getMenuDataFromZaafoo(user.getRestaurant());

            return null;
        }
    }




    private void getMenuDataFromZaafoo(String restid) {

        AndroidNetworking.post("http://zaafoo.in/cusresview/")
                .addBodyParameter("restid", restid)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("res",response.toString());
                            menu_array=response.getJSONArray("menus");

                            // CUISINE LOOP
                            for(int j=0;j<menu_array.length();j++){
                                menu_object=menu_array.getJSONObject(j);
                                if(!cuisineNameList.contains(menu_object.getString("cuisine")))
                                    cuisineNameList.add(menu_object.getString("cuisine"));
                            }

                            for(String key:cuisineNameList)
                            {
                                for(int i=0;i<menu_array.length();i++){
                                    menu_object=menu_array.getJSONObject(i);
                                    if(menu_object.getString("cuisine").equalsIgnoreCase(key)) {
                                        Menu m=new Menu();
                                        String m_name = menu_object.getString("name");
                                        String m_price = menu_object.getString("price");
                                        String m_id = menu_object.getString("id");
                                        m.setId(m_id);
                                        m.setName(m_name);
                                        m.setPrice(m_price);
                                        menuList.add(m);
                                        Paper.book().write("menu",menuList);
                                    }
                                }
                                ArrayList<Menu> myMenu=Paper.book().read("menu");
                                Cuisine c=new Cuisine();
                                c.setName(key);
                                c.setMenus(myMenu);
                                cuisineList.add(c);
                                menuList.clear();

                            }
                            myadapter=new MenuExpandableAdapter(cuisineList,getActivity());
                            listView.setAdapter(myadapter);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                }
                            });

                        } catch (JSONException e) {
                            Log.e("json err",e.getMessage());
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                }
                            });
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        Log.e("error",error.getErrorDetail());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.dismiss();
                            }
                        });
                    }
                });
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


}
