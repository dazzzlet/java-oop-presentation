package com.netcompany.menu.newBalanceSheetMenu;

import java.util.Scanner;

import com.netcompany.commonFeature.AbstractBalanceSheetFeature;
import com.netcompany.core.ConsoleContext;
import com.netcompany.dto.ValidationResult;
import com.netcompany.exception.ValidationException;
import com.netcompany.service.GpaBalancingService;
import com.netcompany.service.impl.GpaBalancingServiceImpl;
import com.netcompany.utils.ConsoleUtils;

public class CleanBalanceSheetAdjustmentFeature extends AbstractBalanceSheetFeature {
    private GpaBalancingService gpaBalancingService = new GpaBalancingServiceImpl();

    public CleanBalanceSheetAdjustmentFeature(
            ConsoleContext consoleContext) {
        super(consoleContext);
    }

    @Override
    public String getItemName() {
        return "Clean adjustment for a course";
    }

    @Override
    public void launch() {
        Scanner scanner = this.consoleContext.getScanner();
        ConsoleUtils.cleanConsole();
        System.out.println("Clean grade for a course");
        System.out.println("     ---       ");

        System.out.print("Enter course code: ");
        String courseCode = scanner.nextLine();
        try {
            this.gpaBalancingService.resetBalanceCourse(currentBalanceSheet, courseCode);
            System.out.println(
                    String.format("Clean adjustment successfully for course \"%s\".", courseCode));
        } catch (ValidationException e) {
            if (!e.getMessage().isEmpty()) {
                System.out.println(e.getMessage());
            } else {
                System.out.println("Fail to reset adjustment:");
                for (ValidationResult result : e.getValidationMessage()) {
                    System.out.println(result.getValidationMessage());
                }
            }
        }
    }
}
