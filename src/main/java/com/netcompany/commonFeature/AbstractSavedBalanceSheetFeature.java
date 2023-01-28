package com.netcompany.commonFeature;

import java.util.List;

import com.netcompany.core.AbstractMenu;
import com.netcompany.core.ConsoleContext;
import com.netcompany.core.MenuItem;
import com.netcompany.entity.BalanceSheet;
import com.netcompany.service.GpaBalancingService;
import com.netcompany.service.impl.GpaBalancingServiceImpl;
import com.netcompany.utils.StringUtils;

public abstract class AbstractSavedBalanceSheetFeature extends AbstractMenu implements MenuItem {
    protected GpaBalancingService gpaBalancingService = new GpaBalancingServiceImpl();

    public AbstractSavedBalanceSheetFeature(ConsoleContext consoleContext) {
        super(consoleContext);
    }

    @Override
    public void launch() {
        this.loadMenuItems();
        super.launch();
    }

    protected void loadMenuItems() {
        this.menuItems.clear();
        List<BalanceSheet> savedSheets = this.gpaBalancingService.getSavedBalanceSheets();
        for (BalanceSheet balanceSheet : savedSheets) {
            this.menuItems.add(new BalanceSheetMenuItem(balanceSheet));
        }
    }

    @Override
    public String getMenuHeader() {
        return "List of saved balance sheets\n" +
                "            ----            \n";
    }

    @Override
    protected void launchMenuItem(int index) {
        MenuItem menuItem = this.menuItems.get(index);
        if (menuItem instanceof BalanceSheetMenuItem) {
            BalanceSheet balanceSheet = ((BalanceSheetMenuItem) menuItem).getBalanceSheet();
            balanceSheet = this.gpaBalancingService.findBalanceSheetWithCreatedTime(balanceSheet.getCreatedOn());
            this.launchBalanceSheetFeature(balanceSheet);
        }
    }

    protected abstract void launchBalanceSheetFeature(BalanceSheet balanceSheet);

    private class BalanceSheetMenuItem implements MenuItem {
        private BalanceSheet balanceSheet;

        public BalanceSheetMenuItem(BalanceSheet balanceSheet) {
            this.balanceSheet = balanceSheet;
        }

        @Override
        public String getItemName() {
            return StringUtils.toEpocTimeString(this.balanceSheet.getCreatedOn());
        }

        @Override
        public void launch() {
        }

        public BalanceSheet getBalanceSheet() {
            return balanceSheet;
        }
    }

}
