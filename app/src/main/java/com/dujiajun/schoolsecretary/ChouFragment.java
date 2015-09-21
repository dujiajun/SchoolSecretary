package com.dujiajun.schoolsecretary;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ChouFragment extends Fragment {

    private View view;
    private Button chou_btn;
    private TextView main_text;
    private ArrayList<String> std_names;
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Spinner spinner;
    private ArrayList<String> classnames;
    private boolean bj = false;
    private ArrayAdapter<String> spinnerAdapter;
    private int spinner_now = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chou, container, false);
        spinner = (Spinner) view.findViewById(R.id.chou_spinner);
        Init();
        return view;
    }

    private void Init(){
        main_text = (TextView) view.findViewById(R.id.chou_name);
        chou_btn = (Button) view.findViewById(R.id.chou_btn);
        chou_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(std_names.size()==0){
                    Toast.makeText(getActivity(), "还没有学生", Toast.LENGTH_SHORT).show();
                    return;
                }
                new AsyncTask<Void, String, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        for(int i = 0 ;i < 20 ;i++){
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            int ranint = (int) (Math.random()*std_names.size());
                            String curname = std_names.get(ranint);
                            publishProgress(curname);
                        }
                        return null;
                    }

                    @Override
                    protected void onProgressUpdate(String... values) {
                        super.onProgressUpdate(values);
                        main_text.setText(values[0]);
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        Toast.makeText(getActivity(), "抽签结束，抽到结果是 " + main_text.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                }.execute();
            }
        });

        dbHelper = new MyDatabaseHelper(getActivity(), "student.db", null, 2);
        db = dbHelper.getWritableDatabase();

        std_names = new ArrayList<>();

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
        //spinner.setSelection(0);
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

}
