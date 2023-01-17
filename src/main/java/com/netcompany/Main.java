package com.netcompany;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Pattern;

public class Main {

  static String[] courses = new String[100];
  static Float[] grades = new Float[100];
  static Float[] balancingGrades = new Float[100];
  static String[] savedBalanceSheets = new String[100];
  static String[] savedBalanceSheetFiles = new String[100];
  static int noSaved = 0;
  static Float expectedGpa = null;
  static int noCourses = 0;

  static final String COURSES_FILENAME = "courses.txt";
  static final String SAVED_BALANCE_SHEETS_FILENAME = "balanceSheets.txt";

  static final String[] MAIN_MENU_OPTIONS = new String[] {
      "Manage courses",
      "Manage grades",
      "Create grade balance sheet",
      "Manage saved balance sheets",
  };

  static final String[] MANAGE_COURSES_OPTIONS = new String[] {
      "Add courses",
      "Remove a course",
      "Show all courses",
  };

  static final String[] MANAGE_GRADES_OPTIONS = new String[] {
      "Update grade for a course",
      "Clean grade for a course",
      "Show all courses",
  };

  static final String[] CREATE_BALANCE_SHEET_OPTIONS = new String[] {
      "Update expected GPA",
      "Adjust grade for a course",
      "Clean adjustment for a course",
      "Clean all adjustment",
      "Save this balance sheet",
  };

  static final String[] MANAGE_SAVED_BALANCE_SHEET_OPTIONS = new String[] {
      "Show a saved sheet",
      "Delete a saved sheet",
  };
  static final Pattern decimalPattern = Pattern.compile("^\\d+(.\\d+)?$");
  static final Pattern numericPattern = Pattern.compile("^\\d+$");
  static final Pattern courseCodePattern = Pattern.compile("^[a-z0-9]{3,6}$");
  private static final float MAX_GRADE_VALUE = 10f;

  static boolean checkUserChoise(String userChoise, int optionsLength) {
    if (!numericPattern.matcher(userChoise).find()) {
      System.out.println("Invalid choise: the choise must be integer and follow instruction above.");
      return false;
    }
    int userChoiseValue = Integer.parseInt(userChoise);
    if (userChoiseValue < 0 || userChoiseValue > optionsLength) {
      System.out.println(
          String.format("Invalid choise: the choise must between 0 and %d", optionsLength));
      return false;
    }
    return true;
  }

  static boolean writeToFile(String filePath, String content) {
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

  static String readFromFile(String filePath) {
    try {
      byte[] encoded = Files.readAllBytes(Paths.get(filePath));
      return new String(encoded, StandardCharsets.UTF_8);
    } catch (IOException e) {
      return "";
    }
  }

  static int readSavedBalanceSheet(
      String[] courses,
      Float[] grades,
      Float[] balancingGrades,
      Float[] expectedGpa,
      String fileName) {
    int noCourses = 0;
    float expectedGpaFloat = 0f;
    String courseFileContent = readFromFile(fileName);
    if (courseFileContent.isEmpty()) {
      return 0;
    }
    String[] courseFileContentLines = courseFileContent.split("\n");
    String metaLine = courseFileContentLines[0];
    String[] metaContent = metaLine.split(" ");
    noCourses = Integer.parseInt(metaContent[0]);
    expectedGpaFloat = Float.parseFloat(metaContent[1]);
    expectedGpa[0] = expectedGpaFloat;
    if (noCourses != courseFileContentLines.length - 1) {
      return 0;
    }
    for (int i = 0; i < noCourses; i++) {
      String line = courseFileContentLines[i + 1];
      String[] lineContent = line.split(" ");
      if (lineContent.length == 3) {
        courses[i] = lineContent[0];
        if (decimalPattern.matcher(lineContent[1]).find()) {
          grades[i] = Float.parseFloat(lineContent[1]);
        }
        if (decimalPattern.matcher(lineContent[2]).find()) {
          balancingGrades[i] = Float.parseFloat(lineContent[2]);
        }
      }
    }
    return noCourses;
  }

  static String writeCurrentBalanceSheet(
      String[] courses,
      Float[] grades,
      Float[] balancingGrades,
      int noCourses,
      float expectedGpa,
      String fileName) {
    if (fileName == null) {
      fileName = UUID.randomUUID().toString() + ".bsh";
    }
    StringBuilder sb = new StringBuilder();
    sb.append(noCourses);
    sb.append(' ');
    sb.append(expectedGpa);
    sb.append('\n');
    for (int i = 0; i < noCourses; i++) {
      sb.append(courses[i]);
      sb.append(' ');
      sb.append(grades[i]);
      sb.append(' ');
      sb.append(balancingGrades[i]);
      sb.append('\n');
    }
    writeToFile(fileName, sb.toString());
    return fileName;
  }

  static void writeSavedBalanceSheets(
      String[] savedTimes, String[] savedFilenames, int noSaves) {

    StringBuilder sb = new StringBuilder();
    sb.append(noSaves);
    sb.append('\n');
    for (int i = 0; i < noSaves; i++) {
      sb.append(savedTimes[i]);
      sb.append('$');
      sb.append(savedFilenames[i]);
      sb.append('\n');
    }
    writeToFile(SAVED_BALANCE_SHEETS_FILENAME, sb.toString());
  }

  static int readSavedBalanceSheets(
      String[] savedTimes, String[] savedFilenames) {
    int noSaves = 0;
    String courseFileContent = readFromFile(SAVED_BALANCE_SHEETS_FILENAME);
    for (int i = 0; i < grades.length; i++) {
      savedTimes[i] = null;
      savedFilenames[i] = null;
    }
    if (courseFileContent.isEmpty()) {
      return 0;
    }
    String[] courseFileContentLines = courseFileContent.split("\n");
    noSaves = Integer.parseInt(courseFileContentLines[0]);
    if (noSaves != courseFileContentLines.length - 1) {
      return 0;
    }
    for (int i = 0; i < noSaves; i++) {
      String line = courseFileContentLines[i + 1];
      String[] lineContent = line.split("\\$");
      if (lineContent.length == 2) {
        savedTimes[i] = lineContent[0];
        savedFilenames[i] = lineContent[1];
      }
    }
    return noSaves;
  }

  static void writeCourses(
      String[] courses, Float[] grades, int noCourses) {
    StringBuilder sb = new StringBuilder();
    sb.append(noCourses);
    sb.append('\n');
    for (int i = 0; i < noCourses; i++) {
      sb.append(courses[i]);
      sb.append(' ');
      sb.append(grades[i]);
      sb.append('\n');
    }
    writeToFile(COURSES_FILENAME, sb.toString());
  }

  static int readCourses(
      String[] courses, Float[] grades) {
    int noCourses = 0;
    String courseFileContent = readFromFile(COURSES_FILENAME);
    for (int i = 0; i < grades.length; i++) {
      courses[i] = null;
      grades[i] = null;
    }
    if (courseFileContent.isEmpty()) {
      return 0;
    }
    String[] courseFileContentLines = courseFileContent.split("\n");
    noCourses = Integer.parseInt(courseFileContentLines[0]);
    if (noCourses != courseFileContentLines.length - 1) {
      return 0;
    }
    for (int i = 0; i < noCourses; i++) {
      String line = courseFileContentLines[i + 1];
      String[] lineContent = line.split(" ");
      courses[i] = lineContent[0];
      if (decimalPattern.matcher(lineContent[1]).find()) {
        grades[i] = Float.parseFloat(lineContent[1]);
      }
    }
    return noCourses;
  }

  static String padRight(String s, int n) {
    return String.format("%-" + n + "s", s);
  }

  static String padLeft(int value, int n) {
    return padLeft(String.valueOf(value), n);
  }

  static String padLeft(String s, int n) {
    return String.format("%" + n + "s", s);
  }

  static int printMenu(Scanner scanner, String[] options, boolean isMainMenu) {
    return printMenu(scanner, options, options.length, isMainMenu);
  }

  static int printMenu(Scanner scanner, String[] options, int length, boolean isMainMenu) {
    String userChoise = null;
    for (int i = 0; i < length; i++) {
      System.out.println(String.format("%d. %s", i + 1, options[i]));
    }
    if (isMainMenu) {
      System.out.println("0. Exit");
    } else {
      System.out.println("0. Back");
    }
    do {
      System.out.print("Enter your choise: ");
      userChoise = scanner.nextLine();
    } while (!checkUserChoise(userChoise, options.length));
    return Integer.parseInt(userChoise);
  }

  static void cleanConsole() {
    try {
      if (System.getProperty("os.name").contains("Windows")) {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
      } else {
        System.out.print("\033\143");
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  static int indexOfCourseCode(String courseCode, int fromIndex) {
    for (int i = fromIndex; i < noCourses; i++) {
      if (courseCode.equals(courses[i])) {
        return i;
      }
    }
    return -1;
  }

  static boolean checkValidCourseCode(String courseCode) {
    if (!courseCodePattern.matcher(courseCode).find()) {
      System.out.println(
          String.format("Course \"%s\" is invaid: the course code must contains 3-6 alphanumberic characters",
              courseCode));
      return false;
    }
    int indexOfExisted = indexOfCourseCode(courseCode, 0);

    if (indexOfExisted != -1) {
      System.out.println(
          String.format("Course \"%s\" was existed", courseCode));
      return false;
    }
    return true;
  }

  private static boolean isCourseListAvailable() {
    return noCourses < courses.length;
  }

  static void addNewCourses(Scanner scanner) {
    cleanConsole();
    System.out.println("Add new courses");
    System.out.println("     ---       ");
    System.out.print("Enter your course codes (delimeter by ,): ");
    String courseString = scanner.nextLine();
    String[] courseCodes = courseString.split("[\\, ]+");
    System.out.println(courseString);
    int noAdded = 0;
    int noInvalid = 0;
    for (String courseCode : courseCodes) {
      if (!isCourseListAvailable()) {
        System.out.println("The course list is full now!");
        noInvalid = courseCodes.length - noAdded;
        break;
      }
      if (checkValidCourseCode(courseCode)) {
        courses[noCourses] = courseCode.toLowerCase();
        noCourses++;
        noAdded++;
      } else {
        noInvalid++;
      }
    }
    System.out.print(
        String.format("Added %d course(s)", noAdded));
    if (noInvalid > 0) {
      System.out.println(
          String.format(", %d course(s) was invalid", noInvalid));
    } else {
      System.out.println("");
    }
  }

  private static void removeCourses(Scanner scanner) {
    cleanConsole();
    System.out.println("Remove courses");
    System.out.println("     ---      ");
    System.out.print("Enter course codes (delimeter by ,): ");
    String courseString = scanner.nextLine();
    String[] courseCodes = courseString.split("[, ]+");
    int noRemoved = 0;
    int noInvalid = 0;
    for (String courseCode : courseCodes) {
      courseCode = courseCode.toLowerCase();
      int result = removeCourse(courseCode);
      if (result == 0) {
        noInvalid++;
      } else {
        noRemoved += result;
      }
    }
    System.out.print(
        String.format("Removed %d course(s)", noRemoved));
    if (noInvalid > 0) {
      System.out.println(
          String.format(", %d course(s) was invalid", noRemoved, noInvalid));
    } else {
      System.out.println("");
    }
  }

  private static int removeCourse(String courseCode) {
    int numberOfRemoved = 0;
    for (int i = 0; i < noCourses;) {
      if (courseCode.equals(courses[i])) {
        numberOfRemoved++;
      } else {
        i++;
      }
      if (numberOfRemoved > 0) {
        if (i == (noCourses - numberOfRemoved)) {
          courses[i] = null;
          grades[i] = null;
        } else {
          courses[i] = courses[i + numberOfRemoved];
          grades[i] = grades[i + numberOfRemoved];
        }
      }
    }
    if (numberOfRemoved > 0) {
      noCourses -= numberOfRemoved;
      return numberOfRemoved;
    } else {
      System.out.println(
          String.format("Course code \"%s\" is not existed", courseCode));
      return 0;
    }
  }

  static void showBalanceSheet(Float[] balanceSheetGrades, Float[] grades, int noCourses, Float needAvgGrade) {
    System.out.println("Balance sheet detail");
    System.out.println("         ---        ");
    if (noCourses == 0) {
      System.out.println("No course was inputed!");
      return;
    }
    for (int i = 0; i < noCourses; i++) {
      if (balanceSheetGrades[i] == null) {
        if (needAvgGrade < 0) {
          System.out.println(
              String.format("- %s | 0 (*)", padLeft(courses[i], 7), needAvgGrade));
        } else if (needAvgGrade >= MAX_GRADE_VALUE) {
          System.out.println(
              String.format("- %s | Beyond reality (*)", padLeft(courses[i], 7), needAvgGrade));
        } else {
          System.out.println(
              String.format("- %s | %.2f (*)", padLeft(courses[i], 7), needAvgGrade));
        }
      } else if (balanceSheetGrades[i] != grades[i]) {
        System.out.println(
            String.format("- %s | %.2f (**)", padLeft(courses[i], 7), balanceSheetGrades[i]));
      } else {
        System.out.println(
            String.format("- %s | %.2f", padLeft(courses[i], 7), balanceSheetGrades[i]));
      }
    }
    System.out.println("(*) expected grade");
    System.out.println("(**) adjusted grade");
  }

  static void showCourses(Scanner scanner) {
    cleanConsole();
    System.out.println("List of courses");
    System.out.println("     ---       ");
    if (noCourses == 0) {
      System.out.println("No course was inputed!");
      return;
    }
    for (int i = 0; i < noCourses; i++) {
      System.out.println(
          String.format("- %s | %s", padLeft(courses[i], 7), getCourseGrade(i)));
    }
  }

  static String getCourseGrade(int i) {
    if (grades[i] == null) {
      return "not graded";
    } else {
      DecimalFormat df = new DecimalFormat("#.##");
      return df.format(grades[i]);
    }
  }

  static void updateCourseGrade(Scanner scanner) {
    updateCourseGrade(scanner, grades);
  }

  static void updateCourseGrade(Scanner scanner, Float[] gradeList) {
    cleanConsole();
    System.out.println("Update grade for a course");
    System.out.println("     ---       ");

    System.out.print("Enter course code: ");
    String courseCode = scanner.nextLine();
    int indexOfExisted = indexOfCourseCode(courseCode.toLowerCase(), 0);
    if (indexOfExisted == -1) {
      System.out.println(
          String.format("Course code \"%s\" is not existed", courseCode));
      return;
    }
    System.out.print("Enter course grade: ");
    String courseGradeString = scanner.nextLine();
    if (!decimalPattern.matcher(courseGradeString).find()) {
      System.out.println("Invalid: Course grade must be decimal format");
      return;
    }
    float gradeValue = Float.parseFloat(courseGradeString);

    if (gradeValue < 0 || gradeValue > MAX_GRADE_VALUE) {
      System.out.println(String.format("Invalid: Course grade must between 0 and %.2f", MAX_GRADE_VALUE));
      return;
    }
    gradeList[indexOfExisted] = gradeValue;
    System.out.println(
        String.format("Update grade successfully for course \"%s\".", courseCode));
  }

  static void cleanCourseGrade(Scanner scanner) {
    cleanCourseGrade(scanner, grades);
  }

  static void cleanCourseGrade(Scanner scanner, Float[] gradeList) {
    cleanConsole();
    System.out.println("Clean grade for a course");
    System.out.println("     ---       ");

    System.out.print("Enter course code: ");
    String courseCode = scanner.nextLine();
    int indexOfExisted = indexOfCourseCode(courseCode.toLowerCase(), 0);
    if (indexOfExisted == -1) {
      System.out.println(
          String.format("Course code \"%s\" is not existed", courseCode));
      return;
    }
    gradeList[indexOfExisted] = null;
    System.out.println(
        String.format("Clean grade successfully for course \"%s\".", courseCode));
  }

  private static String saveCurrentBalanceSheet(String currentBalanceSheetFileName) {
    return writeCurrentBalanceSheet(
        courses,
        grades,
        balancingGrades,
        noCourses,
        expectedGpa,
        currentBalanceSheetFileName);
  }

  private static float calculateGpa(Float[] courseList, int numberOfCourse, Float defaultGrade) {
    float totalGrade = calculateTotalGrade(courseList, numberOfCourse, defaultGrade);
    int countedCourses = numberOfCourse - countNonGradedCourse(courseList, numberOfCourse);
    return totalGrade / countedCourses;
  }

  private static Float calculateNeededGrade(Float[] gradeList, int numberOfCourse, float expectedGpa) {
    float totalGrade = calculateTotalGrade(gradeList, numberOfCourse, null);
    int noNonGradedCourses = countNonGradedCourse(gradeList, numberOfCourse);
    if (noNonGradedCourses == 0) {
      return null;
    }
    return (expectedGpa * numberOfCourse - totalGrade) / noNonGradedCourses;
  }

  private static float calculateTotalGrade(Float[] gradeList, int numberOfCourse, Float defaultGrade) {
    float totalGrade = 0;
    for (int i = 0; i < numberOfCourse; i++) {
      if (gradeList[i] != null) {
        totalGrade += gradeList[i];
      } else if (defaultGrade != null) {
        totalGrade += defaultGrade;
      }
    }
    return totalGrade;
  }

  private static int countNonGradedCourse(Float[] gradeList, int numberOfCourse) {
    int countedCourses = 0;
    for (int i = 0; i < numberOfCourse; i++) {
      if (gradeList[i] == null) {
        countedCourses++;
      }
    }
    return countedCourses;
  }

  private static void copyCurrentGrades() {
    for (int i = 0; i < noCourses; i++) {
      balancingGrades[i] = grades[i];
    }
  }

  static void runManageCourses(Scanner scanner) {
    int userChoise = 0;
    do {
      cleanConsole();
      System.out.println("Manage courses");
      System.out.println("     ---      ");
      userChoise = printMenu(scanner, MANAGE_COURSES_OPTIONS, false);
      switch (userChoise) {
        case 1:
          addNewCourses(scanner);
          break;
        case 2:
          removeCourses(scanner);
          break;
        case 3:
          showCourses(scanner);
          break;
        default:
          break;
      }
      if (userChoise != 0) {
        if (userChoise != 3) {
          writeCourses(courses, grades, noCourses);
        }
        System.out.println("Press Enter to continues");
        scanner.nextLine();
      }
    } while (userChoise != 0);
  }

  private static void runManageGrades(Scanner scanner) {
    int userChoise = 0;
    do {
      cleanConsole();
      System.out.println("Manage grades");
      System.out.println("     ---      ");
      userChoise = printMenu(scanner, MANAGE_GRADES_OPTIONS, false);
      switch (userChoise) {
        case 1:
          updateCourseGrade(scanner);
          break;
        case 2:
          cleanCourseGrade(scanner);
          break;
        case 3:
          showCourses(scanner);
          break;
        default:
          break;
      }
      if (userChoise != 0) {
        if (userChoise != 3) {
          writeCourses(courses, grades, noCourses);
        }
        System.out.println("Press Enter to continues");
        scanner.nextLine();
      }
    } while (userChoise != 0);
  }

  static Float inputExpectedGPA(Scanner scanner) {
    Float gpaValue = null;
    do {
      System.out.print("Enter your expected GPA (input empty to cancel): ");
      String courseGradeString = scanner.nextLine();
      if (courseGradeString.isEmpty()) {
        return null;
      }
      if (!decimalPattern.matcher(courseGradeString).find()) {
        System.out.println("Invalid: Course grade must be decimal format");
      }
      gpaValue = Float.parseFloat(courseGradeString);
      if (gpaValue < 0 || gpaValue > MAX_GRADE_VALUE) {
        System.out.println(String.format("Invalid: Course grade must between 0 and %.2f", MAX_GRADE_VALUE));
      }
    } while (gpaValue == null);
    return gpaValue;
  }

  private static void runCreateBalanceSheet(Scanner scanner) {
    int userChoise = 0;
    Float gpaValue = inputExpectedGPA(scanner);
    if (gpaValue == null) {
      return;
    }
    expectedGpa = gpaValue;
    copyCurrentGrades();
    float currentGpa = calculateGpa(grades, noCourses, null);
    String currentBalanceSheetFileName = null;
    int thisSheetIndex = noSaved;
    do {
      cleanConsole();
      Float needAvgGrade = calculateNeededGrade(balancingGrades, noCourses, expectedGpa);
      System.out.println("  Create balance sheet ");
      System.out.println(
          String.format("  -Current GPA: %.2f- ", currentGpa));
      System.out.println(
          String.format("  -Expected GPA: %.2f- ", expectedGpa));
      showBalanceSheet(balancingGrades, grades, noCourses, needAvgGrade);
      userChoise = printMenu(scanner, CREATE_BALANCE_SHEET_OPTIONS, false);
      switch (userChoise) {
        case 1:
          gpaValue = inputExpectedGPA(scanner);
          if (gpaValue != null) {
            expectedGpa = gpaValue;
          }
          break;
        case 2:
          updateCourseGrade(scanner, balancingGrades);
          break;
        case 3:
          cleanCourseGrade(scanner, balancingGrades);
          break;
        case 4:
          copyCurrentGrades();
          System.out.println("Balance sheet has been cleaned up");
          break;
        case 5:
          String newFileName = saveCurrentBalanceSheet(currentBalanceSheetFileName);
          if (currentBalanceSheetFileName == null) {
            currentBalanceSheetFileName = newFileName;
            noSaved++;
            savedBalanceSheetFiles[thisSheetIndex] = newFileName;
            savedBalanceSheets[thisSheetIndex] = Calendar.getInstance().getTime().toString();
            writeSavedBalanceSheets(savedBalanceSheets, savedBalanceSheetFiles, noSaved);
          }
          System.out.println("Balance sheet has been saved");
          break;
        default:
          break;
      }
      if (userChoise != 0) {
        System.out.println("Press Enter to continues");
        scanner.nextLine();
      }
    } while (userChoise != 0);
  }

  private static void runManageSavedBalanceSheets(Scanner scanner) {
    int userChoise = 0;
    do {
      cleanConsole();
      System.out.println("Manage saved balance sheets");
      System.out.println("           -----           ");
      userChoise = printMenu(scanner, MANAGE_SAVED_BALANCE_SHEET_OPTIONS, false);
      switch (userChoise) {
        case 1:
          showSavedBalanceSheet(scanner);
          break;
        case 2:
          removeSavedBalanceSheet(scanner);
          break;
        default:
          break;
      }
      if (userChoise != 0) {
        System.out.println("Press Enter to continues");
        scanner.nextLine();
      }
    } while (userChoise != 0);
  }

  private static void showSavedBalanceSheet(Scanner scanner) {
    cleanConsole();
    System.out.println("List of saved balance sheets");
    System.out.println("            ----            ");
    int userChoise = 0;
    userChoise = printMenu(scanner, savedBalanceSheets, noSaved, false);
    if (userChoise > 0) {
      cleanConsole();
      String[] savedCourses = new String[100];
      Float[] savedGrades = new Float[100];
      Float[] savedBalanceGrades = new Float[100];
      Float[] expectedGpaArray = new Float[1];
      int savedNoCourses = readSavedBalanceSheet(
          savedCourses,
          savedGrades,
          savedBalanceGrades,
          expectedGpaArray,
          savedBalanceSheetFiles[userChoise - 1]);
      Float neededGrade = calculateNeededGrade(savedGrades, savedNoCourses, expectedGpaArray[0]);
      showBalanceSheet(savedBalanceGrades, savedGrades, savedNoCourses, neededGrade);
    }
  }

  private static void removeSavedBalanceSheet(Scanner scanner) {
    cleanConsole();
    System.out.println("List of saved balance sheets");
    System.out.println("            ----            ");
    int userChoise = 0;
    userChoise = printMenu(scanner, MANAGE_SAVED_BALANCE_SHEET_OPTIONS, false);
    if (userChoise > 0) {
      String removingSaved = savedBalanceSheetFiles[userChoise];
      for (int i = userChoise - 1; i < noSaved; i++) {
        if (userChoise == noSaved - 1) {
          savedBalanceSheetFiles[i] = null;
          savedBalanceSheets[i] = null;
        } else {
          savedBalanceSheetFiles[i] = savedBalanceSheetFiles[i - 1];
          savedBalanceSheets[i] = savedBalanceSheets[i - 1];
        }
      }
      noSaved--;
      writeSavedBalanceSheets(savedBalanceSheets, savedBalanceSheetFiles, noSaved);
      System.out.println(
          String.format("Balance sheet \"%s\" has been removed", removingSaved));
    }
  }

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    int userChoise = 0;
    noCourses = readCourses(courses, grades);
    noSaved = readSavedBalanceSheets(savedBalanceSheets, savedBalanceSheetFiles);
    cleanConsole();
    do {
      System.out.println("GPA Self-tracking application");
      System.out.println("          - Powered by Dazzle\n");
      userChoise = printMenu(scanner, MAIN_MENU_OPTIONS, true);
      switch (userChoise) {
        case 1:
          runManageCourses(scanner);
          break;
        case 2:
          runManageGrades(scanner);
          break;
        case 3:
          runCreateBalanceSheet(scanner);
          break;
        case 4:
          runManageSavedBalanceSheets(scanner);
          break;
        default:
          break;
      }
      if (userChoise != 0) {
        cleanConsole();
      }
    } while (userChoise != 0);
    System.out.println("Bye!");
    scanner.close();
  }
}