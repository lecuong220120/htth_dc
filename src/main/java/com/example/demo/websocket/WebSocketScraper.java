package com.example.demo.websocket;

import com.example.demo.Obj.History;
import com.example.demo.Obj.Player;
import com.example.demo.Service.DctkService;
import com.example.demo.Service.PlayerService;
import com.example.demo.Utils.DctkUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

public class WebSocketScraper extends WebSocketClient {
    public WebSocketScraper(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to WebSocket server.");
    }
    static DctkService dctkService = new DctkService();

    @Override
    public void onMessage(String message) {
        // Xử lý dữ liệu nhận được từ WebSocket
        JSONObject jsonObject = new JSONObject(message);
        JSONArray object = (JSONArray) jsonObject.get("body");
        for (int i =0 ; i<object.length(); i++){
            Player player = new Player();
            JSONObject obj = object.getJSONObject(i);
            player.setId(obj.getInt("id"));
            player.setCoin(obj.getInt("coin"));
            player.setSelection(obj.getInt("selection"));
            player.setName(obj.getString("name"));
            player.setType(obj.getInt("type"));
            player.setWin_coin(obj.getInt("win_coin"));
            player.setStatus(obj.getInt("status"));
            player.setTime(obj.getString("time"));
            player.setName_server(obj.getString("name_server"));
            if(!dctkService.listMapPlayer.containsKey(player.getId())){
                dctkService.listMapPlayer.put(player.getId(), player);
            }
            if(!PlayerService.listMapPlayerSave.containsKey(player.getId())){
                PlayerService.listMapPlayerSave.put(player.getId(), player);
            }

        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed. Code: " + code + ", Reason: " + reason);
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
