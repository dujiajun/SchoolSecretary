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
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resId, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_name = (TextView) view.findViewById(R.id.item_exam_name);
            viewHolder.tv_score = (TextView) view.findViewById(R.id.item_exam_score);
            viewHolder.tv_rank = (TextView) view.findViewById(R.id.item_exam_rank);
            viewHolder.tv_classname = (TextView) view.findViewById(R.id.item_exam_classname);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tv_name.setText(exam.getStudentName());
        viewHolder.tv_score.setText("分数：" + String.valueOf(exam.getScore()));
        viewHolder.tv_rank.setText("排名：" + String.valueOf(exam.getRank()));
        viewHolder.tv_classname.setText(exam.getClassname());
        return view;
    }

    class ViewHolder {
        TextView tv_name, tv_rank, tv_score, tv_classname;
    }
}
