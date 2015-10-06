package com.example.adminp2p.alarmslauncher;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by admin(p2p) on 2015/07/07.
 */

public class ClockReceiver {

    Context mContext;
    Alarm alarm;
    AlarmManager alr;
    ArrayList alarm_list = new ArrayList();
    IntentFilter filter = new IntentFilter();

    public ClockReceiver(Context context, int thread_number, int stress, int alarm_windowstart, int alarm_windowlength, int alarm_time) {

        mContext = context;
        SettingFilter(mContext,thread_number);//ブロードキャストレシーバーの登録
        SettingAlarmTime(thread_number,stress,alarm_windowstart,alarm_windowlength,alarm_time);//ALARMに各種設定落とし込み
        launche();//Alarmのセット
    }
    public void SettingFilter(Context mContext,int thread_number){

        for (int i = 0; thread_number > i; i++) {
            String intent = "Intent.Alarmlancher_" + String.valueOf(i);
            filter.addAction(intent);
        }
        mContext.registerReceiver(mReceiver, filter);
    }

    public void SettingAlarmTime(int thread_number,int stress,int alarm_windowstart, int alarm_windowlength, int alarm_time) {

        for (int i = 0; thread_number > i; i++) {
            int AlarmSettingStress = stress;
            int AlarmSettingStartTime = (int) (alarm_windowstart * (1 + Math.random()));//とりあえずalarm_windowstart*1~2倍設定
            int AlarmSettingLengthTime = (int)(alarm_windowlength * (1 + 2*Math.random()));//とりあえずalarm_windowlength*1~3倍設定
            int AlarmSettingTime = (int)(alarm_time * (1 + Math.random()));//とりあ・・・etc
            int AlarmSettingWakeup;

            //とりあえずwakeupするかしないかは運で。
            if(Math.random()>0.5){
                AlarmSettingWakeup = 2;//端末起動時間でwakeup
            }else {
                AlarmSettingWakeup = 3;//端末起動時間になってもwakeupしない
            }

            Log.d("SettingAlarm","Alarm number:"+ i +" Stress:"+ AlarmSettingStress + " StartingTime:"+AlarmSettingStartTime +
                    " LengthTime:"+ AlarmSettingLengthTime +" time:"+ AlarmSettingTime +" wakeup:"+ AlarmSettingWakeup);

            String string = "Intent.Alarmlancher_" + String.valueOf(i);
            Intent intent = new Intent(string);
            intent.putExtra("alarm_number",i);
            PendingIntent Alarm_pendingIntent = PendingIntent.getBroadcast(mContext,0,intent,PendingIntent.FLAG_ONE_SHOT);


            alarm = new Alarm(i,stress,AlarmSettingStartTime,AlarmSettingLengthTime,AlarmSettingTime,AlarmSettingWakeup,Alarm_pendingIntent);
            alarm_list.add(alarm);

        }


    }
    public void launche(){

        int number = alarm_list.size();
        for(int i=0;number>i;i++){

            alr = (AlarmManager)mContext.getSystemService(mContext.ALARM_SERVICE);
            long device_time = SystemClock.elapsedRealtime();
            Alarm al = (Alarm) alarm_list.get(i);
            int alarm_wakeup = al.alarm_wakeup;
            long alarm_windowstart = device_time + (al.alarm_windowstart*60*1000);
            long alarm_windowlength = device_time+(al.alarm_windowlength*60*1000);
            PendingIntent alarm_PendingIntent = al.alarm_pendingIntent;

            alr.setWindow(alarm_wakeup,alarm_windowstart,alarm_windowlength,alarm_PendingIntent);

        }

    }


    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int alarm_number = intent.getExtras().getInt("alarm_number",-1);
            if(alarm_number < 0){
                Log.d("onReceive","intent.getIntExtra is not found");
            }else{
            Alarm alarm = (Alarm)alarm_list.get(alarm_number);

            Log.d("onReceive","number:"+ alarm_number +" stress:"+alarm.stress);

            }

        }
    };

    public void DeleteSettingFilter(Context mContext){
        mContext.unregisterReceiver(mReceiver);

        int size = alarm_list.size();
        for(int i = 0;size>i;i++){
            Alarm delete_alarm =(Alarm)alarm_list.get(i);
            PendingIntent delete_pendingintent = delete_alarm.alarm_pendingIntent;
            alr.cancel(delete_pendingintent);
        }

    }
}


