package org.com.application;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import org.com.application.API.InterfaceAPI;
import org.com.application.API.RetrofitClient;
import org.com.application.Adapter.RecyclerViewAdapter;
import org.com.application.Model.PostModel;
import org.com.application.SessionManager.SessionManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class HomeActivity extends AppCompatActivity {
//    private static final String ipaddress = "192.168.100.30";
    private static final String ipaddressLaravel = "10.0.2.2:8000";
    private static final String URL_GET_POSTS = "http://"+ipaddressLaravel+"/api/posts";
    private static final String URL_GET_USER = "http://"+ipaddressLaravel+"/api/auth/user";

    private static String ACCESS_TOKEN;

    private static final String URL_GET_COVIDCASE = "https://api.kawalcorona.com/indonesia/";

    ArrayList<PostModel> data;
    RecyclerViewAdapter adapter;
    RecyclerView recyclerView;

    Button btn_form,btn_hospitalMap,btn_stepCount,btn_showArticle;
    TextView tv_CovidTotal, tv_tanggalNow, tv_nama, tv_ktp;

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

        System.out.println(ACCESS_TOKEN);

        recyclerView = findViewById(R.id.recycler_Home);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

//        initialize all things here
        recyclerView.setLayoutManager(layoutManager);
        tv_CovidTotal = findViewById(R.id.tv_covidTotal);
        tv_tanggalNow = findViewById(R.id.tv_tanggalNow);
        tv_nama = findViewById(R.id.tv_nama);
        btn_form = findViewById(R.id.btn_inputData);
        btn_hospitalMap = findViewById(R.id.btn_hospital);
        btn_stepCount = findViewById(R.id.btn_stepCount);
        btn_showArticle = findViewById(R.id.btn_article);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        tv_tanggalNow.setText(formattedDate);

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
                                    System.out.println(data);
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

//    private void LoadUser(){
//
//        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
//        final InterfaceAPI apiClient = retrofit.create(InterfaceAPI.class);
//
//        Call<ResponseBody> call = apiClient.getUser("Bearer "+ACCESS_TOKEN);
//
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
//                if (response.isSuccessful()) {
//                    try {
//                        ACCESS_TOKEN = response.body().string();
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//            }
//        });
//    }

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
