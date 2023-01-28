package com.netcompany.menu.manageBalanceSheet;

import com.netcompany.commonFeature.AbstractSavedBalanceSheetFeature;
import com.netcompany.core.ConsoleContext;
import com.netcompany.dto.ValidationResult;
import com.netcompany.entity.BalanceSheet;
import com.netcompany.exception.ValidationException;
import com.netcompany.utils.ConsoleUtils;
import com.netcompany.utils.StringUtils;

public class RemoveSavedBalanceSheetFeature extends AbstractSavedBalanceSheetFeature {
    public RemoveSavedBalanceSheetFeature(ConsoleContext consoleContext) {
        super(consoleContext);
    }

    @Override
    public String getItemName() {
        return "Delete a saved sheet";
    }

    @Override
    public String getBackItemName() {
        return "Back to manage saved balance sheets";
    }

    @Override
    protected void launchBalanceSheetFeature(BalanceSheet balanceSheet) {
        ConsoleUtils.cleanConsole();
        String removingSaved = StringUtils.toEpocTimeString(balanceSheet.getCreatedOn());
        try {
            this.gpaBalancingService.removeBalanceSheet(balanceSheet);
            System.out.println(
                    String.format("Balance sheet \"%s\" has been removed", removingSaved));
            this.loadMenuItems();
        } catch (ValidationException e) {
            if (!e.getMessage().isEmpty()) {
                System.out.println(e.getMessage());
            } else {
                System.out.println(
                        String.format("Fail to remove balance sheet \"%s\":", removingSaved));
                for (ValidationResult result : e.getValidationMessage()) {
                    System.out.println(result.getValidationMessage());
                }
            }
        }
    }
}
