package com.netcompany.utils;

import java.time.Instant;
import java.util.regex.Pattern;

public class StringUtils {
    static final Pattern decimalPattern = Pattern.compile("^\\d+(.\\d+)?$");

    public static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    public static String padLeft(int value, int n) {
        return padLeft(String.valueOf(value), n);
    }

    public static String padLeft(String s, int n) {
        return String.format("%" + n + "s", s);
    }

    public static boolean isMatchedDecimalPattern(String decimalValue) {
        return decimalPattern.matcher(decimalValue).find();
    }

    public static String toEpocTimeString(long epochMilli) {
        return Instant.ofEpochMilli(epochMilli).toString();
    }
}
