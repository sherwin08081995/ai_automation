package utils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Utility class for generating randomized test data for business-related forms.
 * <p>
 * This class provides methods to generate random values for:
 * <ul>
 *     <li>Business types</li>
 *     <li>Company names</li>
 *     <li>Locations (Indian states and UTs)</li>
 *     <li>Employee counts</li>
 *     <li>Industry types</li>
 *     <li>Annual turnover ranges</li>
 * </ul>
 * Useful for automated UI/API test data population in organization setup flows.
 *
 * @author Sherwin
 * @since 08-07-2025
 */

public class TestDataGenerator {

    private static final Random random = new Random();

    /**
     * Returns a random business type from a predefined list.
     */

    public static String getRandomBusinessType() {
        List<String> types = List.of("Private limited company(PVT)", "Limited liability partnership(LLP)",
                "One person company(OPC)",
                "Sole proprietorship",
                "Partnership firm",
                "Nidhi Company",
                "Section8 Company",
                "Trust Registration",
                "Foriegn Company", "Public limited",
                "Producer Company", "Society Company", "Individual Company");
        return types.get(random.nextInt(types.size()));
    }

    /**
     * Generates a random company name prefixed with "Zolvit" and suffixed with "Company Ltd".
     */

    public static String getRandomCompanyName() {
        String randomWord = generateRandomAlphabeticString(5, 8);
        return "Zolvit " + randomWord + " Company Ltd";
    }


    /**
     * Generates a random alphabetic string with mixed case letters.
     *
     * @param minLength Minimum length of the string
     * @param maxLength Maximum length of the string
     * @return Randomly generated string
     */

    private static String generateRandomAlphabeticString(int minLength, int maxLength) {
        int length = minLength + random.nextInt(maxLength - minLength + 1);
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char c = (char) ('a' + random.nextInt(26));
            builder.append(random.nextBoolean() ? Character.toUpperCase(c) : c);
        }
        return builder.toString();
    }


    /**
     * Returns a random Indian state or union territory.
     */

    public static String getRandomLocation() {
        List<String> locations = List.of("Andaman and Nicobar Islands", "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chandigarh", "Chhattisgarh", "Dadra and Nagar Haveli", "Daman and Diu", "Delhi", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir", "Jharkhand", "Karnataka", "Kerala", "Ladakh", "Lakshadweep", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "West Bengal", "Uttarakhand", "Uttar Pradesh", "Tripura", "Telangana", "Tamil Nadu", "Rajasthan", "Sikkim", "Puducherry", "Odisha", "Punjab", "Nagaland");
        return locations.get(random.nextInt(locations.size()));
    }

    /**
     * Returns a random employee count range.
     */

    public static String getRandomEmployeeCount() {
        List<String> counts = List.of(
                "No employees",
                "1 - 10",
                "11 - 20",
                "21 - 50",
                "51 - 100", "100+");
        return counts.get(random.nextInt(counts.size()));
    }

    /**
     * Returns a random employee count range.
     */

    public static String getRandomEmployeeCountDuringOnboarding() {
        List<String> counts = List.of("No employees", "1 - 10", "11 - 20", "21 - 50", "51 - 100", "100+");
        return counts.get(random.nextInt(counts.size()));
    }

    /**
     * Returns a random industry type from a predefined list.
     */

    public static String getRandomIndustry() {
        List<String> industries = List.of("Agriculture / Dairy Farming", "Accounting", "Advertising Services", "Architecture and Engineering Services", "Auditing Service", "Business and Management Services", "Pharmaceutical", "Clothing / Apparel / Accessories Related - Textiles", "E-Commerce", "Electrical and Electronics");
        return industries.get(random.nextInt(industries.size()));
    }

    /**
     * Returns a random annual turnover range.
     */

    public static String getRandomTurnover() {
        List<String> turnovers = List.of(
                "0 - 10 Lakhs",
                "10 - 20 Lakhs",
                "20 Lakhs - 40 Lakhs",
                "40 Lakhs - 1 CR",
                "1 CR - 2 CR", "2 CR - 5 CR", "5 CR - 10 CR", "10 CR - 20 CR", "20 CR - 100 CR", "100 CR - 500 CR", "500 CR+");
        return turnovers.get(random.nextInt(turnovers.size()));
    }

    /**
     * Returns a random annual turnover range.
     */

    public static String getRandomTurnoverDuringOnboarding() {
        List<String> turnovers = List.of("0 - 10 Lakhs", "10 - 20 Lakhs", "20 Lakhs - 40 Lakhs", "40 Lakhs - 1 CR", "1 CR - 2 CR", "2 CR - 5 CR", "5 CR - 10 CR", "10 CR - 20 CR", "20 CR - 100 CR", "100 CR - 500 CR", "500 CR+");
        return turnovers.get(random.nextInt(turnovers.size()));
    }


    /**
     * Returns a random month name excluding the specified one.
     *
     * @param excludeMonth The month to exclude (e.g., "July")
     * @return A different random month
     */
    public static String getRandomMonthExcluding(String excludeMonth) {
        String[] allMonths = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

        List<String> availableMonths = new ArrayList<String>();
        for (int i = 0; i < allMonths.length; i++) {
            if (!allMonths[i].equalsIgnoreCase(excludeMonth)) {
                availableMonths.add(allMonths[i]);
            }
        }

        return availableMonths.get(random.nextInt(availableMonths.size()));
    }

    /**
     * Returns a random year excluding the specified one.
     *
     * @param excludeYear The year to exclude (e.g., "2025")
     * @return A different random year
     */
    public static String getRandomYearExcluding(String excludeYear) {
        String[] allYears = {"2024", "2025", "2026"};

        List<String> availableYears = new ArrayList<String>();
        for (int i = 0; i < allYears.length; i++) {
            if (!allYears[i].equals(excludeYear)) {
                availableYears.add(allYears[i]);
            }
        }

        return availableYears.get(random.nextInt(availableYears.size()));
    }


    public static String getMonthAbbreviation(String fullMonth) {
        Map<String, String> map = Map.ofEntries(Map.entry("January", "Jan"), Map.entry("February", "Feb"), Map.entry("March", "Mar"), Map.entry("April", "Apr"), Map.entry("May", "May"), Map.entry("June", "Jun"), Map.entry("July", "Jul"), Map.entry("August", "Aug"), Map.entry("September", "Sep"), Map.entry("October", "Oct"), Map.entry("November", "Nov"), Map.entry("December", "Dec"));
        return map.getOrDefault(fullMonth, fullMonth);
    }


    public static String getRandomFeedbackMessage() {
        String[] words = {"system", "feature", "module", "issue", "feedback", "response", "support", "user", "application", "working", "problem", "testing", "validation", "performance", "screen", "button", "dropdown", "form", "message", "error", "success", "option", "input", "field", "data", "random", "selection", "page", "action", "request"};

        int wordCount = 8 + random.nextInt(8); // 8–15 words
        StringBuilder sb = new StringBuilder("Feedback: ");

        for (int i = 0; i < wordCount; i++) {
            String word = words[random.nextInt(words.length)];
            sb.append(word);
            if (i < wordCount - 1) {
                sb.append(" ");
            }
        }

        sb.append(" #auto");
        return sb.toString();
    }


    public static String getRandomFolderName() {
        String randomWord = generateRandomAlphabeticString(5, 8);
        return "AutoFolder_" + randomWord;
    }


    /**
     * Reusable alpha helper (A–Z).
     */
    public static String randomAlpha(int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            sb.append((char) ('A' + random.nextInt(26)));
        }
        return sb.toString();
    }


    public static String getRandomDesignation() {
        List<String> designations = List.of("Chief Financial Officer (CFO)", "Finance Manager / Controller", "Accounts Executive / Accountant", "Company Secretary (CS)", "Compliance Officer", "HR Manager / HR Team", "Payroll Manager", "Tax Consultant / CA", "Legal Advisor / Legal Team", "Operations Manager", "Business Owner / Founder / CEO", "IT Administrator", "Office Manager / Admin", "External Auditor", "Others");
        return designations.get(random.nextInt(designations.size()));
    }


    /**
     * Returns a random business type from a predefined list.
     */

    public static String getRandomBusinessTypeInGrcWelcome() {
        List<String> types = List.of("Private limited company(PVT)", "Limited liability partnership(LLP)", "One person company(OPC)", "Sole proprietorship", "Partnership firm", "Nidhi Company", "Section 8 Company", "Trust Registration", "Foreign Company", "Public limited", "Non Banking Financial Company(NBFC)", "Producer Company", "Society Company");
        return types.get(random.nextInt(types.size()));
    }

}




