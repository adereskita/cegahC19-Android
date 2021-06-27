package com.anaminase.shapeyou.Step;

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
import android.widget.TextView;
import android.widget.Toast;

import com.anaminase.shapeyou.Calori.CalculatorActivity;
import com.anaminase.shapeyou.DiscoveryActivity;
import com.anaminase.shapeyou.HomeActivity;
import com.anaminase.shapeyou.Models.Users;
import com.anaminase.shapeyou.R;
import com.anaminase.shapeyou.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class StepActivity extends AppCompatActivity implements SensorEventListener, StepListener {

    public static final String EXTRA_STEP = "step";

    private AcceleroDetector mAcceleroDetector;
    private SensorManager mSensorManager;
    private Sensor mStepCounterSensor;
    private Sensor mStepDetectorSensor;
    private Sensor mAccelerometer;

    private TextView tv_step;

    private String dates, id, nowTime;
    private int NumStep, nStep;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private FirebaseAuth mAuth;
    private DatabaseReference dbUser;

    //boolean running = false;

    private HorizontalCalendar mHorizontalCalendar;
    private BottomNavigationView mBottomNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        mBottomNavView = findViewById(R.id.bottom_nav);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        id = mUser.getUid();

        tv_step = findViewById(R.id.tv_numbstep);

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
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            nowTime = df.format(thisTime);

        //IF THE DATES IS NULL
        if (dates == null) {
            Date currentTime = Calendar.getInstance().getTime();
            dates = df.format(currentTime);

            dbUser = FirebaseDatabase.getInstance().getReference("user")
                    .child(id).child("step").child(dates);

            tv_step.setText(String.valueOf(nStep));
        }

        schedAlarm(getApplicationContext());

        //calendar
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);

        mHorizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarViews)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .build();

        mHorizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {


            @Override
            public void onDateSelected(Calendar date, int position) {
                Date nowTime = mHorizontalCalendar.getSelectedDate().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                dates = df.format(nowTime);

                if (!dates.equals(nowTime)){
                    tv_step.setText(String.valueOf(NumStep));
                }

                dbUser = FirebaseDatabase.getInstance().getReference("user")
                        .child(id).child("step").child(dates);

                dbUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){
//                            for (DataSnapshot ds : dataSnapshot.getChildren()){
                            Users mData= new Users();

                            mData.setStep(dataSnapshot.getValue(Users.class).getStep());//step User

                                if (mData.getStep() != 0){
                                    tv_step.setText(String.valueOf(mData.getStep()));
                                    System.out.println(mData.getStep());
                                }else{
                                    tv_step.setText(String.valueOf(NumStep));
                                }
//                            }

                        }else {
                            tv_step.setText(String.valueOf(NumStep));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCalendarScroll(HorizontalCalendarView calendarView,
                                         int dx, int dy) {

            }

            @Override
            public boolean onDateLongClicked(Calendar date, int position) {
                return true;
            }
        });

        mBottomNavView.setSelectedItemId(R.id.nav_step);
        mBottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){

                    case R.id.nav_home:
                        Intent home = new Intent(StepActivity.this, HomeActivity.class);
                        startActivity(home);
                        finish();
                        return true;
                    case R.id.nav_step:
                        menuItem.isEnabled();
                        Intent step = new Intent(StepActivity.this, StepActivity.class);
                        startActivity(step);
                        finish();
                        return true;
                    case R.id.nav_discover:
                        Intent discover = new Intent(StepActivity.this, DiscoveryActivity.class);
                        startActivity(discover);
                        finish();
                        return true;
                    case R.id.nav_calories:
                        Intent calori = new Intent(StepActivity.this, CalculatorActivity.class);
                        startActivity(calori);
                        finish();
                        return true;
                    case R.id.nav_profile:
                        Intent profile = new Intent(StepActivity.this, ProfileActivity.class);
                        startActivity(profile);
                        finish();
                        return true;

                    default:
                        return false;
                }
            }
        });
    }
    //onCreate end here

    private void schedAlarm(Context context) {
        Date date = new Date();
        Calendar time = Calendar.getInstance();
        time.setTime(date);
        time.set(Calendar.HOUR_OF_DAY, 23);// for 23 hour
        time.set(Calendar.MINUTE, 59);// for 0 min
        time.set(Calendar.SECOND, 50);// for 0 sec

        PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(context, SchedBroadcast.class), PendingIntent.FLAG_UPDATE_CURRENT);
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
            Toast.makeText(this, "Sensor not found. This device is not supported. :(", Toast.LENGTH_SHORT).show();
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
//            tv_step.setText(String.valueOf(nStep));

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

                Users mUser = new Users(id,nStep);

                dbUser.setValue(mUser);

                tv_step.setText(String.valueOf(nStep));
            }

        }else {
            tv_step.setText(String.valueOf(0));
        }
    }
}
