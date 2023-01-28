package com.netcompany.menu.manageBalanceSheet;

import com.netcompany.commonFeature.AbstractSavedBalanceSheetFeature;
import com.netcompany.commonFeature.ShowBalanceSheetDetailFeature;
import com.netcompany.core.ConsoleContext;
import com.netcompany.entity.BalanceSheet;
import com.netcompany.utils.ConsoleUtils;
import com.netcompany.utils.StringUtils;

public class ShowSavedBalanceSheetFeature extends AbstractSavedBalanceSheetFeature {
    private ShowBalanceSheetDetailFeature showBalanceSheetDetailFeature;

    public ShowSavedBalanceSheetFeature(ConsoleContext consoleContext) {
        super(consoleContext);
        this.showBalanceSheetDetailFeature = new ShowBalanceSheetDetailFeature(consoleContext);
    }

    @Override
    public String getItemName() {
        return "Show a saved sheet";
    }

    @Override
    public String getBackItemName() {
        return "Back to manage saved balance sheets";
    }

    @Override
    protected void launchBalanceSheetFeature(BalanceSheet balanceSheet) {
        ConsoleUtils.cleanConsole();
        System.out.println("Details for GPA sheet " + StringUtils.toEpocTimeString(balanceSheet.getCreatedOn()));
        showBalanceSheetDetailFeature.setCurrentBalanceSheet(balanceSheet);
        showBalanceSheetDetailFeature.launch();
    }

}
