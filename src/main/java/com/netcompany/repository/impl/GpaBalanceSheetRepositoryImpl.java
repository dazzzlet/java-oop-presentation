package com.netcompany.repository.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.netcompany.dto.FileBalanceSheet;
import com.netcompany.entity.BalanceSheet;
import com.netcompany.entity.BalancingCourse;
import com.netcompany.repository.GpaBalanceSheetRepository;
import com.netcompany.utils.FileUtils;
import com.netcompany.utils.StringUtils;

public class GpaBalanceSheetRepositoryImpl implements GpaBalanceSheetRepository {
    public static final String SAVED_BALANCE_SHEETS_FILENAME = "balanceSheets.txt";
    private static List<FileBalanceSheet> cachedSheets = null;

    private static void loadCache() {
        if (cachedSheets == null) {
            cachedSheets = readSavedBalanceSheets();
        }
    }

    private static FileBalanceSheet readSavedBalanceSheet(FileBalanceSheet fileBalanceSheet) {
        int noCourses = 0;
        float expectedGpaFloat = 0f;
        String courseFileContent = FileUtils.readStringFromFile(fileBalanceSheet.getFileName());
        if (courseFileContent.isEmpty()) {
            return null;
        }
        fileBalanceSheet.clearCourses();
        String[] courseFileContentLines = courseFileContent.split("\n");
        String metaLine = courseFileContentLines[0];
        String[] metaContent = metaLine.split(" ");
        noCourses = Integer.parseInt(metaContent[0]);
        expectedGpaFloat = Float.parseFloat(metaContent[1]);
        fileBalanceSheet.setExpectedGpa(expectedGpaFloat);
        if (noCourses != courseFileContentLines.length - 1) {
            return fileBalanceSheet;
        }
        List<BalancingCourse> courses = new ArrayList<>();
        for (int i = 0; i < noCourses; i++) {
            String line = courseFileContentLines[i + 1];
            String[] lineContent = line.split(" ");
            if (lineContent.length == 3) {
                String courseCode = lineContent[0];
                Float grade = null;
                Float adjustedGrade = null;
                if (StringUtils.isMatchedDecimalPattern(lineContent[1])) {
                    grade = Float.parseFloat(lineContent[1]);
                }
                if (StringUtils.isMatchedDecimalPattern(lineContent[2])) {
                    adjustedGrade = Float.parseFloat(lineContent[2]);
                }
                BalancingCourse balancingCourse = new BalancingCourse(
                    courseCode, grade, adjustedGrade
                );
                courses.add(balancingCourse);
            }
        }
        fileBalanceSheet.addCourses(courses);
        return fileBalanceSheet;
    }

    private static void writeCurrentBalanceSheet(
            FileBalanceSheet balanceSheet) {
        if (balanceSheet.getFileName() == null) {
            balanceSheet.setFileName(UUID.randomUUID().toString() + ".bsh");
        }
        StringBuilder sb = new StringBuilder();
        List<BalancingCourse> courses = balanceSheet.getCourses();
        sb.append(courses.size());
        sb.append(' ');
        sb.append(balanceSheet.getExpectedGpa());
        sb.append('\n');
        for (BalancingCourse balancingCourse : courses) {
            sb.append(balancingCourse.getCode());
            sb.append(' ');
            sb.append(balancingCourse.getGrade());
            sb.append(' ');
            sb.append(balancingCourse.getAdjustedGrade());
            sb.append('\n');
        }
        FileUtils.writeStringToFile(balanceSheet.getFileName(), sb.toString());
    }

    private static void writeSavedBalanceSheets(List<FileBalanceSheet> balanceSheets) {

        StringBuilder sb = new StringBuilder();
        sb.append(balanceSheets.size());
        sb.append('\n');
        for (FileBalanceSheet fileBalanceSheet : balanceSheets) {
            sb.append(fileBalanceSheet.getCreatedOn());
            sb.append('$');
            sb.append(fileBalanceSheet.getFileName());
            sb.append('\n');
        }
        FileUtils.writeStringToFile(SAVED_BALANCE_SHEETS_FILENAME, sb.toString());
    }

    private static List<FileBalanceSheet> readSavedBalanceSheets() {
        int noSaves = 0;
        String courseFileContent = FileUtils.readStringFromFile(SAVED_BALANCE_SHEETS_FILENAME);
        List<FileBalanceSheet> fileBalanceSheets = new ArrayList<>();
        if (courseFileContent.isEmpty()) {
            return fileBalanceSheets;
        }
        String[] courseFileContentLines = courseFileContent.split("\n");
        noSaves = Integer.parseInt(courseFileContentLines[0]);
        if (noSaves != courseFileContentLines.length - 1) {
            return fileBalanceSheets;
        }
        for (int i = 0; i < noSaves; i++) {
            String line = courseFileContentLines[i + 1];
            String[] lineContent = line.split("\\$");
            if (lineContent.length == 2) {
                FileBalanceSheet fileBalanceSheet = new FileBalanceSheet();
                String createOnString = lineContent[0];
                Long createOnDate = Long.parseLong(createOnString);
                String fileName = lineContent[1];
                fileBalanceSheet.setFileName(fileName);
                fileBalanceSheet.setCreatedOn(createOnDate);
                fileBalanceSheets.add(fileBalanceSheet);
            }
        }
        return fileBalanceSheets;
    }

    private static boolean deleteBalanceSheet(FileBalanceSheet balanceSheet) {
        File balanceSheetFile = new File(balanceSheet.getFileName());
        return balanceSheetFile.delete();
    }

    @Override
    public void add(BalanceSheet balanceSheet) {
        BalanceSheet existingSheet = this._findBalanceSheetWithCreatedTime(balanceSheet.getCreatedOn());
        if (existingSheet == null) {
            FileBalanceSheet fileBalanceSheet = new FileBalanceSheet();
            fileBalanceSheet.copy(balanceSheet);
            writeCurrentBalanceSheet(fileBalanceSheet);
            fileBalanceSheet.setLoaded(true);
            cachedSheets.add(fileBalanceSheet);
            writeSavedBalanceSheets(cachedSheets);
        } else {
            existingSheet.copy(balanceSheet, true);
        }
    }

    @Override
    public void update(BalanceSheet balanceSheet) {
        FileBalanceSheet _balanceSheet = this._findBalanceSheetWithCreatedTime(balanceSheet.getCreatedOn());
        if (_balanceSheet != null) {
            _balanceSheet.copy(balanceSheet, true);
            writeCurrentBalanceSheet(_balanceSheet);
        }
    }

    @Override
    public void delete(BalanceSheet balanceSheet) {
        FileBalanceSheet _balanceSheet = this._findBalanceSheetWithCreatedTime(balanceSheet.getCreatedOn());
        if (_balanceSheet != null) {
            if (deleteBalanceSheet(_balanceSheet)) {
                cachedSheets.remove(_balanceSheet);
                writeSavedBalanceSheets(cachedSheets);
            }
        }
    }

    @Override
    public BalanceSheet findByCreateOn(long createOn) {
        FileBalanceSheet balanceSheet = this._findBalanceSheetWithCreatedTime(createOn);
        if (balanceSheet != null) {
            if (!balanceSheet.isLoaded()) {
                readSavedBalanceSheet(balanceSheet);
            }
            return balanceSheet.clone();
        }
        return null;
    }

    @Override
    public List<BalanceSheet> getAll() {
        loadCache();
        List<BalanceSheet> returnList = new ArrayList<>();
        for (FileBalanceSheet balanceSheet : cachedSheets) {
            returnList.add(balanceSheet.clone());
        }
        return returnList;
    }

    public FileBalanceSheet _findBalanceSheetWithCreatedTime(long createdTime) {
        loadCache();
        for (FileBalanceSheet balanceSheet : cachedSheets) {
            if (balanceSheet.getCreatedOn() == createdTime) {
                return balanceSheet;
            }
        }
        return null;
    }

}
