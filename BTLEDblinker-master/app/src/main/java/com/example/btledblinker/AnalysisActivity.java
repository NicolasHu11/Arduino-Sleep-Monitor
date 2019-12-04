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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.btledblinker.MainActivity.gydb;
import static com.example.btledblinker.MainActivity.mydb;
import static com.example.btledblinker.MainActivity.tmpdb;

public class AnalysisActivity extends AppCompatActivity {

    public static LineChart movementCountPlot;
    public static LineChart heartRateProPlot;
    public static LineChart breathRatePlot;
    private Button bluetooth_activity;
    private TextView mSleeptimeBuffer;
    private TextView mTotaltimeBuffer;
    public float sleepPercent = 1;


    public static PieChart sleepPlot;




    // this activity analyze the raw signals and store that in to

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);
        bluetooth_activity = (Button) findViewById(R.id.Bluetooth1);
        mSleeptimeBuffer = (TextView) findViewById(R.id.textViewsleep);
        mTotaltimeBuffer = (TextView) findViewById(R.id.textViewtotal);

        // 1. initialize all five plots, and have initial settings
        heartRateProPlot = (LineChart) findViewById(R.id.heart_rate_processed);
//        configurePlotBasic(heartRateProPlot, "Heart Rate Processed");
//        configureLegendsAndAxes(heartRateProPlot);
//        configureData(heartRateProPlot); // here data is empty

//         LineChart breathRatePlot= (LineChart) findViewById(R.id.breath_rate);
        PieChart sleep24hPlot  =(PieChart) findViewById(R.id.sleep_24h_piechart);
        breathRatePlot= (LineChart) findViewById(R.id.breath_rate);
        movementCountPlot = (LineChart) findViewById(R.id.movement_count);
//        BarChart sleep24hPlot = (BarChart) findViewById(R.id.sleep_24h);
//        BarChart sleepWeekPlot = (BarChart) findViewById(R.id.sleep_week);

        processMovement();
        processHeartRate();
        processTemperature();


        bluetooth_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 2. process all the data here.






        // 3. display data on the Analysis screen


        // ==================================================================
        // this is for pie plot
        Description pieDesc = new Description();
        pieDesc.setText("pie chart for sleep ");
        pieDesc.setTextSize(20f);
        sleep24hPlot.setDescription(pieDesc);

        sleep24hPlot.setHoleRadius(30f);
        sleep24hPlot.setTransparentCircleRadius(30f);

        sleep24hPlot.setUsePercentValues(true);
        List<PieEntry> pieValue = new ArrayList<>();
        pieValue.add(new PieEntry(sleepPercent, "sleep"));
        pieValue.add(new PieEntry(1-sleepPercent, "awake"));

        PieDataSet pieDataSet = new PieDataSet(pieValue, "");
        PieData pieData = new PieData(pieDataSet);
        sleep24hPlot.setData(pieData);

        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        sleep24hPlot.animateXY(1400,1400);
        sleep24hPlot.invalidate(); // refresh
        // end of pit plot
        // ==================================================================

    }



    private void processHeartRate() {
        ArrayList<ArrayList<String>> hr_data = mydb.getAllCotacts();
        ArrayList<Integer> hr = new ArrayList<Integer>();
        for (int i =0; i <hr_data.get(0).size(); i++)
        {
            hr.add(Integer.parseInt(hr_data.get(0).get(i)));
        }
        SimpleDateFormat formatter =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ArrayList<Date> time = new ArrayList<Date>();
        for(String a:hr_data.get(1))
        {
            try{
                time.add(formatter.parse(a));
            }
            catch(java.text.ParseException e){

            }
        }
        ArrayList<Integer> hrpm = new ArrayList<Integer>();

        int data_count;
        int hr_count;
        for(int i =0; i<time.size()-1;i++) {
            data_count=0;
            hr_count =0;
            for (int j = i; j < time.size() - 1; j++)
                if (calculatetimediff(time.get(j), time.get(i)) < 6) {
                    data_count=data_count+1;
                    hr_count=hr_count+hr.get(j);
                } else {
                    hrpm.add(hr_count/data_count);
//                    Log.d("mpm else ",String.valueOf(movement_count));
                    i=j;
                    break;
                }
        }

        List<Entry> hrEntries = constructEntries(hrpm);
        LineDataSet hrDataSet = constructHRDataSet(hrEntries, heartRateProPlot,"heartrate");
        constructLineData(hrDataSet, heartRateProPlot);

    }


    private void processTemperature() {
        ArrayList<ArrayList<String>> temp_data = tmpdb.getAllCotacts();
        ArrayList<Double> tp = new ArrayList<Double>();
        for (int i =0; i <temp_data.get(0).size(); i++)
        {
            tp.add(Double.parseDouble(temp_data.get(0).get(i)));
        }
        SimpleDateFormat formatter =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ArrayList<Date> time = new ArrayList<Date>();
        for(String a:temp_data.get(1))
        {
            try{
                time.add(formatter.parse(a));
            }
            catch(java.text.ParseException e){

            }
        }
        ArrayList<Double> x_delta = new ArrayList<Double>();
        for(int i = 1; i< tp.size();i++) {
            x_delta.add(tp.get(i) - tp.get(i - 1));
        }
        ArrayList<Double> tp_delta = new ArrayList<Double>();
        for(int i = 1; i< x_delta.size();i++)
        {
            tp_delta.add(x_delta.get(i)-x_delta.get(i-1));
        }
        ArrayList<Boolean> breath = new ArrayList<Boolean>();
        for(int i=0; i< time.size()-2; i++)
        {
            if(tp_delta.get(i)>0)
            {
                for(int j=i; j<time.size()-2;j++)
                {
                    if(tp_delta.get(j)>0) {
                        breath.add(false);
                        continue;
                    }
                    else
                        breath.add(true);
                }
            }
        }

        ArrayList<Integer> bpm = new ArrayList<Integer>();

        int breath_count = 0;
        for(int i =0; i<time.size()-2;i++) {
            breath_count=0;
            for (int j = i; j < time.size() - 2; j++)
                if (calculatetimediff(time.get(j), time.get(i)) < 6) {
                    if (breath.get(j)) {
                        breath_count=breath_count+1;
                    }
                } else {
                    bpm.add(breath_count);
                    Log.d("bpm else ",String.valueOf(breath_count));
                    i=j;
                    break;
                }
        }

        Log.d("bpm after for loop",String.valueOf(bpm.size()));

        List<Entry> hrEntries = constructEntries(bpm);
        LineDataSet hrDataSet = constructHRDataSet(hrEntries, breathRatePlot,"breath");
        constructLineData(hrDataSet, breathRatePlot);



    }


    private void processMovement() {
        ArrayList<ArrayList<String>> gyro_data = gydb.getAllCotacts();
        ArrayList<Integer> x = new ArrayList<Integer>();
        ArrayList<Integer> y = new ArrayList<Integer>();
        ArrayList<Integer> z = new ArrayList<Integer>();
        for (int i =0; i <gyro_data.get(0).size(); i++)
        {
            x.add(Integer.parseInt(gyro_data.get(0).get(i)));
            y.add(Integer.parseInt(gyro_data.get(1).get(i)));
            z.add(Integer.parseInt(gyro_data.get(2).get(i)));
        }
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
        ArrayList<Integer> x_delta = new ArrayList<Integer>();
        ArrayList<Integer> y_delta = new ArrayList<Integer>();
        ArrayList<Integer> z_delta = new ArrayList<Integer>();

        ArrayList<Integer> total_difference = new ArrayList<Integer>();
        for(int i = 1; i< x.size();i++)
        {
            x_delta.add(x.get(i)-x.get(i-1));
            y_delta.add(y.get(i)-y.get(i-1));
            z_delta.add(z.get(i)-z.get(i-1));
            total_difference.add(Math.abs(x.get(i)-x.get(i-1))+Math.abs(y.get(i)-y.get(i-1))+Math.abs(z.get(i)-z.get(i-1)));
        }

        ArrayList <Integer> movement = new ArrayList<Integer>();
        for(int i =0; i<total_difference.size();i++)
        {
            if(total_difference.get(i)<10000)
                movement.add(0);
            else
                movement.add(1);
        }


//        ArrayList<ArrayList<String>> ac_data = acdb.getAllCotacts();
//        ArrayList<Integer> x2 = new ArrayList<Integer>();
//        ArrayList<Integer> y2 = new ArrayList<Integer>();
//        ArrayList<Integer> z2 = new ArrayList<Integer>();
//        for (int i =0; i <ac_data.get(0).size(); i++)
//        {
//            x2.add(Integer.parseInt(ac_data.get(0).get(i)));
//            y2.add(Integer.parseInt(ac_data.get(1).get(i)));
//            z2.add(Integer.parseInt(ac_data.get(2).get(i)));
//        }
//        SimpleDateFormat formatter2 =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        ArrayList<Date> time2 = new ArrayList<Date>();
//        for(String a:ac_data.get(3))
//        {
//            try{
//                time2.add(formatter2.parse(a));
//            }
//            catch(java.text.ParseException e){
//
//            }
//        }
//        ArrayList<Integer> x2_delta = new ArrayList<Integer>();
//        ArrayList<Integer> y2_delta = new ArrayList<Integer>();
//        ArrayList<Integer> z2_delta = new ArrayList<Integer>();
//
//        ArrayList<Integer> total_difference2 = new ArrayList<Integer>();
//        for(int i = 1; i< x2.size();i++)
//        {
//            x2_delta.add(x2.get(i)-x2.get(i-1));
//            y2_delta.add(y2.get(i)-y2.get(i-1));
//            z2_delta.add(z2.get(i)-z2.get(i-1));
//            total_difference2.add(Math.abs(x2.get(i)-x2.get(i-1))+Math.abs(y2.get(i)-y2.get(i-1))+Math.abs(z2.get(i)-z2.get(i-1)));
//        }
//
//        ArrayList <Integer> movement2 = new ArrayList<Integer>();
//        for(int i =0; i<total_difference2.size();i++)
//        {
//            if(total_difference2.get(i)<10000)
//                movement2.add(0);
//            else
//                movement2.add(1);
//        }



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

        sleepPercent = (float)sleeptime/(float)totaltime; // update the sleep percent
        Log.d("after sleep time,", "sleep Percent is" + String.valueOf(sleepPercent));

        ArrayList<Integer> mpm = new ArrayList<Integer>();

        int movement_count = 0;
        for(int i =0; i<time.size()-1;i++) {
            movement_count=0;
            for (int j = i; j < time.size() - 1; j++)

                if (calculatetimediff(time.get(j), time.get(i)) < 6) {
                    if (movement.get(j) == 1) {
                        movement_count=movement_count+1;
                    }
                } else {
                    mpm.add(movement_count);
                    Log.d("mpm else ",String.valueOf(movement_count));
                    i=j;
                    break;
                }
        }

        Log.d("mpm after for loop", "mpm size:"+String.valueOf(mpm.size()));



        // plotting
        List<Entry> hrEntries = constructEntries(mpm);
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