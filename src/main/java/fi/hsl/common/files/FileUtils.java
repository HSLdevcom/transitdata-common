package fi.hsl.common.files;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileUtils {
    /**
     * e.g. get resource as stream with:
     * InputStream stream = getClass().getResourceAsStream("/routes.sql");
     */
    @NotNull
    public static String readFileFromStreamOrThrow(@NotNull InputStream stream) throws Exception {
        return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    }
}
