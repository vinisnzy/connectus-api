package com.vinisnzy.connectus_api.infra.email;

import com.vinisnzy.connectus_api.api.exception.SendEmailException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.username}")
    private String senderEmail;

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void sendVerificationEmail(String to, String name, String verificationLink) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("verificationLink", verificationLink);

            String html = templateEngine.process("email-verification", context);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

            messageHelper.setTo(to);
            messageHelper.setSubject("Verifique seu Email - ConnectusCRM");
            messageHelper.setText(html, true);

            javaMailSender.send(message);
            log.info("Email de verificação enviado para o destinatário: '{}'", to);
        } catch (Exception e) {
            throw new SendEmailException("Erro ao enviar e-mail de verificação:" + e.getLocalizedMessage());
        }
    }

    @Async
    public void sendResetPasswordEmail(String to, String name, String resetLink) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("resetLink", resetLink);

            String html = templateEngine.process("email-reset-password", context);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

            messageHelper.setTo(to);
            messageHelper.setSubject("Redefina sua Senha - ConnectusCRM");
            messageHelper.setText(html, true);

            javaMailSender.send(message);
            log.info("Email para redefinir senha enviado para o destinatário: '{}'", to);
        } catch (Exception e) {
            throw new SendEmailException("Erro ao enviar e-mail de redefinição de senha:" + e.getLocalizedMessage());
        }
    }
}
