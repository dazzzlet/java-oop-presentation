package com.netcompany.menu.newBalanceSheetMenu;

import java.util.Scanner;

import com.netcompany.commonFeature.AbstractBalanceSheetFeature;
import com.netcompany.core.ConsoleContext;
import com.netcompany.dto.ValidationResult;
import com.netcompany.exception.ValidationException;
import com.netcompany.service.GpaBalancingService;
import com.netcompany.service.impl.GpaBalancingServiceImpl;
import com.netcompany.utils.ConsoleUtils;
import com.netcompany.utils.StringUtils;

public class AdjustBalanceSheetFeature extends AbstractBalanceSheetFeature {
    GpaBalancingService gpaBalancingService = new GpaBalancingServiceImpl();

    public AdjustBalanceSheetFeature(ConsoleContext consoleContext) {
        super(consoleContext);
    }

    @Override
    public String getItemName() {
        return "Adjust grade for a course";
    }

    @Override
    public void launch() {
        Scanner scanner = this.consoleContext.getScanner();
        ConsoleUtils.cleanConsole();
        System.out.println("Update grade for a course");
        System.out.println("     ---       ");

        System.out.print("Enter course code: ");
        String courseCode = scanner.nextLine();
        System.out.print("Enter course grade: ");
        String courseGradeString = scanner.nextLine();
        if (!StringUtils.isMatchedDecimalPattern(courseGradeString)) {
            System.out.println("Invalid: Course grade must be decimal format");
            return;
        }
        float gradeValue = Float.parseFloat(courseGradeString);
        try {
            this.gpaBalancingService.updateBalanceCourse(currentBalanceSheet, courseCode, gradeValue);
            System.out.println(
                    String.format("Update grade successfully for course \"%s\".", courseCode));
        } catch (ValidationException e) {
            if (!e.getMessage().isEmpty()) {
                System.out.println(e.getMessage());
            } else {
                System.out.println("Fail to adjust course grade:");
                for (ValidationResult result : e.getValidationMessage()) {
                    System.out.println(result.getValidationMessage());
                }
            }
        }
    }
}
