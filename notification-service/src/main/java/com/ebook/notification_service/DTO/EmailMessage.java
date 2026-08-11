package com.ebook.notification_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessage implements Serializable {
    private String to;
    private String subject;
    private String body;
    private String templateName; // Optional: for HTML emails
    private Map<String, Object> variables; // Optional: template variables
    private boolean isHtml = false; // Flag to determine if HTML email
}
