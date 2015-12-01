package com.example.adminp2p.alarmslauncher;

import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by admin(p2p) on 2015/09/18.
 */
public class Data_Check {
    static String TAG = "Data_Check";
    public SamplingSnapshot mPresnapshot;

    public class SamplingSnapshot {
        public long mTxByteCount;
        public long mRxByteCount;
        public long mTxPacketCount;
        public long mRxPacketCount;
        public long mTxPacketErrorCount;
        public long mRxPacketErrorCount;
        public long mTimestamp;
    }

    //送信されてないと何も入ってないMAPが出力されるよ。
    public HashMap<String, String> get() {
        HashMap<String, String> result = new HashMap<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/net/dev"));

            // Skip over the line bearing column titles (there are 2 lines)
            String line;
            reader.readLine();
            reader.readLine();

            while ((line = reader.readLine()) != null) {

                // remove leading whitespace
                line = line.trim();

                String[] tokens = line.split("[ ]+");
                if (tokens.length < 17) {
                    continue;
                }

                String currentIface = tokens[0].split(":")[0];
                if (currentIface.equals("rmnet0")) {

                    try {
                        SamplingSnapshot ss = new SamplingSnapshot();

                        ss.mTxByteCount = Long.parseLong(tokens[1]);
                        ss.mTxPacketCount = Long.parseLong(tokens[2]);
                        ss.mTxPacketErrorCount = Long.parseLong(tokens[3]);
                        ss.mRxByteCount = Long.parseLong(tokens[9]);
                        ss.mRxPacketCount = Long.parseLong(tokens[10]);
                        ss.mRxPacketErrorCount = Long.parseLong(tokens[11]);

                        ss.mTimestamp = SystemClock.elapsedRealtime();

                        if (mPresnapshot != null) {
                            long differ = (ss.mTxByteCount + ss.mRxByteCount) - (mPresnapshot.mTxByteCount + mPresnapshot.mRxByteCount);
                            if (differ != 0) {
                                mPresnapshot = ss;
                                int size = result.size();
                                CheckUidTransmit(result);
                                if (result.size() != size) {
                                    result.put("unknown", String.valueOf(differ));
                                }
                            }
                        } else {
                            mPresnapshot = new SamplingSnapshot();
                            mPresnapshot = ss;
                        }
                    } catch (NumberFormatException e) {
                        // just ignore this data point
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "could not find /proc/net/dev");
        } catch (IOException e) {
            Log.e(TAG, "could not read /proc/net/dev");
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "could not close /proc/net/dev");
            }
        }
        return result;
    }


        Map<String, Long> MapUidTransmit = new HashMap<>();

    public void CheckUidTransmit(HashMap<String, String> result) {

        String path = "/proc/uid_stat/";
        File f = new File(path);
        File[] files = f.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                try {
                    File ts = new File(file.getPath() + "/tcp_snd");
                    File tr = new File(file.getPath() + "/tcp_rcv");

                    BufferedReader brs = new BufferedReader(new FileReader(ts));
                    long tcpsed = Long.parseLong(brs.readLine());
                    BufferedReader brr = new BufferedReader(new FileReader(tr));
                    long tcprcv = Long.parseLong(brr.readLine());

                    long tcptransmit = tcpsed + tcprcv;

                    if (MapUidTransmit.get(file.getName()) != null) {
                        long diff = tcptransmit - MapUidTransmit.get(file.getName()).longValue();
                        if (diff != 0) {
                            MapUidTransmit.put(file.getName(), tcptransmit);
                            result.put(file.getName(), String.valueOf(diff));
                        }
                    } else {
                        MapUidTransmit.put(file.getName(), tcptransmit);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
