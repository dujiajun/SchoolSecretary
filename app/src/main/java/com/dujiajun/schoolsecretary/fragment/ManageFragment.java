package com.dujiajun.schoolsecretary.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dujiajun.schoolsecretary.MyDatabaseHelper;
import com.dujiajun.schoolsecretary.R;
import com.dujiajun.schoolsecretary.model.Student;
import com.dujiajun.schoolsecretary.activity.StudentInfoActivity;

import java.util.ArrayList;

public class ManageFragment extends Fragment {

    private FloatingActionButton fabtn_add;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private ArrayList<Student> stdlist;
    private ArrayList<String> std_names;
    private View view;
    private String classname;

    public void setClassname(String classname) {
        this.classname = classname;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manage, container, false);
        listView = (ListView) view.findViewById(R.id.f_mng_listview);
        std_names = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, std_names);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), StudentInfoActivity.class);
                intent.putExtra("isEdit", true);
                intent.putExtra("id", stdlist.get(position).getIdname());
                intent.putExtra("name", stdlist.get(position).getName());
                intent.putExtra("phone", stdlist.get(position).getPhone());
                intent.putExtra("remark", stdlist.get(position).getRemark());
                intent.putExtra("classname", classname);
                startActivity(intent);
            }
        });
        fabtn_add = (FloatingActionButton) view.findViewById(R.id.f_mng_add_btn);
        fabtn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), StudentInfoActivity.class);
                intent.putExtra("isEdit", false);
                intent.putExtra("classname", classname);
                startActivity(intent);
            }
        });
        stdlist = new ArrayList<>();
        dbHelper = new MyDatabaseHelper(getActivity(), "student.db", null, 3);
        db = dbHelper.getWritableDatabase();
        ListRefresh();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ListRefresh();
    }

    private void ListRefresh() {
        std_names.clear();
        stdlist.clear();
        Cursor cursor = db.query("Students", null, "classname = ?", new String[]{classname}, null, null, null);
        //db.rawQuery("select * from students where classname = '" + classname + "';", null);
        //
        //Log.d("TAG", "select * from students where classname = '" + classname + "';");
        //if (cursor.isNull(cursor.getColumnIndex("name")))return;
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String phone = cursor.getString(cursor.getColumnIndex("phone"));
                String remark = cursor.getString(cursor.getColumnIndex("remark"));
                //Log.d("TAG",name+"|"+phone+"|"+remark);
                stdlist.add(new Student(id, name, phone, remark, classname));
                std_names.add(name);
            } while (cursor.moveToNext());
        }
        cursor.close();
        arrayAdapter.notifyDataSetChanged();
    }
}
