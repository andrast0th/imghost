package work.octane.controller;

import groovy.lang.IntRange;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class UploadController {

    //Save the uploaded file to this folder
    private static String UPLOADED_FOLDER = System.getProperty("imgpath", System.getProperty("java.io.tmpdir") + "/imghost/");

    private static List<String> images = new ArrayList<>();
    static {
        images.add("what");
        images.add("the");
        images.add("fuck");
    }

    @GetMapping("/")
    public String index(Model model) {
        return redirectUpload(model);
    }

    @GetMapping("/upload")
    public String redirectUpload(Model model) {
        List<File> files = ImageUtil.getAllImages();
        List<String> fileSrcs =
                files.stream()
                        .map(file -> "../image/" + file.getName() + "/download")
                        .collect(Collectors.toList());
        model.addAttribute("fileSrcs", fileSrcs);
        return "upload";
    }

    @PostMapping("/upload") // //new annotation since 4.3
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) throws IOException {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:upload";
        }

        // Get the file and save it somewhere
        byte[] bytes = file.getBytes();

        //that an img???!!?!
        String contentType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(bytes));
        if(contentType == null) {
            redirectAttributes.addFlashAttribute("message", "Can't detect mime type! Piss off!");
            return "redirect:upload";
        }

        if(!contentType.contains("image")) {
            redirectAttributes.addFlashAttribute("message", "That's not an image! Piss off!");
            return "redirect:upload";
        }

        Path path = Paths.get(UPLOADED_FOLDER);
        if (Files.notExists(path)) {
            if (!new File(path.toUri()).mkdirs()) {
                throw new IOException("Failed to create img directory: " + path);
            }
        }

        String filename = file.getOriginalFilename();
        filename = filename.replaceAll("\\s+","");

        Path filePath = Paths.get(UPLOADED_FOLDER + filename);
        Files.write(filePath, bytes);

        redirectAttributes.addFlashAttribute(
                "message",
                "You successfully uploaded '" + file.getOriginalFilename() + "'");

        return "redirect:/upload";
    }

}