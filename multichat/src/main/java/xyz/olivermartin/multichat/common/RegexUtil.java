package xyz.olivermartin.multichat.common;

import java.util.regex.Pattern;

public class RegexUtil {
    public static final Pattern COLOR_LEGACY = Pattern.compile("(?i)[a-f0-9]");
    public static final Pattern FORMAT = Pattern.compile("(?i)[k-or]");
    public static final Pattern COLOR_LEGACY_FORMAT = Pattern.compile("(?i)[a-fk-or0-9]");
}
