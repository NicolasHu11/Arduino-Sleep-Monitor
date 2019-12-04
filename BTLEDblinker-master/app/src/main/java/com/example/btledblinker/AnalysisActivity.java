package com.example.btledblinker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.lang.Math;

import static com.example.btledblinker.MainActivity.gydb;

public class AnalysisActivity extends AppCompatActivity {
    private Button bluetooth_activity;


    // this activity analyze the raw signals and store that in to

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);
        bluetooth_activity = (Button) findViewById(R.id.Bluetooth1);

        // 1. initialize all five plots, and have initial settings
        LineChart heartRateProPlot = (LineChart) findViewById(R.id.heart_rate_processed);
//        configurePlotBasic(heartRateProPlot, "Heart Rate Processed");
//        configureLegendsAndAxes(heartRateProPlot);
//        configureData(heartRateProPlot); // here data is empty

        LineChart breathRatePlot= (LineChart) findViewById(R.id.breath_rate);


        LineChart movementCountPlot = (LineChart) findViewById(R.id.movement_count);


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
        ArrayList<ArrayList<String>> gyro_data = gydb.getAllCotacts();
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


    }

    private void processSleep24h() {

    }
    private void processSleepWeek() {

    }





}
