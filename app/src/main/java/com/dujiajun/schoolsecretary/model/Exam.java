package com.dujiajun.schoolsecretary.model;

public class Exam {
    private String examName;
    private String studentName;
    private int score;
    private int rank;

    public Exam(String examName, String studentName) {
        this.examName = examName;
        this.studentName = studentName;
    }

    public Exam(String examName, String studentName, int rank, int score) {
        this.examName = examName;
        this.rank = rank;
        this.score = score;
        this.studentName = studentName;
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
