package com.mietrix.myqruum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;

public class PastVisited extends AppCompatActivity implements PastVisitedAdapter.OnItemClicked{

    ImageView imgGone,imgJobOff;
    TextView txtGone,txtJobOff;
    List<Log> logList;
    String userID;

    //the recyclerview
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_visited);
        recyclerView = findViewById(R.id.recylcerView);
        imgGone = findViewById(R.id.imageViewGone);
        txtGone = findViewById(R.id.textViewGone);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        userID = sharedPreferences.getString(Config.USER_ID2, "Not Available");


        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        //initializing the loglist

        logList = new ArrayList<>();

        loadLog();

    }

    public void loadLog() {
        final ProgressDialog loading = ProgressDialog.show(this, "Please Wait", "Contacting Server", false, false);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.URL_API + "loadpastvisit.php?userID=" + userID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting log object from json array
                                JSONObject log = array.getJSONObject(i);

                                //adding the log to log list
                                logList.add(new Log(
                                        log.getString("logID"),
                                        log.getString("enterDate"),
                                        log.getString("enterTime"),
                                        log.getString("exitDate"),
                                        log.getString("exitTime"),
                                        log.getString("placeID"),
                                        log.getString("placename"),
                                        log.getString("logStatus")
                                ));
                            }

                            //creating adapter object and setting it to recyclerview
                            PastVisitedAdapter adapter = new PastVisitedAdapter(getApplicationContext(), logList);
                            recyclerView.setAdapter(adapter);
                            adapter.setOnClick(PastVisited.this);

                            if (adapter.getItemCount() == 0) {
                                imgGone.setVisibility(View.VISIBLE);
                                txtGone.setVisibility(View.VISIBLE);
                            } else {

                                imgGone.setVisibility(View.GONE);
                                txtGone.setVisibility(View.GONE);
                            }

                            //add shared preference ID,nama,credit here
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
                            Toast.makeText(PastVisited.this, "No internet . Please check your connection",
                                    Toast.LENGTH_LONG).show();
                        } else {

                            Toast.makeText(PastVisited.this, error.toString(), Toast.LENGTH_LONG).show();
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

    @Override
    public void onItemClick(int position) {
        // The onClick implementation of the RecyclerView item click
        //ur intent code here
        Log log = logList.get(position);
        //Toast.makeText(FoodMenu.this, log.getLongdesc(),
        //      Toast.LENGTH_LONG).show();

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME,
                Context.MODE_PRIVATE);

        // Creating editor to store values to shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Adding values to editor

        editor.putString(Config.LOG_ID2, log.getLogID());
        editor.putString(Config.SCAN_DATE, log.getEnterDate());
        editor.putString(Config.SCAN_TIME, log.getEnterTime());
        editor.putString(Config.EXIT_DATE, log.getExitDate());
        editor.putString(Config.EXIT_TIME, log.getExitTime());
        editor.putString(Config.PLACE_ID, log.getPlaceID());
        editor.putString(Config.LOG_STATUS, log.getLogStatus());



        // Saving values to editor
        editor.commit();

        Intent i = new Intent(PastVisited.this, UserLocationDetails.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
    public void onBackPressed() {
        Intent i = new Intent(PastVisited.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}
