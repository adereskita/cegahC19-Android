package org.com.application;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FormGejalaActivity extends AppCompatActivity {
    private static final String ipaddressLaravel = "10.0.2.2:8000";
    private static final String URL_GET_USER = "http://"+ipaddressLaravel+"/api/auth/user";

    private static String ACCESS_TOKEN;



    EditText et_name, et_age, et_nik, et_telfo,et_address;
    Spinner sp_gender;
    Button btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_gejala);


    //initial variable
        et_name = findViewById(R.id.edt_nama_reg);
        et_age = findViewById(R.id.edt_age);
        et_nik = findViewById(R.id.edt_nik);
        et_telfo = findViewById(R.id.edt_no_telfon);
        et_address = findViewById(R.id.edt_alamat);

    }


    private void LoadUser(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URL_GET_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            for (int i = 0; i<jsonArray.length(); i++){
                                JSONObject obj = jsonArray.getJSONObject(i);

                                if (obj != null) {
                                    et_name.setText(obj.getString("name"));
                                    et_nik.setText(obj.getString("nik"));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                headers.put("Authorization", "Bearer " + ACCESS_TOKEN);
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}