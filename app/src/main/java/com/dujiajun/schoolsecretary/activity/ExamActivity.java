package com.dujiajun.schoolsecretary.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dujiajun.schoolsecretary.MyDatabaseHelper;
import com.dujiajun.schoolsecretary.R;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

import jxl.Cell;
import jxl.LabelCell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ExamActivity extends AppCompatActivity {

    private final int EXCEL_EXIST = 0;
    private final int EXCEL_NULL = 1;
    private final int EXCEL_WRONG = 2;
    private final int EXCEL_FAIL = 3;
    private final int EXCEL_SUCCESS = 4;
    //private TextView text_test;
    private ListView exam_list;
    private ArrayList<String> exams;
    private ArrayAdapter<String> exam_adapter;
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private boolean bj = false;
    private TextView ins_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        UIInit();

        dbHelper = new MyDatabaseHelper(this, "student.db", null, 3);
        db = dbHelper.getWritableDatabase();
        //ListRefresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ListRefresh();
    }

    private void ListRefresh() {
        exams.clear();
        Cursor cursor = db.rawQuery("select distinct examname from exams;", null);
        if (cursor.moveToFirst()) {
            ins_text.setVisibility(View.GONE);
            exam_list.setVisibility(View.VISIBLE);
            bj = true;
            do {
                String examname = cursor.getString(cursor.getColumnIndex("examname"));
                //Toast.makeText(ExamActivity.this,examname, Toast.LENGTH_SHORT).show();
                exams.add(examname);
            } while (cursor.moveToNext());
        } else {
            bj = false;
            ins_text.setVisibility(View.VISIBLE);
            exam_list.setVisibility(View.GONE);
        }
        cursor.close();
        exam_adapter.notifyDataSetChanged();
    }

    private void UIInit() {
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.exam_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //text_test = (TextView) findViewById(R.id.exam_text);
        //text_test.setText(R.string.xls_instruction);
        exam_list = (ListView) findViewById(R.id.exam_list);
        exams = new ArrayList<>();
        exam_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, exams);
        exam_list.setAdapter(exam_adapter);
        ins_text = (TextView) findViewById(R.id.exam_ins_text);
        exam_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ExamActivity.this, ExamInfoActivity.class);
                intent.putExtra("isEdit", true);
                intent.putExtra("examname", exams.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_exam, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.exam_insert_xls: {
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
                    Toast.makeText(ExamActivity.this, "请先将xls文件放在/sdcard/dbs/ss/xls/目录下", Toast.LENGTH_SHORT).show();
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
                                                Toast.makeText(ExamActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                                                break;
                                            case EXCEL_FAIL:
                                                Toast.makeText(ExamActivity.this, "解析xls文件失败", Toast.LENGTH_SHORT).show();
                                                break;
                                            case EXCEL_EXIST:
                                                Toast.makeText(ExamActivity.this, "已存在考试", Toast.LENGTH_SHORT).show();
                                                break;
                                            case EXCEL_NULL:
                                                Toast.makeText(ExamActivity.this, "文件为空，请选择正确的xls文件", Toast.LENGTH_SHORT).show();
                                                break;
                                            case EXCEL_WRONG:
                                                Toast.makeText(ExamActivity.this, "xls文件内容格式有误，请确认该xls文件内容", Toast.LENGTH_SHORT).show();
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
            case R.id.exam_insert: {
                Intent intent = new Intent(ExamActivity.this, ExamInfoActivity.class);
                intent.putExtra("isEdit", false);
                startActivity(intent);
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    private int parseExcel(File xlsFile) {
        String str = "";
        String examname = xlsFile.getName();
        examname = examname.substring(0, examname.indexOf(".xls"));
        Cursor cursor = db.query("exams", null, "examname = ?", new String[]{examname}, null, null, null);
        if (cursor.getCount() != 0) {
            //
            return EXCEL_EXIST;
        }
        cursor.close();
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
                //
                return EXCEL_NULL;
            }
            Cell cell1 = null, cell2 = null, cell3 = null, cell4 = null;
            cell1 = sheet.findCell("姓名");
            cell2 = sheet.findCell("分数");
            cell3 = sheet.findCell("排名");
            cell4 = sheet.findCell("班级");
            if (cell1 == null || cell2 == null || cell3 == null || cell4 == null) {
                //
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
                str = str + name;
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
        if (str.equals("")) {
            //
            return EXCEL_FAIL;
        }
        //text_test.setText(str);

        return EXCEL_SUCCESS;
    }
}
