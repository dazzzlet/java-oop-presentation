package com.netcompany.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.netcompany.config.Constant;
import com.netcompany.dto.ValidationResult;
import com.netcompany.entity.BalanceSheet;
import com.netcompany.entity.BalancingCourse;
import com.netcompany.entity.Course;
import com.netcompany.exception.ValidationException;
import com.netcompany.repository.GpaBalanceSheetRepository;
import com.netcompany.repository.impl.GpaBalanceSheetRepositoryImpl;
import com.netcompany.service.GpaBalancingService;

public class GpaBalancingServiceImpl implements GpaBalancingService {
    GpaBalanceSheetRepository gpaBalanceSheetRepository = new GpaBalanceSheetRepositoryImpl();

    @Override
    public BalanceSheet createNewBalanceSheet(List<Course> courses, float expectedGpa) {
        BalanceSheet balanceSheet = new BalanceSheet();
        List<BalancingCourse> balancingCourses = new ArrayList<>();
        for (Course course : courses) {
            BalancingCourse balancingCourse = new BalancingCourse();
            balancingCourse.setCode(course.getCode());
            balancingCourse.copy(course);
            balancingCourses.add(balancingCourse);
        }
        balanceSheet.setCourses(balancingCourses);
        balanceSheet.setExpectedGpa(expectedGpa);
        balanceSheet.setCreatedOn(Calendar.getInstance().getTimeInMillis());
        return balanceSheet;
    }

    @Override
    public BalanceSheet findBalanceSheetWithCreatedTime(long createdTime) {
        return this.gpaBalanceSheetRepository.findByCreateOn(createdTime);
    }

    @Override
    public ValidationResult checkExist(long createdTime) {
        BalanceSheet balanceSheet = this.gpaBalanceSheetRepository.findByCreateOn(createdTime);
        if (balanceSheet == null) {
            return new ValidationResult(
                    String.format("Balance sheet \"%t\" was not existed",
                            Date.from(Instant.ofEpochMilli(createdTime))));
        }
        return null;
    }

    @Override
    public float calculateCurrentGpa(BalanceSheet balanceSheet) {
        float total = this.calculateTotalGrade(balanceSheet.getCourses(), null, false);
        int noNonGradedCourses = countNonGradedCourse(balanceSheet.getCourses());
        return total / noNonGradedCourses;
    }

    @Override
    public Float calculateNeededGrade(BalanceSheet balanceSheet) {
        float totalGrade = this.calculateTotalGrade(balanceSheet.getCourses(), null, true);
        int noNonGradedCourses = countNonGradedCourse(balanceSheet.getCourses());
        if (noNonGradedCourses == 0) {
            return null;
        }
        return (balanceSheet.getExpectedGpa() * balanceSheet.getCourses().size() - totalGrade) / noNonGradedCourses;
    }

    @Override
    public void saveBalanceSheet(BalanceSheet balanceSheet) {
        this.gpaBalanceSheetRepository.add(balanceSheet);
    }

    @Override
    public void removeBalanceSheet(BalanceSheet balanceSheet) throws ValidationException {
        this.checkExist(balanceSheet.getCreatedOn());
        this.gpaBalanceSheetRepository.delete(balanceSheet);
    }

    @Override
    public void updateBalanceCourse(BalanceSheet balanceSheet, String courseCode, float adjustedGrade)
            throws ValidationException {
        List<ValidationResult> validationResults = new ArrayList<>();
        BalancingCourse updatingCourse = this.findBalancingCourse(balanceSheet, courseCode);
        if (updatingCourse == null) {
            validationResults.add(
                    new ValidationResult(String.format("Course code \"%s\" is not existed", courseCode)));
        }
        if (adjustedGrade < 0 || adjustedGrade > Constant.MAX_GRADE_VALUE) {
            validationResults.add(
                    new ValidationResult(
                            String.format("Invalid: Course grade must between 0 and %.2f", Constant.MAX_GRADE_VALUE)));
        }
        if (validationResults.isEmpty()) {
            updatingCourse.setAdjustedGrade(adjustedGrade);
            this.gpaBalanceSheetRepository.update(balanceSheet);
        } else {
            throw new ValidationException("", validationResults);
        }
    }

    @Override
    public void resetBalanceCourse(BalanceSheet balanceSheet, String courseCode) throws ValidationException {
        List<ValidationResult> validationResults = new ArrayList<>();
        BalancingCourse updatingCourse = this.findBalancingCourse(balanceSheet, courseCode);
        if (updatingCourse == null) {
            validationResults.add(
                    new ValidationResult(String.format("Course code \"%s\" is not existed", courseCode)));
        }
        if (validationResults.isEmpty()) {
            updatingCourse.resetAdjustedGrade();
            this.gpaBalanceSheetRepository.update(balanceSheet);
        } else {
            throw new ValidationException("", validationResults);
        }
    }

    @Override
    public void resetBalanceSheet(BalanceSheet balanceSheet) {
        balanceSheet.reset();
        this.gpaBalanceSheetRepository.update(balanceSheet);
    }

    @Override
    public List<BalanceSheet> getSavedBalanceSheets() {
        return this.gpaBalanceSheetRepository.getAll();
    }

    private <T extends Course> float calculateTotalGrade(List<T> courses, Float defaultGrade, boolean includeAdjusted) {
        float totalGrade = 0;
        for (Course course : courses) {
            totalGrade += this.calculateCourseGrade(course, defaultGrade, includeAdjusted);
        }
        return totalGrade;
    }

    private float calculateCourseGrade(Course course, Float defaultGrade, boolean includeAdjusted) {
        Float grade = course.getGrade();
        if (includeAdjusted && course instanceof BalancingCourse) {
            BalancingCourse balancingCourse = (BalancingCourse) course;
            if (balancingCourse.getAdjustedGrade() != null) {
                grade = balancingCourse.getAdjustedGrade();
            }
        }
        if (grade != null) {
            return course.getGrade();
        } else if (defaultGrade != null) {
            return defaultGrade;
        }
        return 0;
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

    private BalancingCourse findBalancingCourse(BalanceSheet balanceSheet, String courseCode)
            throws ValidationException {
        if (balanceSheet == null) {
            throw new ValidationException("Error! Could not adjust this course", Collections.emptyList());
        }
        if (balanceSheet.getCourses().isEmpty()) {
            throw new ValidationException("Error! Could not adjust this course", Collections.emptyList());
        }
        BalancingCourse existingCourse = null;
        for (BalancingCourse course : balanceSheet.getCourses()) {
            if (course.getCode().equalsIgnoreCase(courseCode)) {
                existingCourse = course;
            }
        }
        return existingCourse;
    }

}
