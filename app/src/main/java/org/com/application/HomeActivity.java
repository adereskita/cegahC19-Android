package org.com.application;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.com.application.Adapter.RecyclerViewAdapter;
import org.com.application.Model.PostModel;
import org.com.application.SessionManager.SessionManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
//    private static final String ipaddress = "192.168.100.30";
    private static final String ipaddressLaravel = "10.0.2.2:8000"; //berdasarkan emulator masing2
    private static final String URL_GET_POSTS = "http://"+ipaddressLaravel+"/api/posts";
    private static final String URL_GET_USER = "http://"+ipaddressLaravel+"/api/auth/user";

    public static final String URL_BASE_STORAGE = "http://"+ipaddressLaravel+"/storage/";

    private static String ACCESS_TOKEN;

    private static final String URL_GET_COVIDCASE = "https://api.kawalcorona.com/indonesia/";

    ArrayList<PostModel> data;
    RecyclerViewAdapter adapter;
    RecyclerView recyclerView;

    Button btn_form,btn_hospitalMap,btn_stepCount,btn_showArticle;
    TextView tv_CovidTotal, tv_tanggalNow, tv_nama, tv_ktp;
    BottomNavigationView bottomNavigation;

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent i = getIntent();
//        Bundle b = getIntent().getExtras();
//        int imgID = b.getInt("IMGSOURCE_EXTRA");
        String token = i.getStringExtra("ACCESS_TOKEN_EXTRA");

        // Session class instance
        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // token
        ACCESS_TOKEN = user.get(SessionManager.KEY_TOKEN);
        // email
        String email = user.get(SessionManager.KEY_EMAIL);

//        initialize all things here
        recyclerView = findViewById(R.id.recycler_Home);
        tv_CovidTotal = findViewById(R.id.tv_covidTotal);
        tv_tanggalNow = findViewById(R.id.tv_tanggalNow);
        tv_nama = findViewById(R.id.tv_nama);
        tv_ktp = findViewById(R.id.tv_nik_id);
        btn_form = findViewById(R.id.btn_inputData);
        btn_hospitalMap = findViewById(R.id.btn_hospital);
        btn_stepCount = findViewById(R.id.btn_stepCount);
        btn_showArticle = findViewById(R.id.btn_article);
        bottomNavigation = findViewById(R.id.bottomNavigationView);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_menu:
                        System.out.println("HOME");
                        return true;
                    case R.id.klinik_menu:
                        //TODO: ganti ke intent
                        System.out.println("klinik");
                        return true;
                    case R.id.profile_menu:
                        //TODO: ganti ke intent
                        System.out.println("PROFILE");
                        return true;
                }
                return false;
            }
        });

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        tv_tanggalNow.setText(formattedDate);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false){
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                // force height of viewHolder here, this will override layout_height from xml
                return super.checkLayoutParams(lp);
//                lp.height = getHeight() / 2;
//                return true;
            }
        });

        LoadCovidCase();
        LoadPostURL();
        LoadUser();
    }

    private void LoadPostURL(){
        data = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URL_GET_POSTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            for (int i = 0; i<jsonArray.length(); i++){
                                JSONObject obj = jsonArray.getJSONObject(i);

                                if (obj != null) {
                                    PostModel item = new PostModel(
                                            obj.getString("id").trim(),
                                            obj.getString("category_id"),
                                            obj.getString("title"),
                                            obj.getString("created_at"),
                                            obj.getString("image")
                                    );
                                    data.add(item);
                                }
                            }
                            adapter = new RecyclerViewAdapter(data,
                                    getApplicationContext());
                            recyclerView.setAdapter(adapter);
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
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void LoadCovidCase(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URL_GET_COVIDCASE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArr = new JSONArray(response);

                            for (int i = 0; i<jsonArr.length(); i++){
                                JSONObject obj = jsonArr.getJSONObject(i);

                                if (obj != null) {
                                   String totalCv19 = obj.getString("positif").trim();
                                    tv_CovidTotal.setText(totalCv19);
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
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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
                                    tv_nama.setText(obj.getString("name"));
                                    tv_ktp.setText(obj.getString("nik"));
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
