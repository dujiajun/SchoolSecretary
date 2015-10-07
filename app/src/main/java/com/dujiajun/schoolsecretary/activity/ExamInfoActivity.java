package com.dujiajun.schoolsecretary.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.dujiajun.schoolsecretary.model.Exam;
import com.dujiajun.schoolsecretary.MyDatabaseHelper;
import com.dujiajun.schoolsecretary.R;
import com.dujiajun.schoolsecretary.adapter.ExamAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ExamInfoActivity extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private String examname;
    private Boolean isEdit = true;
    private ListView listView;
    private Toolbar toolbar;
    private ArrayList<Exam> exams;
    //private ArrayList<String> stds;
    private ExamAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_info);
        UIInit();
        dbHelper = new MyDatabaseHelper(this, "student.db", null, 3);
        db = dbHelper.getWritableDatabase();
        Intent i = getIntent();
        isEdit = i.getBooleanExtra("isEdit", true);
        if (isEdit) {
            examname = i.getStringExtra("examname");
            toolbar.setTitle(examname);
            ListRefresh();
        } else {
            toolbar.setTitle("添加考试");
        }

    }

    //private ArrayAdapter<String> adapter;
    private void UIInit() {

        toolbar = (Toolbar) findViewById(R.id.exam_info_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listView = (ListView) findViewById(R.id.exam_info_list);
        exams = new ArrayList<>();
        //adapter = new ArrayAdapter<>(ExamInfoActivity.this,android.R.layout.simple_list_item_1,stds);
        adapter = new ExamAdapter(this, R.layout.item_exam, exams);
        listView.setAdapter(adapter);
    }

    private void ListRefresh() {
        exams.clear();
        Cursor cursor = db.query("exams", null, "examname = ?", new String[]{examname}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("stdname"));
                int score = cursor.getInt(cursor.getColumnIndex("score"));
                int rank = cursor.getInt(cursor.getColumnIndex("rank"));
                String classname = cursor.getString(cursor.getColumnIndex("classname"));
                //Toast.makeText(ExamInfoActivity.this, name, Toast.LENGTH_SHORT).show();
                exams.add(new Exam(examname, classname, name, score, rank));
                //Log.d("TAG", name + " " + String.valueOf(exams.size()));
            } while (cursor.moveToNext());
            Collections.sort(exams, new SortByRank());
            adapter.notifyDataSetChanged();
        }
        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_exam_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.exam_info_insert: {
                View view = LayoutInflater.from(this).inflate(R.layout.dialog_exam_std, null);
                new AlertDialog.Builder(this)
                        .setView(view)
                        .setTitle("添加一个新的结果：")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确认", null)
                        .show();
            }
            break;
            case R.id.exam_info_insert_xls: {

            }
            break;
            case R.id.exam_info_modify: {
                final EditText edit = new EditText(this);
                final String name = examname;
                edit.setMaxWidth(12);
                edit.setText(name);
                new AlertDialog.Builder(this)
                        .setView(edit)
                        .setTitle("输入考试名称:")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newname = edit.getText().toString();
                                if (newname.equals("")) {
                                    Toast.makeText(ExamInfoActivity.this, "未填写考试名称", Toast.LENGTH_SHORT).show();
                                    return;
                                } else if (newname.equals(name)) {
                                    return;
                                }
                                Cursor cursor = db.query("exams", null, "examname = ?", new String[]{newname}, null, null, null);
                                if (cursor.getCount() != 0) {
                                    Toast.makeText(ExamInfoActivity.this, "已存在考试 " + newname, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                cursor.close();

                                db.execSQL("update exams set examname = ? where examname = ?", new String[]{newname, name});

                                toolbar.setTitle(newname);

                            }
                        }).show();
            }
            break;
            case R.id.exam_info_delete: {
                new AlertDialog.Builder(this)
                        .setTitle("删除后不可恢复，确认删除？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.delete("exams", "examname = ?", new String[]{examname});
                                finish();
                            }
                        }).show();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    class SortByRank implements Comparator {

        @Override
        public int compare(Object lhs, Object rhs) {
            Exam exam1 = (Exam) lhs;
            Exam exam2 = (Exam) rhs;
            if (exam1.getScore() > exam2.getScore()) return 1;
            else if (exam1.getScore() == exam2.getScore()) return 0;
            else return 0;
        }
    }
}
