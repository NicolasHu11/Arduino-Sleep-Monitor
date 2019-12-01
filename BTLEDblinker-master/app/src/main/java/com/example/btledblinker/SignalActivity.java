package com.example.btledblinker;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;



public class SignalActivity extends AppCompatActivity {

    // ref: https://www.youtube.com/watch?v=QEbljbZ4dNs
    // ref: https://pusher.com/tutorials/graph-android

    private LineChart hrChart;
    private Thread thread;
    private boolean plotData = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signal);

        // heart rate plot
        hrChart = (LineChart) findViewById(R.id.heart_rate_signal);
        configurePlotBasic(hrChart, "Heart Rate Signal");
        configureLegendsAndAxes(hrChart);
        configureData(hrChart); // here data is empty

        if(plotData){
            addEntry(hrChart, "Heart Rate Data");
            plotData = false;
        }

        // Temperature Plot
        LineChart tempChart = (LineChart) findViewById(R.id.temperature_signal);
        configurePlotBasic(tempChart, "Temperature Signal");
        configureLegendsAndAxes(tempChart);
        configureData(tempChart); // here data is empty

        if(plotData){
            addEntry(tempChart, "Temperature Data");
            plotData = false;
        }




    }


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
        leftAxis.setAxisMaximum(10f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = thisPlot.getAxisRight();
        rightAxis.setEnabled(false);

        thisPlot.getAxisLeft().setDrawGridLines(false);
        thisPlot.getXAxis().setDrawGridLines(false);
        thisPlot.setDrawBorders(false);

    }

    private LineData configureData(LineChart thisPlot){

        LineData thisData = new LineData();
        thisData.setValueTextColor(Color.WHITE);

        // add empty data
        thisPlot.setData(thisData);

        return thisData;
    }


    private LineDataSet createSet(String dataSetLabel) {

        LineDataSet set = new LineDataSet(null, dataSetLabel); //"Dynamic Data"
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.MAGENTA);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        // To show values of each point
        set.setDrawValues(true);



        return set;
    }

    private void addEntry(LineChart thisPlot, String dataSetLabel) {

        LineData data = thisPlot.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet(dataSetLabel);
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 80) + 10f), 0);
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


}
