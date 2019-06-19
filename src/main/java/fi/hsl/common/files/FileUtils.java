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
    public static String readFileFromURLOrThrow(URL url) {
        File file;
        if (url == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            file = new File(url.getFile());
        }
        try {
            FileReader fr = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

}