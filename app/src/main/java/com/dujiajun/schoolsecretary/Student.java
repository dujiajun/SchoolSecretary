package com.dujiajun.schoolsecretary;

public class Student {
    private String name;
    private String phone;
    private String remark;
    private String classname;
    private int id;
    private String idname;

    public Student(int id, String name, String phone, String remark, String classname) {
        this.name = name;
        this.phone = phone;
        this.remark = remark;
        this.classname = classname;
        this.id = id;
        idname = String.valueOf(id);
    }

    public Student(int id, String name, String classname) {
        this.name = name;
        this.phone = "";
        this.remark = "";
        this.classname = classname;
        this.id = id;
        idname = String.valueOf(id);
    }

    public String getIdname() {
        return idname;
    }

    public void setIdname(String idname) {
        this.idname = idname;
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
