package com.netcompany.commonFeature;

import com.netcompany.config.Constant;
import com.netcompany.core.ConsoleContext;
import com.netcompany.entity.BalancingCourse;
import com.netcompany.service.GpaBalancingService;
import com.netcompany.service.impl.GpaBalancingServiceImpl;
import com.netcompany.utils.ConsoleUtils;
import com.netcompany.utils.StringUtils;

public class ShowBalanceSheetDetailFeature extends AbstractBalanceSheetFeature {
    private GpaBalancingService gpaBalancingService = new GpaBalancingServiceImpl();

    public ShowBalanceSheetDetailFeature(ConsoleContext consoleContext) {
        super(consoleContext);
    }

    @Override
    public String getItemName() {
        return "Show balance sheet detail";
    }

    @Override
    public void launch() {
        System.out.println(this.getBalanceSheetDetailString());
    }

    public String getBalanceSheetDetailString() {
        StringBuilder stringBuilder = new StringBuilder();
        float currentGpa = this.currentBalanceSheet.getCurrentGpa();
        Float expectedGpa = this.currentBalanceSheet.getExpectedGpa();
        stringBuilder.append(String.format("  -Current GPA: %.2f- \n", currentGpa));
        stringBuilder.append(String.format("  -Expected GPA: %.2f- \n", this.currentBalanceSheet.getExpectedGpa()));
        stringBuilder.append("     ---      \n");
        if (expectedGpa != -1) {
            Float needAvgGrade = this.currentBalanceSheet.getNeededAverageGrade();
            this.getBalanceSheet(stringBuilder, needAvgGrade);
            return stringBuilder.toString();
        }
        return "";
    }

    private void getBalanceSheet(StringBuilder sb, Float needAvgGrade) {
        sb.append("Balance sheet detail\n");
        sb.append("         ---        \n");
        if (this.currentBalanceSheet.getCourses().isEmpty()) {
            sb.append("No course was inputed!\n");
            return;
        }
        for (BalancingCourse course : this.currentBalanceSheet.getCourses()) {
            if (course.getGrade() == null && course.getAdjustedGrade() == null) {
                if (needAvgGrade < 0) {
                    sb.append(
                            String.format("- %s | 0 (*)\n", StringUtils.padLeft(course.getCode(), 7), needAvgGrade));
                } else if (needAvgGrade >= Constant.MAX_GRADE_VALUE) {
                    sb.append(
                            String.format("- %s | Beyond reality (*)\n", StringUtils.padLeft(course.getCode(), 7),
                                    needAvgGrade));
                } else {
                    sb.append(
                            String.format("- %s | %.2f (*)\n", StringUtils.padLeft(course.getCode(), 7), needAvgGrade));
                }
            } else if (course.getAdjustedGrade() != null && course.getAdjustedGrade() != course.getGrade()) {
                sb.append(
                        String.format("- %s | %.2f (**)\n", StringUtils.padLeft(course.getCode(), 7),
                                course.getAdjustedGrade()));
            } else {
                sb.append(
                        String.format("- %s | %.2f\n", StringUtils.padLeft(course.getCode(), 7),
                                course.getGrade()));
            }
        }
        sb.append("(*) expected grade\n");
        sb.append("(**) adjusted grade\n");
        return;
    }
}
