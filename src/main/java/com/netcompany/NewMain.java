package com.netcompany;

import java.util.Scanner;

import com.netcompany.core.ConsoleContext;
import com.netcompany.menu.MainMenu;

public class NewMain {
    public static void main(String[] args) {
        ConsoleContext appContext = initiateApplicationContext();
        MainMenu application = new MainMenu(appContext);
        application.launch();
    }

    private static ConsoleContext initiateApplicationContext() {
        Scanner scanner = new Scanner(System.in);
        ConsoleContext appContext = new ConsoleContext(scanner);
        return appContext;
    }
}
