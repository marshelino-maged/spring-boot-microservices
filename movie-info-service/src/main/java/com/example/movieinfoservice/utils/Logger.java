package com.example.movieinfoservice.utils;

public class Logger {
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m"; // Reset color

    public static void green(String message) {
        System.out.println(ANSI_GREEN + message + ANSI_RESET);
    }

    public static void red(String message) {
        System.out.println(ANSI_RED + message + ANSI_RESET);
    }

    public static void normal(String message) {
        System.out.println(message);
    }
}

