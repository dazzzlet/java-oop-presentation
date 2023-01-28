package com.netcompany.entity;

import com.netcompany.core.Entity;

public class Course implements Entity {
    private String code;
    private Float grade;

    public Course() {
    }

    public Course(String courseCode) {
        this.code = courseCode.toLowerCase();
        this.grade = null;
    }

    private Course(Course course) {
        this.code = course.getCode();
        this.grade = course.getGrade();
    }

    public Float getGrade() {
        return grade;
    }

    public void setGrade(Float grade) {
        this.grade = grade;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void copy(Course course) {
        this.code = course.getCode();
        this.grade = course.getGrade();
    }

    public Course clone() {
        return new Course(this);
    }
}
