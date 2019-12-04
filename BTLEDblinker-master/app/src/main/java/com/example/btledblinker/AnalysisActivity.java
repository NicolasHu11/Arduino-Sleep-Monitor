package com.example.btledblinker;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.btledblinker.MainActivity.gydb;

public class AnalysisActivity extends AppCompatActivity {

    public static LineChart movementCountPlot;
    private Button bluetooth_activity;
    private TextView mSleeptimeBuffer;
    private TextView mTotaltimeBuffer;



    // this activity analyze the raw signals and store that in to

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);
        bluetooth_activity = (Button) findViewById(R.id.Bluetooth1);
        mSleeptimeBuffer = (TextView) findViewById(R.id.textViewsleep);
        mTotaltimeBuffer = (TextView) findViewById(R.id.textViewtotal);

        // 1. initialize all five plots, and have initial settings
        LineChart heartRateProPlot = (LineChart) findViewById(R.id.heart_rate_processed);
//        configurePlotBasic(heartRateProPlot, "Heart Rate Processed");
//        configureLegendsAndAxes(heartRateProPlot);
//        configureData(heartRateProPlot); // here data is empty

        LineChart breathRatePlot= (LineChart) findViewById(R.id.breath_rate);


        movementCountPlot = (LineChart) findViewById(R.id.movement_count);


        BarChart sleep24hPlot = (BarChart) findViewById(R.id.sleep_24h);

        BarChart sleepWeekPlot = (BarChart) findViewById(R.id.sleep_week);

        processMovement();


        bluetooth_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 2. process all the data here.






        // 3. display data on the Analysis screen




    }



    private void processHeartRate() {

    }


    private void processTemperature() {

    }


    private void processMovement() {
        // get data from DB, as String
        ArrayList<ArrayList<String>> gyro_data = gydb.getAllCotacts();
        // turn all String into Int.
        ArrayList<Integer> x = new ArrayList<Integer>();
        for(String a:gyro_data.get(0))
        {
            x.add(Integer.parseInt(a));
        }
        ArrayList<Integer> y = new ArrayList<Integer>();
        for(String a:gyro_data.get(1))
        {
            y.add(Integer.parseInt(a));
        }
        ArrayList<Integer> z = new ArrayList<Integer>();
        for(String a:gyro_data.get(2))
        {
            z.add(Integer.parseInt(a));
        }
        // turn timestamp string to Date
        SimpleDateFormat formatter =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ArrayList<Date> time = new ArrayList<Date>();
        for(String a:gyro_data.get(3))
        {
            try{
                time.add(formatter.parse(a));
            }
            catch(java.text.ParseException e){

            }
        }
        // process the XYZ data
        ArrayList<Integer> x_delta = new ArrayList<Integer>();
        for(int i = 1; i< x.size(); i++)
        {
            x_delta.add(x.get(i)-x.get(i-1));
        }
        ArrayList<Integer> y_delta = new ArrayList<Integer>();
        for(int i = 1; i< y.size(); i++)
        {
            y_delta.add(y.get(i)-y.get(i-1));
        }
        ArrayList<Integer> z_delta = new ArrayList<Integer>();
        for(int i = 1; i< z.size(); i++)
        {
            z_delta.add(z.get(i)-z.get(i-1));
        }
        ArrayList<Integer> total_difference = new ArrayList<Integer>();

        for(int i = 1; i< x.size();i++)
        {
            total_difference.add(Math.abs(x.get(i)-x.get(i-1))+Math.abs(y.get(i)-y.get(i-1))+Math.abs(z.get(i)-z.get(i-1)));
        }
        // get movement data, 1 for movement, 0 for non
        ArrayList <Integer> movement = new ArrayList<Integer>();
        for(int i =0; i<total_difference.size();i++)
        {
            if(total_difference.get(i)<10000) // hard threshold
                movement.add(0);
            else
                movement.add(1);
        }

        // process to get sleep data.
        ArrayList<Boolean> sleep = new ArrayList<Boolean>();
//        int check;
        for(int i =0; i<time.size()-1;i++) {
            for (int j = i; j < time.size() - 1; j++)
                if (calculatetimediff(time.get(j), time.get(i)) < 10) {
                    if (movement.get(j) == 1) {
                        sleep.add(false);
                        break;
                    }
                } else {
                    sleep.add(true);
                    break;
                }
        }

        Log.d("sleeplength",String.valueOf(sleep.size()));
        Log.d("timelength",String.valueOf(time.size()));

        //
        long sleeptime=0;
        long totaltime=0;
//        long totaltime= calculatetimediff(time.get(time.size()-1),time.get(0));
        for(int i=1; i < sleep.size();i++){
            totaltime=totaltime+calculatetimediff(time.get(i),time.get(i-1));
            if(sleep.get(i) && sleep.get(i-1))
                sleeptime=sleeptime+calculatetimediff(time.get(i),time.get(i-1));
        }
        sleeptime=sleeptime;
        totaltime=totaltime;
        mSleeptimeBuffer.setText(String.valueOf(sleeptime));
        mTotaltimeBuffer.setText(String.valueOf(totaltime));





        // plotting
        List<Entry> hrEntries = constructEntries(total_difference);
        LineDataSet hrDataSet = constructHRDataSet(hrEntries, movementCountPlot,"movement");
        constructLineData(hrDataSet, movementCountPlot);



    }

    private void processSleep24h() {

    }
    private void processSleepWeek() {

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
        thisPlot.setDrawGridBackground(false);
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
        l.setTextColor(Color.WHITE);


        XAxis xl = thisPlot.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = thisPlot.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);
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


    // 2. construct entries using this data
    private  List<Entry> constructEntries(ArrayList<Integer> data){
        List<Entry> entries = new ArrayList<Entry>();
        Integer i = 0;

        // this is for multiple lines
//        for (ArrayList<String> thisArray : data) {
//
//
//        }

        for (Integer thisData : data) {
            // turn your data into Entry objects
            Integer x =  i; i++;
            Integer y = thisData;
//            try {
//                y = Integer.parseInt(thisData);
//            } catch (Exception e ){
//                Log.d("Real Time data from DB", "Wrong Data");
//            }
            entries.add(new Entry(x, y));

        };
        Log.d("entries from DB data", entries.toString());
        return entries;
    }

    // 3. construct data set, using  entries, set up the data set style as well.
    // initialize dataset, if there's no data in the plot
    private static LineDataSet createSet(String dataSetLabel) {

        LineDataSet set = new LineDataSet(null, dataSetLabel); //"Dynamic Data"
        // configure dataset display info
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.MAGENTA);
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
        LineDataSet dataSet = createSet(label);
        for (Entry thisEntry : data) {
            dataSet.addEntry(thisEntry);
        }
        return dataSet;
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

    public long calculatetimediff(Date date1, Date date2){
        long diff = date1.getTime()-date2.getTime();
        long diffSeconds = diff / 1000 % 60;
        return diffSeconds;
    }

}
