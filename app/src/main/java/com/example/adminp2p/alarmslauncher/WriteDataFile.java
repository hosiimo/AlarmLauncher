package com.example.adminp2p.alarmslauncher;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.telephony.TelephonyManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
    Context mContext;

    File file;
    PrintWriter printWriter = null;

    public WriteDataFile(Context context){
        mContext = context;
        File directory = new File(default_path);
        if(!directory.isDirectory()){
            directory.mkdir();
        }

        TelephonyManager manager = (TelephonyManager)mContext.getSystemService(mContext.TELEPHONY_SERVICE);
        path = default_path+"/"+getDeviceName(manager.getDeviceId())+"_"+TimeStam()+".csv";
        file = new File(path);
        try {
            file.createNewFile();
            printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true),"UTF-8")));
            printWriter.write("時刻,CPU(user),CPU(nice),CPU(system),CPU(idle),voltage[mV],amper[mA],capacity,DATA(UID),TRAFFIC,ALARM\n");
            printWriter.close();
            Log.d(TAG,"create file in "+ path);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public String getDeviceName(String id){
        if(id.equals("359250050101151")){
            return "active_device2";
        }else if (id.equals("359250050293073")){
            return "active_device1";
        }else if ( id.equals("359250053716500")){
            return "non_device1";
        }else if (id.equals("359250051347563")){
            return "non_device2";
        }
        return id;
    }

    public String TimeStam(){
        String stamp = "";

        SimpleDateFormat sdf = new SimpleDateFormat(
                "MMddkkmmss");
        stamp = sdf.format(new Date());
        return stamp;
    }
    ArrayList<String> stringdatalist = new ArrayList<>();
    ArrayList<String> stringalamrlist = new ArrayList<>();
    public void write(ArrayList<Long> mTime,ArrayList<Long[]> mCpu_array,
                      ArrayList<Integer[]> mBattery_array,ArrayList<Map<String,String>> mData_array,ArrayList<HashMap<Long,String>> mAlarm_array){

        try {
            printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true),"UTF-8")));
            for(int i=0;i<mTime.size();i++){

                long mtime = mTime.get(i);
                String time = FormatToTimeString(mtime);

                Long[] cpu  = mCpu_array.get(i);
                int user = cpu[0].intValue();
                int nice = cpu[1].intValue();
                int system = cpu[2].intValue();
                int idle = cpu[3].intValue();

                Integer[] battery = mBattery_array.get(i);
                int voltage = battery[0]/1000;
                int amper = battery[1]/1000;
                int capacity = battery[2];

                String line = time+","+user+","+nice+","+system+","+idle+","+voltage+","+amper+","+capacity+",";

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
                ListStringDataMap(stringdatalist,mData_map);
                ListStringAlarmMap(stringalamrlist,mAlarm_map);
                for(int k =0;writelinenumber>k;k++) {
                    if(k>0){
                        line = " , , , ,,, , ,";
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

    public void ListStringDataMap(ArrayList<String> result,Map<String,String> mData_map){
        for (Iterator ite = mData_map.entrySet().iterator(); ite.hasNext(); ) {
            Map.Entry ent = (Map.Entry) ite.next();
            result.add(String.valueOf(ent.getKey()));
            result.add(String.valueOf(ent.getValue()));
        }
    }

    public void ListStringAlarmMap(ArrayList<String> result,Map<Long,String> mAlarm_map){
        for (Iterator ite = mAlarm_map.entrySet().iterator(); ite.hasNext(); ) {
            Map.Entry ent = (Map.Entry) ite.next();
            result.add(FormatToTimeString(Long.parseLong(String.valueOf(ent.getKey()))));
            result.add(String.valueOf(ent.getValue()));
        }
    }

}
