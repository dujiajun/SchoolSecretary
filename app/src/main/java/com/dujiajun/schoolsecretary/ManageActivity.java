package com.dujiajun.schoolsecretary;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class ManageActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private MyPagerAdapter pagerAdapter;
    private ArrayList<Fragment> fragments;
    private ArrayList<String> titles;
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private boolean bj = false;

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

        viewPager = (ViewPager) findViewById(R.id.mng_viewpager);
        tabLayout = (TabLayout) findViewById(R.id.mng_tablayout);
        //tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        fragments = new ArrayList<>();
        titles = new ArrayList<>();
        dbHelper = new MyDatabaseHelper(ManageActivity.this, "student.db", null, 2);
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select distinct classname from students;", null);
        if (cursor.moveToFirst()) {
            bj = true;
            do {
                String classname = cursor.getString(cursor.getColumnIndex("classname"));
                ManageFragment fragment = new ManageFragment();
                fragment.setClassname(classname);
                titles.add(classname);
                fragments.add(fragment);
            } while (cursor.moveToNext());
        }
        pagerAdapter = new MyPagerAdapter(getFragmentManager(), fragments, titles);
        if (bj) {
            viewPager.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
            viewPager.setAdapter(pagerAdapter);
            tabLayout.setupWithViewPager(viewPager);
        } else {
            Toast.makeText(ManageActivity.this, "点击右上角的加号添加班级", Toast.LENGTH_SHORT).show();
            viewPager.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
        }
        cursor.close();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_manage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.mng_add:
                final EditText edit_class = new EditText(this);
                new AlertDialog.Builder(this)
                        .setTitle("输入班级名称：")
                        .setView(edit_class)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String class_name = edit_class.getText().toString();
                                if (class_name.equals("")) {
                                    Toast.makeText(ManageActivity.this, "未填写班级名称", Toast.LENGTH_SHORT).show();
                                    return;
                                } else {
                                    Cursor cursor = db.rawQuery("select distinct classname from students where classname = ?;"
                                            , new String[]{class_name});
                                    if (cursor.getCount() != 0) {
                                        Toast.makeText(ManageActivity.this, "已存在班级 " + class_name, Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    cursor.close();
                                    ManageFragment fragment = new ManageFragment();
                                    fragment.setClassname(class_name);
                                    titles.add(class_name);
                                    fragments.add(fragment);
                                    pagerAdapter.notifyDataSetChanged();
                                    if (bj) {
                                        tabLayout.setupWithViewPager(viewPager);
                                    } else {
                                        viewPager.setVisibility(View.VISIBLE);
                                        tabLayout.setVisibility(View.VISIBLE);
                                        viewPager.setAdapter(pagerAdapter);
                                        tabLayout.setupWithViewPager(viewPager);
                                    }

                                    //viewPager.setCurrentItem(pagerAdapter.getItemPosition(fragment));
                                    Toast.makeText(ManageActivity.this, "若未添加学生，不会保存本班级", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
        }

        return super.onOptionsItemSelected(item);
    }
}
