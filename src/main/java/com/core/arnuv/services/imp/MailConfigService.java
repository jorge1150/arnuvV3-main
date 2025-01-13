package com.core.arnuv.services.imp;

import com.core.arnuv.service.IMailConfigService;
import com.core.arnuv.service.IParametroService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
@RequiredArgsConstructor
public class MailConfigService implements IMailConfigService {
    private final IParametroService serviceParam;

    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        String username = getUsernameFromDatabase();
        String password = getPasswordFromDatabase();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setProtocol("smtp");
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.quitwait", "false");
        return mailSender;
    }

    private String getUsernameFromDatabase() {
        return serviceParam.getParametro("MAILSENDER").getValorText();
    }

    private String getPasswordFromDatabase() {
        return serviceParam.getParametro("MAILPASSWORD").getValorText();
    }
}
