package com.dujiajun.schoolsecretary.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.dujiajun.schoolsecretary.R;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class ChartActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private LineChartView chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        toolbar = (Toolbar) findViewById(R.id.chart_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        chart = (LineChartView) findViewById(R.id.chart);
        List<PointValue> values = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<>();

        Line line = new Line(values);
        line.setColor(R.attr.colorAccent);
        line.setHasLabels(true);

        List<Line> lines = new ArrayList<>();
        lines.add(line);

        LineChartData data = new LineChartData();

        data.setLines(lines);
        Axis axisY = new Axis();
        Axis axisX = new Axis(axisValues);
        axisX.setHasTiltedLabels(true);
        axisX.setHasLines(true);
        data.setAxisYLeft(axisY);
        data.setAxisXBottom(axisX);
        chart.setLineChartData(data);
        //chart.setScrollEnabled(true);
    }
}
