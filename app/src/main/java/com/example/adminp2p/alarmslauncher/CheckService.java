package com.example.adminp2p.alarmslauncher;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * Created by admin(p2p) on 2015/07/14.
 */
public  class CheckService extends Service {

    String TAG="CheckService";
    Context mContext;
    BroadcastReceiver mReceiver;
    int frequence;

    ArrayList<Long> mTime;
    ArrayList<ArrayList<Integer>> mCpu_array;
    ArrayList<Integer> mBattery_array;
    ArrayList<Map<String,String>> mData_array;
    ArrayList<HashMap<Long,String>> mAlarm_array;

    Time_Check time;
    Cpu_Check cpu;
    Data_Check data;
    Battery_Check battery;
    Alarm_Check alarm;

    WriteDataFile file;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mContext = getApplicationContext();

        mTime = new ArrayList();
        mCpu_array = new ArrayList();
        mBattery_array = new ArrayList();
        mData_array = new ArrayList();
        mAlarm_array = new ArrayList<>();

        time = new Time_Check();
        cpu = new Cpu_Check();
        data = new Data_Check();
        battery = new Battery_Check();
        alarm = new Alarm_Check(mContext);

        file = new WriteDataFile();

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        frequence = intent.getIntExtra("frequence", 3);
        IntentFilter fileter = new IntentFilter();
        fileter.addAction("Intent.AlarmLaucher.CheckService");

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                mTime.add(time.get());
                mCpu_array.add(cpu.get());
                mBattery_array.add(battery.get());
                mData_array.add(data.get());
                mAlarm_array.add(alarm.getalarm());

                if(mTime.size() >=20){//とりあえず20ためたら書き込む

                    file.write(mTime,mCpu_array,mBattery_array,mData_array,mAlarm_array);

                    mTime.clear();
                    mCpu_array.clear();
                    mBattery_array.clear();
                    mData_array.clear();
                    mAlarm_array.clear();
                }

                Make_Pendingintent(frequence);
            }

        };

        mContext.registerReceiver(mReceiver,fileter);

        Make_Pendingintent(frequence);

        return START_STICKY;
    }

    public void Make_Pendingintent(int frequence){

        long time = SystemClock.elapsedRealtime() + frequence * 1000;
        Intent intent = new Intent("Intent.AlarmLaucher.CheckService");
        PendingIntent pendingintent = PendingIntent.getBroadcast(getApplicationContext(),0,intent,PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarm = (AlarmManager)mContext.getSystemService(ALARM_SERVICE);
        alarm.setWindow(2,time,0,pendingintent);
    }

    @Override
    public void onDestroy() {
        mContext.unregisterReceiver(mReceiver);
        alarm.AlarmStop();

        super.onDestroy();
    }


}
