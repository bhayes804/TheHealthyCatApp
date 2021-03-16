package wsu.group18.thehealthycat;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
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
import java.util.Date;
import java.util.Locale;

public class ChartActivity extends AppCompatActivity {
    private LineChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        mChart = findViewById(R.id.chart);
        mChart.setTouchEnabled(true);
        mChart.setPinchZoom(true);
        MyMarkerView mv = new MyMarkerView(getApplicationContext(), R.layout.custom_marker_view);
        mv.setChartView(mChart);
        mChart.setMarker(mv);
        renderData();
    }

    ArrayList<Entry> weights = new ArrayList<>();

    float targetWeight = 0;

    public void renderData() {
        setData();

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter((new MyXAxisValueFormatter()));
        xAxis.setLabelCount(3, true);
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setAxisMaximum((float) weights.get(weights.size()-1).getX());
        xAxis.setAxisMinimum(weights.get(0).getX());
        xAxis.setDrawLimitLinesBehindData(true);

        LimitLine targetWeightLine = new LimitLine(targetWeight, "Target Weight");
        targetWeightLine.setLineWidth(4f);
        targetWeightLine.enableDashedLine(10f, 10f, 0f);
        targetWeightLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        targetWeightLine.setTextSize(10f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(targetWeightLine);
        leftAxis.setAxisMaximum(25f);
        leftAxis.setAxisMinimum(0f);
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
//        float dif = weights.get(1).getX() - weights.get(0).getX();
//
//        for(int i = 0; i < 25; i++) {
//            weights.add(new Entry(weights.get(0).getX() + ((3+i) * dif), (float)(Math.random() * 5) + 10));
//        }

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
            weightData = new LineDataSet(weights, getIntent().getStringExtra("CAT_NAME"));
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
}