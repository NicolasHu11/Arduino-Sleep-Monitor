package com.example.btledblinker;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;


//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;




public class MainActivity extends Activity {

    // GUI Components
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
    Date date = new Date(System.currentTimeMillis());

    private TextView mBluetoothStatus;
    private TextView mResultBuffer;
    private TextView mReadBuffer;
    private Button mScanBtn;
    private Button mOffBtn;
    private Button mListPairedDevicesBtn;
    private Button mDiscoverBtn;
    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;
    private ListView mDevicesListView;
    private Button mLED1;
    private Button Signal_activity;

    private final String TAG = MainActivity.class.getSimpleName();
    private Handler mHandler; // Our main handler that will receive callback notifications
    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier


    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status


    public final static String EXTRA_MESSAGE = "MESSAGE";
    private ListView obj;
    DBHelper mydb;
    TEMPHelper tmpdb;
    GyroHelper gydb;
    ACHelper acdb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mydb = new DBHelper(this);
        tmpdb = new TEMPHelper(this);
        gydb = new GyroHelper(this);
        acdb = new ACHelper(this);
        setContentView(R.layout.activity_main);

        mResultBuffer = (TextView) findViewById(R.id.resultBuffer);
        mBluetoothStatus = (TextView)findViewById(R.id.bluetoothStatus);
        mReadBuffer = (TextView) findViewById(R.id.readBuffer);
        mScanBtn = (Button)findViewById(R.id.scan);
        mOffBtn = (Button)findViewById(R.id.off);
        mDiscoverBtn = (Button)findViewById(R.id.discover);
        mListPairedDevicesBtn = (Button)findViewById(R.id.PairedBtn);
        mLED1 = (Button) findViewById(R.id.checkboxLED1);
        Signal_activity = (Button) findViewById(R.id.Activitybutton1);

        mBTArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio

        mDevicesListView = (ListView)findViewById(R.id.devicesListView);
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // Ask for location permission if not already allowed
        //if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        //    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        final char[] buffer = new char[1];

//        String readMessage = null;
//        Handler hnd = mConnectedThread.getThreadHandler();
//        readMessage = hnd.sendMessage();
//        mReadBuffer.setText(readMessage);


        mHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == MESSAGE_READ){
                    String readMessage = null;
                    try {
//                        readMessage = new String((String) msg.obj);
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    mReadBuffer.setText(readMessage);

                }

                if(msg.what == CONNECTING_STATUS){
                    if(msg.arg1 == 1)
                        mBluetoothStatus.setText("Connected to Device: " + (String)(msg.obj));
                    else
                        mBluetoothStatus.setText("Connection Failed");
                }
            }
        };

        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
            mBluetoothStatus.setText("Status: Bluetooth not found");
            Toast.makeText(getApplicationContext(),"Bluetooth device not found!",Toast.LENGTH_SHORT).show();
        }
        else {

            mLED1.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    launch_AnalysisActivity();

//                    if (mLED1.isChecked()==false) {
//                        mLED1.setBackgroundColor(Color.GREEN);
//                    }
//                    else {
//                        mLED1.setBackgroundColor(Color.RED);
//                    }
//                    if(mConnectedThread != null) //First check to make sure thread created
//                    {
////                        AsyncTaskRunner task= new AsyncTaskRunner();
//                        Intent intent = new Intent(MainActivity.this, MyService.class);
//                        if(mLED1.isChecked()){
//                            startService(intent);
//                        }
//                        else
//                        {
//                            stopService(intent);
//                        }
//                        while (true) {
//                            String readMessage = (String) mReadBuffer.getText();
//                            String returnMessage = null;
//                            if (readMessage.isEmpty()) {
//                                mResultBuffer.setText("N/A");
//                                Toast toast = Toast.makeText(getApplicationContext(), "You need to enter a valid number!", Toast.LENGTH_LONG);
//                                toast.setGravity(Gravity.TOP | Gravity.LEFT, 265, 600);
//                                toast.show();
//                                return;
//                            } else {
//
//                                String[] Number = readMessage.split("\n");
//                                for (String a : Number) {
//                                    a = a.trim();
//                                    if (a.contains("HR")) {
//                                        mydb.insertContact(Integer.valueOf(a.substring(2, a.length())), simpleDateFormat.format(date).toString());
//                                    }
//                                    if (a.contains("TEMP")) {
//                                        tmpdb.insertContact(Double.valueOf(a.substring(4, a.length())), simpleDateFormat.format(date).toString());
//                                    }
//                                    if (a.contains("GY")) {
//                                        String[] gy = a.split(",");
//                                        gydb.insertContact(Integer.valueOf(gy[0].substring(2, gy[0].length())), Integer.valueOf(gy[1]),
//                                                Integer.valueOf(gy[2]), simpleDateFormat.format(date).toString());
//                                    }
//                                    if (a.contains("AC")) {
//                                        String[] ac = a.split(",");
//                                        acdb.insertContact(Integer.valueOf(ac[0].substring(2, ac[0].length())), Integer.valueOf(ac[1]),
//                                                Integer.valueOf(ac[2]), simpleDateFormat.format(date).toString());
//                                    }
//                                }
//
//                            }
//                            mResultBuffer.setText(returnMessage);
//                            String str = returnMessage + "\n";
//                            mConnectedThread.write(str);

                        }
//                    }





//                        if(mLED1.isChecked())
//                            buffer[0] = '1'; // button says on, light is off
//                        else {
//                            buffer[0] = '0';
//                        }
//                    String str = new String( buffer );


//                        }


            });

            Signal_activity.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             launch_SignalActivity();
                                         }
                                     });


            mScanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bluetoothOn(v);
                }
            });

            mOffBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    bluetoothOff(v);
                }
            });

            mListPairedDevicesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    listPairedDevices(v);
                }
            });

            mDiscoverBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    discover(v);
                }
            });
        }
    }

    private void bluetoothOn(View view){
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            mBluetoothStatus.setText("Bluetooth enabled");
            Toast.makeText(getApplicationContext(),"Bluetooth turned on",Toast.LENGTH_SHORT).show();

        }
        else{
            Toast.makeText(getApplicationContext(),"Bluetooth is already on", Toast.LENGTH_SHORT).show();
        }
    }

    // Enter here after user selects "yes" or "no" to enabling radio
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data){
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                mBluetoothStatus.setText("Enabled");
            }
            else
                mBluetoothStatus.setText("Disabled");
        }
    }

    private void bluetoothOff(View view){
        mBTAdapter.disable(); // turn off
        mBluetoothStatus.setText("Bluetooth disabled");
        Toast.makeText(getApplicationContext(),"Bluetooth turned Off", Toast.LENGTH_SHORT).show();
    }

    private void discover(View view){
        // Check if the device is already discovering
        if(mBTAdapter.isDiscovering()){
            mBTAdapter.cancelDiscovery();
            Toast.makeText(getApplicationContext(),"Discovery stopped",Toast.LENGTH_SHORT).show();
        }
        else{
            if(mBTAdapter.isEnabled()) {
                mBTArrayAdapter.clear(); // clear items
                mBTAdapter.startDiscovery();
                Toast.makeText(getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT).show();
                registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            }
            else{
                Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
            }
        }
    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mBTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    private void listPairedDevices(View view){
        mBTArrayAdapter.clear();
        mPairedDevices = mBTAdapter.getBondedDevices();
        if(mBTAdapter.isEnabled()) {
            // put it's one to the adapter
            for (BluetoothDevice device : mPairedDevices)
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());

            Toast.makeText(getApplicationContext(), "Show Paired Devices", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            if(!mBTAdapter.isEnabled()) {
                Toast.makeText(getBaseContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
                return;
            }

            mBluetoothStatus.setText("Connecting...");
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);

            // Spawn a new thread to avoid blocking the GUI one
            new Thread()
            {
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                    try {
                        mBTSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }
                    // Establish the Bluetooth socket connection.
                    try {
                        mBTSocket.connect();
                    } catch (IOException e) {
                        try {
                            fail = true;
                            mBTSocket.close();
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                    .sendToTarget();
                        } catch (IOException e2) {
                            //insert code to deal with this
                            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(fail == false) {
                        mConnectedThread = new ConnectedThread(mBTSocket);
                        mConnectedThread.start();
                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                .sendToTarget();
                    }
                }
            }.start();
        }
    };

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BTMODULEUUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }


//    private class AsyncTaskRunner extends AsyncTask<Void, Void, Void> {
//        String readMessage= (String) mReadBuffer.getText();
//        ProgressDialog progressDialog;
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            while(isCancelled()==false) {
//                String returnMessage = null;
//                if (readMessage.isEmpty()) {
//                    Toast toast = Toast.makeText(getApplicationContext(), "You need to enter a valid number!", Toast.LENGTH_LONG);
//                    toast.setGravity(Gravity.TOP | Gravity.LEFT, 265, 600);
//                    toast.show();
//                    return null;
//                } else {
//
//                    String[] Number = readMessage.split("\n");
//                    for (String a : Number) {
//                        a = a.trim();
//                        if (a.contains("HR")) {
//                            mydb.insertContact(Integer.valueOf(a.substring(2, a.length())), simpleDateFormat.format(date).toString());
//                        }
//                        if (a.contains("TEMP")) {
//                            tmpdb.insertContact(Double.valueOf(a.substring(4, a.length())), simpleDateFormat.format(date).toString());
//                        }
//                        if (a.contains("GY")) {
//                            String[] gy = a.split(",");
//                            gydb.insertContact(Integer.valueOf(gy[0].substring(2, gy[0].length())), Integer.valueOf(gy[1]),
//                                    Integer.valueOf(gy[2]), simpleDateFormat.format(date).toString());
//                        }
//                        if (a.contains("AC")) {
//                            String[] ac = a.split(",");
//                            acdb.insertContact(Integer.valueOf(ac[0].substring(2, ac[0].length())), Integer.valueOf(ac[1]),
//                                    Integer.valueOf(ac[2]), simpleDateFormat.format(date).toString());
//                        }
//                    }
//
//                }
//                if (isCancelled()) break;
//            }
//            return null;
//        }
//    }
//
    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if(bytes != 0) {
                        buffer = new byte[10240];
                        SystemClock.sleep(200); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read

                        String storedmessage = new String((byte[]) buffer, "UTF-8");
//                        String storedmessage = convertStreamToString(mmInStream);
//                        mReadBuffer.setText(storedmessage);
//                        mHandler.obtainMessage(MESSAGE_READ,storedmessage).sendToTarget();
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget(); // Send the obtained bytes to the UI activity

                        if (storedmessage.isEmpty()) {
                            Toast toast = Toast.makeText(getApplicationContext(), "You need to enter a valid number!", Toast.LENGTH_LONG);
//                            toast.setGravity(Gravity.TOP | Gravity.LEFT, 265, 600);
                            toast.show();
                            return;
                        } else {

                            String[] Number = storedmessage.split("\n");
                            for (String a : Number) {
                                a = a.trim();
                                if (a.contains("HR")) {
                                    Date date = new Date(System.currentTimeMillis());
                                    mydb.insertContact(Integer.valueOf(a.substring(2, a.length())), simpleDateFormat.format(date).toString());
                                }
                                if (a.contains("TEMP")) {
                                    Date date = new Date(System.currentTimeMillis());
                                    tmpdb.insertContact(Double.valueOf(a.substring(4, a.length())), simpleDateFormat.format(date).toString());
                                }
                                if (a.contains("GY")) {
                                    Date date = new Date(System.currentTimeMillis());
                                    String[] gy = a.split(",");
                                    try
                                    {
                                        gydb.insertContact(Integer.valueOf(gy[0].substring(2, gy[0].length())), Integer.valueOf(gy[1]),
                                                Integer.valueOf(gy[2]), simpleDateFormat.format(date).toString());
                                    }
                                    catch(Exception e)
                                    {
                                        Log.d("gyoutofbounds",simpleDateFormat.format(date).toString());
                                    }
                                }
                                if (a.contains("AC")) {
                                    Date date = new Date(System.currentTimeMillis());
                                    String[] ac = a.split(",");
                                    try
                                    {
                                        acdb.insertContact(Integer.valueOf(ac[0].substring(2, ac[0].length())), Integer.valueOf(ac[1]),
                                                Integer.valueOf(ac[2]), simpleDateFormat.format(date).toString());
                                    }
                                    catch(Exception e)
                                    {
                                        Log.d("acoutofbounds",simpleDateFormat.format(date).toString());
                                    }
                                }
                            }

                        }





                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is, "UTF-8").useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private void launch_AnalysisActivity()
    {
        Intent intent1 = new Intent(this, AnalysisActivity.class);
        startActivity(intent1);
    }
    private void launch_SignalActivity()
    {
        Intent intent2 = new Intent(this, SignalActivity.class);
        startActivity(intent2);
    }
}

