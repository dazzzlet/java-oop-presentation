package com.netcompany.commonFeature;

import java.text.DecimalFormat;
import java.util.List;

import com.netcompany.core.MenuItem;
import com.netcompany.entity.Course;
import com.netcompany.service.CourseService;
import com.netcompany.service.impl.CourseServiceImpl;
import com.netcompany.utils.ConsoleUtils;
import com.netcompany.utils.StringUtils;

public class ShowCourseFeature implements MenuItem {
    private CourseService courseService = new CourseServiceImpl();

    public ShowCourseFeature() {
    }

    @Override
    public String getItemName() {
        return "Show all courses";
    }

    @Override
    public void launch() {
        ConsoleUtils.cleanConsole();
        System.out.println("List of courses");
        System.out.println("     ---       ");
        List<Course> courses = this.courseService.getAll();
        if (courses.isEmpty()) {
            System.out.println("No course was inputed!");
            return;
        }
        for (Course course : courses) {
            System.out.println(
                    String.format("- %s | %s", StringUtils.padLeft(course.getCode(), 7), this.getCourseGrade(course)));
        }
    }

    String getCourseGrade(Course course) {
        if (course.getGrade() == null) {
            return "not graded";
        } else {
            DecimalFormat df = new DecimalFormat("#.##");
            return df.format(course.getGrade());
        }
    }

}
