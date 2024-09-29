package com.example.demo.SchedualedTask;
import com.example.demo.Obj.Player;
import com.example.demo.Service.DctkService;
import com.example.demo.Service.NsoService;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.example.demo.Service.NsoService.isDone;

@Component
public class ScheduledTasks {
    @Autowired
    NsoService nsoService;
//     Chạy job mỗi 5 giây
    @Scheduled(fixedRate = 2 * 60 * 1000)
//    @Scheduled(fixedRate = 120 * 1000)
    public void playDc() throws InterruptedException {
//        System.out.println("Start auto nso");
//        nsoService.playVersion4NSO();
//        System.out.println("Time next start: "+ new Date(System.currentTimeMillis() + (5*6*1000)) );
    }
    public static void sendMail(String text){
        // Cấu hình SMTP server
        String host = "smtp.gmail.com";
        final String user = "lecuong220120@gmail.com"; // Thay thế bằng email của bạn
        final String password = "whye puod tlxo oxmq"; // Thay thế bằng mật khẩu email của bạn

        // Thiết lập thuộc tính cho SMTP
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587"); // Cổng SMTP cho Gmail
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); // Bật TLS

        // Tạo phiên làm việc với cấu hình đã thiết lập
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });
        try {
            // Tạo email message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress("cutoongacteo@gmail.com"));
            message.setSubject("Log static DCTK");
            message.setText(text);

            // Gửi email
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    // Chạy job sau 5 giây từ lần chạy cuối cùng
//    @Scheduled(fixedDelay = 5000)
//    public void performTaskWithDelay() {
//        System.out.println("Job chạy với delay: " + System.currentTimeMillis());
//    }
//
//    // Chạy job theo cron expression (VD: mỗi 10 giây)
//    @Scheduled(cron = "*/10 * * * * *")
//    public void performTaskUsingCron() {
//        System.out.println("Job chạy theo cron: " + System.currentTimeMillis());
//    }
}