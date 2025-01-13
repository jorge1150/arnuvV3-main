package com.core.arnuv.service;

import org.springframework.mail.javamail.JavaMailSender;

public interface IMailConfigService {
    JavaMailSender getJavaMailSender();
}
