package com.example.adminp2p.alarmslauncher;

import android.provider.ContactsContract;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by admin(p2p) on 2015/09/18.
 */
public class Time_Check {
    public long get() {
        long result;

        result = System.currentTimeMillis();

        return  result;
    }
}