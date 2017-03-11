package com.zaafoohpl.zaafoo.myapplication;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.ebs.android.sdk.Config;
import com.ebs.android.sdk.EBSPayment;
import com.ebs.android.sdk.PaymentRequest;
import com.zaafoohpl.zaafoo.fragment.Menu;
import com.zaafoohpl.zaafoo.fragment.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import io.paperdb.Paper;



public class FinalActivity extends AppCompatActivity {


    final String SECRET_KEY="58a990f5bab2422a53f185a7def64f63";
    final int MERCHANT_ID=22818;
    int noOfTables;
    ArrayList<String> bookedTables;
    HashMap<String,String> menuDetails;
    ArrayList<Menu> menus;
    ZaafooUser user;
    double totalAmount;
    TextView table,menu,discount,vat,transaction,total;
    Button payNow;
    String reference_No;
    ProgressDialog pd;
    ArrayList<HashMap<String, String>> custom_post_parameters;
    private JSONObject book_object;
    int trials=0;
    double finalTotalAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);
        setTitle("Zaafoo");
        Intent intent = getIntent();
        String payment_id = intent.getStringExtra("payment_id");

        if(payment_id!=null) {
            Intent i=new Intent(FinalActivity.this,PaymentResponse.class);
            i.putExtra("payment_response",payment_id.toString());
            startActivity(i);
            finish();
        }
        Paper.init(this);
        bookedTables=new ArrayList<>();
        user=Paper.book().read("user");

        table=(TextView)findViewById(R.id.table_amount);
        menu=(TextView)findViewById(R.id.menu_amount);
        discount=(TextView)findViewById(R.id.discount_amount);
        vat=(TextView)findViewById(R.id.vat_amount);
        transaction=(TextView)findViewById(R.id.transaction_amount);
        total=(TextView)findViewById(R.id.total_amount);
        payNow=(Button)findViewById(R.id.button11);


        payNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(reference_No!=null){
                    goForPayment(user.getName(),user.getEmail(),String.valueOf(finalTotalAmount),reference_No);
                    Paper.book().write("bookingObject",book_object);
                }
            }
        });




    }

    private double getBookingData() {

        ArrayList<Table> myTables=new SessionManagement(this).loadTables();
        for(Table t:myTables){
            if(t.isSelected()) {
                bookedTables.add(t.getId());
                noOfTables++;

            }
        }

        String token= user.getToken();
        String restid=user.getRestaurant();
        String personalRequest=Paper.book().read("personalRequest","No Request");
        String date=Paper.book().read("date")+"T"+Paper.book().read("time");

        DecimalFormat df=new DecimalFormat(".##");

        ArrayList<Menu> mymenu=new SessionManagement(this).loadCartItems();
        double menuAmount=0;
        for(Menu m:mymenu){
            menuAmount+=Double.parseDouble(m.getAmount())*Double.parseDouble(m.getPrice());
        }


        menuAmount= Double.parseDouble(df.format(menuAmount));

        double tableAmount=noOfTables*40;
        tableAmount= Double.parseDouble(df.format(tableAmount));

        totalAmount=menuAmount+tableAmount;
        double disc=0;
        if(totalAmount>=300.0) {
            disc=0.1*totalAmount;
            disc= Double.parseDouble(df.format(disc));
            totalAmount = totalAmount - (0.1 * totalAmount);
            discount.setText("-Rs."+disc);
        }
        else
        discount.setText("-Rs.0");

        table.setText("Rs."+tableAmount);
        menu.setText("Rs."+menuAmount);
        double tax=0.15*totalAmount;
        tax= Double.parseDouble(df.format(tax));
        vat.setText("Rs."+tax);
        double transact=0.02*(tax+totalAmount);
        transact= Double.parseDouble(df.format(transact));
        transaction.setText("Rs."+transact);
        double finalTotal=totalAmount+tax+transact;
        finalTotal= Double.parseDouble(df.format(finalTotal));
        total.setText("Rs."+finalTotal);

        JSONObject booking_object=createJsonObject(bookedTables,noOfTables,mymenu,token,restid,personalRequest,date);
        book_object=booking_object;
        getBookingIdFromServer(booking_object);
        return  finalTotal;



    }

    // Get  Reference ID from server
    private void getBookingIdFromServer(final JSONObject booking_object) {

        if(pd==null) {
            pd = new ProgressDialog(this);
            pd.setCancelable(false);
            pd.setTitle("Zaafoo");
            pd.setMessage("Loading...");
            if (!(pd.isShowing()))
                pd.show();
        }

        AndroidNetworking.post("http://zaafoo.in/resbookview/")
                .addJSONObjectBody(booking_object)
                .addHeaders("Authorization","Token "+user.getToken())
                .setTag("booking object")
                .setContentType("application/json")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {
                            reference_No=response.getString("BookingID");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(pd!=null&&pd.isShowing())
                        pd.dismiss();
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        if(pd!=null&&pd.isShowing())
                            pd.dismiss();

                            Paper.book().write("cart_items",new ArrayList<Menu>());
                            Intent intent = new Intent(FinalActivity.this, HomeDrawar.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            Toast.makeText(FinalActivity.this, "Seems Like Internet is too Slow.Try Later", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    public JSONObject createJsonObject(ArrayList<String> bookedTableId,int noOfTables,ArrayList<Menu> mymenu,String token,String restid,String perRequest,String date){


        JSONObject book_obj=new JSONObject();
        JSONArray table_array=new JSONArray();
        JSONArray menu_array=new JSONArray();
        JSONObject table_Obj;
        JSONObject menu_obj;


        for(String key:bookedTableId){
                table_Obj=new JSONObject();
            try {
                table_Obj.put("tableid",Integer.parseInt(key));
                }
            catch (JSONException e1) {
                e1.printStackTrace();
            }
                table_array.put(table_Obj);
        }


            for(int i=0;i<mymenu.size();i++){
                try {
                    menu_obj=new JSONObject();
                    menu_obj.put("menuid",Integer.parseInt(mymenu.get(i).getId()));
                    menu_obj.put("value",Integer.parseInt(mymenu.get(i).getAmount()));
                    menu_array.put(i,menu_obj);
                } catch (JSONException e) {
                }
            }

        try {
            book_obj.put("tables",noOfTables);
            book_obj.put("Personal Request",perRequest);
            book_obj.put("restid",restid);
            book_obj.put("tablnos",table_array);
            book_obj.put("menus",menu_array);
            book_obj.put("date",date);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Memory Optimization
        table_array=null;
        menu_array=null;
        table_Obj=null;
        menu_obj=null;
        //
        Log.e("booking object",book_obj.toString());
        return  book_obj;

    }


    private void goForPayment(String user,String email,String amount,String referenceNo) {

        PaymentRequest.getInstance().setFailureid("1");
        PaymentRequest.getInstance().setTransactionAmount(amount);
        PaymentRequest.getInstance().setReferenceNo(referenceNo);
        PaymentRequest.getInstance().setCurrency("INR");
        PaymentRequest.getInstance().setLogEnabled("1");
        PaymentRequest.getInstance().setTransactionDescription("Zaafoo Pre_order and Table Booking");
        PaymentRequest.getInstance().setAccountId(MERCHANT_ID);
        PaymentRequest.getInstance().setSecureKey(SECRET_KEY);
        // Payment Options
        PaymentRequest.getInstance().setHidePaymentOption(false);
        PaymentRequest.getInstance().setHideCashCardOption(false);
        PaymentRequest.getInstance().setHideCreditCardOption(false);
        PaymentRequest.getInstance().setHideDebitCardOption(false);
        PaymentRequest.getInstance().setHideNetBankingOption(false);
        PaymentRequest.getInstance().setHideStoredCardOption(true);
        // Shipping Details
        PaymentRequest.getInstance().setShippingName(user);
        PaymentRequest.getInstance().setShippingEmail(email);
        PaymentRequest.getInstance().setShippingAddress("NA");
        PaymentRequest.getInstance().setShippingCity("NA");
        PaymentRequest.getInstance().setShippingPostalCode("NA");
        PaymentRequest.getInstance().setShippingState("NA");
        PaymentRequest.getInstance().setShippingCountry("IND");
        PaymentRequest.getInstance().setShippingPhone("9078728771");
        // Billing Details
        PaymentRequest.getInstance().setBillingName(user);
        PaymentRequest.getInstance().setBillingEmail(email);
        PaymentRequest.getInstance().setBillingAddress("NA");
        PaymentRequest.getInstance().setBillingCity("NA");
        PaymentRequest.getInstance().setBillingPostalCode("NA");
        PaymentRequest.getInstance().setBillingState("NA");
        PaymentRequest.getInstance().setBillingCountry("IND");
        PaymentRequest.getInstance().setBillingPhone("9078728771");

        custom_post_parameters = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> hashpostvalues = new HashMap<String, String>();
        hashpostvalues.put("account_details", "saving");
        hashpostvalues.put("merchant_type", "gold");
        custom_post_parameters.add(hashpostvalues);

        PaymentRequest.getInstance()
                .setCustomPostValues(custom_post_parameters);

        EBSPayment.getInstance().init(this,MERCHANT_ID, SECRET_KEY, Config.Mode.ENV_TEST,
                Config.Encryption.ALGORITHM_SHA512, "zaafoo.in" );

    }

    @Override
    protected void onStart() {
        super.onStart();
        finalTotalAmount=getBookingData();

    }
}
