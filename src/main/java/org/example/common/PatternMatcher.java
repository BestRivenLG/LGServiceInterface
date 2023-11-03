package org.example.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternMatcher {
    public static Boolean textInputPass(String input, PatterRegexType type) {
        String regex = type.getRegex();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        Boolean result = matcher.matches();
        return result;
    }
}
