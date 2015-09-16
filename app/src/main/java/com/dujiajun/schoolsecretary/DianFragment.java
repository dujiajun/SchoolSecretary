package com.dujiajun.schoolsecretary;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DianFragment extends Fragment {
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dian, container, false);
        Init();
        return view;
    }
    private Button dian_btn,dao_btn,weidao_btn;
    private TextView main_text;
    private ArrayList<String> std_names;
    private ArrayList<String> weidao;
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private LinearLayout mLayout;
    public void Init(){

        dbHelper = new MyDatabaseHelper(getActivity(), "student.db", null, 1);
        db = dbHelper.getWritableDatabase();

        std_names = new ArrayList<>();
        weidao = new ArrayList<>();

        Cursor cursor = db.query("Students", null, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                std_names.add(name);
            } while (cursor.moveToNext());
        }
        cursor.close();

        dian_btn = (Button) view.findViewById(R.id.dian_btn);
        dao_btn = (Button) view.findViewById(R.id.dao_btn);
        weidao_btn = (Button) view.findViewById(R.id.weidao_btn);
        main_text = (TextView) view.findViewById(R.id.dian_name);
        mLayout = (LinearLayout) view.findViewById(R.id.dian_action_layout);
        dian_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(std_names.size()==0){
                    Toast.makeText(getActivity(), "还没有学生", Toast.LENGTH_SHORT).show();
                    return;
                }
                dian_btn.setVisibility(View.GONE);
                mLayout.setVisibility(View.VISIBLE);
                now = 0;
                main_text.setText(std_names.get(now));
            }
        });
        dao_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                now = now + 1;
                if(now == std_names.size() ){
                    DianOver();
                    return;
                }
                main_text.setText(std_names.get(now));
            }
        });
        weidao_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weidao.add(std_names.get(now));
                now = now + 1;
                if(now == std_names.size() ){
                    DianOver();
                    return;
                }
                main_text.setText(std_names.get(now));
            }
        });
    }

    private int now;
    @Override
    public void onResume() {
        super.onResume();
        std_names.clear();
        Cursor cursor = db.query("Students", null, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                std_names.add(name);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    public void onPause() {
        super.onPause();
        dian_btn.setVisibility(View.VISIBLE);
        mLayout.setVisibility(View.GONE);
    }

    public void DianOver(){
        if (weidao.size()==0){
            Toast.makeText(getActivity(), "所有学生已到齐", Toast.LENGTH_SHORT).show();
        }else{
            String[] tmp = new String[weidao.size()];
            weidao.toArray(tmp);
            new AlertDialog.Builder(getActivity()).
                    setTitle("以下学生未到:").
                    setItems(tmp, null).
                    setNegativeButton("确定", null).
                    show();
        }
        dian_btn.setVisibility(View.VISIBLE);
        mLayout.setVisibility(View.GONE);
    }
}
