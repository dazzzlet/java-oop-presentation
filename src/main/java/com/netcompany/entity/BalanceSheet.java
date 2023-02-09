package com.netcompany.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.netcompany.core.Entity;

public class BalanceSheet implements Entity {
    protected List<BalancingCourse> courses;
    protected float expectedGpa;
    protected long createdOn;
    protected Float currentGpa;
    protected Float neededAverageGrade;

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
        List<BalancingCourse> courses = new ArrayList<>();
        for (BalancingCourse balancingCourse : this.courses) {
            courses.add(balancingCourse.clone());
        }
        return courses;
    }

    public boolean isEmptyCourses() {
        return this.courses.isEmpty();
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
            List<BalancingCourse> sourceCourses = balanceSheet.getCourses();
            Map<String, BalancingCourse> existingCourseCode = getBalancingMap(this.courses);
            Map<String, BalancingCourse> newCourseCode = this.getBalancingMap(sourceCourses);
            for (BalancingCourse balancingCourse : sourceCourses) {
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
            this.courses = balanceSheet.getCourses();
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
        BalancingCourse existingCourse = this._getCourse(courseCode);
        if (existingCourse != null) {
            return existingCourse.clone();
        }
        return null;
    }

    private BalancingCourse _getCourse(String courseCode) {
        BalancingCourse existingCourse = null;
        for (BalancingCourse course : this.courses) {
            if (course.getCode().equalsIgnoreCase(courseCode)) {
                existingCourse = course;
            }
        }
        return existingCourse;
    }

    public void setAdjustedGrade(String courseCode, float adjustedGrade) {
        BalancingCourse course = this._getCourse(courseCode);
        if (course != null) {
            course.setAdjustedGrade(adjustedGrade);
            this.calculateNeededGrade();
        }
    }

    public void resetAdjustedGrade(String courseCode) {
        BalancingCourse course = this._getCourse(courseCode);
        if (course != null) {
            course.resetAdjustedGrade();
            this.calculateNeededGrade();
        }
    }

    private void calculateCurrentGpa() {
        float total = this.calculateTotalGrade(null, false);
        int noNonGradedCourses = countNonGradedCourse();
        this.currentGpa = total / noNonGradedCourses;
    }

    private void calculateNeededGrade() {
        float totalGrade = this.calculateTotalGrade(null, true);
        int noNonGradedCourses = this.countNonGradedCourse();
        if (noNonGradedCourses == 0) {
            this.neededAverageGrade = null;
        } else {
            this.neededAverageGrade = (this.getExpectedGpa() * this.courses.size() - totalGrade)
                    / noNonGradedCourses;
        }
    }

    private <T extends Course> float calculateTotalGrade(Float defaultGrade, boolean includeAdjusted) {
        float totalGrade = 0;
        for (Course course : this.courses) {
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

    private <T extends Course> int countNonGradedCourse() {
        int countedCourses = 0;
        for (Course course : this.courses) {
            if (course.getGrade() == null) {
                countedCourses++;
            }
        }
        return countedCourses;
    }

}
