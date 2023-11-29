package com.mietrix.myqruum;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UserLocationDetails extends AppCompatActivity {

    String userID,name,phone,email,logid,enterDate,enterTime,exitDate,exitTime,logStatus,placeID,placeName,placePhone,placeAddress;
    TextView nametxt,phonetxt,emailtxt,logidtxt,enterdatetime,exitdatetime,logstatustxt,placenametxt,placephonetxt,placeaddresstxt;
    Button enter,exit;
    String curTime;
    SimpleDateFormat sdf;
    Calendar currTime;
    String currentDate;
    MediaPlayer mediaPlayer;
    ImageView tick;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location_details);

        currTime = Calendar.getInstance();
        sdf = new SimpleDateFormat("hh:mm:ss a");
        currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        nametxt = findViewById(R.id.userNametxt);
        phonetxt = findViewById(R.id.phoneNumtxt);
        emailtxt = findViewById(R.id.userEmailtxt);
        logidtxt = findViewById(R.id.logIDtxt);
        enterdatetime = findViewById(R.id.enterdateNtime);
        exitdatetime = findViewById(R.id.exitdatentime);
        logstatustxt = findViewById(R.id.logStatus);
        placenametxt = findViewById(R.id.placeName);
        placephonetxt = findViewById(R.id.placephone);
        placeaddresstxt = findViewById(R.id.placeaddress);
        enter = findViewById(R.id.enterBtn);
        exit = findViewById(R.id.exitBtn);
        tick = findViewById(R.id.tick);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        userID = sharedPreferences.getString(Config.USER_ID2, "0");
        name = sharedPreferences.getString(Config.NAME_ID2, "0");
        phone= sharedPreferences.getString(Config.PHONE_ID2, "0");
        email = sharedPreferences.getString(Config.EMAIL_ID2, "0");
        logid = sharedPreferences.getString(Config.LOG_ID2, "New Entry");
        enterDate = sharedPreferences.getString(Config.SCAN_DATE, "0");
        enterTime = sharedPreferences.getString(Config.SCAN_TIME, "0");
        exitDate = sharedPreferences.getString(Config.EXIT_DATE, "");
        exitTime = sharedPreferences.getString(Config.EXIT_TIME, "");
        logStatus = sharedPreferences.getString(Config.LOG_STATUS, "New Entry");
        placeID = sharedPreferences.getString(Config.PLACE_ID, "0");

        nametxt.setText(name);
        phonetxt.setText(phone);
        emailtxt.setText(email);
        logidtxt.setText(logid);
        enterdatetime.setText(enterDate+" "+enterTime);
        exitdatetime.setText(exitDate+" "+exitTime);
        logstatustxt.setText(logStatus);

        //Toast.makeText(UserLocationDetails.this,placeID,Toast.LENGTH_LONG).show();

        if("Not Exist".equalsIgnoreCase(logStatus)){


        }else if ("New".equalsIgnoreCase(logStatus)){
            loadPlaces();
            exit.setVisibility(View.GONE);
            enter.setVisibility(View.GONE);
            tick.setVisibility(View.GONE);
            logstatustxt.setTextColor(getResources().getColor(R.color.colorDeepBlue));
            enterPlaces();

        }else if ("Inside".equalsIgnoreCase(logStatus)){
            enter.setVisibility(View.GONE);
            exit.setVisibility(View.VISIBLE);
            tick.setVisibility(View.VISIBLE);
            logstatustxt.setTextColor(getResources().getColor(R.color.colorLightGreen));
            loadPlaces();
        }else{
            enter.setVisibility(View.GONE);
            exit.setVisibility(View.GONE);
            tick.setVisibility(View.GONE);
            logstatustxt.setTextColor(getResources().getColor(R.color.red));
            loadPlaces();
        }

       // enter.setOnClickListener(new View.OnClickListener() {
           // @Override
           // public void onClick(View v) {

               /* AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UserLocationDetails.this);
                alertDialogBuilder.setTitle("Confirmation");
                alertDialogBuilder.setMessage("Do you want to enter the area?");

                final Dialog dialog = new Dialog(UserLocationDetails.this);

                alertDialogBuilder.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                dialog.setCanceledOnTouchOutside(true);
*/
                              /*  final ProgressDialog loading = ProgressDialog.show(UserLocationDetails.this,"Please Wait","Contacting Server",false,false);

                                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                        Config.URL_API+"enterlog.php", new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        loading.dismiss();

                                        if(response.contains("Success")){

                                            mediaPlayer = MediaPlayer.create(UserLocationDetails.this, R.raw.checkin);
                                            mediaPlayer.start();

                                            Toast.makeText(UserLocationDetails.this, "Successfully enter", Toast.LENGTH_LONG)
                                                    .show();

                                            Intent intent = new Intent(UserLocationDetails.this, PastVisited.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();

                                        }
                                        else if(response.contains("Exist")) {

                                            Toast.makeText(UserLocationDetails.this, "Sorry. You already enter the area.Please scan leave before entering again", Toast.LENGTH_LONG)
                                                    .show();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        loading.dismiss();
                                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                            Toast.makeText(UserLocationDetails.this,"No internet . Please check your connection",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                        else{

                                            Toast.makeText(UserLocationDetails.this, error.toString(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("userID", userID);
                                        params.put("placeID", placeID);
                                        params.put("enterDate", enterDate);
                                        params.put("enterTime", enterTime);
                                        return params;
                                    }

                                };

                                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                        30000,
                                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                requestQueue.add(stringRequest);

                            }

                        });
/*
                alertDialogBuilder.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                dialog.setCanceledOnTouchOutside(true);

                            }
                        });
                alertDialogBuilder.setOnCancelListener(
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {

                            }
                        }
                );

                //Showing the alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        }); */

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME) == 0) {

                        Toast.makeText(getApplicationContext(),
                                "Please set Automatic Date & Time to ON in the Settings",
                                Toast.LENGTH_LONG).show();

                        startActivityForResult(
                                new Intent(Settings.ACTION_DATE_SETTINGS), 0);
                    } else if (Settings.Global.getInt(getContentResolver(),
                            Settings.Global.AUTO_TIME_ZONE) == 0) {

                        Toast.makeText(getApplicationContext(),
                                "Please set Automatic Time Zone to ON in the Settings",
                                Toast.LENGTH_LONG).show();

                        startActivityForResult(
                                new Intent(Settings.ACTION_DATE_SETTINGS), 0);
                    }else {

                        curTime = sdf.format(currTime.getTime());

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UserLocationDetails.this);
                        alertDialogBuilder.setTitle("Confirmation");
                        alertDialogBuilder.setMessage("Do you want to leave the area?");

                        final Dialog dialog = new Dialog(UserLocationDetails.this);

                        alertDialogBuilder.setPositiveButton("YES",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        dialog.setCanceledOnTouchOutside(true);

                                        final ProgressDialog loading = ProgressDialog.show(UserLocationDetails.this, "Please Wait", "Contacting Server", false, false);

                                        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                                Config.URL_API + "exitlog.php", new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                                loading.dismiss();

                                                if (response.contains("Success")) {

                                                    mediaPlayer = MediaPlayer.create(UserLocationDetails.this, R.raw.checkout);
                                                    mediaPlayer.start();

                                                    Toast.makeText(UserLocationDetails.this, "Successfully checking out. Thank you", Toast.LENGTH_LONG)
                                                            .show();

                                                    Intent intent = new Intent(UserLocationDetails.this, PastVisited.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();


                                                } else if (response.contains("Exist")) {

                                                    Toast.makeText(UserLocationDetails.this, "Sorry. You already enter the area.Please scan leave before entering again", Toast.LENGTH_LONG)
                                                            .show();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                loading.dismiss();
                                                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                                    Toast.makeText(UserLocationDetails.this, "No internet . Please check your connection",
                                                            Toast.LENGTH_LONG).show();
                                                } else {

                                                    Toast.makeText(UserLocationDetails.this, error.toString(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }) {
                                            @Override
                                            protected Map<String, String> getParams() {
                                                Map<String, String> params = new HashMap<String, String>();
                                                params.put("logID", logid);
                                                params.put("exitDate", currentDate);
                                                params.put("exitTime", curTime);
                                                return params;
                                            }

                                        };

                                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                                30000,
                                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                        requestQueue.add(stringRequest);

                                    }

                                });

                        alertDialogBuilder.setNegativeButton("NO",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        dialog.setCanceledOnTouchOutside(true);

                                    }
                                });
                        alertDialogBuilder.setOnCancelListener(
                                new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {

                                    }
                                }
                        );

                        //Showing the alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
            } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }}
            });
    }
    public void enterPlaces(){

        final ProgressDialog loading = ProgressDialog.show(UserLocationDetails.this,"Please Wait","Contacting Server",false,false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Config.URL_API+"enterlog.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                loading.dismiss();

                if(response.contains("Success")){

                    mediaPlayer = MediaPlayer.create(UserLocationDetails.this, R.raw.checkin);
                    mediaPlayer.start();

                    Toast.makeText(UserLocationDetails.this, "Successfully checking in", Toast.LENGTH_LONG)
                            .show();

                    Intent intent = new Intent(UserLocationDetails.this, PastVisited.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                }
                else if(response.contains("Exist")) {

                    Toast.makeText(UserLocationDetails.this, "Sorry. You already enter the area.Please scan leave before entering again", Toast.LENGTH_LONG)
                            .show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(UserLocationDetails.this,"No internet . Please check your connection",
                            Toast.LENGTH_LONG).show();
                }
                else{

                    Toast.makeText(UserLocationDetails.this, error.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userID", userID);
                params.put("placeID", placeID);
                params.put("enterDate", enterDate);
                params.put("enterTime", enterTime);
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }


    public void loadPlaces(){

        final ProgressDialog loading = ProgressDialog.show(this,"Please Wait","Contacting Server",false,false);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.URL_API+"loadplace.php?placeID="+placeID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject user = array.getJSONObject(i);

                                //adding the product to product list
                                placeName = user.getString("placename");
                                placePhone = user.getString("placephone");
                                placeAddress = user.getString("placeaddr");
                            }

                            placenametxt.setText("Name   : "+placeName);
                            placephonetxt.setText("Phone   : "+placePhone);
                            placeaddresstxt.setText("Address: "+placeAddress);

                            loading.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(UserLocationDetails.this,"No internet . Please check your connection",
                                    Toast.LENGTH_LONG).show();
                        }
                        else{

                            Toast.makeText(UserLocationDetails.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //adding our stringrequest to queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    public void onBackPressed() {
        Intent i = new Intent(UserLocationDetails.this, PastVisited.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}
