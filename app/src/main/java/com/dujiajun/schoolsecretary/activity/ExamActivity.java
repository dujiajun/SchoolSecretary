package com.dujiajun.schoolsecretary.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dujiajun.schoolsecretary.R;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

import jxl.Cell;
import jxl.LabelCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ExamActivity extends AppCompatActivity {

    private TextView text_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        UIInit();
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
        text_test = (TextView) findViewById(R.id.exam_text);
        text_test.setText(R.string.xls_instruction);
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
                        if (pathname.isDirectory()) return true;
                        else {
                            String name = pathname.getName();
                            return name.endsWith(".xls");
                        }
                    }
                };
                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/dbs/");
                final File[] files = file.listFiles(filter);
                if (files == null) {
                    Toast.makeText(ExamActivity.this, "请先将xls文件放在/sdcard/dbs/目录下", Toast.LENGTH_SHORT).show();
                    return false;
                }
                ArrayList<String> filenames = new ArrayList<>();
                for (File curFile : files) {
                    filenames.add(curFile.getName());
                }
                final String[] strings = new String[filenames.size()];
                filenames.toArray(strings);
                new AlertDialog.Builder(this).setTitle("选择XLS文件（存放地址/sdcard/dbs/）")
                        .setItems(strings, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                parseExcel(files[which]);
                                //Toast.makeText(ExamActivity.this, files[which].getPath(), Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("取消", null)
                        .show();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void parseExcel(File xlsFile) {
        String str = "";
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
            //Toast.makeText(ExamActivity.this,String.valueOf(columnCount)+" "+String.valueOf(rowCount), Toast.LENGTH_SHORT).show();
            Cell cell = null;
            for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < columnCount; col++) {
                    cell = sheet.getCell(col, row);
                    String data = cell.getContents();
                    str = str + " " + data;
                }
                str = str + "\n";
            }

        } catch (Exception e) {
        }
        if (str.equals("")) {
            Toast.makeText(ExamActivity.this, "解析xls文件失败", Toast.LENGTH_SHORT).show();
            return;
        }
        text_test.setText(str);
    }
}
