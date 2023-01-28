package com.netcompany.repository.impl;

import java.util.ArrayList;
import java.util.List;

import com.netcompany.entity.Course;
import com.netcompany.repository.CourseRepository;
import com.netcompany.utils.FileUtils;
import com.netcompany.utils.StringUtils;

public class CourseRepositoryImpl implements CourseRepository {
    static final String COURSES_FILENAME = "courses.txt";
    private static List<Course> cachedCourses = null;

    private static void loadCache() {
        if (cachedCourses == null) {
            cachedCourses = new ArrayList<>();
            String courseFileContent = FileUtils.readStringFromFile(COURSES_FILENAME);
            if (courseFileContent.isEmpty()) {
                return;
            }
            String[] courseFileContentLines = courseFileContent.split("\n");
            int noCourses = Integer.parseInt(courseFileContentLines[0]);
            if (noCourses != courseFileContentLines.length - 1) {
                return;
            }
            for (int i = 0; i < noCourses; i++) {
                String line = courseFileContentLines[i + 1];
                String[] lineContent = line.split(" ");
                String courseCode = lineContent[0];
                Course course = new Course();
                course.setCode(courseCode);
                if (StringUtils.isMatchedDecimalPattern(lineContent[1])) {
                    float grade = Float.parseFloat(lineContent[1]);
                    course.setGrade(grade);
                }
                cachedCourses.add(course);
            }
        }
    }

    private void writeCourses(List<Course> courses) {
        StringBuilder sb = new StringBuilder();
        sb.append(courses.size());
        sb.append('\n');
        for (Course course : courses) {
            sb.append(course.getCode());
            sb.append(' ');
            sb.append(course.getGrade());
            sb.append('\n');
        }
        FileUtils.writeStringToFile(COURSES_FILENAME, sb.toString());
    }

    @Override
    public void add(Course course) {
        Course _course = this._findCourseByCode(course.getCode());
        if (_course == null) {
            cachedCourses.add(course.clone());
            this.writeCourses(cachedCourses);
        }
    }

    @Override
    public void update(Course course) {
        Course _course = this._findCourseByCode(course.getCode());
        if (_course != null) {
            _course.copy(course);
            this.writeCourses(cachedCourses);
        }
    }

    @Override
    public void delete(Course course) {
        Course _course = this._findCourseByCode(course.getCode());
        if (_course != null) {
            cachedCourses.remove(_course);
            this.writeCourses(cachedCourses);
        }
    }

    @Override
    public Course findByCode(String courseCode) {
        Course course = this._findCourseByCode(courseCode);
        if (course != null) {
            return course.clone();
        }
        return null;
    }

    private Course _findCourseByCode(String courseCode) {
        loadCache();
        for (Course course : cachedCourses) {
            if (course.getCode().equalsIgnoreCase(courseCode)) {
                return course;
            }
        }
        return null;
    }

    @Override
    public List<Course> getAll() {
        loadCache();
        return cachedCourses;
    }

}
