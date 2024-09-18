package com.example.demo.Service;

import com.example.demo.NsoObj.GameData;
import com.example.demo.NsoObj.PlayerNSO;
import com.example.demo.Obj.TimeSocket;
import com.example.demo.Utils.DctkUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
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
                Long selectU = 0L;
                Long selectQ = 0L;
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
                String textDc = "";
                String textTk = "";

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
                    String text1 = "**************NSO SITE****************" + "\n\n";
                    String playString = "";
                    String playStringDc = "";
                    String playStringTk = "";
                    Map<Long, Integer> playMap = new HashMap<>();
//                    long coinPlayDcPre =  ((Math.abs(selectD - selectC) * 10)  /100)/2;
//                    long coinPlayDc  = roundToNearestHundredThousand((int) coinPlayDcPre);
//                    if(Math.abs(selectD - selectC) > 500000000){
//                        coinPlayDc = 10000000;
//                    }
//                    long coinPlayTkPre =  ((Math.abs(selectT - selectK) * 10)  /100)/2;
//                    long coinPlayTk = roundToNearestHundredThousand((int) coinPlayTkPre);
//                    if(Math.abs(selectT - selectK) > 500000000){
//                        coinPlayTk = 10000000;
//                    }
                    int coinRandom = getCoinLogic();
                    long coinPlayTk = coinRandom;
                    long coinPlayDc = coinRandom;
                    if(Math.abs(selectD-selectC)>Math.abs(selectT-selectK)){
                        if(selectD > selectC){
                            if (selectC != 0){
                                long check = selectD / selectC;
                                if (check >= DctkUtils.DCTK.checkNSO){
                                    if(isSwap){
                                        playString = "D";
                                    }else{
                                        playString = "C";
                                    }
                                    text1 = text1 + "(D)" + formatNumber(selectD) + "/" +  formatNumber(selectC) +"(C)"+"): ===>>> " + check +", chenh lech: "+formatNumber(Math.abs(selectD-selectC) )+ ", chon: "+playString+", swap is :" +isSwap+", tham gia: "+ formatNumber(coinPlayDc)+ "\n";
                                }else{
                                    text1 = text1 + "(D)" + formatNumber(selectD) + "/" +  formatNumber(selectC) +"(C): ===>>> " + check +", chenh lech: "+formatNumber(Math.abs(selectD-selectC) )+ ", bo chon: C" + "\n";
                                }
                            }else{
                                playString = "C";
                                text1 = text1 + "=>>> select C = 0, chon: C , tham gia: "+ formatNumber(coinPlayDc)+ "\n";
                            }

                        }else{
                            if (selectD != 0){
                                long check = selectC / selectD;
                                if (check >= DctkUtils.DCTK.checkNSO){
                                    if(isSwap){
                                        playString = "C";
                                    }else{
                                        playString = "D";
                                    }
                                    text1 = text1 + "(C)" + formatNumber(selectC) + "/" +  formatNumber(selectD) +"(D): ===>>> " + check +", chenh lech: "+formatNumber(Math.abs(selectD-selectC) )+ ", chon: "+playString+", swap is :" +isSwap+", tham gia: "+ formatNumber(coinPlayDc)+ "\n";
                                }else{
                                    text1 = text1 + "(C)" + formatNumber(selectC) + "/" +  formatNumber(selectD) +"(D): ===>>> " + check +", chenh lech: "+formatNumber(Math.abs(selectD-selectC) )+ ", bo chon: D" + "\n";
                                }
                            }else{
                                playString = "D";
                                text1 = text1 + "=>>> select D = 0, chon: D, tham gia: "+ formatNumber(coinPlayDc)+ "\n";
                            }
                        }
                    } else {
                        if(selectT > selectK){
                            if (selectK != 0){
                                long check = selectT / selectK;
                                if (check >= DctkUtils.DCTK.check){
                                    if(isSwap){
                                        playString = "T";
                                    }else{
                                        playString = "K";
                                    }
                                    text1 = text1 + "(T)" + formatNumber(selectT) + "/" +  formatNumber(selectK) +"(K): ===>>> " + check+", chenh lech: "+formatNumber(Math.abs(selectT-selectK) )+ ", chon: "+playString+", swap is :" +isSwap+", tham gia: "+ formatNumber(coinPlayTk)+ "\n";

                                }else{
                                    text1 = text1 + "(T)" + formatNumber(selectT) + "/" +  formatNumber(selectK) +"(K): ===>>> " + check +", chenh lech: "+formatNumber(Math.abs(selectT-selectK))+ ", bo chon: K"+ "\n";
                                }
                            }else{

                                playString = "K";
                                text1 = text1 + "=>>> select K = 0, chon: K, tham gia: "+ formatNumber(coinPlayTk)+ "\n";
                            }

                        }else{
                            if (selectT != 0){
                                long check = selectK / selectT;
                                if (check >= DctkUtils.DCTK.check){
                                    if(isSwap){
                                        playString = "K";
                                    }else{
                                        playString = "T";
                                    }
                                    text1 = text1 + "(K)" + formatNumber(selectK) + "/" +  formatNumber(selectT) +"(T): ===>>> " + check+", chenh lech: "+formatNumber(Math.abs(selectT-selectK)) + ", chon: "+playString+", swap is :" +isSwap+", tham gia: "+ formatNumber(coinPlayTk)+ "\n";
                                }else{
                                    text1 = text1 + "(K)" + formatNumber(selectK) + "/" +  formatNumber(selectT) +"(T): ===>>> " + check +", chenh lech: "+formatNumber(Math.abs(selectT-selectK))+ ", bo chon: T"+ "\n";
                                }
                            }else{
                                playString = "T";
                                text1 = text1 + "=>>> select T = 0, chon: T, tham gia: "+ formatNumber(coinPlayTk)+ "\n";
                            }
                        }
                    }

                    playStringDc = playString;


                    if (Objects.equals(playString, "D")) {
                        playMap.put(coinPlayDc, DctkUtils.CLNSO.D);
                    } else if  (Objects.equals(playString, "C")) {
                        playMap.put(coinPlayDc, DctkUtils.CLNSO.C);
                    } else if (Objects.equals(playString, "T")) {
                        playMap.put(coinPlayTk, DctkUtils.CLNSO.T);
                    } else if (Objects.equals(playString, "K")){
                        playMap.put(coinPlayTk, DctkUtils.CLNSO.K);
                    }
                    callApi(playMap);
                    text = text + text1;
                    System.out.println(text);
                    text = "";
                    Thread.sleep(50 * 1000);
                }
            }
        }catch (Exception e){
            System.out.println(""+e.getMessage());
        }

    }
    public static TimeSocket timeSocket = null;
    public void getTimeCurrent1(){
        try {
            boolean check = true;
            timeSocket = null;
            while (check){
                if(timeSocket == null || timeSocket.getTime() == 120 ||timeSocket.getTime() < 25 || timeSocket.getTime() > 90){
                    Thread.sleep(100);
                }else{
                    check = false;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
