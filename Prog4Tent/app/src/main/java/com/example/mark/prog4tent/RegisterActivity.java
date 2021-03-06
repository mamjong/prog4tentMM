package com.example.mark.prog4tent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private TextView usernameTextView, passwordTextView, confirmTextView;
    private Button registerButton;
    public static final String PREFS_NAME_TOKEN = "Prefsfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameTextView = (TextView) findViewById(R.id.register_username_tv);
        passwordTextView = (TextView) findViewById(R.id.register_password_tv);
        confirmTextView = (TextView) findViewById(R.id.register_confirm_tv);

        registerButton = (Button) findViewById(R.id.register_button_btn);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(confirmTextView.getText().toString().equals("") || confirmTextView.getText().toString().equals("") || confirmTextView.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }else{
                    Log.i("TV CON", confirmTextView.getText().toString());
                    Log.i("TV PAS", passwordTextView.getText().toString());
                    if (confirmTextView.getText().toString().equals(passwordTextView.getText().toString())) {
                        volleyRegister(usernameTextView.getText().toString(), passwordTextView.getText().toString());


                    } else {
                        Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    public void volleyRegister(String un, String pw){

        final ProgressDialog dialog = ProgressDialog.show(RegisterActivity.this, "",
                "Registreren. Please wait...", true);


        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME_TOKEN, Context.MODE_PRIVATE);
        String ipTemp = "";

        if (sharedPreferences.getInt("USEIP", 0) == 0) {
            ipTemp = sharedPreferences.getString("IPLOCAL", "no ip");
        }else if(sharedPreferences.getInt("USEIP", 0) == 1) {
            ipTemp = sharedPreferences.getString("IPHEROKU", "no ip");
        }


        final String ipFinal = ipTemp;
        final String username = un;
        final String password = pw;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = "http:/" + ipFinal + "/api/v1/register";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.cancel();
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.cancel();
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                        Log.e("TEMP", "Something went wrong");
                    }
                }) {

            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            public byte[] getBody() throws AuthFailureError {
                String mContent = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
                byte[] body = new byte[0];
                try {
                    body = mContent.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return body;
            }
        };

        requestQueue.add(stringRequest);
    }

}
