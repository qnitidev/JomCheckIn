package com.mietrix.myqruum;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    //boolean variable to check user is logged in or not
    //initially it is false
    private boolean loggedIn = false;

    String userEmailID;
    String passwordP;
    private EditText inputICnum;
    private EditText inputPassword;
    String userID;
    String nameID;
    String phoneID;
    String emailID;
    //String addressID;
    ImageButton about;
    CheckBox dontShowAgain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale(getResources().getConfiguration());
        setContentView(R.layout.activity_login);
        getWindow().setBackgroundDrawableResource(R.mipmap.bg2);

        inputICnum = findViewById(R.id.icnum);
        inputPassword = findViewById(R.id.password);
        about = findViewById(R.id.aboutBtn);

        Button btnLogin = findViewById(R.id.btnLogin);
        Button toRegister = findViewById(R.id.btntoRegister);
        Button toForgot = findViewById(R.id.btntoForget);
        // Button btnLinkToRegister =(Button)findViewById(R.id.btnLinkToRegisterScreen);
        //Button tos =(Button)findViewById(R.id.tos);
        // ImageButton exit =(ImageButton)findViewById(R.id.exit);

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        LayoutInflater adbInflater = LayoutInflater.from(this);
        View eulaLayout = adbInflater.inflate(R.layout.create_checkbox, null);
        SharedPreferences settings = getSharedPreferences(Config.SHARED_PREF_NAME, 0);
        String skipMessage = settings.getString("skipMessage", "NOT checked");

        dontShowAgain = (CheckBox) eulaLayout.findViewById(R.id.skip);
        adb.setView(eulaLayout);
        adb.setCancelable(false);
        adb.setMessage(Html.fromHtml("For <b>NEW</b> user, please create a <b>Jom Check-In</b> account before login" +
                "<br><br>" +
                "<b>Your current UUM ID and password will not work with this application</b>"));

        adb.setPositiveButton("I Understand", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String checkBoxResult = "NOT checked";

                if (dontShowAgain.isChecked()) {
                    checkBoxResult = "checked";
                }

                SharedPreferences settings = getSharedPreferences(Config.SHARED_PREF_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();

                editor.putString("skipMessage", checkBoxResult);
                editor.commit();

                // Do what you want to do on "OK" action

                return;
            }
        });

        if (!skipMessage.equals("checked")) {
            adb.show();
        }

       about.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
               alertDialogBuilder.setMessage(Html.fromHtml("<b>Jom Check-In</b> is a mobile application used to help track movement of individuals " +
                       "within a specific location conveniently without filling up hard copy forms or online forms " +
                       "during Covid-19 pandemic.  For more information, please contact us.<br>" +
                       "<br>" +
                       "<b>Jom Check-In Admin</b><br>" +
                       "<b>Email: shahrulazmi1972@gmail.com</b><br>" +
                       "<b>Phone: 0134808554</b>"));

               final Dialog dialog = new Dialog(LoginActivity.this);

               alertDialogBuilder.setPositiveButton("I Understand",
                       new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface arg0, int arg1) {
                               dialog.setCanceledOnTouchOutside(true);
                               dialog.cancel();

                           }

                       });


               alertDialogBuilder.setOnCancelListener(
                       new DialogInterface.OnCancelListener() {
                           @Override
                           public void onCancel(DialogInterface dialog) {
                            dialog.cancel();
                           }
                       }
               );

               //Showing the alert dialog
               AlertDialog alertDialog = alertDialogBuilder.create();
               alertDialog.show();
           }
       });

        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }
        });

        toForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this, ForgetPassword.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }
        });

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                userEmailID = inputICnum.getText().toString().trim();
                passwordP = inputPassword.getText().toString().trim();
                // Check for empty data in the form
                if (!userEmailID.isEmpty() && !passwordP.isEmpty()) {
                    // login user
                    checkLogin();

                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),"Please enter your information",
                            Toast.LENGTH_LONG).show();
                }
            }
        });


        // Link to Register Screen
       /* btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Register.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });
        tos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent enter = new Intent (MainActivity.this, Notice.class);
                startActivity(enter);
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });*/
    }    @Override
    protected void onResume() {
        super.onResume();
        //In onresume fetching value from sharedpreference
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        //Fetching the boolean value form sharedpreferences
        loggedIn = sharedPreferences.getBoolean(Config.LOGGEDIN_SHARED_PREF, false);

        //If we will get true
        if(loggedIn){
            //We will start the Profile Activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void checkLogin(){
        //Getting values from edit texts
        final ProgressDialog loading = ProgressDialog.show(this,"Please Wait","Contacting Server",false,false);
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_API+"login.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        //If we are getting success from server
                        if(response.equalsIgnoreCase("success")){
                            //Creating a shared preference
                            SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

                            //Creating editor to store values to shared preferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            //Adding values to editor
                            editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, true);
                            editor.putString(Config.ID_SHARED_PREF, userEmailID);

                            //Saving values to editor
                            editor.commit();

                            loadUser();

                        }else{
                            //If the server response is not success
                            //Displaying an error message on toast
                            loading.dismiss();
                            Toast.makeText(LoginActivity.this, "Invalid ID or password", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        loading.dismiss();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(LoginActivity.this,"No internet . Please check your connection",
                                    Toast.LENGTH_LONG).show();
                        }
                        else{

                            Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }){


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                //Adding parameters to request
                params.put("userEmail", userEmailID);
                params.put(Config.KEY_PASSWORD, passwordP);

                //returning parameter
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void loadUser(){
        final ProgressDialog loading = ProgressDialog.show(this,"Please Wait","Contacting Server",false,false);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.URL_API+"loaduser.php?userEmail="+userEmailID,
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
                                userID = user.getString("userID");
                                nameID = user.getString("username");
                                //addressID = user.getString("useraddr");
                                phoneID = user.getString("userphone");
                                emailID = user.getString("useremail");
                            }

                            //add shared preference ID,nama,credit here
                            SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME,
                                    Context.MODE_PRIVATE);

                            // Creating editor to store values to shared preferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            // Adding values to editor

                            editor.putString(Config.USER_ID2, userID);
                            editor.putString(Config.NAME_ID2, nameID);
                            editor.putString(Config.PHONE_ID2, phoneID);
                            editor.putString(Config.EMAIL_ID2, emailID);
                            //editor.putString(Config.ADDRESS_ID2, addressID);


                            // Saving values to editor
                            editor.commit();

                            loading.dismiss();

                            //Starting profile activity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

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
                            Toast.makeText(LoginActivity.this,"No internet . Please check your connection",
                                    Toast.LENGTH_LONG).show();
                        }
                        else{

                            Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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

    public void adjustFontScale(Configuration configuration)
    {
        configuration.fontScale = (float) 1.0;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        getBaseContext().getResources().updateConfiguration(configuration, metrics);
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
