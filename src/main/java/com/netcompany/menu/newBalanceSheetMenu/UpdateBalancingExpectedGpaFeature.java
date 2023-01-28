package com.netcompany.menu.newBalanceSheetMenu;

import java.util.Scanner;

import com.netcompany.commonFeature.AbstractBalanceSheetFeature;
import com.netcompany.config.Constant;
import com.netcompany.core.ConsoleContext;
import com.netcompany.utils.ConsoleUtils;
import com.netcompany.utils.StringUtils;

public class UpdateBalancingExpectedGpaFeature extends AbstractBalanceSheetFeature {
    public UpdateBalancingExpectedGpaFeature(ConsoleContext consoleContext) {
        super(consoleContext);
    }

    @Override
    public String getItemName() {
        return "Update expected GPA";
    }

    @Override
    public void launch() {
        ConsoleUtils.cleanConsole();
        Scanner scanner = this.consoleContext.getScanner();
        Float gpaValue = null;
        do {
            System.out.print("Enter your expected GPA (input empty to cancel): ");
            String courseGradeString = scanner.nextLine();
            if (courseGradeString.isEmpty()) {
                return;
            }
            if (!StringUtils.isMatchedDecimalPattern(courseGradeString)) {
                System.out.println("Invalid: Course grade must be decimal format");
            }
            gpaValue = Float.parseFloat(courseGradeString);
            if (gpaValue < 0 || gpaValue > Constant.MAX_GRADE_VALUE) {
                System.out.println(
                        String.format("Invalid: Course grade must between 0 and %.2f", Constant.MAX_GRADE_VALUE));
            }
        } while (gpaValue == null);
        this.currentBalanceSheet.setExpectedGpa(gpaValue);
    }
}
