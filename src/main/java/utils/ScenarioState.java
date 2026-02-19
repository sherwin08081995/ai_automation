package utils;

import java.util.List;
import java.util.Map;

/**
 * @author Sherwin
 * @since 12-11-2025
 */
public final class ScenarioState {

    private static final ThreadLocal<String> NAME  = new ThreadLocal<>();
    private static final ThreadLocal<String> OFFICE = new ThreadLocal<>();
    private static final ThreadLocal<String> DUE    = new ThreadLocal<>();

    // 🔹 NEW: all compliances rows across pages (page -> list of rows)
    private static final ThreadLocal<Map<Integer, List<ComplianceRow>>> ALL_ROWS =
            new ThreadLocal<>();

    public static void setCreatedComplianceName(String v){ NAME.set(v); }
    public static String getCreatedComplianceName(){ return NAME.get(); }

    public static void setCreatedOffice(String v){ OFFICE.set(v); }
    public static String getCreatedOffice(){ return OFFICE.get(); }

    public static void setCreatedDueDate(String v){ DUE.set(v); }
    public static String getCreatedDueDate(){ return DUE.get(); }

    // NEW getters/setters for all rows
    public static void setAllComplianceRows(Map<Integer, List<ComplianceRow>> rows) {
        ALL_ROWS.set(rows);
    }

    public static Map<Integer, List<ComplianceRow>> getAllComplianceRows() {
        return ALL_ROWS.get();
    }

    // optional – call from @After to clear state
    public static void clear() {
        NAME.remove();
        OFFICE.remove();
        DUE.remove();
        ALL_ROWS.remove();
    }
}





