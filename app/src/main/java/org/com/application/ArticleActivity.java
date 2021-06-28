package org.com.application;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.com.application.Adapter.RecyclerViewAdapter;
import org.com.application.Model.PostModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ArticleActivity extends AppCompatActivity {

//    private static final String ipaddressLaravel = "192.168.100.30:8000";
    private static final String ipaddressLaravel = "10.0.2.2:8000"; //berdasarkan emulator masing2
    private static final String URL_GET_POST_DETAIL = "http://"+ipaddressLaravel+"/api/post?";
    public static final String URL_BASE_STORAGE = "http://"+ipaddressLaravel+"/storage/";


    TextView tv_body,tv_tittle,tv_tanggal;
    ImageView iv_home_post;
    String id_post;
    PostModel data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artikel);

        Intent i = getIntent();
        id_post = i.getStringExtra("EXTRA_ID_POST");

        System.out.println(id_post);

        tv_body = findViewById(R.id.tv_body);
        tv_tittle = findViewById(R.id.tv_title);
        tv_tanggal = findViewById(R.id.tv_tanggal);
        iv_home_post= findViewById(R.id.img_home_post);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        tv_tanggal.setText(formattedDate);


        getDetails();
    }


    private void getDetails(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_GET_POST_DETAIL+"id="+id_post,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            for (int i = 0; i<jsonArray.length(); i++){
                                JSONObject obj = jsonArray.getJSONObject(i);

                                if (obj != null) {
                                    obj.getString("image");
//                                            obj.getString("id").trim();
//                                            obj.getString("category_id");
                                    tv_tittle.setText(obj.getString("title"));
                                    tv_tanggal.setText(obj.getString("created_at"));
                                    tv_body.setText(obj.getString("body"));
//                                            obj.getString("created_at");
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
}
