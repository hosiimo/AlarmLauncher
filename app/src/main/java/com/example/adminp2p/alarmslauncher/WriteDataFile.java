package com.example.adminp2p.alarmslauncher;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by admin(p2p) on 2015/09/18.
 */
public class WriteDataFile {

    String TAG  ="WriteDataFile";
    String default_path = Environment.getExternalStorageDirectory().getPath() + "/CheckService";
    String path ="";

    File file;
    PrintWriter printWriter = null;

    public WriteDataFile(){
        File directory = new File(default_path);
        if(!directory.isDirectory()){
            directory.mkdir();
        }
        path = default_path+"/"+TimeStam()+".csv";
        file = new File(path);
        try {
            file.createNewFile();
            printWriter = new PrintWriter(new BufferedWriter(new FileWriter(
                    file,true)));
            printWriter.write("時刻,CPU(user),CPU(nice),CPU(system),CPU(idle),BATTERY,DATA(UID),TRAFFIC,ALARM\n");
            printWriter.close();
            Log.d(TAG,"create file in "+ path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String TimeStam(){
        String stamp = "";

        SimpleDateFormat sdf = new SimpleDateFormat(
                "MMddkkmmss");
        stamp = sdf.format(new Date());
        return stamp;
    }

    public void write(ArrayList<Long> mTime,ArrayList<ArrayList<Integer>> mCpu_array,
                      ArrayList<Integer> mBattery_array,ArrayList<Map<String,String>> mData_array,ArrayList<HashMap<Long,String>> mAlarm_array){

        try {
            printWriter = new PrintWriter(new BufferedWriter(new FileWriter(
                    file,true)));

            for(int i=0;i<mTime.size();i++){

                long mtime = mTime.get(i);
                String time = FormatToTimeString(mtime);

                ArrayList<Integer> cpu  = mCpu_array.get(i);
                int user = cpu.get(0);
                int nice = cpu.get(1);
                int system = cpu.get(2);
                int idle = cpu.get(3);

                int battery = mBattery_array.get(i);

                String line = time+","+user+","+nice+","+system+","+idle+","+battery+",";

                int writelinenumber = 1;
                Map<String,String> mData_map = mData_array.get(i);
                Map<Long,String> mAlarm_map = mAlarm_array.get(i);
                if(mAlarm_map.size()+mData_map.size() >0) {
                    if (mData_map.size() > mAlarm_array.size()) {
                        writelinenumber = mData_map.size();
                    } else {
                        writelinenumber = mAlarm_map.size();
                    }
                }
                ArrayList<String> stringdatalist = ListStringDataMap(mData_map);
                ArrayList<String> stringalamrlist = ListStringAlarmMap(mAlarm_map);
                for(int k =0;writelinenumber>k;k++) {
                    if(k>0){
                        line = " , , , , , ,";
                    }
                    if(stringdatalist.size()>1){
                    line += stringdatalist.get(0)+","+ stringdatalist.get(1)+",";
                    stringdatalist.remove(0);
                    stringdatalist.remove(0);
                    } else {
                        line +=" , ,";
                    }

                    if(stringalamrlist.size()>1){
                        line +=stringalamrlist.get(0)+","+stringalamrlist.get(1)+"\n";
                        stringalamrlist.remove(0);
                        stringalamrlist.remove(0);
                    }else {
                        line +=" , \n";
                    }
                    printWriter.write(line);
                }
            }


            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String FormatToTimeString(long time){
        SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy'年'MM'月'dd'日'kk'時'mm'分'ss'秒'");
        String result = sdf.format(time);
        return  result;
    }

    public ArrayList<String> ListStringDataMap(Map<String,String> mData_map){
        ArrayList result = new ArrayList();
        for (Iterator ite = mData_map.entrySet().iterator(); ite.hasNext(); ) {
            Map.Entry ent = (Map.Entry) ite.next();
            result.add(String.valueOf(ent.getKey()));
            result.add(String.valueOf(ent.getValue()));
        }
        return result;
    }

    public ArrayList<String> ListStringAlarmMap(Map<Long,String> mAlarm_map){
        ArrayList<String> result = new ArrayList<>();
        for (Iterator ite = mAlarm_map.entrySet().iterator(); ite.hasNext(); ) {
            Map.Entry ent = (Map.Entry) ite.next();
            result.add(FormatToTimeString(Long.parseLong(String.valueOf(ent.getKey()))));
            result.add(String.valueOf(ent.getValue()));
        }
        return result;
    }

}
