package com.example.adminp2p.alarmslauncher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends Activity {

    ClockReceiver clock;
    boolean mBu_jugement = false;
    Context context = this;

    String TAG = this.getCallingPackage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText Ed_Thread_number = (EditText)findViewById(R.id.Ed_Thread_number);
        final EditText Ed_stress = (EditText)findViewById(R.id.Ed_stress);
        final EditText Ed_Alarm_windowstartmillis = (EditText)findViewById(R.id.Ed_Alarm_windowstartmillis);
        final EditText Ed_Alarm_window_LengthMillis =(EditText)findViewById(R.id.Ed_Alarm_window_LengthMillis);
        final EditText Ed_Alarm_times =(EditText)findViewById(R.id.Ed_Alarm_times);
        final EditText Ed_check_frequence_second = (EditText)findViewById(R.id.Ed_check_frequence_second);

        Button Bu_launcher = (Button)findViewById(R.id.Bu_launch);
        Button Bu_kill = (Button)findViewById(R.id.Bu_Kill_Threads);
        Button Bu_Check_Service= (Button)findViewById(R.id.Bu_Cpu_checker);
        Button Bu_stop_cpu_check = (Button)findViewById(R.id.Bu_Stop_cpu_checke);

        Bu_launcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mBu_jugement = true;

                int Thread_number = Integer.valueOf(Ed_Thread_number.getText().toString());
                int Stress = Integer.valueOf(Ed_stress.getText().toString());
                int Alarm_windowstartmillis = Integer.valueOf(Ed_Alarm_windowstartmillis.getText().toString());
                int Alarm_window_LengthMillis = Integer.valueOf(Ed_Alarm_window_LengthMillis.getText().toString());
                int Alarm_time = Integer.valueOf(Ed_Alarm_times.getText().toString());
                clock = new ClockReceiver(getApplicationContext(),Thread_number,Stress,
                        Alarm_windowstartmillis,Alarm_window_LengthMillis,Alarm_time);



            }
        });

        Bu_kill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBu_jugement) {
                    clock.DeleteSettingFilter(getApplicationContext());
                    Toast.makeText(context,"delete alarm",Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"delete alarm");
                }else{
                    mBu_jugement = false;
                    Toast.makeText(context,"Not Setting Filter",Toast.LENGTH_SHORT).show();
                }
            }
        });

        Bu_Check_Service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int frequence = Integer.valueOf(Ed_check_frequence_second.getText().toString());
                Intent intent = new Intent(getApplicationContext(),CheckService.class);
                intent.putExtra("frequence",frequence);
                startService(intent);
            }
        });

        Bu_stop_cpu_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),CheckService.class);
                stopService(intent);
            }
        });

    }

    public void onDestroy(){
        super.onDestroy();
        if(mBu_jugement){
            clock.DeleteSettingFilter(getApplicationContext());
        }
    }

 
 
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
