package com.netcompany.menu.manageCourse;

import com.netcompany.commonFeature.ShowCourseFeature;
import com.netcompany.core.AbstractMenu;
import com.netcompany.core.ConsoleContext;
import com.netcompany.core.MenuItem;

public class ManageCourseMenu extends AbstractMenu implements MenuItem {
    public ManageCourseMenu(ConsoleContext appCtx) {
        super(appCtx);
        this.menuItems.add(new AddCourseFeature(appCtx));
        this.menuItems.add(new RemoveCourseFeature(appCtx));
        this.menuItems.add(new ShowCourseFeature());
    }

    @Override
    public String getItemName() {
        return "Manage courses";
    }

    @Override
    public String getBackItemName() {
        return "Back to main menu";
    }

    @Override
    public String getMenuHeader() {
        return "Manage courses\n" +
                "     ---      \n";
    }
}
