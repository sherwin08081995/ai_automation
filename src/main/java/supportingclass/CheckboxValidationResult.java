package supportingclass;


/**
 * @author Sherwin
 * @since 29-07-2025
 */

public class CheckboxValidationResult {
    private boolean success;
    private int actualSelectedCount;
    private int displayedCount;
    private boolean archiveVisible;

    public CheckboxValidationResult(boolean success, int actualSelectedCount, int displayedCount, boolean archiveVisible) {
        this.success = success;
        this.actualSelectedCount = actualSelectedCount;
        this.displayedCount = displayedCount;
        this.archiveVisible = archiveVisible;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getActualSelectedCount() {
        return actualSelectedCount;
    }

    public int getDisplayedCount() {
        return displayedCount;
    }

    public boolean isArchiveVisible() {
        return archiveVisible;
    }
}
