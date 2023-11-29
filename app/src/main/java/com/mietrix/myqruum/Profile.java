package com.mietrix.myqruum;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    String userID,name,phone,email;
    TextView showname;
    EditText chgphone,chgemail,chgaddr;
    Button logout,toChgPass,editProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        userID = sharedPreferences.getString(Config.USER_ID2, "0");
        name = sharedPreferences.getString(Config.NAME_ID2, "0");
        phone= sharedPreferences.getString(Config.PHONE_ID2, "0");
        email = sharedPreferences.getString(Config.EMAIL_ID2, "0");
        //address = sharedPreferences.getString(Config.ADDRESS_ID2, "0");

        showname = findViewById(R.id.username);
        chgphone = findViewById(R.id.chgphone);
        chgemail = findViewById(R.id.chgemail);
        //chgaddr = findViewById(R.id.chgaddress);
        logout = findViewById(R.id.logoutBtn);
        toChgPass = findViewById(R.id.chgpassBtn);
        editProfile = findViewById(R.id.saveBtn);

        showname.setText(name);
        chgphone.setText(phone);
        chgemail.setText(email);
        //chgaddr.setText(address);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Creating an alert dialog to confirm logout
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(Profile.this);
                alertDialogBuilder.setMessage("Do you want to logout?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                                //Getting out sharedpreferences
                                SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                                //Getting editor
                                SharedPreferences.Editor editor = preferences.edit();

                                //Puting the value false for loggedin
                                editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);

                                //Putting blank value to email
                                editor.putString(Config.ID_SHARED_PREF, "");

                                //Saving the sharedpreferences
                                editor.clear();
                                editor.commit();

                                //Starting login activity
                                Intent intent = new Intent(Profile.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });

                alertDialogBuilder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                            }
                        });

                //Showing the alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }

        });

        toChgPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Profile.this, ChangePassword.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                phone = chgphone.getText().toString().trim();
                email = chgemail.getText().toString().trim();
                //address = chgaddr.getText().toString().trim();

                if (email.length() < 8) {
                    Toast.makeText(getApplicationContext(), "Please enter proper email address",
                            Toast.LENGTH_LONG).show();
                } else if (phone.length() < 10) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter proper phone number", Toast.LENGTH_LONG).show();
                } /*else if (address.length() < 10) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter proper address", Toast.LENGTH_LONG).show();
                } */else {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Profile.this);
                    alertDialogBuilder.setTitle("Confirmation");
                    alertDialogBuilder.setMessage("Do you want to update your profile?");

                    final Dialog dialog = new Dialog(Profile.this);

                    alertDialogBuilder.setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    dialog.setCanceledOnTouchOutside(true);

                                    final ProgressDialog loading = ProgressDialog.show(Profile.this, "Please Wait", "Contacting Server", false, false);

                                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                            Config.URL_API + "chgprofile.php", new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {

                                            loading.dismiss();

                                            if (response.contains("Success")) {

                                                Toast.makeText(Profile.this, "Successfully updating", Toast.LENGTH_LONG)
                                                        .show();

                                                //add shared preference ID,nama,credit here
                                                SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME,
                                                        Context.MODE_PRIVATE);

                                                // Creating editor to store values to shared preferences
                                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                                // Adding values to editor
                                                editor.putString(Config.PHONE_ID2, phone);
                                                editor.putString(Config.EMAIL_ID2, email);
                                                //editor.putString(Config.ADDRESS_ID2, address);

                                                // Saving values to editor
                                                editor.commit();

                                               /* Intent intent = new Intent(Profile.this, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();*/

                                               recreate();


                                            } else {

                                                Toast.makeText(Profile.this, "Sorry. Please try again", Toast.LENGTH_LONG)
                                                        .show();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            loading.dismiss();
                                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                                Toast.makeText(Profile.this, "No internet . Please check your connection",
                                                        Toast.LENGTH_LONG).show();
                                            } else {

                                                Toast.makeText(Profile.this, error.toString(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }) {
                                        @Override
                                        protected Map<String, String> getParams() {
                                            Map<String, String> params = new HashMap<String, String>();
                                            params.put("userID", userID);
                                            params.put("userphone", phone);
                                            params.put("useremail", email);
                                           // params.put("useraddr", address);
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
            }
        });
    }
    public void onBackPressed() {
        Intent i = new Intent(Profile.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}
