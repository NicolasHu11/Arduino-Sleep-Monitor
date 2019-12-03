package com.example.btledblinker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;

public class AnalysisActivity extends AppCompatActivity {

    // this activity analyze the raw signals and store that in to

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        // 1. initialize all five plots, and have initial settings
        LineChart heartRateProPlot = (LineChart) findViewById(R.id.heart_rate_processed);
        // these need to re-implement in this class
//        configurePlotBasic(heartRateProPlot, "Heart Rate Processed");
//        configureLegendsAndAxes(heartRateProPlot);
//        configureLegendsAndAxesData(heartRateProPlot); // here data is empty

        LineChart breathRatePlot= (LineChart) findViewById(R.id.breath_rate);


        LineChart movementCountPlot = (LineChart) findViewById(R.id.movement_count);


        BarChart sleep24hPlot = (BarChart) findViewById(R.id.sleep_24h);

        BarChart sleepWeekPlot = (BarChart) findViewById(R.id.sleep_week);

        // 2. process all the data here.







        // 3. display data on the Analysis screen




    }



    private void processHeartRate() {

    }


    private void processTemperature() {

    }


    private void processMovement() {

    }

    private void processSleep24h() {

    }
    private void processSleepWeek() {

    }




}
