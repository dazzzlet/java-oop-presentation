package com.netcompany.menu.manageGrade;

import java.util.Scanner;

import com.netcompany.core.ConsoleContext;
import com.netcompany.core.MenuItem;
import com.netcompany.dto.ValidationResult;
import com.netcompany.entity.Course;
import com.netcompany.exception.ValidationException;
import com.netcompany.service.CourseService;
import com.netcompany.service.impl.CourseServiceImpl;
import com.netcompany.utils.ConsoleUtils;
import com.netcompany.utils.StringUtils;

public class UpdateGradeFeature implements MenuItem {
    private ConsoleContext consoleContext;
    private CourseService courseService = new CourseServiceImpl();

    public UpdateGradeFeature(ConsoleContext consoleContext) {
        this.consoleContext = consoleContext;
    }

    @Override
    public String getItemName() {
        return "Update grade for a course";
    }

    @Override
    public void launch() {
        Scanner scanner = consoleContext.getScanner();
        ConsoleUtils.cleanConsole();
        System.out.println("Update grade for a course");
        System.out.println("     ---       ");

        System.out.print("Enter course code: ");
        String courseCode = scanner.nextLine();
        Course course = this.courseService.findCourseByCode(courseCode);
        if (course == null) {
            System.out.println(
                    String.format("Course code \"%s\" is not existed", courseCode));
            return;
        }
        System.out.print("Enter course grade: ");
        String courseGradeString = scanner.nextLine();
        if (!StringUtils.isMatchedDecimalPattern(courseGradeString)) {
            System.out.println("Invalid: Course grade must be decimal format");
            return;
        }
        float gradeValue = Float.parseFloat(courseGradeString);
        course.setGrade(gradeValue);
        try {
            this.courseService.updateCourse(course);
            System.out.println(
                    String.format("Update grade successfully for course \"%s\".", courseCode));
        } catch (ValidationException e) {
            for (ValidationResult result : e.getValidationMessage()) {
                System.out.println(result.getValidationMessage());
            }
        }
    }

}
