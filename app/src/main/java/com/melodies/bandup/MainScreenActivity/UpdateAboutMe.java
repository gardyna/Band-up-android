package com.melodies.bandup.MainScreenActivity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.melodies.bandup.R;
import com.melodies.bandup.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateAboutMe extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateaboutme);
        userRequest();
    }

    // Request REAL user info from server
    public void userRequest() {
        JSONObject user = new JSONObject();
        try {
            user.put("userId", getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = "http://10.0.2.2:3000/get-user";//getResources().getString(R.string.api_address).concat("/get-user");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                user,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null) {
                            try {
                                // putting user description into EditText
                                EditText et = (EditText)findViewById(R.id.etAboutMe);
                                String s = response.getString("aboutme");
                                et.setText(s);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UpdateAboutMe.this, "Bad response: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );
        // insert request into queue
        VolleySingleton.getInstance(UpdateAboutMe.this).addToRequestQueue(jsonObjectRequest);
    }

    // Send user AboutMe data to server
    public void updateUser(String id, final String aboutMe) {
        JSONObject user = new JSONObject();
        try {
            user.put("userId", id);
            user.put("aboutMe", aboutMe);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = getResources().getString(R.string.api_address).concat("/edit-user");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                user,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(UpdateAboutMe.this, "Updated", Toast.LENGTH_SHORT).show();

                        Intent i = new Intent();
                        i.putExtra("MESSAGE", aboutMe);
                        setResult(2, i);
                        onBackPressed();
                        //finishActivity(2);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UpdateAboutMe.this, "Bad response: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );
        // insert request into queue
        VolleySingleton.getInstance(UpdateAboutMe.this).addToRequestQueue(jsonObjectRequest);
    }

    // get users essay and send it to server
    public void onClickSave(View view) throws JSONException {
        final EditText etAboutMe = (EditText)findViewById(R.id.etAboutMe);
        String about = etAboutMe.getText().toString();

        if (!about.isEmpty()) {
            updateUser(getUserId(), about);
        }
        else if (about.isEmpty()) {
            updateUser(getUserId(), " ");
        }
    }

    // get user ID
    public String getUserId() throws JSONException {
        SharedPreferences srdPref = UpdateAboutMe.this.getSharedPreferences("SessionIdData", Context.MODE_PRIVATE);
        String response = srdPref.getString("response", "N/A");
        JSONObject obj = new JSONObject(response);
        String id = obj.get("userID").toString();
        return (!id.equals("N/A")) ? id : "No data Found";
    }

    @Override
    public void onBackPressed() {
        finish(); // finishActivity(2);
    }
}
