package com.whatsappemailnotificationservice.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WhatsappService {

    private static final Logger log = LoggerFactory.getLogger(WhatsappService.class);

    private final WebClient webClient;

    // phone-number-id (the one used in the endpoint). Keep this as config in production.
    @Value("${whatsapp.api.url}")
    private  String whatsAppurl ;

    // store only the token value here (without the "Bearer " prefix)
    @Value("${whatsapp.api.token}")
    private  String bearerToken ;

    /**
     * Send a plain text message using WhatsApp Cloud API.
     * toNumber should be in international format without leading plus sign, e.g. "919876543210".
     */
    public String sendMessage(String toNumber, String message) {
        Map<String, Object> body = new HashMap<>();
        //Map<String,Object> langMap = new HashMap<>();
       // Map<String, Object> template = new HashMap<>();
       // langMap.put("code", "en_US");
        Map<String,Object> map = new HashMap<>();
        body.put("messaging_product", "whatsapp");
        body.put("to", toNumber);
        body.put("type", "text");
        Map<String, Object> objectMap = new HashMap<>();
     //   template.put("name", message);
      //  template.put("language", langMap);
        objectMap.put("body", message);
        body.put("text", objectMap);

        try {
            return webClient.post()
                    .uri(whatsAppurl)
                    .header("Authorization", "Bearer " + bearerToken)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException ex) {
            // Log and return the response body to help debugging (Facebook returns detailed error JSON)
            log.error("WhatsApp API returned status {}: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            return ex.getResponseBodyAsString();
        } catch (Exception ex) {
            log.error("Unexpected error while sending WhatsApp message", ex);
            return ex.getMessage();
        }
    }

    /**
     * Send an image message by providing a publicly accessible image URL and include a message as the image caption.
     * The Cloud API uses the image "caption" field for the text that accompanies the image. If you need a
     * separate plain-text message (not as caption), send a text message first using {@link #sendMessage}.
     */
    public String sendImageMessage(String toNumber, String imageUrl, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("messaging_product", "whatsapp");
        body.put("to", toNumber);
        body.put("type", "image");
        Map<String, String> image = new HashMap<>();
        image.put("link", imageUrl);
        if (message != null) image.put("caption", message);
        body.put("image", image);

        try {
            return webClient.post()
                    .uri(whatsAppurl)
                    .header("Authorization", "Bearer " + bearerToken)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("WhatsApp API returned status {}: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            return ex.getResponseBodyAsString();
        } catch (Exception ex) {
            log.error("Unexpected error while sending WhatsApp image message", ex);
            return ex.getMessage();
        }
    }

    /**
     * Convenience method to send an image without a caption.
     */
    public String sendImageMessage(String toNumber, String imageUrl) {
        return sendImageMessage(toNumber, imageUrl, null);
    }

    /**
     * Send image with message. If sendAsCaption is true the provided text will be sent as the image's caption
     * (single API call). If sendAsCaption is false the method will first send a text message and then send the
     * image (two API calls). Returns a combined response string for debugging — first text response (if any)
     * followed by image response.
     */
    public String sendImageWithText(String toNumber, String imageUrl, String text, boolean sendAsCaption) {
        StringBuilder result = new StringBuilder();
        if (sendAsCaption) {
            // single call, caption attached to image
            String imageResp = sendImageMessage(toNumber, imageUrl, text);
            result.append("image:").append(imageResp);
        } else {
            // send text first, then image without caption
            if (text != null && !text.isEmpty()) {
                String textResp = sendMessage(toNumber, text);
                result.append("text:").append(textResp).append("\n");
            }
            String imageResp = sendImageMessage(toNumber, imageUrl, null);
            result.append("image:").append(imageResp);
        }
        return result.toString();
    }

    public String sendGenerateMessage(String toNumber, String url , String caption) {
        Map<String, Object> body = new HashMap<>();
        body.put("messaging_product", "whatsapp");
        body.put("to", toNumber);
        body.put("type", "image");
        Map<String, String> image = new HashMap<>();
        image.put("link", url);
        image.put("caption", caption);
        body.put("image", image);


        try {
            return webClient.post()
                    .uri(whatsAppurl)
                    .header("Authorization", "Bearer " + bearerToken)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("WhatsApp API returned status {}: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            return ex.getResponseBodyAsString();
        } catch (Exception ex) {
            log.error("Unexpected error while sending WhatsApp message", ex);
            return ex.getMessage();
        }
    }

}
