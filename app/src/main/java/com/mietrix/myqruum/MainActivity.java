package com.mietrix.myqruum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    int requestCode;
    String userID,scanID,scanDate,scanTime,logID,logStatus,enterDate,enterTime,name;
    LinearLayout scanQR,pastVisited,profile;
    String curTime;
    SimpleDateFormat sdf;
    Calendar currTime;
    String currentDate;
    AudioManager audioManager;
    String subScan;
    TextView versionTXT,welcomeName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanQR = findViewById(R.id.scanQR);
        pastVisited = findViewById(R.id.pastVisited);
        profile = findViewById(R.id.profile);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        userID = sharedPreferences.getString(Config.USER_ID2, "0");
        name = sharedPreferences.getString(Config.NAME_ID2, "0");

        currTime = Calendar.getInstance();
        sdf = new SimpleDateFormat("hh:mm:ss a");
        currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        versionTXT = findViewById(R.id.versionTXT);
        welcomeName = findViewById(R.id.welcomeName);

        welcomeName.setText("Welcome Back, "+capitalize(name));

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;
            versionTXT.setText("Version "+versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
/*
        if(EasyPermissions.hasPermissions(MainActivity.this, Manifest.permission.ACCESS_NOTIFICATION_POLICY)){

            audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
// changing to silent mode
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

        }else{
            EasyPermissions.requestPermissions(MainActivity.this, "No Permission", 11, Manifest.permission.ACCESS_NOTIFICATION_POLICY);
        }
*/
        pastVisited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, PastVisited.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Profile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {

                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, requestCode);
                } else {

                    startQRScanner();
                }

            }
        });
    }
    private void startQRScanner() {
        new IntentIntegrator(this).initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(MainActivity.this, "Scan Cancelled", Toast.LENGTH_LONG).show();
            } else {

                scanID = result.getContents();
                scanID.trim();

                checkLog();
            }
        }
    }

    public void checkLog(){
            final ProgressDialog loading = ProgressDialog.show(this,"Please Wait","Contacting Server",false,false);

            if (scanID.contains("https://myqr.qniti.com/webregister.php?placeID=")) {

            scanID =scanID.substring(scanID.indexOf("placeID=") + 8);

            }
            //Toast.makeText(MainActivity.this, scanID, Toast.LENGTH_LONG).show();

            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    Config.URL_API+"checklog.php?userID="+userID+"&placeID="+scanID, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    loading.dismiss();

                    if ("Not Exist".equalsIgnoreCase(response)) {

                        Toast.makeText(MainActivity.this,
                                "QR Code not exist",
                                Toast.LENGTH_LONG).show();

                    }else if("maximum".equalsIgnoreCase(response)){

                        Toast.makeText(MainActivity.this,
                                "Maximum user inside. Please wait for another user to checkout before trying again",
                                Toast.LENGTH_LONG).show();

                    }else if("New".equalsIgnoreCase(response)){

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

                                //add shared preference ID,nama,credit here
                                SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME,
                                        Context.MODE_PRIVATE);

                                // Creating editor to store values to shared preferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                // Adding values to editor
                                editor.putString(Config.PLACE_ID, scanID);
                                editor.putString(Config.SCAN_DATE, currentDate);
                                editor.putString(Config.SCAN_TIME, curTime);
                                editor.putString(Config.LOG_STATUS, "New");
                                editor.putString(Config.LOG_ID2, "New Entry");
                                editor.putString(Config.EXIT_DATE, "");
                                editor.putString(Config.EXIT_TIME, "");

                                // Saving values to editor
                                editor.commit();

                                loading.dismiss();

                                //Starting profile activity
                                Intent intent = new Intent(MainActivity.this, UserLocationDetails.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                    } catch (Settings.SettingNotFoundException e) {
                            e.printStackTrace();}
                        } else {

                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject user = array.getJSONObject(i);

                                //adding the product to product list
                                logID = user.getString("logID");
                                enterDate = user.getString("enterDate");
                                enterTime = user.getString("enterTime");
                                logStatus = user.getString("logStatus");
                            }

                            //add shared preference ID,nama,credit here
                            SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME,
                                    Context.MODE_PRIVATE);

                            // Creating editor to store values to shared preferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            // Adding values to editor

                            editor.putString(Config.PLACE_ID, scanID);
                            editor.putString(Config.LOG_ID2, logID);
                            editor.putString(Config.SCAN_DATE, enterDate);
                            editor.putString(Config.SCAN_TIME, enterTime);
                            editor.putString(Config.LOG_STATUS, logStatus);


                            // Saving values to editor
                            editor.commit();

                            loading.dismiss();

                            //Starting profile activity
                            Intent intent = new Intent(MainActivity.this, UserLocationDetails.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }}
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            loading.dismiss();
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(MainActivity.this,"No internet . Please check your connection",
                                        Toast.LENGTH_LONG).show();
                            }
                            else{

                                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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
    //Auto Capitalize First Character of Each Word
    private String capitalize(String capString){
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z-éá])([a-z-éá]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()){
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        System.exit(0);
    }
    }
