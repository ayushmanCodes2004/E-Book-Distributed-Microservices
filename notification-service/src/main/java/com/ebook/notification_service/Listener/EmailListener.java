package com.ebook.notification_service.Listener;

import com.ebook.notification_service.Config.RabbitMQConfig;
import com.ebook.notification_service.DTO.EmailMessage;
import com.ebook.notification_service.Service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailListener {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void handleEmailMessage(EmailMessage emailMessage) {
        log.info("Received email message from RabbitMQ for: {}", emailMessage.getTo());
        try {
            if (emailMessage.isHtml() && emailMessage.getTemplateName() != null) {
                // Send HTML email with template
                emailService.sendHtmlEmail(
                        emailMessage.getTo(),
                        emailMessage.getSubject(),
                        emailMessage.getTemplateName(),
                        emailMessage.getVariables()
                );
                log.info("HTML email sent successfully to: {}", emailMessage.getTo());
            } else {
                // Send plain text email
                emailService.sendEmail(
                        emailMessage.getTo(),
                        emailMessage.getSubject(),
                        emailMessage.getBody()
                );
                log.info("Plain text email sent successfully to: {}", emailMessage.getTo());
            }
        } catch (Exception e) {
            log.error("Failed to send email to: {}", emailMessage.getTo(), e);
        }
    }
}
