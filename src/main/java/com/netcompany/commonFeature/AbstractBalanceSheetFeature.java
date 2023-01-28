package com.netcompany.commonFeature;

import com.netcompany.core.ConsoleContext;
import com.netcompany.core.MenuItem;
import com.netcompany.entity.BalanceSheet;

public abstract class AbstractBalanceSheetFeature implements MenuItem {
    protected ConsoleContext consoleContext;
    protected BalanceSheet currentBalanceSheet;

    public AbstractBalanceSheetFeature(ConsoleContext consoleContext) {
        this.consoleContext = consoleContext;
    }

    public void setCurrentBalanceSheet(BalanceSheet currentBalanceSheet) {
        this.currentBalanceSheet = currentBalanceSheet;
    }
}
