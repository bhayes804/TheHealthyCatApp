package wsu.group18.thehealthycat;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
//import android.support.v7.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.text.DateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ChartActivity extends AppCompatActivity {
    private LineChart mChart;
    ArrayList<Entry> weights = new ArrayList<>();
    float targetWeight = 0;
    int numberOfDays = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        mChart = findViewById(R.id.chart);
        mChart.setTouchEnabled(true);
        mChart.setPinchZoom(true);
        mChart.getDescription().setText("");
        MyMarkerView mv = new MyMarkerView(getApplicationContext(), R.layout.custom_marker_view);
        mv.setChartView(mChart);
        mChart.setMarker(mv);
        Spinner timePeriod = findViewById(R.id.spinner);
        String[] choices = new String[]{"1 Week", "1 Month", "6 Months", "1 Year"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, choices);
        timePeriod.setAdapter(adapter);
        timePeriod.setOnItemSelectedListener(new SpinnerActivity());
        renderData();
    }

    public void renderData() {
        setData();

        //dropdown menu on how far back to look at weights
        float chartPeriod = (numberOfDays * 24 * 3600);

        //create chart
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter((new MyXAxisValueFormatter()));
        xAxis.setLabelCount(5, true);
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setAxisMaximum((float) weights.get(weights.size()-1).getX());
        xAxis.setAxisMinimum(weights.get(weights.size()-1).getX() - chartPeriod);
        xAxis.setDrawLimitLinesBehindData(true);
        mChart.notifyDataSetChanged();
        mChart.invalidate();

        LimitLine targetWeightLine = new LimitLine(targetWeight, "Target Weight");
        targetWeightLine.setLineWidth(4f);
        targetWeightLine.enableDashedLine(10f, 10f, 0f);
        targetWeightLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        targetWeightLine.setTextSize(10f);

        //find max and min weights to center the chart
        float maxWeight = weights.get(0).getY();
        float minWeight = weights.get(0).getY();
        for(Entry e : weights) {
            if (e.getY() > maxWeight)
                maxWeight = e.getY();
            if(e.getY() < minWeight)
                minWeight = e.getY();
        }

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(targetWeightLine);
        leftAxis.setAxisMaximum(maxWeight * 1.25f);
        leftAxis.setAxisMinimum(minWeight / 1.25f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawLimitLinesBehindData(false);

        mChart.getAxisRight().setEnabled(false);
    }

    private void setData() {

        Cat cat;

        //get data for historic weights
        //manual data entered for now to test chart

        weights = (ArrayList)getIntent().getSerializableExtra("CAT_HISTORICAL_WEIGHTS");

        //get value for target weight
        //manual value for now to test chart

        targetWeight = (float)getIntent().getDoubleExtra("CAT_TARGET_WEIGHT", 0.0f);

        //create data set of past weights
        LineDataSet weightData;
        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            weightData = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            weightData.setValues(weights);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            weightData = new LineDataSet(weights, getIntent().getStringExtra("CAT_NAME") + "'s weight");
            weightData.setDrawIcons(false);
            weightData.enableDashedLine(10f, 5f, 0f);
            weightData.enableDashedHighlightLine(10f, 5f, 0f);
            weightData.setColor(Color.DKGRAY);
            weightData.setCircleColor(Color.DKGRAY);
            weightData.setDrawValues(false);
            weightData.setLineWidth(1f);
            weightData.setCircleRadius(3f);
            weightData.setDrawCircleHole(false);
            weightData.setValueTextSize(9f);
            weightData.setDrawFilled(true);
            weightData.setFormLineWidth(1f);
            weightData.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            weightData.setFormSize(15.f);

            if (Utils.getSDKInt() >= 18) {
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_blue);
                weightData.setFillDrawable(drawable);
            } else {
                weightData.setFillColor(Color.DKGRAY);
            }
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(weightData);
            LineData data = new LineData(dataSets);
            mChart.setData(data);

        }
    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            Date timeInMilliseconds = new Date((long)value * 1000);
            DateFormat dateTimeFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());

            return dateTimeFormat.format(timeInMilliseconds);
        }
    }

    public class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)
            if(parent.getItemAtPosition(pos).equals("1 Week")) {
                numberOfDays = 7;
            }
            else if(parent.getItemAtPosition(pos).equals("1 Month")) {
                numberOfDays = 30;
            }
            else if(parent.getItemAtPosition(pos).equals("6 Months")) {
                numberOfDays = 180;
            }
            else if (parent.getItemAtPosition(pos).equals("1 Year")) {
                numberOfDays = 365;
            }

            renderData();

        }

        public void onNothingSelected(AdapterView<?> parent) {
            numberOfDays = 7;
        }
    }
}