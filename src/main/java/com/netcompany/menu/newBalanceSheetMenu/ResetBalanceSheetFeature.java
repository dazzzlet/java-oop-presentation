package com.netcompany.menu.newBalanceSheetMenu;

import com.netcompany.commonFeature.AbstractBalanceSheetFeature;
import com.netcompany.core.ConsoleContext;
import com.netcompany.service.GpaBalancingService;
import com.netcompany.service.impl.GpaBalancingServiceImpl;
import com.netcompany.utils.ConsoleUtils;

public class ResetBalanceSheetFeature extends AbstractBalanceSheetFeature {
    private GpaBalancingService gpaBalancingService = new GpaBalancingServiceImpl();

    public ResetBalanceSheetFeature(
            ConsoleContext consoleContext) {
        super(consoleContext);
    }

    @Override
    public String getItemName() {
        return "Clean all adjustment";
    }

    @Override
    public void launch() {
        ConsoleUtils.cleanConsole();
        this.gpaBalancingService.resetBalanceSheet(this.currentBalanceSheet);
        System.out.println("Balance sheet has been cleaned up");
    }

}
