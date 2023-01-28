package com.netcompany.service;

import java.util.List;

import com.netcompany.dto.ValidationResult;
import com.netcompany.entity.BalanceSheet;
import com.netcompany.entity.Course;
import com.netcompany.exception.ValidationException;

public interface GpaBalancingService {
    BalanceSheet createNewBalanceSheet(List<Course> courses, float expectedGpa);

    float calculateCurrentGpa(BalanceSheet balanceSheet);

    Float calculateNeededGrade(BalanceSheet balanceSheet);

    void updateBalanceCourse(BalanceSheet balanceSheet, String courseCode, float adjustedGrade)
            throws ValidationException;

    void saveBalanceSheet(BalanceSheet balanceSheet) throws ValidationException;

    void removeBalanceSheet(BalanceSheet balanceSheet) throws ValidationException;

    BalanceSheet findBalanceSheetWithCreatedTime(long createdTime);

    ValidationResult checkExist(long createdTime);

    void resetBalanceCourse(BalanceSheet currentBalanceSheet, String courseCode) throws ValidationException;

    void resetBalanceSheet(BalanceSheet balanceSheet);

    List<BalanceSheet> getSavedBalanceSheets();
}
