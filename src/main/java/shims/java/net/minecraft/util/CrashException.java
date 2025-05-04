package shims.java.net.minecraft.util;

import net.minecraft.CrashReport;

public class CrashException extends RuntimeException {
    private final CrashReport report;

    public CrashException(CrashReport report) {
        this.report = report;
    }

    public CrashReport getReport() {
        return this.report;
    }

    public Throwable getCause() {
        return this.report.getCrashCause();
    }

    public String getMessage() {
        return this.report.getDescription();
    }
}