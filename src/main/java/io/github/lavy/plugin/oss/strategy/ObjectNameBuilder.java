package io.github.lavy.plugin.oss.strategy;

/**
 * Builds object names used by object storage providers.
 */
public final class ObjectNameBuilder {

    private ObjectNameBuilder() {
    }

    /**
     * Builds a normalized object name from an optional path and a file name.
     *
     * @param path storage path, may be blank
     * @param fileName file name to append
     * @return normalized object name without a leading slash
     */
    public static String build(String path, String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("fileName must not be blank");
        }
        String normalizedFileName = trimSlashes(fileName.trim());
        if (path == null || path.trim().isEmpty()) {
            return normalizedFileName;
        }
        String normalizedPath = trimSlashes(path.trim());
        if (normalizedPath.isEmpty()) {
            return normalizedFileName;
        }
        return normalizedPath + "/" + normalizedFileName;
    }

    private static String trimSlashes(String value) {
        String result = value;
        while (result.startsWith("/")) {
            result = result.substring(1);
        }
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
