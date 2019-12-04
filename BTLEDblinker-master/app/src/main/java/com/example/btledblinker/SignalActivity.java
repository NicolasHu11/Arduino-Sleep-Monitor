package com.example.btledblinker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.example.btledblinker.MainActivity.acdb;
import static com.example.btledblinker.MainActivity.gydb;
import static com.example.btledblinker.MainActivity.mydb;
import static com.example.btledblinker.MainActivity.tmpdb;


public class SignalActivity extends AppCompatActivity {

    // ref: https://www.youtube.com/watch?v=QEbljbZ4dNs
    // ref: https://pusher.com/tutorials/graph-android

    public static LineChart hrChart;
    public static LineChart tempChart;
    public static LineChart accChart;
    public static LineChart gyroChart;

    private Thread thread;
    private Button bluetooth_activity;
//    private boolean plotData = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signal);

        //
        bluetooth_activity = (Button) findViewById(R.id.Bluetooth1);
        bluetooth_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // ==========================================================================
        // heart rate plot
        hrChart = (LineChart) findViewById(R.id.heart_rate_signal);
        configurePlotBasic(hrChart, "Heart Rate Signal");
        configureLegendsAndAxes(hrChart);

        // four steps for constructing data
        ArrayList<ArrayList<String>> hrDataDB = getDataFromHRDB();
        List<Entry> hrEntries = constructEntries(hrDataDB.get(0));
        LineDataSet hrDataSet = constructHRDataSet(hrEntries, hrChart,"heart rate");
        constructLineData(hrDataSet, hrChart);

        // ==========================================================================
        // Temperature Plot
        tempChart = (LineChart) findViewById(R.id.temperature_signal);
        configurePlotBasic(tempChart, "Temperature Signal");
        configureLegendsAndAxes(tempChart);

        ArrayList<ArrayList<String>> tempDataDB = getDataFromTempDB();
        List<Entry> tempEntries = constructEntries(tempDataDB.get(0));
        LineDataSet tempDataSet = constructHRDataSet(tempEntries, tempChart,"temperature");
        constructLineData(tempDataSet, tempChart);


        // ==========================================================================
        // Accelerometer plot
        accChart = (LineChart) findViewById(R.id.Acc_signal);
        configurePlotBasic(accChart, "Accelerometer Signal");
        configureLegendsAndAxes(accChart);

        ArrayList<ArrayList<String>> accDataDB = getDataFromAccDB();
        ArrayList<Entry> accEntriesX = constructEntries(accDataDB.get(0));
        ArrayList<Entry> accEntriesY = constructEntries(accDataDB.get(1));
        ArrayList<Entry> accEntriesZ = constructEntries(accDataDB.get(2));

        ArrayList<ArrayList<Entry>> accEntries = new ArrayList<ArrayList<Entry>>();
        accEntries.add(accEntriesX);
        accEntries.add(accEntriesY);
        accEntries.add(accEntriesZ);

        ArrayList<ILineDataSet> accDataSet = constructGyroDataSet(accEntries, accChart,"accelerometer");
        constructLineData(accDataSet, accChart);

        // ==========================================================================
        // Gyroscope plot
        gyroChart = (LineChart) findViewById(R.id.Gyro_signal);
        configurePlotBasic(gyroChart, "Gyroscope Signal");
        configureLegendsAndAxes(gyroChart);
//        configureData(gyroChart); // here data is empty

        ArrayList<ArrayList<String>> gyroDataDB = getDataFromGyroDB();
        ArrayList<Entry> gyroEntriesX = constructEntries(gyroDataDB.get(0));
        ArrayList<Entry> gyroEntriesY = constructEntries(gyroDataDB.get(1));
        ArrayList<Entry> gyroEntriesZ = constructEntries(gyroDataDB.get(2));

        ArrayList<ArrayList<Entry>> gyroEntries = new ArrayList<ArrayList<Entry>>();
        gyroEntries.add(accEntriesX);
        gyroEntries.add(accEntriesY);
        gyroEntries.add(accEntriesZ);

        ArrayList<ILineDataSet> gyroDataSet = constructGyroDataSet(gyroEntries, accChart,"accelerometer");

        constructLineData(gyroDataSet, gyroChart);


    }

    // ==========================================================================
    // configure the plot format
    private void configurePlotBasic(LineChart thisPlot, String description){
        // this is to configure the plot

        // set descriptions
        // enable description text
        thisPlot.getDescription().setEnabled(true);
        thisPlot.getDescription().setText(description);
//        thisPlot.getDescription().setText("Heart Rate Signal");

        // enable touch gestures
        thisPlot.setTouchEnabled(true);
        // enable scaling and dragging
        thisPlot.setDragEnabled(true);
        thisPlot.setScaleEnabled(true);
        thisPlot.setDrawGridBackground(true);
        // if disabled, scaling can be done on x- and y-axis separately
        thisPlot.setPinchZoom(true);
        // set an alternative background color
        thisPlot.setBackgroundColor(Color.WHITE);

    }

    private void configureLegendsAndAxes(LineChart thisPlot){
        // get the legend (only possible after setting data)
        Legend l = thisPlot.getLegend();
        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);


        XAxis xl = thisPlot.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = thisPlot.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
//        leftAxis.setDrawGridLines(false);
//        leftAxis.setAxisMaximum(10f);
//        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = thisPlot.getAxisRight();
        rightAxis.setEnabled(false);

        thisPlot.getAxisLeft().setDrawGridLines(false);
        thisPlot.getXAxis().setDrawGridLines(false);
        thisPlot.setDrawBorders(false);

    }
    // end of the plot format configuration
    // ==========================================================================

    // this will give the plot empty data set
    private LineData initEmptyData(LineChart thisPlot, ArrayList<String> data ){

        LineData thisData = new LineData();
        thisData.setValueTextColor(Color.WHITE);

        // add empty data
        thisPlot.setData(thisData);


        return thisData;
    }





    // ==========================================================================
    // here are the steps to construct data using DB and display them
    // 1. get array list from DB, always pick  the last 50 points,
    private ArrayList<ArrayList<String>> getDataFromHRDB() {
        try {
            ArrayList<ArrayList<String>> hr = mydb.getAllCotacts();

            Log.d("HR data", hr.toString());
            return hr;
        }catch (Exception e){

            Log.d("HR data", "Database not initialized");
        };
        // if no data, return empty
        return new ArrayList<ArrayList<String>>();
    };
    private ArrayList<ArrayList<String>> getDataFromTempDB() {
        try {
            ArrayList<ArrayList<String>> hr = tmpdb.getAllCotacts();

            Log.d("HR data", hr.toString());
            return hr;
        }catch (Exception e){

            Log.d("HR data", "Database not initialized");
        };
        // if no data, return empty
        return new ArrayList<ArrayList<String>>();
    };
    private ArrayList<ArrayList<String>> getDataFromAccDB() {
        try {
            ArrayList<ArrayList<String>> hr = acdb.getAllCotacts();

            Log.d("HR data", hr.toString());
            return hr;
        }catch (Exception e){

            Log.d("HR data", "Database not initialized");
        };
        // if no data, return empty
        return new ArrayList<ArrayList<String>>();
    };
    private ArrayList<ArrayList<String>> getDataFromGyroDB() {
        try {
            ArrayList<ArrayList<String>> hr = gydb.getAllCotacts();

            Log.d("HR data", hr.toString());
            return hr;
        }catch (Exception e){

            Log.d("HR data", "Database not initialized");
        };
        // if no data, return empty
        return new ArrayList<ArrayList<String>>();
    };
    // how to know that there are new data????


    // 2. construct entries using this data
    private  ArrayList<Entry> constructEntries(ArrayList<String> data){
        ArrayList<Entry> entries = new ArrayList<Entry>();
        Integer i = 0;

        for (String thisData : data) {
            // turn your data into Entry objects
            Integer x =  i; i++;
            Integer y = 0;
            try {
                y = Integer.parseInt(thisData);
            } catch (Exception e ){
                Log.d("Real Time data from DB", "Wrong Data");
            }
            entries.add(new Entry(x, y));

        };
        Log.d("entries from DB data", entries.toString());
        return entries;
    }

    //    private  List<Entry> constructEntries(ArrayList<String> data){
//        List<Entry> entries = new ArrayList<Entry>();
//        Integer i = 0;
//
//        for (String thisData : data) {
//            // turn your data into Entry objects
//            Integer x =  i; i++;
//            Integer y = 0;
//            try {
//                y = Integer.parseInt(thisData);
//            } catch (Exception e ){
//                Log.d("Real Time data from DB", "Wrong Data");
//            }
//            entries.add(new Entry(x, y));
//
//        };
//        Log.d("entries from DB data", entries.toString());
//        return entries;
//    }



    // 3. construct data set, using  entries, set up the data set style as well.
    // initialize dataset, if there's no data in the plot
    private static LineDataSet createSet(String dataSetLabel, int colorId) {

        LineDataSet set = new LineDataSet(null, dataSetLabel); //"Dynamic Data"
        // configure dataset display info
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        // need to set color
//        set.setColor(Color.MAGENTA);
        set.setColor(colorId);


        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        // To show values of each point
        set.setDrawValues(false);
        return set;
    }

    private LineDataSet constructHRDataSet(List<Entry> data, LineChart thisPlot, String label){
        int colorID = Color.RED;
        if (thisPlot == tempChart) {
            colorID = Color.GREEN;
        }
        LineDataSet dataSet = createSet(label, colorID);
        for (Entry thisEntry : data) {
            dataSet.addEntry(thisEntry);
        }
        return dataSet;
    }
    // this is for GYRO/ACC data set construction
    private ArrayList<ILineDataSet> constructGyroDataSet(ArrayList<ArrayList<Entry>> data, LineChart thisPlot, String label){

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        Integer i = 0;
        int colorID = Color.RED;


        for (List<Entry> thisList : data){
            if (i == 1){
                colorID = Color.YELLOW;
            } else if (i == 2) {
                colorID = Color.BLUE;
            }
            LineDataSet dataSet = createSet(label, colorID);
            for (Entry thisEntry : thisList) {
                dataSet.addEntry(thisEntry);
            }
            dataSets.add(dataSet);
            i++;
        }
        return dataSets;
    }

    // 4. construct Line Data using the dataset, here we display the data.
    private void constructLineData(LineDataSet dataSet, LineChart thisPlot){
        LineData lineData = new LineData(dataSet);
        thisPlot.setData(lineData);
        thisPlot.invalidate(); // refresh

        // these might not work.
        // limit the number of visible entries
        thisPlot.setVisibleXRangeMaximum(50);
        // move to the latest entry
        thisPlot.moveViewToX(lineData.getEntryCount());

    }

    private void constructLineData(ArrayList<ILineDataSet> dataSet, LineChart thisPlot){
        LineData lineData = new LineData(dataSet);
        thisPlot.setData(lineData);
        thisPlot.invalidate(); // refresh

        // these might not work.
        // limit the number of visible entries
        thisPlot.setVisibleXRangeMaximum(50);
        // move to the latest entry
        thisPlot.moveViewToX(lineData.getEntryCount());

    }

    // 5. add new entry. this is used in thread for real time plot
    // this is for heart rate and temperature
    public static void addEntry(LineChart thisPlot, String dataSetLabel, Integer thisValue) {
        // this is only for one line
        int colorID = Color.RED;
        if (thisPlot == tempChart) {
            colorID = Color.GREEN;
        }
        LineData data = thisPlot.getData();
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well
            if (set == null) {
                set = createSet(dataSetLabel, colorID);
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), thisValue), 0);
//            data.addEntry(new Entry(set.getEntryCount(), event.values[0] + 5), 0);

            // let the chart know it's data has changed
            data.notifyDataChanged();
            thisPlot.notifyDataSetChanged();

            // limit the number of visible entries
            thisPlot.setVisibleXRangeMaximum(150);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            thisPlot.moveViewToX(data.getEntryCount());

        }
    }
    // this is adding new float value, for temperature
    public static void addEntry(LineChart thisPlot, String dataSetLabel, float thisValue) {
        // this is only for one line

        LineData data = thisPlot.getData();
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well
            if (set == null) {
                set = createSet(dataSetLabel, Color.GREEN);
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), thisValue), 0);
//            data.addEntry(new Entry(set.getEntryCount(), event.values[0] + 5), 0);

            // let the chart know it's data has changed
            data.notifyDataChanged();
            thisPlot.notifyDataSetChanged();

            // limit the number of visible entries
            thisPlot.setVisibleXRangeMaximum(100);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            thisPlot.moveViewToX(data.getEntryCount());

        }
    }
    // this is for gyroscope and accelerometer
    public static void addEntry(LineChart thisPlot, String dataSetLabel, ArrayList<Integer> Values) {
        // this is only for one line

        LineData data = thisPlot.getData();
        if (data != null) {

            Integer i = 0;
            int colorID = Color.RED;

            for (Integer thisValue : Values){
                if (i == 1){
                    colorID = Color.YELLOW;
                } else if (i == 2) {
                    colorID = Color.BLUE;
                }
                ILineDataSet set = data.getDataSetByIndex(i);
                if (set == null) {
                    set = createSet(dataSetLabel, colorID);
                    data.addDataSet(set);
                }
                data.addEntry(new Entry(set.getEntryCount(), thisValue), i);
                i++;
            }
            // let the chart know it's data has changed
            data.notifyDataChanged();
            thisPlot.notifyDataSetChanged();

            // limit the number of visible entries
            thisPlot.setVisibleXRangeMaximum(100);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            thisPlot.moveViewToX(data.getEntryCount());

        }
    }

    // ==========================================================================
    private void constructDataSet(List<Entry> data, LineChart thisPlot, String label, Integer dataSetIndex ) {

//        dataSetIndex = 0 should be the default value

//        LineDataSet dataSet = new LineDataSet(data, label); // add entries to data set

        LineData dataSet = thisPlot.getData();

        if (dataSet != null) {
            ILineDataSet set = dataSet.getDataSetByIndex(dataSetIndex); // this is the first line
            if (set == null) {
                set = createSet(label, Color.BLACK); // dataset is configured already
                dataSet.addDataSet(set);
            }

            // now add new entries to this line
            for (Entry thisEntry : data) {
                dataSet.addEntry(thisEntry, dataSetIndex);
            }

            // let the chart know it's data has changed
            dataSet.notifyDataChanged();
            thisPlot.notifyDataSetChanged();

            // limit the number of visible entries
            thisPlot.setVisibleXRangeMaximum(50);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            thisPlot.moveViewToX(dataSet.getEntryCount());

            thisPlot.invalidate(); // refresh


        }

    }





}
