package com.netcompany.repository;

import java.util.List;

import com.netcompany.entity.Course;

public interface CourseRepository {
    void add(Course course);

    void update(Course course);

    void delete(Course course);

    Course findByCode(String courseCode);

    List<Course> getAll();
}
