package com.netcompany.core;

public interface FileHandler {
    String getStorageFileName();

    Entity loadData();

    Entity loadData(String fileName);

    void saveData(Entity entity);

    void saveData(Entity entity, String fileName);
}
