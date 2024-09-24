package com.example.demo.Controller;

import com.example.demo.Service.DctkService;
import com.example.demo.Service.NsoService;
import com.example.demo.Service.PlayerNsoService;
import com.example.demo.Service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api")
public class DctkController {
    @Autowired
    private DctkService dctkService;
    @Autowired
    private NsoService nsoService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private PlayerNsoService playerNsoService;
    @GetMapping("/run")
    public String sendEmail() {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(() -> {
            try {
                System.out.println("Start DCTK");
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
                System.out.println("Start NSO");
                nsoService.playVersion4NSO();
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
        ExecutorService executorService1 = Executors.newFixedThreadPool(1);
        executorService1.submit(() -> {
            try {
                System.out.println("Start Save Player");
                playerService.save();
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }); ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(() -> {
            try {
                System.out.println("Start Save NSO Player");
                playerNsoService.save();
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        });
        return "Run success";
    }
    @PostMapping("/ws")
    public void getWS(@RequestBody String data){
        System.out.println(data);
    }
}
