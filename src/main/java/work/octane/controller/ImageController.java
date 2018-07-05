package work.octane.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

@Controller
public class ImageController {

    private static String UPLOADED_FOLDER = System.getProperty("imgpath", System.getProperty("java.io.tmpdir") + "/imghost/");
    private Random random = new Random();

    private static File lastFile = null;

    @RequestMapping(value="/image/{name}/download", method = RequestMethod.GET)
    public void downloadImage(HttpServletResponse response, @PathVariable("name") String name) throws IOException {

        File file = new File(UPLOADED_FOLDER + name);

        if(!file.exists()){
            throw new FileNotFoundException(name);
        }

        String mimeType= URLConnection.guessContentTypeFromName(file.getName());
        mimeType = mimeType == null ? "application/octet-stream" : mimeType;

        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() +"\"");
        response.setContentLength((int)file.length());
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        FileCopyUtils.copy(inputStream, response.getOutputStream());
        inputStream.close();
    }

    @GetMapping("/image/{name:.+}")
    public String getImageByName(@PathVariable String name, Model model) {
        model.addAttribute("imgSrc", "/image/" + name + "/download");
        return "image";
    }

    @GetMapping("/image/random")
    public String getRandomImage(Model model) {
        List<File> files = ImageUtil.getAllImages();

        if(files.size() == 0) {
            return "image";
        }

        int index = random.nextInt(files.size());

        model.addAttribute("imgSrc", "/image/" + files.get(index).getName() + "/download");
        return "image";
    }

    @GetMapping("/image/next")
    public String getNextImage(Model model) {
        List<File> files = ImageUtil.getAllImages();
        if(files.size() == 0) {
            return "image";
        }

        int nextIndex = 0;

        if(lastFile != null && files.contains(lastFile)){
            nextIndex = files.indexOf(lastFile) + 1;
        }

        if(files.size() <= nextIndex) {
            nextIndex = 0;
        }

        model.addAttribute("imgSrc", "/image/" + files.get(nextIndex).getName() + "/download");
        lastFile = files.get(nextIndex);
        return "image";
    }

}