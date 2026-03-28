package com.whatsappemailnotificationservice.controller;


import com.whatsappemailnotificationservice.dto.WhatsAppRequest;
import com.whatsappemailnotificationservice.services.WhatsappService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/whatsapp")
public class WhatsappController {

    private final WhatsappService whatsappService;



    @GetMapping("/send-whatsapp")
    public String  send(){
        return  whatsappService.sendMessage("918333800495", "Hi Rajesh Welcome to Test WhatsApp . \uD83C\uDF89");
    }

   @PostMapping("/send")
    public List<String> send(@RequestBody WhatsAppRequest whatsAppRequest) throws Exception {
        List<String> responses = new ArrayList<>();
        for (var user : whatsAppRequest.getUsers()) {
            String type = user.getType();
            String encodedName = URLEncoder.encode(user.getName(), StandardCharsets.UTF_8);
            String url = "https://whatsapp-email-notificationservice.onrender.com/api/greeting?name="+encodedName+"&type="+type;
            String response = whatsappService.sendGenerateMessage(user.getNumber(), url, whatsAppRequest.getCaption());
           responses.add(response);
        }
        return responses;
    }
}
