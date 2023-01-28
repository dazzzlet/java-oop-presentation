package com.netcompany.fileHandler;

import com.netcompany.core.Entity;
import com.netcompany.core.FileHandler;

public class ListCourseFileHandler implements FileHandler {
    private static final String COURSES_FILENAME = "courses.txt";

    @Override
    public String getStorageFileName() {
        return COURSES_FILENAME;
    }

    @Override
    public Entity loadData() {
        return this.loadData(this.getStorageFileName());
    }

    @Override
    public Entity loadData(String fileName) {
        return null;
    }

    @Override
    public void saveData(Entity entity) {
        this.saveData(entity, this.getStorageFileName());
    }

    @Override
    public void saveData(Entity entity, String fileName) {

    }

}
