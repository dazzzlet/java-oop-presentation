package com.netcompany.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import com.netcompany.config.Constant;
import com.netcompany.dto.ValidationResult;
import com.netcompany.entity.Course;
import com.netcompany.exception.ValidationException;
import com.netcompany.repository.CourseRepository;
import com.netcompany.repository.impl.CourseRepositoryImpl;
import com.netcompany.service.CourseService;

public class CourseServiceImpl implements CourseService {
    static final Pattern courseCodePattern = Pattern.compile("^[a-z0-9]{3,6}$");
    private CourseRepository courseRepository = new CourseRepositoryImpl();

    @Override
    public Course findCourseByCode(String code) {
        return this.courseRepository.findByCode(code);
    }

    @Override
    public ValidationResult checkValidCourseCode(String courseCode) {
        if (!courseCodePattern.matcher(courseCode).find()) {
            return new ValidationResult(
                    String.format("Course \"%s\" is invaid: the course code must contains 3-6 alphanumberic characters",
                            courseCode));
        }
        return null;
    }

    @Override
    public ValidationResult checkNotExistCourseCode(String courseCode) {
        Course existedCourse = this.courseRepository.findByCode(courseCode);
        if (existedCourse != null) {
            return new ValidationResult(
                    String.format("Course \"%s\" was existed", courseCode));
        }
        return null;
    }

    @Override
    public ValidationResult checkExistCourseCode(String courseCode) {
        Course existedCourse = this.courseRepository.findByCode(courseCode);
        if (existedCourse == null) {
            return new ValidationResult(
                    String.format("Course \"%s\" was not existed", courseCode));
        }
        return null;
    }

    @Override
    public ValidationResult checkCourseGrade(Float gradeValue) {
        if (gradeValue != null && (gradeValue < 0 || gradeValue > Constant.MAX_GRADE_VALUE)) {
            return new ValidationResult(
                    String.format("Invalid: Course grade must between 0 and %.2f", Constant.MAX_GRADE_VALUE));
        }
        return null;
    }

    @Override
    public void addNewCourse(Course course) throws ValidationException {
        this.validateNewCourse(course);
        this.courseRepository.add(course);
    }

    @Override
    public void updateCourse(Course course) throws ValidationException {
        this.validateUpdatingCourse(course);
        this.courseRepository.update(course);
    }

    @Override
    public Course removeCourse(String courseCode) throws ValidationException {
        this.validateRemoveCourse(courseCode);
        Course course = this.courseRepository.findByCode(courseCode);
        this.courseRepository.delete(course);
        return course;
    }

    @Override
    public void cleanCourseGrade(String courseCode) throws ValidationException {
        ValidationResult result = this.checkExistCourseCode(courseCode);
        if (result != null) {
            throw new ValidationException("", Collections.singletonList(result));
        }
        Course course = this.courseRepository.findByCode(courseCode);
        course.setGrade(null);
        this.courseRepository.update(course);
    }

    @Override
    public List<Course> getAll() {
        return this.courseRepository.getAll();
    }

    private void validateUpdatingCourse(Course course) throws ValidationException {
        List<ValidationResult> error = new ArrayList<>();
        try {
            ValidationResult result = this.checkExistCourseCode(course.getCode());
            if (result != null) {
                error.add(result);
                return;
            }
            Float gradeValue = course.getGrade();
            result = this.checkCourseGrade(gradeValue);
            if (result != null) {
                error.add(result);
                return;
            }
        } finally {
            if (!error.isEmpty()) {
                throw new ValidationException("", error);
            }
        }
    }

    private void validateNewCourse(Course course) throws ValidationException {
        List<ValidationResult> error = new ArrayList<>();
        ValidationResult result = this.checkValidCourseCode(course.getCode());
        if (result != null) {
            error.add(result);
        }
        result = this.checkNotExistCourseCode(course.getCode());
        if (result != null) {
            error.add(result);
        }
        if (!error.isEmpty()) {
            throw new ValidationException("", error);
        }
    }

    private void validateRemoveCourse(String courseCode) throws ValidationException {
        List<ValidationResult> error = new ArrayList<>();
        ValidationResult result = this.checkExistCourseCode(courseCode);
        if (result != null) {
            error.add(result);
        }
        if (!error.isEmpty()) {
            throw new ValidationException("", error);
        }
    }
}
