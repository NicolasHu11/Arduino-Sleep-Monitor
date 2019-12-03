package com.example.btledblinker;



import android.app.Service;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.Gravity;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MyService extends Service {

    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date = new Date(System.currentTimeMillis());
    private Handler mHandler;
    String middleword = null;
    public final static String EXTRA_MESSAGE = "MESSAGE";
    private ListView obj;
    DBHelper mydb;
    TEMPHelper tmpdb;
    GyroHelper gydb;
    ACHelper acdb;


    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mydb = new DBHelper(this);
        tmpdb = new TEMPHelper(this);
        gydb = new GyroHelper(this);
        acdb = new ACHelper(this);

        Toast.makeText(this, "The new Service was Created", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onStart(Intent intent, int startId) {
        // For time consuming an long tasks you can launch a new thread here...
        // Do your Bluetooth Work Here
//        while(true)
//        {
//            mHandler = new Handler(){
//                public void handleMessage(android.os.Message msg){
//                    if(msg.what == MESSAGE_READ){
//                        String readMessage = null;
//                        try {
//                            readMessage = new String((byte[]) msg.obj, "UTF-8");
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
//                        middleword=readMessage;
////                    mReadBuffer.setText(readMessage);
//
//
//                    }
//
////                if(msg.what == CONNECTING_STATUS){
////                    if(msg.arg1 == 1)
////                        mBluetoothStatus.setText("Connected to Device: " + (String)(msg.obj));
////                    else
////                        mBluetoothStatus.setText("Connection Failed");
////                }
//                }
//            };
            String storedmessage = middleword;
            String returnMessage = null;
            if (storedmessage.isEmpty()) {
                Toast toast = Toast.makeText(getApplicationContext(), "You need to enter a valid number!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP | Gravity.LEFT, 265, 600);
                toast.show();
                return;
            } else {

                String[] Number = storedmessage.split("\n");
                for (String a : Number) {
                    a = a.trim();
                    if (a.contains("HR")) {
                        mydb.insertContact(Integer.valueOf(a.substring(2, a.length())), simpleDateFormat.format(date).toString());
                    }
                    if (a.contains("TEMP")) {
                        tmpdb.insertContact(Double.valueOf(a.substring(4, a.length())), simpleDateFormat.format(date).toString());
                    }
                    if (a.contains("GY")) {
                        String[] gy = a.split(",");
                        gydb.insertContact(Integer.valueOf(gy[0].substring(2, gy[0].length())), Integer.valueOf(gy[1]),
                                Integer.valueOf(gy[2]), simpleDateFormat.format(date).toString());
                    }
                    if (a.contains("AC")) {
                        String[] ac = a.split(",");
                        acdb.insertContact(Integer.valueOf(ac[0].substring(2, ac[0].length())), Integer.valueOf(ac[1]),
                                Integer.valueOf(ac[2]), simpleDateFormat.format(date).toString());
                    }
                }

            }
            Toast.makeText(this, " Service Started", Toast.LENGTH_LONG).show();
//        }


    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

    }



}
