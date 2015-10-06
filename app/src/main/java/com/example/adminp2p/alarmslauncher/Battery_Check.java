package com.example.adminp2p.alarmslauncher;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by admin(p2p) on 2015/09/18.
 */
public class Battery_Check {

    public Battery_Check(){

    }

    public int get(){
        int result = 0;
        File file = new File("/sys/class/power_supply/battery/capacity");
        if(file.exists()){
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                result =Integer.parseInt(br.readLine());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
