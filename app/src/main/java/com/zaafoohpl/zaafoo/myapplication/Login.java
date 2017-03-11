package com.zaafoohpl.zaafoo.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.roger.catloadinglibrary.CatLoadingView;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

import io.paperdb.Paper;

public class Login extends AppCompatActivity {


    Button login;
    LoginButton loginButton;
    CallbackManager callbackManager;

    EditText et1,et2;
    private CatLoadingView cat_view,cat;
    final Context c = this;
    SessionManagement sm;
    ZaafooUser user;
    ArrayList<String> fbPermissions;


    ProfileTracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Paper.init(this);
        FacebookSdk.sdkInitialize(this);
        user=Paper.book().read("user",new ZaafooUser());

        fbPermissions=new ArrayList<>();
        fbPermissions.add("public_profile");
        fbPermissions.add("email");


        tracker=new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                tracker.stopTracking();
                if(currentProfile!=null) {
                    Profile profile = currentProfile;
                    Paper.book().write("image", profile.getProfilePictureUri(120, 120).toString());
                }
            }
        };


        // Check if use data already exists
        if(user.getToken()!=null){
            startActivity(new Intent(Login.this,GetLocation.class));
            finish();
        }

        AndroidNetworking.initialize(this);
        cat_view=new CatLoadingView();
        cat_view.setCancelable(false);

        callbackManager=CallbackManager.Factory.create();

        login=(Button)findViewById(R.id.login);
        et1=(EditText)findViewById(R.id.username_login);
        et2=(EditText)findViewById(R.id.pass_login);
        loginButton=(LoginButton)findViewById(R.id.login_button);
        // FB LOGIN CODE
        loginButton.setReadPermissions(fbPermissions);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                tracker.startTracking();
                AccessToken accessToken=loginResult.getAccessToken();
                Log.e("token",accessToken.getToken());
                getZaafooToken(accessToken.getToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(Login.this,"Login Cancelled By User",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(Login.this,error.toString(),Toast.LENGTH_SHORT).show();
            }
        });




    }

    private void getZaafooToken(String accessToken) {


        AndroidNetworking.post("http://zaafoo.in/facebook-signup/")
                .addBodyParameter("access_token", accessToken)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("fb",response.toString());

                            String name = response.getString("username");
                            String token=response.getString("token");
                            user.setToken(token);
                            user.setName(name);
                            Paper.book().write("user",user);
                            startActivity(new Intent(Login.this,GetLocation.class));
                            finish();
                        } catch (JSONException e) {
                           Log.e("json fb",e.getMessage());
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        Log.e("fb",error.getErrorBody());

                    }
                });


    }


    public void loginUser(View v) {

        cat_view.show(getSupportFragmentManager(), "");
        AndroidNetworking.post("http://zaafoo.in/loginrest/")
                .addBodyParameter("user", et1.getText().toString())
                .addBodyParameter("password", et2.getText().toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        sm=new SessionManagement(c);
                        try {
                            user.setToken(response.getString("token"));
                            user.setName(et1.getText().toString());
                            user.setPassword(et2.getText().toString());
                            Paper.book().write("user",user);
                            Paper.book().write("image","no");
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        cat_view.dismiss();
                        startActivity(new Intent(Login.this,GetLocation.class));


                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        cat_view.dismiss();
                        Toast.makeText(Login.this,"Invalid Credentials",Toast.LENGTH_SHORT).show();
                    }
                });

    }

    // Change Password
    public void changePassword(View v){

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);
        View mView = layoutInflaterAndroid.inflate(R.layout.alert_dialog_input, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
        alertDialogBuilderUserInput.setView(mView);

        final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.alert_email_input);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        // ToDo get user input here
                        sendEmailForgotPass(userInputDialogEditText.getText().toString());
                    }
                })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }



    public void sendEmailForgotPass(String email){

        cat=new CatLoadingView();
        cat.setCancelable(false);
        cat.show(getSupportFragmentManager(),"");
        AndroidNetworking.post("http://zaafoo.in/forgotpasswordview/")
                .addBodyParameter("email",email)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                    cat.dismiss();
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        cat.dismiss();
                        Toast.makeText(Login.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }
}


