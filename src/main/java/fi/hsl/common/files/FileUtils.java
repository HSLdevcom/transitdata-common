package fi.hsl.common.files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;

public class FileUtils {

    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    /**
     * Get e.g. resource with:
     * URL resource = getClass().getClassLoader().getResource(fileName)
     */
    public static String readFileFromURLOrThrow(URL url) throws Exception {
        try {
            File file = new File(url.getFile());
            FileReader fr = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("Could not read text from file at " + url, e);
            throw e;
        }
    }

}
