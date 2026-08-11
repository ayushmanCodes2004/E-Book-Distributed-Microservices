package com.ebook.Auth_Service.Service;

import com.ebook.Auth_Service.Config.RabbitMQConfig;
import com.ebook.Auth_Service.DTO.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishEmailMessage(String to, String subject, String body) {
        EmailMessage emailMessage = new EmailMessage(to, subject, body);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_QUEUE, emailMessage);
        log.info("Email message published to RabbitMQ for: {}", to);
    }

    public void publishHtmlEmailMessage(String to, String subject, String templateName, Map<String, Object> variables) {
        EmailMessage emailMessage = EmailMessage.createHtmlEmail(to, subject, templateName);
        emailMessage.setVariables(variables);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_QUEUE, emailMessage);
        log.info("HTML email message published to RabbitMQ for: {} with template: {}", to, templateName);
    }
}
