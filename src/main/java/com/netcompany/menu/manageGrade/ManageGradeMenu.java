package com.netcompany.menu.manageGrade;

import com.netcompany.commonFeature.ShowCourseFeature;
import com.netcompany.core.AbstractMenu;
import com.netcompany.core.ConsoleContext;
import com.netcompany.core.MenuItem;

public class ManageGradeMenu extends AbstractMenu implements MenuItem {
    public ManageGradeMenu(ConsoleContext appCtx) {
        super(appCtx);
        this.menuItems.add(new UpdateGradeFeature(appCtx));
        this.menuItems.add(new CleanGradeFeature(appCtx));
        this.menuItems.add(new ShowCourseFeature());
    }

    @Override
    public String getItemName() {
        return "Manage grades";
    }

    @Override
    public String getBackItemName() {
        return "Back to main menu";
    }

    @Override
    public String getMenuHeader() {
        return "Manage grades\n" +
                "     ---      \n";
    }
}
