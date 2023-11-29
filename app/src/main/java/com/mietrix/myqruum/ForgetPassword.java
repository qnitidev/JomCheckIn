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

public class ForgetPassword extends AppCompatActivity {

    EditText userEmail;
    Button forgetPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        userEmail = findViewById(R.id.userEmail);
        forgetPass = findViewById(R.id.chgPassForget);



        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String em = userEmail.getText().toString().trim();

                if (em.length() < 5) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter proper email address", Toast.LENGTH_LONG).show();
                }

                else {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ForgetPassword.this);
                    alertDialogBuilder.setTitle("Confirmation");
                    alertDialogBuilder.setMessage("Do you want retrieve your password?");

                    final Dialog dialog = new Dialog(ForgetPassword.this);

                    alertDialogBuilder.setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    dialog.setCanceledOnTouchOutside(true);

                                    final ProgressDialog loading = ProgressDialog.show(ForgetPassword.this, "Please Wait", "Contacting Server", false, false);

                                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                            "https://myqr.qniti.com/forgotpass.php", new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {

                                            loading.dismiss();

                                            if (response.contains("Not Exist")) {

                                                Toast.makeText(ForgetPassword.this,
                                                        "Email not exist",
                                                        Toast.LENGTH_LONG).show();

                                            }else if (response.contains("Message sent")) {

                                                Toast.makeText(ForgetPassword.this, "Successfully sent. Please check your email", Toast.LENGTH_LONG)
                                                        .show();

                                                Intent i = new Intent(ForgetPassword.this, LoginActivity.class);
                                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(i);
                                                finish();

                                            } else {

                                                Toast.makeText(ForgetPassword.this, "Error. Please try again later", Toast.LENGTH_LONG)
                                                        .show();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            loading.dismiss();
                                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                                Toast.makeText(ForgetPassword.this, "No internet . Please check your connection",
                                                        Toast.LENGTH_LONG).show();
                                            } else {

                                                Toast.makeText(ForgetPassword.this, error.toString(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }) {
                                        @Override
                                        protected Map<String, String> getParams() {
                                            Map<String, String> params = new HashMap<String, String>();
                                            params.put("useremail", em);
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
        Intent i = new Intent(ForgetPassword.this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}