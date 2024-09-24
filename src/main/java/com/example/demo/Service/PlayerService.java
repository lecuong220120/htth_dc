package com.example.demo.Service;

import com.example.demo.Obj.Player;
import com.example.demo.Repository.PlayerRepository;
import com.example.demo.websocket.WebSocketScraper;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.example.demo.Service.DctkService.client;

@Service
public class PlayerService {
    @Autowired
    PlayerRepository playerRepository;
    public static Map<Integer, Player> listMapPlayerSave = new HashMap<>();
    public boolean isDisConnect(WebSocketClient webSocketClient){
        if(webSocketClient.isClosed()) return true;
        return false;
    }
    public void connectSocket(){
        if(client != null){
            if(isDisConnect(client)){
                URI uri = null;
                try {
                    uri = new URI("wss://api.dctk.me/ws");
                    client = new WebSocketScraper(uri);
                    client.connect();
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        }else{
            URI uri = null;
            try {
                uri = new URI("wss://api.dctk.me/ws");
                client = new WebSocketScraper(uri);
                client.connect();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void connectSocket1() throws InterruptedException {
        while (true){
            connectSocket();
            Thread.sleep(5 * 1000);
        }
    }
    static SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    public void save() throws InterruptedException {
        while (true){
            try {
                if(!CollectionUtils.isEmpty(listMapPlayerSave)){
                    for(Map.Entry<Integer, Player> entry : listMapPlayerSave.entrySet()){
                        if(entry.getValue().getStatus() == 0){

                        }else{
                            if(playerRepository.existsById((long)entry.getValue().getId())){

                            }else {
                                Player player1 = entry.getValue();
                                player1.setTime(formatter.format(new Date()));
                                player1.setCoinDaThang(player1.getWin_coin() - player1.getCoin());
                                playerRepository.save(entry.getValue());
                            }
                        }
                    }
                    listMapPlayerSave.clear();
                    listMapPlayerSave = new HashMap<>();
                }
                Thread.sleep(1000);
            }catch (Exception e){
                System.out.println(e);
            }
        }
    }
}
