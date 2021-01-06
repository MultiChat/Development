package xyz.olivermartin.multichat.common;

import java.util.regex.Pattern;

public enum RegexUtil {
    LEGACY_COLOR("\b(?i)[a-f0-9]\b"),
    LEGACY_COLORS("(?i)[a-f0-9]"),
    FORMAT("\b(?i)[k-or]\b"),
    FORMATS("(?i)[k-or]"),
    LEGACY_COLOR_FORMAT("\b(?i)[a-fk-or0-9]\b"),
    LEGACY_COLORS_FORMATS("(?i)[a-fk-or0-9]");

    private final Pattern pattern;

    RegexUtil(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    public boolean matches(String toMatch) {
        return pattern.matcher(toMatch).matches();
    }
}
