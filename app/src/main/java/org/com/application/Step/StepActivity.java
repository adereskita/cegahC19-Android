package org.com.application.Step;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.com.application.R;
import org.com.application.SessionManager.SessionManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import abak.tr.com.boxedverticalseekbar.BoxedVertical;

public class StepActivity extends AppCompatActivity implements SensorEventListener, StepListener {

    public static String getCalculatedDate(int days) {
        SimpleDateFormat s = new SimpleDateFormat("EEEE", new Locale("id", "ID"));
        Calendar cal = Calendar.getInstance();
//        SimpleDateFormat s = new SimpleDateFormat(dateFormat);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return s.format(new Date(cal.getTimeInMillis()));
    }
    public static String getPrevDate(int days) {
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd", new Locale("id", "ID"));
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, days);
        return s.format(new Date(cal.getTimeInMillis()));
    }

    private static final String ipaddressLaravel = "192.168.100.30:8000";
    //    private static final String ipaddressLaravel = "10.0.2.2:8000"; //berdasarkan emulator masing2
    private static final String URL_GET_STEP = "http://"+ipaddressLaravel+"/api/get/step?";

    public static final String EXTRA_STEP = "step";

    private AcceleroDetector mAcceleroDetector;
    private SensorManager mSensorManager;
    private Sensor mStepCounterSensor;
    private Sensor mStepDetectorSensor;
    private Sensor mAccelerometer;

    private TextView tv_step, tv_dayPrev_1,tv_dayPrev_2,tv_dayPrev_3,tv_dayPrev_4,tv_nowDate;
    private BoxedVertical bv_dayPrev_1,bv_dayPrev_2,bv_dayPrev_3,bv_dayPrev_4;
    private View view_blocker;
    private ImageButton btn_back_step;

    private String dates, id, nowTime;
    private int NumStep, nStep;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private static String ACCESS_TOKEN;

    SessionManager session;

    //boolean running = false;
    private BottomNavigationView mBottomNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        mBottomNavView = findViewById(R.id.bottomNavigationView);

        // Session class instance
        session = new SessionManager(getApplicationContext());

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // token
        ACCESS_TOKEN = user.get(SessionManager.KEY_TOKEN);

        tv_step = findViewById(R.id.tv_step);
        tv_nowDate = findViewById(R.id.tv_nowDate);
        tv_dayPrev_1 = findViewById(R.id.tv_day_1);
        tv_dayPrev_2 = findViewById(R.id.tv_day_2);
        tv_dayPrev_3 = findViewById(R.id.tv_day_3);
        tv_dayPrev_4 = findViewById(R.id.tv_day_4);
        bv_dayPrev_1 = findViewById(R.id.bar_1);
        bv_dayPrev_2 = findViewById(R.id.bar_2);
        bv_dayPrev_3 = findViewById(R.id.bar_3);
        bv_dayPrev_4 = findViewById(R.id.bar_4);
        view_blocker = findViewById(R.id.view_blocker);
        btn_back_step = findViewById(R.id.btn_back_step);

        view_blocker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });


        // Get an instance of the SensorManager
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAcceleroDetector = new AcceleroDetector();
        mAcceleroDetector.registerListener(this);

        //sharedPreference
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();

        Date thisTime = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("EEEE", new Locale("id", "ID"));
        nowTime = df.format(thisTime);

        tv_nowDate.setText(nowTime);

        //IF THE DATES IS NULL
        if (dates == null) {
            Date currentTime = Calendar.getInstance().getTime();
            dates = df.format(currentTime);

            tv_step.setText(String.valueOf(nStep));
        }

        tv_dayPrev_1.setText(getCalculatedDate(-1));
        getStep(mPreferences.getInt(SessionManager.KEY_ID, 0), getPrevDate(-1), bv_dayPrev_1);
        tv_dayPrev_2.setText(getCalculatedDate(-2));
        getStep(mPreferences.getInt(SessionManager.KEY_ID, 0), getPrevDate(-2), bv_dayPrev_2);
        tv_dayPrev_3.setText(getCalculatedDate(-3));
        getStep(mPreferences.getInt(SessionManager.KEY_ID, 0), getPrevDate(-3), bv_dayPrev_3);
        tv_dayPrev_4.setText(getCalculatedDate(-4));
        getStep(mPreferences.getInt(SessionManager.KEY_ID, 0), getPrevDate(-4), bv_dayPrev_4);

        schedAlarm(getApplicationContext());

        btn_back_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    //onCreate end here

    private void getStep(int id_user, String date, BoxedVertical bv){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_GET_STEP+"id_user="+id_user+"&date="+date,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            for (int i = 0; i<jsonArray.length(); i++){
                                JSONObject obj = jsonArray.getJSONObject(i);

                                if (obj != null) {
                                    bv.setValue(obj.getInt("step"));
                                    System.out.println(date+" step: "+obj.getInt("step"));
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

    private void schedAlarm(Context context) {
        Date date = new Date();
        Calendar time = Calendar.getInstance();
        time.setTime(date);
        time.set(Calendar.HOUR_OF_DAY, 23);// for 23 hour
        time.set(Calendar.MINUTE, 59);// for 0 min
        time.set(Calendar.SECOND, 58);// for 0 sec

//        PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(context, SchedBroadcast.class), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(context, SchedBroadcast.class), 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), 1000*60*60*24, pi);
    }


    @Override
    protected void onResume() {
        super.onResume();

        schedAlarm(getApplicationContext());

//        //running = true;
         Sensor countSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

         Sensor mAccelSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (countSensor != null){
            mSensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
            mSensorManager.registerListener(this, mStepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);


        }else if (mAccelSensor != null){
//            mSensorManager.registerListener(this, mStepDetectorSensor,SensorManager.SENSOR_DELAY_FASTEST);
//            NumStep = 0;
            mSensorManager.registerListener(StepActivity.this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
            if (nStep != 0){
                tv_step.setText(String.valueOf(nStep));
            }

        }else {
            Toast.makeText(this, "Sensor not found. This device is not supported.", Toast.LENGTH_SHORT).show();
//            mSensorManager.registerListener(this, mStepDetectorSensor,SensorManager.SENSOR_DELAY_FASTEST);

        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this, mStepCounterSensor);
        mSensorManager.unregisterListener(this, mStepDetectorSensor);

        //accelerometer
//        mSensorManager.unregisterListener(this);

        //running = false;
//        if you unregister the hardware will stop detecting step
//        sensorManager.unregisterListener(this);
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        //accelerometer
//        mSensorManager.unregisterListener(this);
//
//    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor sensor = event.sensor;
        float[] values = event.values;
        int stepsInSensor = -1;


        /*if (!flag) {
            //Reset the count when reset the apps
            int initValue = (int) values[values.length - 1];
            stepsInSensor = stepsInSensor - initValue;
        }*/

        if (values.length >= 0) {                //some values was inside
            stepsInSensor = (int) values[0];    //the latest value added will be at value[0]
        }

        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            tv_step.setText(stepsInSensor);

        } else if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            tv_step.setText(stepsInSensor);
        }
        else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mAcceleroDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
            nStep = mPreferences.getInt(EXTRA_STEP, NumStep);
            tv_step.setText(String.valueOf(nStep));
        }
//        if (running){
//            tv_step.setText(String.valueOf(event.values[0]));
//        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void step(long timeNs) {

        if (dates.equals(nowTime)){

            if (nStep == 0){
                NumStep++;

                //to store
                mEditor.putInt(EXTRA_STEP, NumStep);
                mEditor.commit();

                tv_step.setText(String.valueOf(NumStep));

            }else {
                nStep++;

                //to store
                mEditor.putInt(EXTRA_STEP, nStep);
                mEditor.commit();

//            Date currentTime = Calendar.getInstance().getTime();
//            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
//            step_time = df.format(currentTime);

//                Users mUser = new Users(id,nStep);
//
//                dbUser.setValue(mUser);

                tv_step.setText(String.valueOf(nStep));
            }

        }else {
            tv_step.setText(String.valueOf(0));
        }
    }
}
