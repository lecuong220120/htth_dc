package com.example.demo.websocket;

import com.example.demo.Obj.History;
import com.example.demo.Obj.TimeSocket;
import com.example.demo.Service.DctkService;
import com.example.demo.Service.NsoService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class WebSocketThreadNso extends WebSocketClient {
    public WebSocketThreadNso(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
    }
    static DctkService dctkService = new DctkService();

    @Override
    public void onMessage(String message) {
        // Xử lý dữ liệu nhận được từ WebSocket
        Gson gson = new GsonBuilder().create();
        TimeSocket user = new TimeSocket();
        if(message.indexOf("42[3,{\"server\":4") != -1 ){
            String json = message.substring(message.indexOf("{"), message.indexOf("}") + 1);
            JSONObject jsonObject = new JSONObject(json);
            user.setServer(jsonObject.getInt("server"));
            user.setTime(jsonObject.getInt("time"));
            DctkService.timeSocket = user;
            NsoService.timeSocket = user;
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection NSO closed. Code: " + code + ", Reason: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("An error occurred: " + ex.getMessage());
    }
    public static SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    public static boolean isBefore(String time, History history){
        try {
            if(history == null) return false;
            if(StringUtils.isEmpty(history.getStop())){
                return false;
            }
            Date date = formatter. parse(history.getStop());
            LocalDateTime currentDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.parse(time));
            LocalDateTime otherDateTime = date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            if (currentDateTime.isBefore(otherDateTime)) {
                return false;
            } else if (currentDateTime.isAfter(otherDateTime)) {
                return true;
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
