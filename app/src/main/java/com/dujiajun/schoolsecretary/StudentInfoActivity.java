package com.dujiajun.schoolsecretary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class StudentInfoActivity extends AppCompatActivity {
    private static final int CROP_PHOTO = 2;
    private Toolbar toolbar;
    private EditText edit_name, edit_phone, edit_remark;
    private ImageButton img_btn1;
    private ImageView imgview1;
    private Uri imguri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        UIInit();
        Intent i = getIntent();
        if (i.getBooleanExtra("isEdit", false)) {
            String name = i.getStringExtra("name");
            String phone = i.getStringExtra("phone");
            String remark = i.getStringExtra("remark");
            if (name.equals("")) {
                edit_name.setText(name);
            }
            if (phone.equals("")) {
                edit_phone.setText(phone);
            }
            if (remark.equals("")) {
                edit_remark.setText(remark);
            }
        }
        else{
            edit_name.setText("");
            edit_phone.setText("");
            edit_remark.setText("");
        }
    }

    protected void UIInit() {
        toolbar = (Toolbar) findViewById(R.id.std_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        edit_name = (EditText) findViewById(R.id.std_name_edit);
        edit_phone = (EditText) findViewById(R.id.std_phone_edit);
        edit_remark = (EditText) findViewById(R.id.std_remark_edit);
        img_btn1 = (ImageButton) findViewById(R.id.std_choose_pic_btn);
        img_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file1 = new File(Environment.getExternalStorageDirectory(), "img.jpg");
                try {
                    if (file1.exists()) {
                        file1.delete();
                    }
                    file1.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imguri = Uri.fromFile(file1);
                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                intent.putExtra("crop", true);
                intent.putExtra("scale", true);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imguri);
                startActivityForResult(intent, CROP_PHOTO);
            }
        });
        imgview1 = (ImageView) findViewById(R.id.std_pic);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imguri));
                        imgview1.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_student, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        String s = "";
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.std_del:
                finish();
                break;
            case R.id.std_save:
                s += "点击保存";
                Toast.makeText(StudentInfoActivity.this, s, Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
