package com.netcompany.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.netcompany.core.Entity;

public class BalanceSheet implements Entity {
    private List<BalancingCourse> courses;
    private float expectedGpa;
    private long createdOn;
    private Float currentGpa;
    private Float neededAverageGrade;

    private BalanceSheet(BalanceSheet balanceSheet) {
        this.copy(balanceSheet);
    }

    public BalanceSheet() {
        this.courses = new ArrayList<>();
    }

    public BalanceSheet(List<BalancingCourse> courses, float expectedGpa, long createdOn) {
        this.courses = courses;
        this.expectedGpa = expectedGpa;
        this.createdOn = createdOn;
        this.calculateCurrentGpa();
        this.calculateNeededGrade();
    }

    public Float getCurrentGpa() {
        return currentGpa;
    }

    public Float getNeededAverageGrade() {
        return neededAverageGrade;
    }

    protected void setCourses(List<BalancingCourse> courses) {
        this.courses = courses;
    }

    public long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(long createdOn) {
        this.createdOn = createdOn;
    }

    public float getExpectedGpa() {
        return expectedGpa;
    }

    public void setExpectedGpa(float expectedGpa) {
        this.expectedGpa = expectedGpa;
        this.calculateCurrentGpa();
        this.calculateNeededGrade();
    }

    public List<BalancingCourse> getCourses() {
        return courses;
    }

    public BalanceSheet clone() {
        return new BalanceSheet(this);
    }

    public void copy(BalanceSheet balanceSheet) {
        this.copy(balanceSheet, false);
    }

    public void copy(BalanceSheet balanceSheet, boolean strictMode) {
        this.expectedGpa = balanceSheet.getExpectedGpa();
        this.createdOn = balanceSheet.getCreatedOn();
        if (strictMode) {
            Map<String, BalancingCourse> existingCourseCode = getBalancingMap(this.courses);
            Map<String, BalancingCourse> newCourseCode = this.getBalancingMap(balanceSheet.getCourses());
            for (BalancingCourse balancingCourse : balanceSheet.getCourses()) {
                if (existingCourseCode.containsKey(balancingCourse.getCode())) {
                    existingCourseCode.get(balancingCourse.getCode())
                            .copy(balancingCourse);
                } else {
                    this.courses.add(balancingCourse.clone());
                }
            }
            for (BalancingCourse balancingCourse : this.courses) {
                if (!newCourseCode.containsKey(balancingCourse.getCode())) {
                    this.courses.remove(balancingCourse);
                }
            }
        } else {
            this.courses = new ArrayList<>();
            for (BalancingCourse balancingCourse : balanceSheet.getCourses()) {
                this.courses.add(balancingCourse.clone());
            }
        }
        this.calculateCurrentGpa();
        this.calculateNeededGrade();
    }

    private Map<String, BalancingCourse> getBalancingMap(
            List<BalancingCourse> balancingCourses) {
        Map<String, BalancingCourse> balancingMap = new HashMap<>();
        for (BalancingCourse balancingCourse : balancingCourses) {
            balancingMap.put(balancingCourse.getCode(), balancingCourse);
        }
        return balancingMap;
    }

    public void reset() {
        for (BalancingCourse balancingCourse : courses) {
            balancingCourse.resetAdjustedGrade();
        }
        this.calculateNeededGrade();
    }

    public BalancingCourse getCourse(String courseCode) {
        BalancingCourse existingCourse = null;
        for (BalancingCourse course : this.getCourses()) {
            if (course.getCode().equalsIgnoreCase(courseCode)) {
                existingCourse = course;
            }
        }
        return existingCourse;
    }

    public void setAdjustedGrade(String courseCode, float adjustedGrade) {
        BalancingCourse course = this.getCourse(courseCode);
        if (course != null) {
            course.setAdjustedGrade(adjustedGrade);
            this.calculateNeededGrade();
        }
    }

    public void resetAdjustedGrade(String courseCode) {
        BalancingCourse course = this.getCourse(courseCode);
        if (course != null) {
            course.resetAdjustedGrade();
            this.calculateNeededGrade();
        }
    }

    private void calculateCurrentGpa() {
        float total = this.calculateTotalGrade(
                this.getCourses(), null, false);
        int noNonGradedCourses = countNonGradedCourse(this.getCourses());
        this.currentGpa = total / noNonGradedCourses;
    }

    private void calculateNeededGrade() {
        float totalGrade = this.calculateTotalGrade(
                this.getCourses(), null, true);
        int noNonGradedCourses = countNonGradedCourse(this.getCourses());
        if (noNonGradedCourses == 0) {
            this.neededAverageGrade = null;
        } else {
            this.neededAverageGrade = (this.getExpectedGpa() * this.getCourses().size() - totalGrade)
                    / noNonGradedCourses;
        }
    }

    private <T extends Course> float calculateTotalGrade(
            List<T> courses, Float defaultGrade, boolean includeAdjusted) {
        float totalGrade = 0;
        for (Course course : courses) {
            totalGrade += this.calculateCourseGrade(
                    course, defaultGrade, includeAdjusted);
        }
        return totalGrade;
    }

    private Float calculateCourseGrade(
            Course course, Float defaultGrade, boolean includeAdjusted) {
        Float grade = course.getGrade();
        if (includeAdjusted && course instanceof BalancingCourse) {
            BalancingCourse balancingCourse = (BalancingCourse) course;
            if (balancingCourse.getAdjustedGrade() != null) {
                grade = balancingCourse.getAdjustedGrade();
            }
        }
        if (grade != null) {
            return grade;
        } else if (defaultGrade != null) {
            return defaultGrade;
        }
        return 0f;
    }

    private <T extends Course> int countNonGradedCourse(List<T> courses) {
        int countedCourses = 0;
        for (Course course : courses) {
            if (course.getGrade() == null) {
                countedCourses++;
            }
        }
        return countedCourses;
    }

}
