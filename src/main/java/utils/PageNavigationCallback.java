package utils;


public interface PageNavigationCallback {
    /**
     * Called after statuses for a given page were read (good for screenshots).
     */
    default void onPage(int pageNumber) {
    }

    /**
     * Called after clicking NEXT and the new page finished loading.
     */
    default void onTiming(int fromPage, int toPage, long elapsedMs) {
    }
}
