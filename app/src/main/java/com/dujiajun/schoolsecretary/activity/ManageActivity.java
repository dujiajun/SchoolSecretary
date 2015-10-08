package com.dujiajun.schoolsecretary.activity;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dujiajun.schoolsecretary.fragment.ManageFragment;
import com.dujiajun.schoolsecretary.MyDatabaseHelper;
import com.dujiajun.schoolsecretary.adapter.MyPagerAdapter;
import com.dujiajun.schoolsecretary.R;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import jxl.Cell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;


public class ManageActivity extends AppCompatActivity {

    private final int EXCEL_NULL = 1;
    private final int EXCEL_WRONG = 2;
    private final int EXCEL_FAIL = 3;
    private final int EXCEL_SUCCESS = 4;
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
        dbHelper = new MyDatabaseHelper(ManageActivity.this, "student.db", null, 3);
        db = dbHelper.getWritableDatabase();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
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
                cursor.close();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                pagerAdapter = new MyPagerAdapter(getFragmentManager(), fragments, titles);
                if (bj) {
                    viewPager.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.VISIBLE);
                    viewPager.setAdapter(pagerAdapter);
                    if (fragments.size() >= 3) {
                        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                    }
                    tabLayout.setupWithViewPager(viewPager);
                } else {
                    Toast.makeText(ManageActivity.this, "点击右上角的加号添加班级", Toast.LENGTH_SHORT).show();
                    viewPager.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.GONE);
                }
            }
        }.execute();

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
            case R.id.mng_add: {
                View view = LayoutInflater.from(this).inflate(R.layout.dialog_input, null);
                final EditText edit_class = (EditText) view.findViewById(R.id.dialog_edit);
                //edit_class.setMaxWidth(12);
                //edit_class.setGravity(View.TEXT_ALIGNMENT_CENTER);
                new AlertDialog.Builder(this)
                        .setTitle("输入班级名称：")
                        .setView(view)
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
                                    if (fragments.size() >= 3) {
                                        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                                    }
                                    if (bj) {
                                        tabLayout.setupWithViewPager(viewPager);
                                    } else {
                                        viewPager.setVisibility(View.VISIBLE);
                                        tabLayout.setVisibility(View.VISIBLE);
                                        viewPager.setAdapter(pagerAdapter);
                                        tabLayout.setupWithViewPager(viewPager);
                                    }

                                    viewPager.setCurrentItem(pagerAdapter.getCount() - 1);
                                    Toast.makeText(ManageActivity.this, "若未添加学生，则不会保存本班级", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
                break;
            case R.id.mng_xls: {
                if (fragments.size() == 0) {
                    Toast.makeText(ManageActivity.this, "请先添加班级", Toast.LENGTH_SHORT).show();
                    return false;
                }
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
                    Toast.makeText(ManageActivity.this, "请先将xls文件放在/sdcard/dbs/ss/xls/目录下", Toast.LENGTH_SHORT).show();
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
                                                ((ManageFragment) fragments.get(viewPager.getCurrentItem())).ListRefresh();
                                                Toast.makeText(ManageActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                                                break;
                                            case EXCEL_FAIL:
                                                Toast.makeText(ManageActivity.this, "解析xls文件失败", Toast.LENGTH_SHORT).show();
                                                break;
                                            case EXCEL_NULL:
                                                Toast.makeText(ManageActivity.this, "文件为空，请选择正确的xls文件", Toast.LENGTH_SHORT).show();
                                                break;
                                            case EXCEL_WRONG:
                                                Toast.makeText(ManageActivity.this, "xls文件内容格式有误，请确认该xls文件内容", Toast.LENGTH_SHORT).show();
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
            case R.id.mng_edit: {
                if (fragments.size() == 0) {
                    Toast.makeText(ManageActivity.this, "请先添加班级", Toast.LENGTH_SHORT).show();
                    return false;
                }
                View view = LayoutInflater.from(this).inflate(R.layout.dialog_input, null);
                final EditText edit_class2 = (EditText) view.findViewById(R.id.dialog_edit);
                //final EditText edit_class2 = new EditText(this);
                final String origin_classname = titles.get(viewPager.getCurrentItem());
                //edit_class2.setMaxWidth(12);
                edit_class2.setText(origin_classname);
                new AlertDialog.Builder(this)
                        .setTitle("输入班级名称：")
                        .setView(view)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String class_name = edit_class2.getText().toString();
                                if (class_name.equals("")) {
                                    Toast.makeText(ManageActivity.this, "未填写班级名称", Toast.LENGTH_SHORT).show();
                                    return;
                                } else if (class_name.equals(origin_classname)) {
                                    return;
                                } else {
                                    Cursor cursor = db.rawQuery("select distinct classname from students where classname = ?;"
                                            , new String[]{class_name});
                                    if (cursor.getCount() != 0) {
                                        Toast.makeText(ManageActivity.this, "已存在班级 " + class_name, Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    cursor.close();
                                    db.execSQL("update students set classname = ? where classname = ?", new String[]{class_name, origin_classname});
                                    finish();
                                    startActivity(new Intent(ManageActivity.this, ManageActivity.class));
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
                break;
            case R.id.mng_del: {
                if (fragments.size() == 0) {
                    Toast.makeText(ManageActivity.this, "请先添加班级", Toast.LENGTH_SHORT).show();
                    return false;
                }
                final String origin_classname = titles.get(viewPager.getCurrentItem());
                new AlertDialog.Builder(ManageActivity.this)
                        .setTitle("确定删除")
                        .setMessage("删除后将不能恢复，确定删除？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.delete("students", "classname = ?", new String[]{origin_classname});
                                finish();
                                startActivity(new Intent(ManageActivity.this, ManageActivity.class));
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
            break;
            case R.id.mng_xls_backup: {
                BackUpToExcel();
            }
            break;
        }

        return super.onOptionsItemSelected(item);
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
            //cell4 = sheet.findCell("班级");
            cell1 = sheet.findCell("姓名");
            cell2 = sheet.findCell("电话");
            cell3 = sheet.findCell("评语");
            if (cell1 == null || cell2 == null || cell3 == null) {
                //Toast.makeText(ExamInfoActivity.this, "xls文件内容格式有误，请确认该xls文件内容", Toast.LENGTH_SHORT).show();
                //Log.d("TAG", String.valueOf(cell1 == null) + " " + String.valueOf(cell2 == null) + " " + String.valueOf(cell3 == null));
                return EXCEL_WRONG;
            }
            String classname = titles.get(viewPager.getCurrentItem());
            //Toast.makeText(ExamActivity.this,String.valueOf(columnCount)+" "+String.valueOf(rowCount), Toast.LENGTH_SHORT).show();
            int i1 = cell1.getColumn(), i2 = cell2.getColumn(), i3 = cell3.getColumn();
            int begin_row = cell1.getRow() + 1;
            for (int row = begin_row; row < rowCount; row++) {
                cell1 = sheet.getCell(i1, row);
                cell2 = sheet.getCell(i2, row);
                cell3 = sheet.getCell(i3, row);
                String name = cell1.getContents();
                String phone = cell2.getContents();
                String remark = cell3.getContents();
                Cursor cursor = db.query("students", null, "classname = ? and name = ?", new String[]{classname, name}, null, null, null);
                if (cursor.getCount() != 0 || name.equals("")) {
                    cursor.close();
                    continue;
                }
                cursor.close();
                ContentValues value = new ContentValues();
                value.put("name", name);
                value.put("classname", classname);
                value.put("phone", phone);
                value.put("remark", remark);
                db.insert("students", null, value);
            }

        } catch (Exception e) {
        }
        //text_test.setText(str);
        //Toast.makeText(ExamInfoActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
        return EXCEL_SUCCESS;
    }

    private void BackUpToExcel() {
        String[] xls_titles = new String[]{"班级", "姓名", "电话", "评语"};
        long time = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        File outFile = new File(Environment.getExternalStorageDirectory()
                + "/dbs/ss/backup/" + format.format(new Date(time)) + ".xls");
        //Toast.makeText(ManageActivity.this, outputFileName, Toast.LENGTH_SHORT).show();
        Log.d("TAG", outFile.toString());
        WritableWorkbook wwb = null;
        try {
            //OutputStream os = new FileOutputStream(outputFileName);
            wwb = Workbook.createWorkbook(outFile);
        } catch (FileNotFoundException e) {
            //Toast.makeText(ManageActivity.this, "1", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            //Toast.makeText(ManageActivity.this, "2", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        if (wwb == null) {
            return;
        }
        try {
            WritableSheet sheet = wwb.createSheet("学生", 0);
            Label label;
            for (int i = 0; i < xls_titles.length; i++) {
                label = new Label(i, 0, xls_titles[i]);

                sheet.addCell(label);

            }
            Cursor cursor = db.rawQuery("select * from students;", null);
            if (cursor.moveToFirst()) {
                int row = 1;
                do {
                    String classname = cursor.getString(cursor.getColumnIndex("classname"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String phone = cursor.getString(cursor.getColumnIndex("phone"));
                    String remark = cursor.getString(cursor.getColumnIndex("remark"));
                    label = new Label(0, row, classname);
                    sheet.addCell(label);
                    label = new Label(1, row, name);
                    sheet.addCell(label);
                    label = new Label(2, row, phone);
                    sheet.addCell(label);
                    label = new Label(3, row, remark);
                    sheet.addCell(label);
                    row = row + 1;
                } while (cursor.moveToNext());
            } else {
                return;
            }
        } catch (WriteException e) {
            e.printStackTrace();
        }
        try {
            wwb.write();
            wwb.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }

        Toast.makeText(ManageActivity.this, "学生信息已保存到 " + outFile.toString(), Toast.LENGTH_SHORT).show();

    }
}
