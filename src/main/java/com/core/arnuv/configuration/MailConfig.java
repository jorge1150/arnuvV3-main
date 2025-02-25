package com.core.arnuv.configuration;

import com.core.arnuv.service.IParametroService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class MailConfig {
    private final IParametroService serviceParam;

    @Bean
    JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        String username = getUsernameFromDatabase();
        String password = getPasswordFromDatabase();
        int port = getPortFromDatabase();
        String host = getHostFromDatabase();

        mailSender.setHost(host);
        mailSender.setPort(port);
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
        return serviceParam.getParametro("MAILUSER").getValorText();
    }

    private String getPasswordFromDatabase() {
        return serviceParam.getParametro("MAILPASSWORD").getValorText();
    }

    private int getPortFromDatabase() {
        return Integer.parseInt(serviceParam.getParametro("MAILPORT").getValorText());
    }

    private String getHostFromDatabase() {
        return serviceParam.getParametro("MAILHOST").getValorText();
    }
}