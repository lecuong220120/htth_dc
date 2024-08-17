package com.example.demo.Controller;

import com.example.demo.Service.DctkService;
import com.example.demo.websocket.WebSocketScraper;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api")
public class DctkController {
    @Autowired
    private DctkService dctkService;
    @GetMapping("/run")
    public String sendEmail() {
        dctkService.connectSocket();
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(() -> {
            try {
                dctkService.playVersion2();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        return "Run success";
    }
    @GetMapping("/stop")
    public String stop() {
        try {
            dctkService.stop();
        } catch (Exception e) {
            System.out.println("ERRORRRR Stopppp");
        }
        return "Stop success";
    }
    @PostMapping("/ws")
    public void getWS(@RequestBody String data){
        System.out.println(data);
    }
}
