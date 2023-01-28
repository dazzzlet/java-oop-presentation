package com.netcompany.menu.manageGrade;

import java.util.Scanner;

import com.netcompany.core.ConsoleContext;
import com.netcompany.core.MenuItem;
import com.netcompany.dto.ValidationResult;
import com.netcompany.exception.ValidationException;
import com.netcompany.service.CourseService;
import com.netcompany.service.impl.CourseServiceImpl;
import com.netcompany.utils.ConsoleUtils;

public class CleanGradeFeature implements MenuItem {
    private ConsoleContext consoleContext;
    private CourseService courseService = new CourseServiceImpl();

    public CleanGradeFeature(ConsoleContext consoleContext) {
        this.consoleContext = consoleContext;
    }

    @Override
    public String getItemName() {
        return "Clean grade for a course";
    }

    @Override
    public void launch() {
        Scanner scanner = consoleContext.getScanner();
        ConsoleUtils.cleanConsole();
        System.out.println("Clean grade for a course");
        System.out.println("     ---       ");

        System.out.print("Enter course code: ");
        String courseCode = scanner.nextLine();
        try {
            this.courseService.cleanCourseGrade(courseCode);
            System.out.println(
                    String.format("Clean grade successfully for course \"%s\".", courseCode));
        } catch (ValidationException e) {
            for (ValidationResult result : e.getValidationMessage()) {
                System.out.println(result.getValidationMessage());
            }
        }
    }

}
