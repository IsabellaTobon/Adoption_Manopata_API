package com.example.adoption_Manopata.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    // USE MAILHOG

    @Bean
    public JavaMailSender getJavaMailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // MAILHOG CONFIGURATION
        mailSender.setHost("localhost");
        mailSender.setPort(1025);  // DEFAULT PORT ON MAILHOG

        // IF AN SMTP SERVER WITH AUTHENTICATION IS USED, ADD THESE LINES
        // mailSender.setUsername("username");
        // mailSender.setPassword("password");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "false"); // CHANGE TO TRUE IF YOUR SMTP SERVER REQUIRES AUTHENTICATION
        props.put("mail.smtp.starttls.enable", "false"); // CHANGE TO TRUE IF TLS IS REQUIRED

        return mailSender;
    }
}
