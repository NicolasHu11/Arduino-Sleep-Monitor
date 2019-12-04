package com.example.btledblinker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;

import static com.example.btledblinker.MainActivity.acdb;
import static com.example.btledblinker.MainActivity.gydb;
import static com.example.btledblinker.MainActivity.mydb;
import static com.example.btledblinker.MainActivity.tmpdb;

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

        ArrayList<ArrayList<String>> gyro_data = gydb.getAllCotacts();
        ArrayList<ArrayList<String>> ac_data = acdb.getAllCotacts();
        ArrayList<ArrayList<String>> temp_data = tmpdb.getAllCotacts();
        ArrayList<ArrayList<String>> hr_data = mydb.getAllCotacts();


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
    }

    private void processSleep24h() {

    }
    private void processSleepWeek() {

    }




}
