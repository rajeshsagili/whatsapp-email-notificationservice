package com.whatsappemailnotificationservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class WhatsAppRequest {

    private List<Person> users;
}
