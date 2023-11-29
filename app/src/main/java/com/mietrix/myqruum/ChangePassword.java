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

public class ChangePassword extends AppCompatActivity {

    String userID;
    EditText currentPass,newPass,confirmNewPass;
    Button chgPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        userID = sharedPreferences.getString(Config.USER_ID2, "0");
        
        currentPass = findViewById(R.id.currPass);
        newPass = findViewById(R.id.newPass);
        confirmNewPass = findViewById(R.id.conNewPass);
        chgPass = findViewById(R.id.chgPassBtn2);
        

        chgPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String cpss = currentPass.getText().toString().trim();
                final String pss = newPass.getText().toString().trim();
                String conpss = confirmNewPass.getText().toString().trim();

                if (cpss.length() < 8) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter minimum 8 character for Password", Toast.LENGTH_LONG).show();
                } else if (pss.length() < 8) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter minimum 8 character for Password", Toast.LENGTH_LONG).show();
                } else if (!pss.equals(conpss)) {
                    Toast.makeText(getApplicationContext(),
                            "Your password does not match", Toast.LENGTH_LONG).show();
                } else {

                   AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChangePassword.this);
                    alertDialogBuilder.setTitle("Confirmation");
                    alertDialogBuilder.setMessage("Do you want to update your password?");

                    final Dialog dialog = new Dialog(ChangePassword.this);

                    alertDialogBuilder.setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    dialog.setCanceledOnTouchOutside(true);

                                    final ProgressDialog loading = ProgressDialog.show(ChangePassword.this, "Please Wait", "Contacting Server", false, false);

                                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                            Config.URL_API + "chgpassword.php", new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {

                                            loading.dismiss();

                                            if (response.contains("Success")) {

                                                Toast.makeText(ChangePassword.this, "Successfully updating.Please login again", Toast.LENGTH_LONG)
                                                        .show();

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


                                                Intent intent = new Intent(ChangePassword.this, LoginActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();


                                            } else {

                                                Toast.makeText(ChangePassword.this, "Sorry. Please try again", Toast.LENGTH_LONG)
                                                        .show();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            loading.dismiss();
                                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                                Toast.makeText(ChangePassword.this, "No internet . Please check your connection",
                                                        Toast.LENGTH_LONG).show();
                                            } else {

                                                Toast.makeText(ChangePassword.this, error.toString(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }) {
                                        @Override
                                        protected Map<String, String> getParams() {
                                            Map<String, String> params = new HashMap<String, String>();
                                            params.put("userID", userID);
                                            params.put("currentPass", cpss);
                                            params.put("newPass", pss);
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
    }    public void onBackPressed() {
        Intent i = new Intent(ChangePassword.this, Profile.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}
