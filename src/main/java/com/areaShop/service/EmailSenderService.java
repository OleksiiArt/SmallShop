package com.areaShop.service;

import com.areaShop.model.ConfirmationToken;
import com.areaShop.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service("emailSenderService")
public class EmailSenderService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailSenderService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendEmail(User user, ConfirmationToken confirmationToken) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Complete Password Reset!");
        mailMessage.setFrom("test-email@gmail.com");
        mailMessage.setText("To complete the password reset process, please click here: "
                + "http://localhost:8090/confirm-reset?token="+confirmationToken.getConfirmationToken());
        javaMailSender.send(mailMessage);
    }
}