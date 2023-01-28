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
    }

    public List<BalancingCourse> getCourses() {
        return courses;
    }

    public void setCourses(List<BalancingCourse> courses) {
        this.courses = courses;
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
                    existingCourseCode.get(balancingCourse.getCode()).copy(balancingCourse);
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
    }

    private Map<String, BalancingCourse> getBalancingMap(List<BalancingCourse> balancingCourses) {
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
    }
}
