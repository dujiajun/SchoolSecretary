package com.dujiajun.schoolsecretary;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class ManageActivity extends AppCompatActivity {

    private FloatingActionButton fabtn_add;
    private ListView listView;
    private Toolbar toolbar;
    private ArrayList<String> std_names;
    //private String[] std_names = {"杜佳骏", "赵中彬", "莫湑", "吕晓钟", "李翰铭"};
    private ArrayAdapter<String> arrayAdapter;
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private ArrayList<Student> stdlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        UIInit();
        stdlist = new ArrayList<>();
        dbHelper = new MyDatabaseHelper(this, "student.db", null, 1);
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Students", null, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                try {
                    String name = new String(cursor.getBlob(cursor.getColumnIndex("name")), "gb2312");
                    String phone = new String(cursor.getBlob(cursor.getColumnIndex("phone")), "gb2312");
                    String remark = new String(cursor.getBlob(cursor.getColumnIndex("remark")), "gb2312");
                    stdlist.add(new Student(name, phone, remark));
                    std_names.add(name);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                //Log.d("TAG", name + "," + phone + "," + remark);

            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    protected void UIInit() {
        toolbar = (Toolbar) findViewById(R.id.mng_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listView = (ListView) findViewById(R.id.mng_listview);
        std_names = new ArrayList<>();
        //std_names.add("杜佳骏");
        arrayAdapter = new ArrayAdapter<>(ManageActivity.this, android.R.layout.simple_list_item_1, std_names);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ManageActivity.this, StudentInfoActivity.class);
                intent.putExtra("isEdit", true);
                intent.putExtra("name", arrayAdapter.getItem(position));
                intent.putExtra("phone", "18888888888");
                intent.putExtra("remark", "成绩优异，乐于助人。");
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ManageActivity.this, arrayAdapter.getItem(position), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        fabtn_add = (FloatingActionButton) findViewById(R.id.mng_add_btn);
        fabtn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ManageActivity.this, "点击添加", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ManageActivity.this, StudentInfoActivity.class);
                intent.putExtra("isEdit", false);
                startActivity(intent);
            }
        });
    }
}
