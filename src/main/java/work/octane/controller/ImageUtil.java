package work.octane.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ImageUtil {

    private static String UPLOADED_FOLDER = System.getProperty("imgpath", System.getProperty("java.io.tmpdir") + "/imghost/");

    static {
        Path path = Paths.get(UPLOADED_FOLDER);
        if (Files.notExists(path)) {
            if (!new File(path.toUri()).mkdirs()) {
                throw new RuntimeException(new IOException("Failed to create img directory: " + path));
            }
        }
    }

    public static List<File> getAllImages() {
        File dir = new File(UPLOADED_FOLDER);
        if(dir.listFiles() != null) {
            List<File> files = Arrays.asList(dir.listFiles());
            files.sort(Comparator.comparing(File::getName));
            return files;
        } else {
            return Collections.emptyList();
        }
    }

    public static File getImageByFilename(String name) throws IOException {
        File file = new File(UPLOADED_FOLDER + name);

        if(!file.exists()){
            throw new FileNotFoundException(name);
        }

        else return file;
    }

}