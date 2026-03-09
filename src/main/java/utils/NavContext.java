package utils;

import java.time.Duration;
import java.time.Instant;

/**
 * @author Sherwin
 * @since 04-09-2025
 */

public final class NavContext {
    private static final ThreadLocal<Instant> CLICK_START = new ThreadLocal<>();

    private NavContext() {
    }

    public static void start(String menuName) {
        CLICK_START.set(Instant.now());
    }

    public static Duration stopDuration() {
        Instant start = CLICK_START.get();
        if (start == null) return Duration.ZERO;
        try {
            return Duration.between(start, Instant.now());
        } finally {
            CLICK_START.remove();
        }
    }
}

