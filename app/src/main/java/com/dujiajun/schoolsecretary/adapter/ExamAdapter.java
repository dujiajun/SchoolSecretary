package com.dujiajun.schoolsecretary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dujiajun.schoolsecretary.model.Exam;
import com.dujiajun.schoolsecretary.R;

import java.util.List;

public class ExamAdapter extends ArrayAdapter<Exam> {

    private int resId;

    public ExamAdapter(Context context, int resource, List<Exam> objects) {
        super(context, resource, objects);
        resId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Exam exam = getItem(position);
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resId, null);
        } else {
            view = convertView;
        }
        TextView tv1 = (TextView) view.findViewById(R.id.item_exam_name);
        TextView tv2 = (TextView) view.findViewById(R.id.item_exam_score);
        TextView tv3 = (TextView) view.findViewById(R.id.item_exam_rank);
        tv1.setText(exam.getStudentName());
        tv2.setText("分数：" + String.valueOf(exam.getScore()));
        tv3.setText("排名：" + String.valueOf(exam.getRank()));
        return view;
    }
}
