package com.whatsappemailnotificationservice.controller;


import com.whatsappemailnotificationservice.services.WorkAndBirthdayImageService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ImageController {


    private final WorkAndBirthdayImageService imageService;

    @GetMapping("/greeting")
    public void generateImage(@RequestParam String name, @RequestParam String type, HttpServletResponse response) throws IOException {

       try {
           BufferedImage image = imageService.generate(name, type);
           response.setContentType("image/jpeg");
           ImageIO.write(image, "jpg", response.getOutputStream());
       } catch (IOException e) {
           e.printStackTrace();
           response.setStatus(500);
       }
    }
}
