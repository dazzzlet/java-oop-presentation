package com.netcompany.entity;

public class BalancingCourse extends Course {
    private Float adjustedGrade;

    public BalancingCourse() {

    }

    public BalancingCourse(Course course) {
        this.setCode(course.getCode());
        this.setGrade(course.getGrade());
        this.adjustedGrade = null;
    }

    public BalancingCourse(String courseCode, Float grade, Float adjustedGrade) {
        super(courseCode);
        this.setGrade(grade);
        this.adjustedGrade = adjustedGrade;
    }

    public Float getAdjustedGrade() {
        return adjustedGrade;
    }

    public void setAdjustedGrade(float adjustedGrade) {
        this.adjustedGrade = adjustedGrade;
    }

    @Override
    public void copy(Course course) {
        super.copy(course);
        if (course instanceof BalancingCourse) {
            BalancingCourse balancingCourse = (BalancingCourse) course;
            this.adjustedGrade = balancingCourse.getAdjustedGrade();
        }
    }

    @Override
    public BalancingCourse clone() {
        BalancingCourse clonedCourse = new BalancingCourse();
        clonedCourse.copy(this);
        return clonedCourse;
    }

    public void resetAdjustedGrade() {
        this.adjustedGrade = this.getGrade();
    }
}
