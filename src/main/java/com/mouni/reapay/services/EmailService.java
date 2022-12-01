package com.mouni.reapay.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class EmailService {

    @Value("${mailing-service.mail.username}")
    private String USERNAME;

    @Value("${mailing-service.mail.password}")
    private String PASSWORD;

    @Value("${mailing-service.mail.auth}")
    private String AUTH;

    @Value("${mailing-service.mail.starttls.enable}")
    private String STARTTLS;

    @Value("${mailing-service.mail.host}")
    private String HOST;

    @Value("${mailing-service.mail.port}")
    private String PORT;

    @Value("${upload.threads}")
    private String threads;


    private ExecutorService threadPool;

    @PostConstruct
    public void init() {
        getThreadPool();
    }

    private ExecutorService getThreadPool() {
        threadPool = Executors.newFixedThreadPool(Integer.parseInt(threads));
        return threadPool;
    }

    public void sendEmail(String message, String subject, String toEmail) throws MessagingException {
        Thread th = new EmailAsync(message, subject, toEmail);
        threadPool.submit(th);
    }

    class EmailAsync extends Thread {

        private final String message;

        private final String subject;

        private final String to;

        public EmailAsync(String message, String subject, String to) {
            this.message = message;
            this.subject = subject;
            this.to = to;
        }

        @Override
        public void run() {

            Properties props = new Properties();
            props.put("mail.smtp.host", HOST);
            props.put("mail.smtp.port", PORT);
            props.put("mail.smtp.auth", AUTH);
            props.put("mail.smtp.starttls.enable", STARTTLS);

            System.out.println(props.toString());

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(USERNAME, PASSWORD);
                        }
                    });

            try {
                Message msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(USERNAME, "Reapay No-Reply"));
                msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                msg.setSubject(subject);
                msg.setContent(message, "text/html; charset=utf-8");
                try {
                    Transport.send(msg);
                } catch (Exception e) {
                    System.out.println(e);
                }
                System.out.println("Mail sent to " + to);

            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }

}
