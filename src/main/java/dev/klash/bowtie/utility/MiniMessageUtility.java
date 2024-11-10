package dev.klash.bowtie.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MiniMessageUtility {

    public static final List<String> VALID_TAGS = Arrays.asList(
            "black", "dark_blue", "dark_green", "dark_aqua", "dark_red", "dark_purple", "gold", "gray", "dark_gray", "blue",
            "green", "aqua", "red", "light_purple", "yellow", "white", "#",
            "bold", "italic", "underlined", "strikethrough", "obfuscated", "reset", "newline",
            "rainbow", "gradient", "hover", "insertion", "click", "key", "lang", "score", "selector"
    );


    public static String findLatestOpenTag(String input) {
        int lastOpenBracket = input.lastIndexOf('<');
        if (lastOpenBracket == -1) {
            // No '<' found
            return "";
        }

        int nextCloseBracket = input.indexOf('>', lastOpenBracket);
        if (nextCloseBracket != -1) {
            // A '>' is found after the last '<', meaning the tag is complete
            return "";
        }

        // Extract the substring after the last '<' till the end
        String potentialTag = input.substring(lastOpenBracket + 1);

        // Return the potential tag if it is not empty
        return potentialTag;
    }

    public static List<String> suggestTags(String input) {
        List<String> suggestions = new ArrayList<>();

        int lastOpenBracket = input.lastIndexOf('<');
        if (lastOpenBracket == -1 || lastOpenBracket == input.length() - 1) {
            // No '<' found or it is the last character
            return suggestions;
        }

        String potentialTag = input.substring(lastOpenBracket + 1);
        if (potentialTag.contains(">")) {
            // Already a complete tag
            return suggestions;
        }

        for (String tag : VALID_TAGS) {
            if (tag.startsWith(potentialTag)) {
                suggestions.add(tag);
            }
        }

        return suggestions;
    }

    public static List<String> getSuggestionsForFullString(String input) {
        // Find the latest open tag
        String latestOpenTag = findLatestOpenTag(input);

        // If there's an incomplete tag, get suggestions
        if (!latestOpenTag.isEmpty()) {
            return suggestTags(input);
        }

        // If the tag is complete or absent, return an empty list
        return new ArrayList<>();
    }

}
