package utils;


/**
 * @author Sherwin
 * @since 13-11-2025
 */

public class ComplianceRow {
    private String name;
    private String office;
    private String dueDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public String toString() {
        return "ComplianceRow{name='" + name + '\'' + ", office='" + office + '\'' + ", dueDate='" + dueDate + '\'' + '}';
    }
}





