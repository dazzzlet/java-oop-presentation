package com.netcompany.menu.manageCourse;

import java.util.Scanner;

import com.netcompany.core.ConsoleContext;
import com.netcompany.core.MenuItem;
import com.netcompany.dto.ValidationResult;
import com.netcompany.entity.Course;
import com.netcompany.exception.ValidationException;
import com.netcompany.service.CourseService;
import com.netcompany.service.impl.CourseServiceImpl;
import com.netcompany.utils.ConsoleUtils;

public class AddCourseFeature implements MenuItem {
    private ConsoleContext consoleContext;
    private CourseService courseService = new CourseServiceImpl();

    public AddCourseFeature(ConsoleContext consoleContext) {
        this.consoleContext = consoleContext;
    }

    @Override
    public String getItemName() {
        return "Add courses";
    }

    @Override
    public void launch() {
        ConsoleUtils.cleanConsole();
        Scanner scanner = consoleContext.getScanner();
        System.out.println("Add new courses");
        System.out.println("     ---       ");
        System.out.print("Enter your course codes (delimeter by ,): ");
        String courseString = scanner.nextLine();
        String[] courseCodes = courseString.split("[\\, ]+");
        System.out.println(courseString);
        int noAdded = 0;
        int noInvalid = 0;
        for (String courseCode : courseCodes) {
            try {
                this.courseService.addNewCourse(new Course(courseCode));
                noAdded++;
            } catch (ValidationException e) {
                noInvalid++;
                for (ValidationResult result : e.getValidationMessage()) {
                    System.out.println(result.getValidationMessage());
                }
            }
        }
        System.out.print(String.format("Added %d course(s)", noAdded));
        if (noInvalid > 0) {
            System.out.println(String.format(", %d course(s) was invalid", noInvalid));
        } else {
            System.out.println();
        }
    }

}
