package com.netcompany.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {

    public static boolean writeStringToFile(String filePath, String content) {
        try (PrintStream out = new PrintStream(
                new FileOutputStream(filePath, false),
                false,
                StandardCharsets.UTF_8.displayName())) {
            out.print(content);
            return true;
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            return false;
        }
    }

    public static String readStringFromFile(String filePath) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(filePath));
            return new String(encoded, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }

}
