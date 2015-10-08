package com.dujiajun.schoolsecretary.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.dujiajun.schoolsecretary.MyDatabaseHelper;
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
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private String std_name, classname;
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
        Toast.makeText(ChartActivity.this, "成绩分析功能完善中", Toast.LENGTH_SHORT).show();
        chart = (LineChartView) findViewById(R.id.chart);
        List<PointValue> value_score = new ArrayList<>();
        List<PointValue> value_rank = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<>();

        Intent i = getIntent();
        std_name = i.getStringExtra("stdname");
        classname = i.getStringExtra("classname");
        dbHelper = new MyDatabaseHelper(this, "student.db", null, 3);
        db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query("exams", null
                , "stdname = ? and classname = ?", new String[]{std_name, classname}, null, null, null);
        if (cursor.moveToFirst()) {
            int j = 1;
            do {
                String examname = cursor.getString(cursor.getColumnIndex("examname"));
                //Toast.makeText(ChartActivity.this, examname, Toast.LENGTH_SHORT).show();
                int rank = cursor.getInt(cursor.getColumnIndex("rank"));
                int score = cursor.getInt(cursor.getColumnIndex("score"));
                PointValue point;
                point = new PointValue(j, rank);
                point.setLabel(String.valueOf(rank));
                axisValues.add(new AxisValue(j).setLabel(examname));
                value_rank.add(point);
                point = new PointValue(j, score);
                point.setLabel(String.valueOf(score));
                value_score.add(point);
                j++;
            } while (cursor.moveToNext());


        } else {
            Toast.makeText(ChartActivity.this, "还没有 " + std_name + " 的成绩记录", Toast.LENGTH_SHORT).show();
            finish();
        }

        Line lineS = new Line(value_score);
        lineS.setColor(R.color.chart_line1);
        lineS.setHasLabels(true);

        Line lineR = new Line(value_rank);
        lineR.setColor(R.color.chart_line2);
        lineR.setHasLabels(true);

        List<Line> lines = new ArrayList<>();
        lines.add(lineS);
        lines.add(lineR);

        LineChartData data = new LineChartData();

        data.setLines(lines);
        Axis axisY = new Axis();
        axisY.setName("成绩");
        Axis axisX = new Axis(axisValues);
        axisX.setName("考试");
        Axis axisR = new Axis();
        axisR.setName("排名");
        axisX.setHasTiltedLabels(true);
        axisX.setHasLines(true);
        data.setAxisYLeft(axisY);
        data.setAxisXBottom(axisX);
        data.setAxisYRight(axisR);
        chart.setLineChartData(data);
        //chart.setScrollEnabled(true);
    }
}
