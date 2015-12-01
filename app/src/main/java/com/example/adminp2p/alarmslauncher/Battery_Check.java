package com.example.adminp2p.alarmslauncher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by admin(p2p) on 2015/09/18.
 */
public class Battery_Check {

    public void get(ArrayList<Integer[]> arrayList) {
        Integer[] result = new Integer[3];
        File voltage = new File("/sys/class/power_supply/battery/voltage_now");
        File amper = new File("/sys/class/power_supply/battery/current_now");
        File capacity = new File("/sys/class/power_supply/battery/capacity/capacity");


        try {
            BufferedReader vo = new BufferedReader(new FileReader(voltage));
            BufferedReader am = new BufferedReader(new FileReader(amper));
            BufferedReader ca = new BufferedReader(new FileReader(capacity));
            result[0] = Integer.valueOf(vo.readLine());
            result[1] = Integer.valueOf(am.readLine());
            result[2] = Integer.valueOf(ca.readLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        arrayList.add(result);
    }
}
