package com.netcompany.core;

import java.util.Scanner;

public class ConsoleContext {
    private Scanner scanner;

    public ConsoleContext(Scanner scanner) {
        this.scanner = scanner;
    }

    public Scanner getScanner() {
        return scanner;
    }

}
