package com.dujiajun.schoolsecretary;

/**
 * Created by 杜佳骏 on 2015/9/13 0013.
 */
public class Student {
    private String name;
    private String phone;
    private String remark;

    public Student(String name, String phone, String remark) {
        this.name = name;
        this.phone = phone;
        this.remark = remark;
    }

    public Student(String name) {
        this.name = name;
        this.phone = "";
        this.remark = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
