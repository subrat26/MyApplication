package com.zaafoohpl.zaafoo.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.squareup.picasso.Picasso;
import com.zaafoohpl.zaafoo.myapplication.R;
import com.zaafoohpl.zaafoo.myapplication.ZaafooUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.paperdb.Paper;

/**
 * Created by SUB on 2/13/2017.
 */

public class RestAdapter extends RecyclerView.Adapter<RestAdapter.ViewHolder> {

    ArrayList<Rest> restaurantList;
    Context con;
    ZaafooUser user;
    ArrayList<Table> tableList;
    View myView;
    ProgressDialog pd;
    ArrayList<String> bookedTableNos;
    ArrayList<FloorText> myFloorText;


    public RestAdapter(ArrayList<Rest> restaurantList,Context con) {
        this.restaurantList = restaurantList;
        this.con=con;
        Paper.init(con);
        user= Paper.book().read("user",new ZaafooUser());
        tableList=new ArrayList<>();
        bookedTableNos=new ArrayList<>();
        myFloorText=new ArrayList<>();
    }

    @Override
    public RestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rest_cards_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RestAdapter.ViewHolder holder, int position) {
        Typeface face= Typeface.createFromAsset(con.getAssets(), "fonts/Roboto-Medium.ttf");
        Picasso.with(con).load("http://zaafoo.in/"+restaurantList.get(position).getRest_image_url()).into(holder.rest_logo);
        holder.rest_name.setTypeface(face);
        holder.rest_name.setText(restaurantList.get(position).getRest_name());
        holder.rest_address.setTypeface(face);
        holder.rest_address.setText(restaurantList.get(position).getRest_address());

    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder {
         ImageView rest_logo;
         TextView rest_name;
         TextView rest_address;



         public ViewHolder(View itemView) {
             super(itemView);
             rest_logo=(ImageView)itemView.findViewById(R.id.imageView7);
             rest_name=(TextView)itemView.findViewById(R.id.textView23);
             rest_address=(TextView)itemView.findViewById(R.id.textView25);

             itemView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     String x=Paper.book().read("date");
                     String y=Paper.book().read("time");
                     if(x==null||y==null){
                         Toast.makeText(con,"Select Date & Time",Toast.LENGTH_SHORT).show();
                         return;
                     }
                     user.setRestaurant(restaurantList.get(getLayoutPosition()).getRest_id());
                     myView=v;
                     Paper.book().write("user",user);
                     getTableData();
                     new Handler().postDelayed(new Runnable() {
                         @Override
                         public void run() {
                             AppCompatActivity activity = (AppCompatActivity) myView.getContext();
                             activity.getFragmentManager().beginTransaction().replace(R.id.content_home_drawar,new BookTable()).commit();
                             pd.dismiss();
                         }
                     }, 4000);

                 }
             });
         }
     }

    private void getTableData() {
        pd=new ProgressDialog(con);
        pd.setMessage("Loading Tables..Please Wait");
        pd.setCancelable(false);
        pd.show();
        user=Paper.book().read("user",new ZaafooUser());
        String restid=user.getRestaurant();
        AndroidNetworking.post("http://zaafoo.in/cusresview/")
                .addBodyParameter("restid", restid)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Table table;
                        FloorText floor;
                        JSONArray table_array;
                        JSONObject table_object;
                        String floorPlan;
                        JSONArray floorText;
                        JSONObject floorObject;

                        try {
                            floorPlan=response.getString("flp");
                            floorText=response.getJSONArray("floortext");
                            Log.e("floorText",floorText.toString());
                            Paper.book().write("floorPlan",floorPlan);
                            table_array=response.getJSONArray("tabls");
                            Log.e("table layout",table_array.toString());

                            // Table Layout Loop
                            for(int i=0;i<table_array.length();i++){
                                table=new Table();
                                table_object=table_array.getJSONObject(i);
                                // Set Table Id
                                String id=table_object.getString("id");
                                table.setId(id);
                                // Set Table x-coordinate
                                String x=table_object.getString("x");
                                table.setX(Double.parseDouble(x));
                                // Set Table y-coordinate
                                String y=table_object.getString("y");
                                table.setY(Double.parseDouble(y));
                                // Set No Of Persons can sit on a table
                                String noOfPersons=table_object.getString("personstosit");
                                table.setNoOfPerson(Integer.parseInt(noOfPersons));

                                tableList.add(table);
                            }

                            // FLoor Text Loop
                            for(int j=0;j<floorText.length();j++){
                                floor=new FloorText();
                                floorObject=floorText.getJSONObject(j);
                                String x=floorObject.getString("x");
                                String y=floorObject.getString("y");
                                String text=floorObject.getString("txt");
                                String angle=floorObject.getString("degree");
                                floor.setX(x);
                                floor.setY(y);
                                floor.setText(text);
                                floor.setAngle(angle);

                                myFloorText.add(floor);

                            }

                            Paper.book().write("tableList",tableList);
                            Paper.book().write("floorText",myFloorText);

                        } catch (JSONException e) {
                            Log.e("Networking Error",e.getMessage());
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("Networking Error",error.getErrorDetail());
                    }
                });
        getBookedTableData();

    }

    public void getBookedTableData(){

        String dateTime=Paper.book().read("date").toString()+"T"+Paper.book().read("time").toString();
        AndroidNetworking.post("http://zaafoo.in/fetchRestaurantBookings/")
                .addBodyParameter("datetime", dateTime)
                .addBodyParameter("restid", user.getRestaurant())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response",response.toString());
                        try {
                            JSONArray arr=response.getJSONArray("bookedtables");
                            for(int i=0;i<arr.length();i++){
                                JSONObject obj=arr.getJSONObject(i);
                                String id=obj.getString("id");
                                bookedTableNos.add(id);
                            }
                            Paper.book().write("bookedTables",bookedTableNos);
                        } catch (JSONException e) {
                            Log.e("json error",e.toString());
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        Log.e("networking error",error.getErrorDetail());
                    }
                });
    }
}
