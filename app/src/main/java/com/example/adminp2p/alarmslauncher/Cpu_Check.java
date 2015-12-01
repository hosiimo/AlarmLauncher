package com.example.adminp2p.alarmslauncher;

import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Created by admin(p2p) on 2015/09/18.
 */
public class Cpu_Check {

    String TAG = "CPU_CHECK";
    final static String stat_path = "proc/stat";
    public  ArrayList<Double> nowArraylist = new ArrayList();
    public  ArrayList<Double> preArraylist = new ArrayList();

    public Cpu_Check() {
        Make_cpu_ArrayList(nowArraylist);
    }

    public void get(ArrayList<Long[]> list) {
        preArraylist = (ArrayList<Double>)nowArraylist.clone();
        nowArraylist.clear();
        Make_cpu_ArrayList(nowArraylist);
        ArrayList<Double> Compere = CompareArrayList(preArraylist, nowArraylist);

        Double total_cpu = Double.valueOf(0);
        for (Double o : Compere) {
            total_cpu += o;
        }
        Long[] result = new Long[4];
        result[0] = Math.round(Compere.get(0) / total_cpu * 100);//user
        result[1] = Math.round(Compere.get(1) / total_cpu * 100);//nice
        result[2] = Math.round(Compere.get(2) / total_cpu * 100);//system
        result[3] = Math.round(Compere.get(3) / total_cpu * 100);//idle

        list.add(result);
    }

    public void Make_cpu_ArrayList(ArrayList<Double> cpu_arraylist) {

        try {
            Scanner scanner = new Scanner(new File(stat_path));
            scanner.useDelimiter("\\s");
            int i = 0;

            scanner.next();
            scanner.next();//文字読込回避
            while (scanner.hasNext() && i < 4) {
                cpu_arraylist.add(Double.valueOf(scanner.next()));
                i++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<Double> CompareArrayList(ArrayList<Double> pre_arraylist, ArrayList<Double> now_arraylist) {

        ArrayList<Double> arrayList = new ArrayList();
        int size = pre_arraylist.size();

        for (int i = 0; size > i; i++) {

            Double pre_number = pre_arraylist.get(i);
            Double now_number = now_arraylist.get(i);

            Double diff = now_number - pre_number;
            arrayList.add(diff);


        }

        return arrayList;
    }
}
