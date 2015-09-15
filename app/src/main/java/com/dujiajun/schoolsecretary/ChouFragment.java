package com.dujiajun.schoolsecretary;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class ChouFragment extends Fragment {

    private View view;
    private Button chou_btn;
    private TextView main_text;
    private int tmp = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chou, container, false);
        Init();
        return view;
    }
    private void Init(){
        main_text = (TextView) view.findViewById(R.id.chou_name);
        chou_btn = (Button) view.findViewById(R.id.chou_btn);
        tmp = 0;
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
                                Thread.sleep(200);
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
                    }
                }.execute();
            }
        });

        dbHelper = new MyDatabaseHelper(getActivity(), "student.db", null, 1);
        db = dbHelper.getWritableDatabase();

        std_names = new ArrayList<>();

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

    private ArrayList<String> std_names;
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;

}
