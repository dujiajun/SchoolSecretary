package com.dujiajun.schoolsecretary.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.dujiajun.schoolsecretary.model.Exam;
import com.dujiajun.schoolsecretary.MyDatabaseHelper;
import com.dujiajun.schoolsecretary.R;
import com.dujiajun.schoolsecretary.adapter.ExamAdapter;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import jxl.Cell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ExamInfoActivity extends AppCompatActivity {

    private final int EXCEL_NULL = 1;
    private final int EXCEL_WRONG = 2;
    private final int EXCEL_FAIL = 3;
    private final int EXCEL_SUCCESS = 4;
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private String examname;
    private Boolean isEdit = true;
    private ListView listView;
    private Toolbar toolbar;
    private ArrayList<Exam> exams;
    //private ArrayList<String> stds;
    private ExamAdapter adapter;
    private boolean bj = false;
    private int spinner_c_now = 0;
    private int spinner_s_now = 0;
    private Comparator<Exam> SortByScore = new Comparator<Exam>() {
        @Override
        public int compare(Exam lhs, Exam rhs) {
            if (lhs.getScore() > rhs.getScore()) return 1;
            else return 0;
        }
    };

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
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_input, null);
            final EditText edit = (EditText) view.findViewById(R.id.dialog_edit);
            new AlertDialog.Builder(this)
                    .setTitle("输入考试名称：")
                    .setView(view)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String name = edit.getText().toString();
                            if (name.equals("")) {
                                Toast.makeText(ExamInfoActivity.this, "未填写考试名称", Toast.LENGTH_SHORT).show();
                                finish();
                                return;
                            }
                            Cursor cursor = db.query("exams", null, "examname = ?", new String[]{name}, null, null, null);
                            if (cursor.getCount() != 0) {
                                Toast.makeText(ExamInfoActivity.this, "已存在考试 " + name, Toast.LENGTH_SHORT).show();
                                cursor.close();
                                finish();
                                return;
                            }
                            cursor.close();
                            examname = name;
                            Toast.makeText(ExamInfoActivity.this, "若未添加记录，将不会保存本次考试信息", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
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
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(ExamInfoActivity.this)
                        .setTitle("确认删除 " + exams.get(position).getStudentName() + " ?")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Exam exam = exams.get(position);
                                db.delete("exams", "classname = ? and stdname = ? and examname = ?"
                                        , new String[]{exam.getClassname(), exam.getStudentName(), exam.getExamName()});
                                ListRefresh();
                            }
                        })
                        .show();
                return false;
            }
        });
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
            Collections.sort(exams, SortByScore);
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private int parseExcel(File xlsFile) {
        try {
            Workbook workbook = null;
            try {
                workbook = Workbook.getWorkbook(xlsFile);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (BiffException e) {
                e.printStackTrace();
            }
            Sheet sheet = workbook.getSheet(0);
            int columnCount = sheet.getColumns();
            int rowCount = sheet.getRows();
            if (rowCount == 1 || columnCount <= 1) {
                //Toast.makeText(ExamInfoActivity.this, "文件为空，请选择正确的xls文件", Toast.LENGTH_SHORT).show();
                return EXCEL_NULL;
            }
            Cell cell1 = null, cell2 = null, cell3 = null, cell4 = null;
            cell1 = sheet.findCell("姓名");
            cell2 = sheet.findCell("分数");
            cell3 = sheet.findCell("排名");
            cell4 = sheet.findCell("班级");
            if (cell1 == null || cell2 == null || cell3 == null || cell4 == null) {
                //Toast.makeText(ExamInfoActivity.this, "xls文件内容格式有误，请确认该xls文件内容", Toast.LENGTH_SHORT).show();
                return EXCEL_WRONG;
            }
            //Toast.makeText(ExamActivity.this,String.valueOf(columnCount)+" "+String.valueOf(rowCount), Toast.LENGTH_SHORT).show();
            int i1 = cell1.getColumn(), i2 = cell2.getColumn(), i3 = cell3.getColumn(), i4 = cell4.getColumn();
            int begin_row = cell1.getRow() + 1;
            for (int row = begin_row; row < rowCount; row++) {
                cell1 = sheet.getCell(i1, row);
                cell2 = sheet.getCell(i2, row);
                cell3 = sheet.getCell(i3, row);
                cell4 = sheet.getCell(i4, row);
                String name = cell1.getContents();
                int score = (int) ((NumberCell) cell2).getValue();
                int rank = (int) ((NumberCell) cell3).getValue();
                String classname = cell4.getContents();
                Cursor cursor = db.query("exams", null, "examname = ? and stdname = ? and classname = ?", new String[]{examname, name, classname}, null, null, null);
                if (cursor.getCount() != 0 || name.equals("")) {
                    cursor.close();
                    continue;
                }
                cursor.close();
                ContentValues value = new ContentValues();
                value.put("stdname", name);
                value.put("examname", examname);
                value.put("score", score);
                value.put("rank", rank);
                value.put("classname", classname);
                db.insert("exams", null, value);
            }

        } catch (Exception e) {
        }
        //text_test.setText(str);
        //Toast.makeText(ExamInfoActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
        return EXCEL_SUCCESS;
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
                final Spinner spinner_class = (Spinner) view.findViewById(R.id.dialog_exam_class);
                final Spinner spinner_std = (Spinner) view.findViewById(R.id.dialog_exam_std_name);
                final ArrayList<String> classnames = new ArrayList<>();
                final ArrayList<String> stdnames = new ArrayList<>();
                Cursor cursor1 = db.rawQuery("select distinct classname from students;", null);
                if (cursor1.moveToFirst()) {
                    bj = true;
                    do {
                        String classname = cursor1.getString(cursor1.getColumnIndex("classname"));
                        classnames.add(classname);
                    } while (cursor1.moveToNext());
                }
                cursor1.close();
                if (bj == false) {
                    Toast.makeText(ExamInfoActivity.this, "还没有班级和学生，请先添加学生", Toast.LENGTH_SHORT).show();
                    return false;
                }
                final ArrayAdapter<String> adapter_class = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classnames);
                adapter_class.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_class.setAdapter(adapter_class);

                final ArrayAdapter<String> adapter_std = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stdnames);
                adapter_std.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_std.setAdapter(adapter_std);


                spinner_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        spinner_c_now = position;
                        String classname = parent.getItemAtPosition(position).toString();
                        Cursor cursor = db.query("Students", null, "classname = ?", new String[]{classname}, null, null, null);
                        stdnames.clear();
                        if (cursor.moveToFirst()) {
                            do {
                                String name = cursor.getString(cursor.getColumnIndex("name"));
                                //Log.d("TAG",classname+" "+name);
                                stdnames.add(name);
                            } while (cursor.moveToNext());
                            adapter_std.notifyDataSetChanged();
                        }
                        cursor.close();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                spinner_std.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        spinner_s_now = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                //Toast.makeText(ExamInfoActivity.this, String.valueOf(spinner_c_now)+" "+String.valueOf(spinner_s_now), Toast.LENGTH_SHORT).show();
                final EditText edit_score = (EditText) view.findViewById(R.id.dialog_exam_std_score);
                final EditText edit_rank = (EditText) view.findViewById(R.id.dialog_exam_std_rank);
                new AlertDialog.Builder(this)
                        .setView(view)
                        .setTitle("添加一个新的结果：")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String scorestring = edit_score.getText().toString();
                                String rankstring = edit_rank.getText().toString();
                                int score, rank;
                                if (scorestring.equals("")) score = 0;
                                else score = Integer.parseInt(scorestring);
                                if (rankstring.equals("")) rank = 0;
                                else rank = Integer.parseInt(rankstring);
                                String stdname = stdnames.get(spinner_s_now);
                                String classname = classnames.get(spinner_c_now);
                                Cursor cursor = db.query("exams", null, "stdname = ? and classname = ? and examname = ?"
                                        , new String[]{stdname, classname, examname}, null, null, null);
                                if (cursor.getCount() != 0) {
                                    Toast.makeText(ExamInfoActivity.this, "已存在记录", Toast.LENGTH_SHORT).show();
                                    cursor.close();
                                    return;
                                }
                                cursor.close();
                                ContentValues values = new ContentValues();
                                values.put("examname", examname);
                                values.put("classname", classnames.get(spinner_c_now));
                                values.put("stdname", stdnames.get(spinner_s_now));
                                values.put("score", score);
                                values.put("rank", rank);
                                db.insert("exams", null, values);
                                ListRefresh();
                                Toast.makeText(ExamInfoActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            }
            break;
            case R.id.exam_info_insert_xls: {
                FileFilter filter = new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        if (pathname.isDirectory()) return false;
                        else {
                            String name = pathname.getName();
                            return name.endsWith(".xls");
                        }
                    }
                };
                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/dbs/ss/xls/");
                final File[] files = file.listFiles(filter);
                if (files == null) {
                    Toast.makeText(ExamInfoActivity.this, "请先将xls文件放在/sdcard/dbs/ss/xls/目录下", Toast.LENGTH_SHORT).show();
                    return false;
                }
                ArrayList<String> filenames = new ArrayList<>();
                for (File curFile : files) {
                    filenames.add(curFile.getName());
                }
                final String[] strings = new String[filenames.size()];
                filenames.toArray(strings);
                new AlertDialog.Builder(this).setTitle("选择XLS文件（存放地址/sdcard/dbs/ss/xls/）")
                        .setItems(strings, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final int t = which;
                                new AsyncTask<Void, Void, Integer>() {
                                    @Override
                                    protected Integer doInBackground(Void... params) {
                                        int res = parseExcel(files[t]);
                                        return res;
                                    }

                                    @Override
                                    protected void onPostExecute(Integer integer) {
                                        switch (integer) {
                                            case EXCEL_SUCCESS:
                                                ListRefresh();
                                                Toast.makeText(ExamInfoActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                                                break;
                                            case EXCEL_FAIL:
                                                Toast.makeText(ExamInfoActivity.this, "解析xls文件失败", Toast.LENGTH_SHORT).show();
                                                break;
                                            case EXCEL_NULL:
                                                Toast.makeText(ExamInfoActivity.this, "文件为空，请选择正确的xls文件", Toast.LENGTH_SHORT).show();
                                                break;
                                            case EXCEL_WRONG:
                                                Toast.makeText(ExamInfoActivity.this, "xls文件内容格式有误，请确认该xls文件内容", Toast.LENGTH_SHORT).show();
                                                break;
                                        }
                                    }
                                }.execute();
                                //Toast.makeText(ExamActivity.this, files[which].getPath(), Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("取消", null)
                        .show();
            }
            break;
            case R.id.exam_info_modify: {
                View view = LayoutInflater.from(this).inflate(R.layout.dialog_input, null);
                final EditText edit = (EditText) view.findViewById(R.id.dialog_edit);
                //final EditText edit = new EditText(this);
                final String name = examname;
                //edit.setMaxWidth(12);
                edit.setText(name);
                new AlertDialog.Builder(this)
                        .setView(view)
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
                                    cursor.close();
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
}
