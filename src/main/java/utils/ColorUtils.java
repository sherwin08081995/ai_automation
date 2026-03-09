package utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sherwin
 * @since 10-07-2025
 */

public class ColorUtils {

    private static final Map<String, String> colorMap = new HashMap<>();

    static {
        colorMap.put("rgba(255, 0, 0, 1)", "Red");
        colorMap.put("rgba(0, 128, 0, 1)", "Green");
        colorMap.put("rgba(0, 0, 255, 1)", "Blue");
        colorMap.put("rgba(255, 255, 0, 1)", "Yellow");
        colorMap.put("rgba(255, 165, 0, 1)", "Orange");
        colorMap.put("rgba(128, 0, 128, 1)", "Purple");
        colorMap.put("rgba(255, 163, 163, 1)", "Light Red");
        colorMap.put("rgba(255, 219, 128, 1)", "Peach/Orange");
        colorMap.put("rgba(0, 0, 0, 0)", "Transparent");
        // Add more mappings as needed
    }

    public static String getColorName(String rgba) {
        return colorMap.getOrDefault(rgba, "Unknown Color");
    }

    public static String resolveColorName(String rgb) {
        return switch (rgb.trim()) {
            case "rgb(255, 163, 163)" -> "Light Red / Soft Pink (High)";
            case "rgb(255, 215, 134)" -> "Peach / Light Orange (Medium)";
            case "rgb(147, 235, 238)" -> "Cyan / Aqua Blue (Low)";
            case "rgba(0, 0, 0, 0)" -> "Transparent";
            default -> "Unknown Color";
        };
    }


}
