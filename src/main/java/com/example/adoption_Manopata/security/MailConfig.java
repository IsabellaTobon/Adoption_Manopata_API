package com.example.adoption_Manopata.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    //Use MailHog

    @Bean
    public JavaMailSender getJavaMailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // Configuración para MailHog (puedes modificarla para usar otro servicio SMTP de prueba)
        mailSender.setHost("localhost");
        mailSender.setPort(1025);  // Puerto por defecto para MailHog

        // Si utilizas un servidor SMTP con autenticación, agrega estas líneas:
        // mailSender.setUsername("tu-username");
        // mailSender.setPassword("tu-password");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "false"); // Cambiar a true si tu servidor SMTP requiere autenticación
        props.put("mail.smtp.starttls.enable", "false"); // Cambiar a true si necesitas TLS

        return mailSender;
    }
}
