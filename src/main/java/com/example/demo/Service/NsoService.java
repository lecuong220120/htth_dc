package com.example.demo.Service;

import com.example.demo.NsoObj.GameData;
import com.example.demo.NsoObj.PlayerNSO;
import com.example.demo.Obj.TimeSocket;
import com.example.demo.Utils.DctkUtils;
import com.example.demo.websocket.WebSocketThreadNso;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.demo.Utils.DctkUtils.CLNSO.percent;

@Service
public class NsoService {
    private static boolean isRun = true;
    private static boolean activeLogicNew1 = true;
    private static boolean isSwap = true;
    private static String  text = "";
    private RestTemplate restTemplate = new RestTemplate();
    private String urlGetPlay = "https://nsocltx.com/get-players?server=4&key=&limit=100";
    private String urlPushGame = "https://nsocltx.com/push-game";
    private String urlNso = "https://nsocltx.com/";
    public  static String subToken1 = "name=\"_token\" content=\"";
    public  static String urlLogin = "https://nsocltx.com/dang-nhap";
    public  static String urlLogout = "https://nsocltx.com/dang-xuat";
    public  static String userName = "thanbai";
    public  static String password = "Abc123@@";
    public  static String token = "";
    public static Map<String, Long> statisticMap = new HashMap<>();
    public void playVersion4NSO() throws InterruptedException {
        try{
            while (isRun){
                Long selectD = 0L;
                Long selectC = 0L;
                Long selectT = 0L;
                Long selectK = 0L;
                //get time
                getTimeCurrent1();
                int minutes = timeSocket.getTime() / 60;
                int remainingSeconds =  timeSocket.getTime() % 60;
                String timeString = String.format("%d:%02d", minutes, remainingSeconds);
                System.out.println("=============Time current NSO: " + timeString);
                Long timeCurrent = Long.valueOf(timeSocket.getTime()) * 1000;
                Long timeEnd = System.currentTimeMillis() + timeCurrent ;
                timeEnd = timeEnd - (25 * 1000);
                while(System.currentTimeMillis() < timeEnd){
                    Thread.sleep(1000);
                }

                List<PlayerNSO> playerNSOS = getPlayerNSO();
                System.out.println("====================Player NSO size(): " + playerNSOS.size());
                for (int i =0; i < playerNSOS.size(); i++){
                    if(playerNSOS.get(i).getSelection() == DctkUtils.CLNSO.D){
                        selectD = selectD + playerNSOS.get(i).getXu();
                    }else if(playerNSOS.get(i).getSelection() == DctkUtils.CLNSO.C){
                        selectC = selectC + playerNSOS.get(i).getXu();
                    }else if(playerNSOS.get(i).getSelection() == DctkUtils.CLNSO.T){
                        selectT = selectT + playerNSOS.get(i).getXu();
                    }else if(playerNSOS.get(i).getSelection() == DctkUtils.CLNSO.K){
                        selectK = selectK + playerNSOS.get(i).getXu();
                    }
                }
                if(activeLogicNew1){
                    long coinDC = selectD - selectC;
                    long coinTk = selectT - selectK;
                    String text1 = "**************NSO SITE****************" + "\n\n";
                    String playStringDc = "";
                    String playStringTk = "";

                    Map<Long, Integer> playMap = new HashMap<>();
                    long coinPlayTk = Math.abs(coinDC) * percent / 100;
                    long coinPlayDc = Math.abs(coinDC) * percent / 100;;
                    if(coinDC > 0){
                        playStringDc = "C";
                    } else{
                        playStringDc = "D";
                    }
                    if(coinTk > 0){
                        playStringTk = "K";
                    }else{
                        playStringTk = "T";
                    }

                    if (Objects.equals(playStringDc, "D")) {
                        playMap.put(coinPlayDc, DctkUtils.CLNSO.D);
                    }else{
                        playMap.put(coinPlayDc, DctkUtils.CLNSO.C);
                    }
                    if (Objects.equals(playStringTk, "T")) {
                        playMap.put(coinPlayTk, DctkUtils.CLNSO.T);
                    }else{
                        playMap.put(coinPlayTk, DctkUtils.CLNSO.K);
                    }
                    callApi(playMap);
                    text = text + text1;
                    System.out.println(text);
                    text = "";
                    Thread.sleep(30 * 1000);
                }
            }
        }catch (Exception e){
            System.out.println(""+e.getMessage());
        }

    }
    public static TimeSocket timeSocket = null;
    public static WebSocketThreadNso clientNso = null;
    public void getTimeCurrent1(){
        try {
            timeSocket = null;
            URI uri = new URI("wss://game-server.nsocltx.com/socket.io/?EIO=3&transport=websocket");
            clientNso = new WebSocketThreadNso(uri);
            clientNso.connect();
            if(clientNso.isClosed()){
                clientNso.connect();
            }
            boolean check = true;
            while (check){
                if(clientNso == null || clientNso.isClosed()){
                    System.out.println("========retry connected nso");
                    clientNso = new WebSocketThreadNso(uri);
                    clientNso.connect();
                }
                if(timeSocket == null || timeSocket.getTime() == 120 || timeSocket.getTime() < 25 || timeSocket.getTime() > 90){
                    Thread.sleep(100);
                }else{
                    check = false;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        clientNso.close();
    }
    public String formatNumber(long number) {
        DecimalFormat df;
        boolean isNegative = number < 0;
        long absNumber = Math.abs(number);

        if (absNumber < 1000) {
            // Không cần định dạng cho số nhỏ hơn 1000
            return (isNegative ? "-" : "") + absNumber;
        } else if (absNumber < 1_000_000) {
            // Định dạng với hàng nghìn
            df = new DecimalFormat("#,###");
        } else if (absNumber < 1_000_000_000) {
            // Định dạng với hàng triệu
            df = new DecimalFormat("#,###.##M");
            return (isNegative ? "-" : "") + df.format(absNumber / 1_000_000.0);
        } else {
            // Định dạng với hàng tỷ
            df = new DecimalFormat("#,###.##B");
            return (isNegative ? "-" : "") + df.format(absNumber / 1_000_000_000.0);
        }

        return (isNegative ? "-" : "") + df.format(absNumber);
    }
    public static int roundToNearestHundredThousand(int number) {
        int factor = 100000;  // Làm tròn đến hàng trăm nghìn
        // Chia số cho factor, làm tròn lên, sau đó nhân lại với factor
        return ((number + factor - 1) / factor) * factor;
    }
    public List<PlayerNSO> getPlayerNSO(){
        ResponseEntity<GameData> responseGameData
                = restTemplate.getForEntity(urlGetPlay, GameData.class);
        List<PlayerNSO> playerNSOS = responseGameData.getBody().getPlayers();
        List<PlayerNSO> result = new ArrayList<>();
        if(!CollectionUtils.isEmpty(playerNSOS)){
            result = playerNSOS.stream().filter(item -> item.getStatus() == 0).collect(Collectors.toList());
        }else{
            return null;
        }
        return result;
    }
    public String getTokenPre(){
        HttpHeaders httpHeaders = new HttpHeaders();
        ResponseEntity<String> response = null;
        HttpEntity<?> requestEntity = null;
        try{
            httpHeaders.add("Cookie", cookie);
            requestEntity = new HttpEntity<>(httpHeaders);
            response = restTemplate.exchange(urlNso, HttpMethod.GET, requestEntity, String.class);
            List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
            if (cookies != null) {
                cookie = cookies.get(0) +";" + cookies.get(1);
            } else {
                System.out.println("No cookies found");
            }
            String responseString = response.getBody();
            String result = responseString.substring(responseString.indexOf(subToken1) + subToken1.length(), responseString.indexOf("\">", responseString.indexOf(subToken1) + subToken1.length()));
            return result;
        }catch (Exception e){
            System.out.println(e);
        }
        return null;
    }
    public static String cookie = "";
    public String logout(){
        ResponseEntity<String> response
                = restTemplate.getForEntity(urlLogout, String.class);
        String responseString = response.getBody();
        List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        if (cookies != null) {
            cookie = cookies.get(0) +";" + cookies.get(1);
        } else {
            System.out.println("No cookies found");
        }
        String result = responseString.substring(responseString.indexOf(subToken1) + subToken1.length(), responseString.indexOf("\">", responseString.indexOf(subToken1) + subToken1.length()));
        return result;
    }
    public String getToken(){
        String token = logout();
        HttpHeaders httpHeaders = new HttpHeaders();
        ResponseEntity<String> response = null;
        HttpEntity<?> requestEntity = null;
        try{
            httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            httpHeaders.add("Cookie", cookie);
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("_token", token);
            body.add("username", userName);
            body.add("password", password);
            requestEntity = new HttpEntity<>(body, httpHeaders);
            response = restTemplate.exchange(urlLogin, HttpMethod.POST, requestEntity, String.class);
            String responseString = response.getBody();
            List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
            if (cookies != null) {
                cookie = cookies.get(0) +";" + cookies.get(1);
            } else {
                System.out.println("No cookies found");
            }
            String result = getTokenPre();
            return result;
        }catch (Exception e){
            System.out.println(e);
        }
        return null;
    }
    public void callApi(Map<Long, Integer> playMap){
        token = getToken();
        HttpHeaders httpHeaders = new HttpHeaders();
        ResponseEntity<String> response = null;
        HttpEntity<?> requestEntity = null;
        try{
            httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            httpHeaders.add("Cookie", cookie);
            for (Map.Entry<Long, Integer> entry : playMap.entrySet()) {
                MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
                body.add("game", "0");
                body.add("server", "4");
                body.add("game_type", "1");
                body.add("money", entry.getKey()+"");
                body.add("selection", entry.getValue() +"");
                body.add("_token", token);
                requestEntity = new HttpEntity<>(body, httpHeaders);
                response = restTemplate.exchange(urlPushGame, HttpMethod.POST, requestEntity, String.class);
                String responseString = response.getBody();
                System.out.println(responseString);
                System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }
    public static Integer getCoinLogic(){
        Random random = new Random();

        // Xác định khoảng giá trị
        int lowerBound = 1500000;
        int upperBound = 3000000;

        int randomNumber = lowerBound + random.nextInt(upperBound - lowerBound + 1);
        return randomNumber;
    }
}
