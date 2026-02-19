package utils;


/**
 * @author Sherwin
 * @since 22-10-2025
 */

public final class ScenarioContext {
    private static final ThreadLocal<java.util.Map<String, Object>> CTX = ThreadLocal.withInitial(java.util.HashMap::new);

    public static void set(String k, Object v) {
        CTX.get().put(k, v);
    }

    public static Object get(String k) {
        return CTX.get().get(k);
    }

    public static Object getOrDefault(String k, Object def) {
        return CTX.get().getOrDefault(k, def);
    }

    public static void clear() {
        CTX.get().clear();
    }
}
