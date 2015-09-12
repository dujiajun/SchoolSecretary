package com.dujiajun.schoolsecretary;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class ManageActivity extends AppCompatActivity {

    private FloatingActionButton fabtn_add;
    private ListView listView;
    private Toolbar toolbar;
    private String[] std_names = {"杜佳骏", "赵中彬", "莫湑", "吕晓钟", "李翰铭"};
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        UIInit();
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
