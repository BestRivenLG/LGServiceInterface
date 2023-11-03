package org.example.common;

public enum PatterRegexType {
    USERNAME("^[a-zA-Z][a-zA-Z0-9]{3,15}$"),
    PASSWORD("^(?=.*[A-Za-z])(?=.*\\d|.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{6,16}$");

    private final String regex;

    PatterRegexType(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }
}