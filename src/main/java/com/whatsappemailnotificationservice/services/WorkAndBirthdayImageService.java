package com.whatsappemailnotificationservice.services;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

@Service
@Slf4j
public class WorkAndBirthdayImageService {

    private BufferedImage birthday;
    private BufferedImage anniversary;

    @PostConstruct
    public void load() throws Exception {

        birthday = ImageIO.read(
                new ClassPathResource("templates/photo_2026-03-22_23-27-54.jpg").getInputStream());

        anniversary = ImageIO.read(
                new ClassPathResource("templates/photo_2026-03-22_23-27-14.jpg").getInputStream());

        log.info("Images loaded successfully");
    }

    public BufferedImage generate(String name, String type) {

        BufferedImage template =
                type.equalsIgnoreCase("anniversary") ? anniversary : birthday;

        // ✅ JPG → RGB
        BufferedImage image = new BufferedImage(
                template.getWidth(),
                template.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        Graphics2D g = image.createGraphics();

        // ✅ background fix
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());

        // draw template
        g.drawImage(template, 0, 0, null);

        // quality
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // =====================
        // FONT
        // =====================
        int fontSize = 90;
        Font font = new Font("Arial", Font.BOLD, fontSize);
        g.setFont(font);

        FontMetrics fm = g.getFontMetrics();

        // auto resize
        while (fm.stringWidth(name) > image.getWidth() - 200) {

            fontSize -= 2;
            font = new Font("Arial", Font.BOLD, fontSize);
            g.setFont(font);
            fm = g.getFontMetrics();
        }

        // center horizontally
        int x = (image.getWidth() - fm.stringWidth(name)) / 2;

        int y;

        // =====================
        // POSITION FIX (IMPORTANT)
        // =====================

        if (type.equalsIgnoreCase("anniversary")) {

            // below "Anniversary"
            y = image.getHeight() / 2 + 70;

            g.setColor(new Color(102, 51, 0)); // brown

        } else {

            // below "Birthday"
            y = image.getHeight() / 2 + 150;

            g.setColor(Color.WHITE);
        }

        // shadow
        g.setColor(new Color(0, 0, 0, 120));
        g.drawString(name, x + 3, y + 3);

        // main text
        if (type.equalsIgnoreCase("anniversary")) {
            g.setColor(new Color(102, 51, 0));
        } else {
            g.setColor(Color.CYAN);
        }

        g.drawString(name, x, y);

        g.dispose();

        log.info("Generated image for {}", name);

        return image;
    }
}