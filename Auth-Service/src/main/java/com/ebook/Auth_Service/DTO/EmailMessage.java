package com.ebook.Auth_Service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
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

    // Constructor for plain text emails (backward compatibility)
    public EmailMessage(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.isHtml = false;
    }

    // Constructor for HTML emails
    public EmailMessage(String to, String subject, String templateName, Map<String, Object> variables) {
        this.to = to;
        this.subject = subject;
        this.templateName = templateName;
        this.variables = variables;
        this.isHtml = true;
    }

    // Builder pattern for easy creation
    public static EmailMessage createHtmlEmail(String to, String subject, String templateName) {
        EmailMessage message = new EmailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setTemplateName(templateName);
        message.setVariables(new HashMap<>());
        message.setHtml(true);
        return message;
    }

    public EmailMessage addVariable(String key, Object value) {
        if (this.variables == null) {
            this.variables = new HashMap<>();
        }
        this.variables.put(key, value);
        return this;
    }
}
