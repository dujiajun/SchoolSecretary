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

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.util.ChartUtils;
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
        List<AxisValue> axisXValues = new ArrayList<>();
        List<AxisValue> axisYValues = new ArrayList<>();
        List<AxisValue> axisRValues = new ArrayList<>();
        List<Integer> scores = new ArrayList<>();
        List<Integer> ranks = new ArrayList<>();
        int maxR = 0, maxY = 0;
        Intent i = getIntent();
        std_name = i.getStringExtra("stdname");
        classname = i.getStringExtra("classname");
        dbHelper = new MyDatabaseHelper(this, "student.db", null, 3);
        db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query("exams", null
                , "stdname = ? and classname = ?", new String[]{std_name, classname}, null, null, null);
        if (cursor.moveToFirst()) {
            if (cursor.getCount() == 1) {
                Toast.makeText(ChartActivity.this, "仅有一次成绩记录，无法做出统计图", Toast.LENGTH_SHORT).show();
                finish();
            }
            int j = 0;
            do {
                String examname = cursor.getString(cursor.getColumnIndex("examname"));
                //Toast.makeText(ChartActivity.this, examname, Toast.LENGTH_SHORT).show();
                int rank = cursor.getInt(cursor.getColumnIndex("rank"));
                int score = cursor.getInt(cursor.getColumnIndex("score"));
                //maxY = Math.max(maxY,score);
                maxR = Math.max(maxR, rank);
                maxY = Math.max(maxR, score);
                ranks.add(rank);
                scores.add(score);
                //PointValue point;
                //point = new PointValue(j, rank);
                //point.setLabel(String.valueOf(rank));
                axisXValues.add(new AxisValue(j).setLabel(examname));
                //value_rank.add(point);
                //point = new PointValue(j, score);
                //point.setLabel(String.valueOf(score));
                //value_score.add(point);
                j++;
            } while (cursor.moveToNext());

            cursor.close();
        } else {
            Toast.makeText(ChartActivity.this, "还没有 " + std_name + " 的成绩记录", Toast.LENGTH_SHORT).show();
            finish();
        }

        Axis axisY;
        Axis axisX = new Axis(axisXValues);
        Axis axisR;

        float scale = 0;
        if (maxY > maxR) {
            scale = maxY / maxR;
            axisY = new Axis();

            for (int j = 0; j < ranks.size(); j++) {
                int rank = ranks.get(j);
                int score = scores.get(j);
                axisRValues.add(new AxisValue(rank * scale).setLabel(String.valueOf(rank)));
                value_rank.add(new PointValue(j, rank * scale).setLabel(String.valueOf(rank)));
                value_score.add(new PointValue(j, score).setLabel(String.valueOf(score)));
            }
            axisR = new Axis(axisRValues);

        } else if (maxR > maxY) {
            scale = maxR / maxY;
            axisR = new Axis();

            for (int j = 0; j < scores.size(); j++) {
                int rank = ranks.get(j);
                int score = scores.get(j);
                axisYValues.add(new AxisValue(score * scale).setLabel(String.valueOf(score)));
                value_rank.add(new PointValue(j, rank).setLabel(String.valueOf(rank)));
                value_score.add(new PointValue(j, score * scale).setLabel(String.valueOf(score)));
            }
            axisY = new Axis(axisYValues);
        } else {
            axisY = new Axis();
            axisR = new Axis();
            for (int j = 0; j < scores.size(); j++) {
                int rank = ranks.get(j);
                int score = scores.get(j);
                value_rank.add(new PointValue(j, rank).setLabel(String.valueOf(rank)));
                value_score.add(new PointValue(j, score).setLabel(String.valueOf(score)));
            }
        }

        Line lineS = new Line(value_score);
        //lineS.setColor(Color.(R.color.chart_line1));
        lineS.setColor(ChartUtils.COLORS[0]);
        lineS.setHasLabels(true);

        Line lineR = new Line(value_rank);
        //lineR.setColor(getResources().getColor(R.color.chart_line2));
        lineR.setColor(ChartUtils.COLORS[1]);
        lineR.setHasLabels(true);

        List<Line> lines = new ArrayList<>();
        lines.add(lineS);
        lines.add(lineR);

        LineChartData data = new LineChartData();

        data.setLines(lines);

        axisY.setName("成绩");
        axisY.setTextColor(ChartUtils.COLORS[0]);


        axisX.setName("考试");
        axisX.setTextColor(Color.BLACK);
        axisX.setHasTiltedLabels(true);


        axisR.setName("排名");
        axisR.setTextColor(ChartUtils.COLORS[1]);

        //axisR.setMaxLabelChars((maxR%100)*100+100);


        data.setAxisYLeft(axisY);
        data.setAxisXBottom(axisX);
        data.setAxisYRight(axisR);
        chart.setLineChartData(data);
        //chart.setScrollEnabled(true);
        chart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
    }
}
