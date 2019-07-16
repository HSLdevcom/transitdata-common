package fi.hsl.common.files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class FileUtils {

    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    /**
     * e.g. get resource as stream with:
     * InputStream stream = getClass().getResourceAsStream("/routes.sql");
     */
    public static String readFileFromStreamOrThrow(InputStream stream) throws Exception {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            throw e;
        }
    }

}
