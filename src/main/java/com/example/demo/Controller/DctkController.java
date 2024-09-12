package com.example.demo.Controller;

import com.example.demo.DTO.HistoryDTO;
import com.example.demo.Obj.History;
import com.example.demo.Service.DctkService;
import com.example.demo.websocket.WebSocketScraper;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class DctkController {
    @Autowired
    private DctkService dctkService;
    @GetMapping("/run")
    public String sendEmail() {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(() -> {
            try {
                System.out.println("Start");
                dctkService.playVersion3();
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        });
        return "Run success";
    }
    @GetMapping("/run1")
    public String run1() {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(() -> {
            try {
                System.out.println("Start");
                dctkService.playVersion3();
            } catch (Exception e) {
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
    @GetMapping("/change")
    public String change() {
        try {
            dctkService.change();
        } catch (Exception e) {
            System.out.println("ERRORRRR Change");
        }
        return "Change success";
    }
    @PostMapping("/ws")
    public void getWS(@RequestBody String data){
        System.out.println(data);
    }
}
