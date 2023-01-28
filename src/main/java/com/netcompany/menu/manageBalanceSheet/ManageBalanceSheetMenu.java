package com.netcompany.menu.manageBalanceSheet;

import com.netcompany.core.AbstractMenu;
import com.netcompany.core.ConsoleContext;
import com.netcompany.core.MenuItem;

public class ManageBalanceSheetMenu extends AbstractMenu implements MenuItem {
    public ManageBalanceSheetMenu(ConsoleContext appCtx) {
        super(appCtx);
        this.menuItems.add(new ShowSavedBalanceSheetFeature(appCtx));
        this.menuItems.add(new RemoveSavedBalanceSheetFeature(appCtx));
    }

    @Override
    public String getItemName() {
        return "Manage saved balance sheets";
    }

    @Override
    public String getBackItemName() {
        return "Back to main menu";
    }

    @Override
    public String getMenuHeader() {
        return "Manage saved balance sheets\n" +
                "           -----           \n";
    }
}
