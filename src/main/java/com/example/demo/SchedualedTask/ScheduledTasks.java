package com.example.demo.SchedualedTask;
import com.example.demo.Obj.Player;
import com.example.demo.Service.DctkService;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ScheduledTasks {

//     Chạy job mỗi 5 giây
    @Scheduled(fixedRate = 1800 * 1000)
//    @Scheduled(fixedRate = 120 * 1000)
    public void performTask() {
//        List<Player> players = new ArrayList<>();
//        String text = "";
//        for (Map.Entry<String, Player> entry : DctkService.playerMap.entrySet()) {
//            text = text + entry.getValue().toString();
//        }
//        text = text + "ADMIN WIN: "+ Player.formatNumber(DctkService.adminWin);
//        sendMail(text);
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