package com.dujiajun.schoolsecretary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    public static final String CREATE_STD = "create table Students ("
            + "id integer primary key autoincrement,"
            + "name text,"
            + "classname text,"
            + "phone text,"
            + "remark text);";

    public static final String CREATE_EXAM = "create table exams ("
            + "id integer primary key autoincrement,"
            + "examname text,"
            + "classname text,"
            + "stdname text,"
            + "score integer,"
            + "rank integer"
            + ");";

    private Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_STD);
        db.execSQL(CREATE_EXAM);
        //Toast.makeText(mContext, "成功创建数据库", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL("alter table students add column classname text;");
                db.execSQL("update students set classname = '默认班级';");
            case 2:
                db.execSQL(CREATE_EXAM);
        }
    }
}
