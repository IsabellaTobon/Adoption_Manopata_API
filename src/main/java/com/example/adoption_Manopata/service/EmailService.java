package com.example.adoption_Manopata.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String to, String resetLink) {

        String subject = "Restablecer tu contraseña";
        String text = "Hola,\n\n" +
                "Has solicitado restablecer tu contraseña. Haz clic en el siguiente enlace para cambiar tu contraseña:\n\n" +
                resetLink + "\n\n" +
                "Si no solicitaste esto, por favor ignora este correo.\n\n" +
                "Saludos,\n" +
                "El equipo de Adoption Manopata";

        sendEmail(to, subject, text);
    }
}
