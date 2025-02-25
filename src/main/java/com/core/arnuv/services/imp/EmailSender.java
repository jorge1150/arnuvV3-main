package com.core.arnuv.services.imp;

import com.core.arnuv.service.IMailConfigService;
import com.core.arnuv.service.IParametroService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailSender {
    private final JavaMailSender javaMailSender;
    private final IParametroService serviceParam;
    private final IMailConfigService mailConfigService;
    public void sendEmail(String email, String subject, String content)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(getEmailFromDatabase(), "ARNUV FUNDACION");
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(content, true);
        javaMailSender.send(message);
    }

    public void sendEmailRemitentePersonalizado(String email, String subject, String content)
            throws MessagingException, UnsupportedEncodingException {
        JavaMailSender mailSender = mailConfigService.getJavaMailSender();
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(getEmailFromDatabase(), "ARNUV FUNDACION");
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);
    }

    private String getEmailFromDatabase() {
        return serviceParam.getParametro("MAILSENDER").getValorText();
    }
}