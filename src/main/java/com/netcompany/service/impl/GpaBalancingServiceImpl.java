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
import com.netcompany.repository.CourseRepository;
import com.netcompany.repository.GpaBalanceSheetRepository;
import com.netcompany.repository.impl.CourseRepositoryImpl;
import com.netcompany.repository.impl.GpaBalanceSheetRepositoryImpl;
import com.netcompany.service.GpaBalancingService;

public class GpaBalancingServiceImpl implements GpaBalancingService {
    GpaBalanceSheetRepository gpaBalanceSheetRepository = new GpaBalanceSheetRepositoryImpl();
    CourseRepository courseRepository = new CourseRepositoryImpl();

    @Override
    public BalanceSheet createNewBalanceSheet(float expectedGpa) {
        List<Course> courses = this.courseRepository.getAll();
        List<BalancingCourse> balancingCourses = new ArrayList<>();
        for (Course course : courses) {
            BalancingCourse balancingCourse = new BalancingCourse();
            balancingCourse.setCode(course.getCode());
            balancingCourse.copy(course);
            balancingCourses.add(balancingCourse);
        }
        BalanceSheet balanceSheet = new BalanceSheet(
                balancingCourses, expectedGpa, Calendar.getInstance().getTimeInMillis());
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
            balanceSheet.setAdjustedGrade(courseCode, adjustedGrade);
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
            balanceSheet.resetAdjustedGrade(courseCode);
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

    private BalancingCourse findBalancingCourse(BalanceSheet balanceSheet, String courseCode)
            throws ValidationException {
        if (balanceSheet == null) {
            throw new ValidationException("Error! Could not adjust this course", Collections.emptyList());
        }
        if (balanceSheet.getCourses().isEmpty()) {
            throw new ValidationException("Error! Could not adjust this course", Collections.emptyList());
        }
        return balanceSheet.getCourse(courseCode);
    }

}
