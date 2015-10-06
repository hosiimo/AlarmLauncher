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
    public ArrayList<Double> nowArraylist = new ArrayList();
    public ArrayList<Double> preArraylist = new ArrayList();

    public Cpu_Check() {
        nowArraylist = Make_cpu_ArrayList();
    }

    public ArrayList<Integer> get() {
        ArrayList<Integer> result = new ArrayList<>();
        preArraylist = new ArrayList(nowArraylist);
        nowArraylist = new ArrayList<>(Make_cpu_ArrayList());
        ArrayList<Double> Compere = new ArrayList<>(CompareArrayList(preArraylist, nowArraylist));

        Double total_cpu = Double.valueOf(0);
        for (Double o : Compere) {
            total_cpu += o;
        }

            Integer user = (int)Math.round(Compere.get(0) / total_cpu * 100);
            Integer nice = (int)Math.round(Compere.get(1) / total_cpu * 100);
            Integer system = (int)Math.round(Compere.get(2) / total_cpu * 100);
            Integer idle = (int)Math.round(Compere.get(3) / total_cpu * 100);

            result.add(user);
            result.add(nice);
            result.add(system);
            result.add(idle);


        return result;
    }

    public ArrayList<Double> Make_cpu_ArrayList() {

        ArrayList<Double> cpu_arraylist = new ArrayList();
        String stat_path = "proc/stat";
        File file_stat = new File(stat_path);

        if (file_stat.isFile()) {
            try {
                Scanner scanner = new Scanner(file_stat);
                scanner.useDelimiter("\\s");
                cpu_arraylist = new ArrayList();
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

        return cpu_arraylist;
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
