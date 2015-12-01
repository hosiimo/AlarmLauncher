package com.example.adminp2p.alarmslauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin(p2p) on 2015/09/18.
 */
public class Alarm_Check {

    Context mContext;
    BroadcastReceiver mReceiver;

    HashMap<Long,String> alarmreserve = new HashMap<>();

    public Alarm_Check(Context Context){

        mContext = Context;
        IntentFilter fileter = new IntentFilter();
        fileter.addAction("Intent.AlarmLaucher.Alarm_Check");

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String mAlarmContain  = intent.getStringExtra("alarmname");
                if(mAlarmContain.indexOf("AlarmLaucher") == -1) {
                    Long time = System.currentTimeMillis();
                    alarmreserve.put(time,mAlarmContain);
                }
            }
        };

        mContext.registerReceiver(mReceiver,fileter);
    }

    public HashMap<Long,String> getalarm(){
        HashMap<Long,String> result = (HashMap<Long,String>)alarmreserve.clone();
        alarmreserve.clear();
        return result;
    }

    public void AlarmStop(){
        mContext.unregisterReceiver(mReceiver);
    }
}
