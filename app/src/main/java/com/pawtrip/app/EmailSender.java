package com.pawtrip.app;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender {

    private static final String SENDER_EMAIL = "sujitha1366@gmail.com";
    private static final String SENDER_PASS  = "weifijpwisiwbspv";

    public static void sendEmail(String toEmail, String subject, String body, Context ctx) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "465");
                props.put("mail.smtp.socketFactory.port", "465");
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.socketFactory.fallback", "false");
                props.put("mail.smtp.ssl.enable", "true");
                props.put("mail.smtp.ssl.protocols", "TLSv1.2");

                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASS);
                    }
                });

                Message msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(SENDER_EMAIL, "PawTrip App"));
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                msg.setSubject(subject);
                msg.setText(body);
                Transport.send(msg);

                new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(ctx, "✅ Email sent!", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(ctx, "Email failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }
}