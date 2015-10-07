package com.dujiajun.schoolsecretary.fragment;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dujiajun.schoolsecretary.MyDatabaseHelper;
import com.dujiajun.schoolsecretary.R;

import java.util.ArrayList;

public class DianFragment extends Fragment {
    private View view;
    private Button dian_btn,dao_btn,weidao_btn;
    private TextView main_text;
    private ArrayList<String> std_names;
    private ArrayList<String> weidao;
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private LinearLayout mLayout;
    private int now;
    private Spinner spinner;
    private ArrayList<String> classnames;
    private boolean bj = false;
    private ArrayAdapter<String> spinnerAdapter;
    private int spinner_now = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dian, container, false);
        spinner = (Spinner) view.findViewById(R.id.dian_spinner);
        Init();
        return view;
    }

    public void Init(){

        dbHelper = new MyDatabaseHelper(getActivity(), "student.db", null, 3);
        db = dbHelper.getWritableDatabase();

        std_names = new ArrayList<>();
        weidao = new ArrayList<>();

        classnames = new ArrayList<>();
        Cursor cursor1 = db.rawQuery("select distinct classname from students;", null);
        if (cursor1.moveToFirst()) {
            bj = true;
            do {
                String classname = cursor1.getString(cursor1.getColumnIndex("classname"));
                classnames.add(classname);
            } while (cursor1.moveToNext());
        }
        cursor1.close();
        spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, classnames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner_now = position;
                String classname = parent.getItemAtPosition(position).toString();
                Cursor cursor = db.query("Students", null, "classname = ?", new String[]{classname}, null, null, null);
                std_names.clear();
                main_text.setText("开始");
                if (cursor.moveToFirst()) {
                    do {
                        String name = cursor.getString(cursor.getColumnIndex("name"));
                        //Log.d("TAG",classname+" "+name);
                        std_names.add(name);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (bj) {
            String classname = classnames.get(spinner_now);
            //String classname = spinner.getSelectedItem().toString();
            Cursor cursor = db.query("Students", null, "classname = ?", new String[]{classname}, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    //Log.d("TAG",classname+" "+name);
                    std_names.add(name);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        dian_btn = (Button) view.findViewById(R.id.dian_btn);
        dao_btn = (Button) view.findViewById(R.id.dao_btn);
        weidao_btn = (Button) view.findViewById(R.id.weidao_btn);
        main_text = (TextView) view.findViewById(R.id.dian_name);
        mLayout = (LinearLayout) view.findViewById(R.id.dian_action_layout);
        dian_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weidao.clear();
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

    @Override
    public void onResume() {
        super.onResume();
        bj = false;
        classnames.clear();
        Cursor cursor1 = db.rawQuery("select distinct classname from students;", null);
        if (cursor1.moveToFirst()) {
            bj = true;
            do {
                String classname = cursor1.getString(cursor1.getColumnIndex("classname"));
                classnames.add(classname);
            } while (cursor1.moveToNext());
        }
        cursor1.close();
        spinnerAdapter.notifyDataSetChanged();
        if (bj) {
            std_names.clear();
            String classname = classnames.get(spinner_now);
            //String classname = spinner.getSelectedItem().toString();
            Cursor cursor = db.query("Students", null, "classname = ?", new String[]{classname}, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    std_names.add(name);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        dian_btn.setVisibility(View.VISIBLE);
        mLayout.setVisibility(View.GONE);
    }

    public void DianOver(){
        main_text.setText("点名完成");
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
