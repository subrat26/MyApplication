package com.zaafoohpl.zaafoo.myapplication;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.StringTokenizer;

import io.paperdb.Paper;

public class PaymentResponse extends AppCompatActivity {

    TextView payment_id,amount,result;
    String PaymentId,Amount;
    String MerchantRefNo;
    String PaymentStatus;
    ImageView result_response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_response);

        Typeface face= Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");

        payment_id=(TextView)findViewById(R.id.textView29);
        amount=(TextView)findViewById(R.id.textView31);
        result=(TextView)findViewById(R.id.textView4);
        result.setTypeface(face);
        result_response=(ImageView)findViewById(R.id.imageView8);
        Intent intent = getIntent();
        String payment = intent.getStringExtra("payment_response");
        try {
            JSONObject jObject=new JSONObject(payment);
            PaymentStatus = jObject.getString("PaymentStatus");
            PaymentId = jObject.getString("PaymentId");
            Amount = jObject.getString("Amount");
            // Response Page Logic
            if(PaymentStatus.equalsIgnoreCase("failed")){
                result_response.setImageResource(R.drawable.error);
                payment_id.setText(PaymentId);
                amount.setText("Rs." + Amount);
                result.setText("Oops..!! Payment Failed");
            }
            else {
                MerchantRefNo = jObject.getString("MerchantRefNo");
                sendTransactionSuccessData(MerchantRefNo, jObject.toString());
                payment_id.setText(PaymentId);
                amount.setText("Rs." + Amount);
                result.setText("Payment Successful");
                createNotification();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        Handler h=new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(PaymentResponse.this, GetLocation.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }, 5000);



    }

    private void sendTransactionSuccessData(String merchantRefNo, String s) {
        Paper.init(this);
        ZaafooUser user=Paper.book().read("user");
        AndroidNetworking.post("http://zaafoo.in/contranview/")
                .addBodyParameter("bookingid", merchantRefNo)
                .addBodyParameter("transactiondump", s)
                .addHeaders("Authorization","Token "+user.getToken())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Toast.makeText(PaymentResponse.this,response.toString(),Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }

    private void createNotification() {

        Paper.init(this);
        ZaafooUser user=Paper.book().read("user");

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setSmallIcon(R.drawable.zaafoo_logo);
            mBuilder.setContentTitle("Zaafoo Booking Confirmation");
            mBuilder.setContentText("Dear," + user.getName().toUpperCase() + ".\nThanks for booking from Zaafoo\n");
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(123, mBuilder.build());
        }
    }

