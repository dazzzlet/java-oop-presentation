package com.netcompany.menu.manageCourse;

import java.util.Scanner;

import com.netcompany.core.ConsoleContext;
import com.netcompany.core.MenuItem;
import com.netcompany.dto.ValidationResult;
import com.netcompany.exception.ValidationException;
import com.netcompany.service.CourseService;
import com.netcompany.service.impl.CourseServiceImpl;
import com.netcompany.utils.ConsoleUtils;

public class RemoveCourseFeature implements MenuItem {
    private ConsoleContext consoleContext;
    private CourseService courseService = new CourseServiceImpl();

    public RemoveCourseFeature(ConsoleContext consoleContext) {
        this.consoleContext = consoleContext;
    }

    @Override
    public String getItemName() {
        return "Remove courses";
    }

    @Override
    public void launch() {
        Scanner scanner = this.consoleContext.getScanner();
        ConsoleUtils.cleanConsole();
        System.out.println("Remove courses");
        System.out.println("     ---      ");
        System.out.print("Enter course codes (delimeter by ,): ");
        String courseString = scanner.nextLine();
        String[] courseCodes = courseString.split("[, ]+");
        int noRemoved = 0;
        int noInvalid = 0;
        for (String courseCode : courseCodes) {
            try {
                this.courseService.removeCourse(courseCode);
                noInvalid++;
            } catch (ValidationException e) {
                noRemoved += 1;
                for (ValidationResult result : e.getValidationMessage()) {
                    System.out.println(result.getValidationMessage());
                }
            }
        }
        System.out.print(String.format("Removed %d course(s)", noRemoved));
        if (noInvalid > 0) {
            System.out.println(String.format(", %d course(s) was invalid", noRemoved, noInvalid));
        } else {
            System.out.println();
        }
    }

}
