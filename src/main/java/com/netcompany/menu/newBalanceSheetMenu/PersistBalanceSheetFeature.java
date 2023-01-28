package com.netcompany.menu.newBalanceSheetMenu;

import com.netcompany.commonFeature.AbstractBalanceSheetFeature;
import com.netcompany.core.ConsoleContext;
import com.netcompany.dto.ValidationResult;
import com.netcompany.exception.ValidationException;
import com.netcompany.service.GpaBalancingService;
import com.netcompany.service.impl.GpaBalancingServiceImpl;
import com.netcompany.utils.ConsoleUtils;

public class PersistBalanceSheetFeature extends AbstractBalanceSheetFeature {
    private GpaBalancingService gpaBalancingService = new GpaBalancingServiceImpl();

    public PersistBalanceSheetFeature(
            ConsoleContext consoleContext) {
        super(consoleContext);
    }

    @Override
    public String getItemName() {
        return "Save this balance sheet";
    }

    @Override
    public void launch() {
        ConsoleUtils.cleanConsole();
        try {
            this.gpaBalancingService.saveBalanceSheet(this.currentBalanceSheet);
            System.out.println("Balance sheet has been saved");
        } catch (ValidationException e) {
            if (!e.getMessage().isEmpty()) {
                System.out.println(e.getMessage());
            } else {
                System.out.println("Fail to save balance sheet:");
                for (ValidationResult result : e.getValidationMessage()) {
                    System.out.println(result.getValidationMessage());
                }
            }
        }
    }

}
