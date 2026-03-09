package utils;

import java.io.File;
import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;
/**
 * @author Sherwin
 * @since 16-08-2025
 */

public class FileUtils {

    /** Resolve the browser's download directory. */
    public static File getDefaultDownloadDir() {
        try {
            String cfg = ConfigReader.get("download.dir");
            if (cfg != null && !cfg.trim().isEmpty()) {
                return new File(cfg.trim()).getAbsoluteFile();
            }
        } catch (Throwable ignored) { /* config may not be present */ }

        String sys = System.getProperty("download.dir");
        if (sys != null && !sys.trim().isEmpty()) {
            return new File(sys.trim()).getAbsoluteFile();
        }

        return new File(System.getProperty("user.home"), "Downloads").getAbsoluteFile();
    }

    /** Legacy exact-name waiter (kept for compatibility). */
    public static File waitForFileDownload(String fileName, int timeoutSeconds) {
        File downloadDir = getDefaultDownloadDir();
        File targetFile  = new File(downloadDir, fileName);

        long deadline = System.currentTimeMillis() + timeoutSeconds * 1000L;
        while (System.currentTimeMillis() < deadline) {
            if (targetFile.exists() && targetFile.isFile() && targetFile.length() > 0) {
                try { Thread.sleep(300); } catch (InterruptedException ignored) {}
                long s1 = targetFile.length();
                try { Thread.sleep(300); } catch (InterruptedException ignored) {}
                long s2 = targetFile.length();
                if (s1 == s2) return targetFile;
            }
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        }
        return targetFile; // may not exist; caller should check exists()
    }

    /** Wait for baseName(.N).extension created after startEpochMs; ignore temp; size-stable. */
    public static File waitForMatchingDownload(File directory,
                                               String baseName,
                                               String extension,
                                               long startEpochMs,
                                               int timeoutSeconds) throws InterruptedException {

        if (directory == null) directory = getDefaultDownloadDir();
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Download directory does not exist: " + directory);
        }

        final Pattern namePattern = Pattern.compile(
                Pattern.quote(baseName) + "(?:\\s*\\(\\d+\\))?" + Pattern.quote(extension),
                Pattern.CASE_INSENSITIVE
        );

        final long deadline = System.currentTimeMillis() + timeoutSeconds * 1000L;
        File best = null;
        long bestTime = -1L;

        while (System.currentTimeMillis() < deadline) {
            File[] candidates = directory.listFiles(new FileFilter() {
                public boolean accept(File f) {
                    String n = f.getName();
                    if (!f.isFile()) return false;
                    if (n.endsWith(".crdownload") || n.endsWith(".part") || n.endsWith(".tmp")) return false;
                    return namePattern.matcher(n).matches();
                }
            });

            if (candidates != null && candidates.length > 0) {
                for (File f : candidates) {
                    long ts = f.lastModified();
                    if (ts >= startEpochMs && ts > bestTime && f.length() > 0) {
                        best = f; bestTime = ts;
                    }
                }
                if (best != null) {
                    long size1 = best.length();
                    Thread.sleep(400);
                    long size2 = best.length();
                    if (size1 == size2 && size2 > 0) return best;
                }
            }
            Thread.sleep(500);
        }
        return null; // timed out
    }

    /** Newest baseName(.N).extension in the directory (ignores temp files). */
    public static File latestMatchingFile(File directory, String baseName, String extension) {
        if (directory == null) directory = getDefaultDownloadDir();
        if (!directory.isDirectory()) return null;

        final Pattern namePattern = Pattern.compile(
                Pattern.quote(baseName) + "(?:\\s*\\(\\d+\\))?" + Pattern.quote(extension),
                Pattern.CASE_INSENSITIVE
        );

        File newest = null;
        long newestTs = -1L;

        File[] files = directory.listFiles(new FileFilter() {
            public boolean accept(File f) {
                if (!f.isFile()) return false;
                String n = f.getName();
                if (n.endsWith(".crdownload") || n.endsWith(".part") || n.endsWith(".tmp")) return false;
                return namePattern.matcher(n).matches();
            }
        });

        if (files != null) {
            for (File f : files) {
                long ts = f.lastModified();
                if (ts > newestTs) { newest = f; newestTs = ts; }
            }
        }
        return newest;
    }
}


