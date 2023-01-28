package com.netcompany.menu.newBalanceSheetMenu;

import java.util.List;

import com.netcompany.commonFeature.AbstractBalanceSheetFeature;
import com.netcompany.commonFeature.ShowBalanceSheetDetailFeature;
import com.netcompany.core.AbstractMenu;
import com.netcompany.core.ConsoleContext;
import com.netcompany.core.MenuItem;
import com.netcompany.entity.BalanceSheet;
import com.netcompany.entity.Course;
import com.netcompany.service.CourseService;
import com.netcompany.service.GpaBalancingService;
import com.netcompany.service.impl.CourseServiceImpl;
import com.netcompany.service.impl.GpaBalancingServiceImpl;

public class NewBalanceSheetMenu extends AbstractMenu implements MenuItem {
    private GpaBalancingService gpaBalancingService = new GpaBalancingServiceImpl();
    private CourseService courseService = new CourseServiceImpl();
    private BalanceSheet currentBalanceSheet;
    private UpdateBalancingExpectedGpaFeature updateBalancingExpectedGpaFeature;
    private ShowBalanceSheetDetailFeature showBalanceSheetDetailFeature;

    public NewBalanceSheetMenu(
            ConsoleContext appCtx) {
        super(appCtx);
        this.updateBalancingExpectedGpaFeature = new UpdateBalancingExpectedGpaFeature(appCtx);
        this.showBalanceSheetDetailFeature = new ShowBalanceSheetDetailFeature(appCtx);
        this.menuItems.add(updateBalancingExpectedGpaFeature);
        this.menuItems.add(new AdjustBalanceSheetFeature(appCtx));
        this.menuItems.add(new CleanBalanceSheetAdjustmentFeature(appCtx));
        this.menuItems.add(new ResetBalanceSheetFeature(appCtx));
        this.menuItems.add(new PersistBalanceSheetFeature(appCtx));
    }

    @Override
    public String getItemName() {
        return "Create grade balance sheet";
    }

    @Override
    public void launch() {
        List<Course> courses = this.courseService.getAll();
        this.currentBalanceSheet = this.gpaBalancingService.createNewBalanceSheet(courses, -1);
        this.updateMenuItemContext();
        updateBalancingExpectedGpaFeature.launch();
        Float expectedGpa = this.currentBalanceSheet.getExpectedGpa();
        if (expectedGpa != -1) {
            super.launch();
        }
    }

    private void updateMenuItemContext() {
        this.showBalanceSheetDetailFeature.setCurrentBalanceSheet(currentBalanceSheet);
        this.updateBalancingExpectedGpaFeature.setCurrentBalanceSheet(currentBalanceSheet);
        for (MenuItem menuItem : menuItems) {
            if (menuItem instanceof AbstractBalanceSheetFeature) {
                ((AbstractBalanceSheetFeature) menuItem).setCurrentBalanceSheet(currentBalanceSheet);
            }
        }
    }

    @Override
    public String getBackItemName() {
        return "Back to main menu";
    }

    @Override
    public String getMenuHeader() {
        return "Create balance sheet\n" +
                this.showBalanceSheetDetailFeature.getBalanceSheetDetailString();
    }

}
