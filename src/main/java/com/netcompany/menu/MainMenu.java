package com.netcompany.menu;

import com.netcompany.core.AbstractMenu;
import com.netcompany.core.ConsoleContext;
import com.netcompany.menu.manageBalanceSheet.ManageBalanceSheetMenu;
import com.netcompany.menu.manageCourse.ManageCourseMenu;
import com.netcompany.menu.manageGrade.ManageGradeMenu;
import com.netcompany.menu.newBalanceSheetMenu.NewBalanceSheetMenu;

public class MainMenu extends AbstractMenu {
    public MainMenu(ConsoleContext appCtx) {
        super(appCtx);
        this.menuItems.add(new ManageCourseMenu(appCtx));
        this.menuItems.add(new ManageGradeMenu(appCtx));
        this.menuItems.add(new NewBalanceSheetMenu(appCtx));
        this.menuItems.add(new ManageBalanceSheetMenu(appCtx));
    }

    @Override
    public String getBackItemName() {
        return "Exit";
    }

    @Override
    public String getMenuHeader() {
        return "GPA Self-tracking application\n" +
                "          - Powered by Dazzle\n";
    }
}
