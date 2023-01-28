package com.netcompany.service;

import java.util.List;

import com.netcompany.dto.ValidationResult;
import com.netcompany.entity.Course;
import com.netcompany.exception.ValidationException;

public interface CourseService {
    Course findCourseByCode(String code);

    ValidationResult checkValidCourseCode(String courseCode);

    ValidationResult checkNotExistCourseCode(String courseCode);

    ValidationResult checkExistCourseCode(String courseCode);

    ValidationResult checkCourseGrade(Float gradeValue);

    void addNewCourse(Course course) throws ValidationException;

    void updateCourse(Course course) throws ValidationException;

    Course removeCourse(String courseCode) throws ValidationException;

    void cleanCourseGrade(String courseCode) throws ValidationException;

    List<Course> getAll();

}
