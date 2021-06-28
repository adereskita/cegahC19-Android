package org.com.application;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.com.application.API.InterfaceAPI;
import org.com.application.API.RetrofitClient;
import org.com.application.Model.CovidModel;
import org.com.application.Model.UserModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FormGejalaActivity extends AppCompatActivity {

    private static final String ipaddressLaravel = "10.0.2.2:8000";
    private static final String URL_GET_USER = "http://" + ipaddressLaravel + "/api/auth/user";

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor meditor;

    private int id;

    private CheckBox cbdemam, cbbatuk, cbdiare, cblemes, cbkakupundak,cbmatakuning,cbkulitruam, cbmatamerah,cbsesaknafas;
    EditText et_age,et_gender,et_telfo,et_provinsi, et_kota, et_address, et_gejala;
    TextView tv_name, tv_nik;
    Spinner sp_gender;
    Button btnsave;

    private static String ACCESS_TOKEN;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_gejala);


        mPreferences = PreferenceManager.getDefaultSharedPreferences(FormGejalaActivity.this);
        meditor = mPreferences.edit();

        System.out.println("ID DI HOME: "+id);

        //initial variable
        tv_name = findViewById(R.id.edt_nama_reg);
        et_age = findViewById(R.id.edt_age);
        sp_gender = findViewById(R.id.sp_gender);
        tv_nik = findViewById(R.id.edt_nik);
        et_telfo = findViewById(R.id.edt_no_telfon);
        et_provinsi = findViewById(R.id.edt_provinsi);
        et_address = findViewById(R.id.edt_alamat);
//        et_gejala = findViewById(R.id.edt_g)

        setCheckBoxListener();
        //setButtonListener();




    }

    private void setCheckBoxListener() {
        cbdemam = (CheckBox) findViewById(R.id.cb_demam);
        cbbatuk= (CheckBox) findViewById(R.id.cb_batuk);
        cbdiare = (CheckBox) findViewById(R.id.cb_diare);
        cbkakupundak = (CheckBox) findViewById(R.id.cb_kuku_ruam);
        cbkulitruam= (CheckBox) findViewById(R.id.cb_kulit_ruam);
        cbmatamerah = (CheckBox) findViewById(R.id.cb_mata_merah);
        cbmatakuning = (CheckBox) findViewById(R.id.cb_matakuning);
        cblemes = (CheckBox) findViewById(R.id.cb_lemas);
        cbsesaknafas = (CheckBox) findViewById(R.id.cb_sesak_nafas);

        btnsave = (Button) findViewById(R.id.btn_save);

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = "Demam " + cbdemam.isChecked()
                        + "\n Batuk " + cbbatuk.isChecked()
                        + "\n Diare " + cbdiare.isChecked()
                        + "\n Lemes " + cblemes.isChecked()
                        + "\n Kaku Pundak " + cbkakupundak.isChecked()
                        + "\n Mata Kuning " + cbmatakuning.isChecked()
                        + "\n Kulit Ruam " + cbkulitruam.isChecked()
                        + "\n Sesak Nafas " + cbsesaknafas.isChecked()
                        + "\n Mata merah " + cbmatamerah.isChecked();

                Toast.makeText(FormGejalaActivity.this,status, Toast.LENGTH_LONG).show();
            }
        });
    }


    private void postCovid() {
        String nama = tv_name.getText().toString().trim();
        String umur = et_age.getText().toString().trim();
        String nik = tv_nik.getText().toString().trim();
        String telepon = et_telfo.getText().toString().trim();
        String provinsi = et_provinsi.getText().toString().trim();
        String kota = et_kota.getText().toString().trim();
        String alamat = et_address.getText().toString().trim();


//        Retrofit.Builder builder = new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create());
//        Retrofit retrofit = builder.build();

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();

        InterfaceAPI apiClient = retrofit.create(InterfaceAPI.class);

        CovidModel covid = new CovidModel(id, "nama", "umur", "gender", "nik", "telepon", "provinsi", "kota", "alamat", "gejala");
        //Call<UserModel> call = apiClient.login("application/json","application/json",user);
        Call<CovidModel> call = apiClient.covidPost(covid);

        call.enqueue(new Callback<CovidModel>() {
            @Override
            public void onResponse(Call<CovidModel> call, retrofit2.Response<CovidModel> response) {
                if (response.isSuccessful()) {
                    try {
                        Toast.makeText(FormGejalaActivity.this, "Berhasil Input Data", Toast.LENGTH_SHORT).show();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(FormGejalaActivity.this, "Input Data Gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CovidModel> call, Throwable t) {
                Toast.makeText(FormGejalaActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadUser() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URL_GET_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);

                                if (obj != null) {
                                    tv_name.setText(obj.getString("name"));
                                    System.out.println(obj.getString("name"));
                                    tv_nik.setText(obj.getString("nik"));
                                    System.out.println(obj.getString("nik"));
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
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
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