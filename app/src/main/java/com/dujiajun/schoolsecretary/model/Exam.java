package com.dujiajun.schoolsecretary.model;

public class Exam {
    private String examName;
    private String studentName;
    private int score;
    private int rank;
    private String classname;

    public Exam(String examName, String classname, String studentName) {
        this.examName = examName;
        this.classname = classname;
        this.studentName = studentName;
    }

    public Exam(String examName, String classname, String studentName, int score, int rank) {
        this.examName = examName;
        this.classname = classname;
        this.studentName = studentName;
        this.score = score;
        this.rank = rank;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
