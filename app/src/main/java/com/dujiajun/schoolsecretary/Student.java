package com.dujiajun.schoolsecretary;

/**
 * Created by 杜佳骏 on 2015/9/13 0013.
 */
public class Student {
    private String name;
    private String phone;
    private String remark;
    private String classname;

    public Student(String name, String phone, String remark, String classname) {
        this.name = name;
        this.phone = phone;
        this.remark = remark;
        this.classname = classname;
    }

    public Student(String name, String classname) {
        this.name = name;
        this.phone = "";
        this.remark = "";
        this.classname = classname;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
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
