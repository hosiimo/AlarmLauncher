package com.example.adminp2p.alarmslauncher;

import android.app.PendingIntent;

/**
 * Created by admin(p2p) on 2015/07/08.
 */
public class Alarm{
    int number;
    int stress;
    int alarm_windowstart;
    int alarm_windowlength;
    int alarm_time;
    int alarm_wakeup;
    PendingIntent alarm_pendingIntent;

    Alarm(int _number,int _stress, int _alarm_windowstart, int _alarm_windowlength, int _alarm_time,int _alarm_wakeup, PendingIntent _alarm_pendingintent) {

        number=_number;
        stress=_stress;
        alarm_windowstart=_alarm_windowstart;
        alarm_windowlength=_alarm_windowlength;
        alarm_time=_alarm_time;
        alarm_wakeup = _alarm_wakeup;
        alarm_pendingIntent = _alarm_pendingintent;


    }


}