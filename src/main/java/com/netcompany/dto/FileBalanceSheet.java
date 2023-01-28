package com.netcompany.dto;

import java.io.File;
import java.util.ArrayList;

import com.netcompany.entity.BalanceSheet;

public class FileBalanceSheet extends BalanceSheet {
    public FileBalanceSheet() {
        this.fileName = null;
        this.isLoaded = false;
        this.setCourses(new ArrayList<>());
    }

    private String fileName;
    private boolean isLoaded;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    @Override
    public void copy(BalanceSheet balanceSheet) {
        if (balanceSheet instanceof FileBalanceSheet) {
            FileBalanceSheet fileBalanceSheet = (FileBalanceSheet) balanceSheet;
            this.fileName = fileBalanceSheet.getFileName();
            this.isLoaded = fileBalanceSheet.isLoaded();
        }
        super.copy(balanceSheet);
    }

    @Override
    public BalanceSheet clone() {
        FileBalanceSheet newBalanceSheet = new FileBalanceSheet();
        newBalanceSheet.copy(this);
        return newBalanceSheet;
    }
}
