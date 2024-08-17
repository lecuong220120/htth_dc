package com.example.demo.Service;

import com.example.demo.DTO.BalanceDTO;
import com.example.demo.DTO.HistoryDTO;
import com.example.demo.DTO.LoginDTO;
import com.example.demo.Obj.*;
import com.example.demo.Utils.DctkUtils;
import com.example.demo.websocket.WebSocketScraper;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.java_websocket.client.WebSocketClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.demo.Utils.DctkUtils.DCTK.coinAdd;

@Service
public class DctkService {
    private static boolean isRun = true;
    private RestTemplate restTemplate = new RestTemplate();
    private static String url = "https://api.dctk.me/api/home/history?page=1";
    private static String urlToken = "https://api.dctk.me/api/login";
    private static String urlPlay = "https://api.dctk.me/api/user/join";
    private static String urlBalance = "https://api.dctk.me/api/user/balance-history?page=1&limit=20";
    private static  String userName = "nenene";
    private static  String password = "Lecuong220120a@";
    private static String token = "";
    private static String to = "cutoongacteo@gmail.com";
    //dc
    private static List<Integer> list1 = new ArrayList<>(Arrays.asList(0, 0, 0));
    private static List<Integer> list2 = new ArrayList<>(Arrays.asList(0, 0, 1));
    private static List<Integer> list3 = new ArrayList<>(Arrays.asList(0, 1, 0));
    private static List<Integer> list4 = new ArrayList<>(Arrays.asList(0, 1, 1));
    private static List<Integer> list5 = new ArrayList<>(Arrays.asList(1, 0, 0));
    private static List<Integer> list6 = new ArrayList<>(Arrays.asList(1, 0, 1));
    private static List<Integer> list7 = new ArrayList<>(Arrays.asList(1, 1, 0));
    private static List<Integer> list8 = new ArrayList<>(Arrays.asList(1, 1, 1));
    //tc
    private static List<Integer> list9  = new ArrayList<>(Arrays.asList(2, 2, 2));
    private static List<Integer> list10 = new ArrayList<>(Arrays.asList(2, 2, 3));
    private static List<Integer> list11 = new ArrayList<>(Arrays.asList(2, 3, 2));
    private static List<Integer> list12 = new ArrayList<>(Arrays.asList(2, 3, 3));
    private static List<Integer> list13 = new ArrayList<>(Arrays.asList(3, 2, 2));
    private static List<Integer> list14 = new ArrayList<>(Arrays.asList(3, 2, 3));
    private static List<Integer> list15 = new ArrayList<>(Arrays.asList(3, 3, 2));
    private static List<Integer> list16 = new ArrayList<>(Arrays.asList(3, 3, 3));
    //last play
    public static History history;
    public static History historyBefore;
    //historyPlay
    public static List<Integer> historyPlay = new ArrayList<>();
    public static List<Integer> historyWin = new ArrayList<>();
    public static  Integer countDC = 0;
    public static  Integer countTK = 0;
    //web socket
    public static Map<Integer, Player> listMapPlayer = new HashMap<>();
    public HistoryDTO getHistory(){
        ResponseEntity<HistoryDTO> response
                = restTemplate.getForEntity(url, HistoryDTO.class);
        System.out.println("=======Call api get history");
        return  response.getBody();
    }
    public String getToken(){
        LoginDTO loginDTO = new LoginDTO(userName,  password);
        ResponseEntity<LoginDTO> response
                = restTemplate.postForEntity(urlToken, loginDTO, LoginDTO.class);
        System.out.println("=======Call api get token");
        return response.getBody().getJwt();
    }
    public void autoPlay(){
        try{
            Integer coinDc = 0;
            Integer coinTk = 0;
            if(history != null){
                Integer idPrev =  history.getId() + 1;
                List<Balance> balances = getBalance().stream().
                        filter(item -> item.getNote().indexOf(idPrev.toString()) != -1)
                        .collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(balances)){
                    String text = "";
                    int countSuccess = 0;
                    for (Balance balance : balances) {
                        if(balance.getNote().indexOf("Trao thưởng phiên") != -1){
                            countSuccess++;
                            text = text + "Win: "+ countSuccess + ", cảm ơn bạn: "+ balance.toString() + "\n";
                        }else{
                            text = text + "Tham gia: " + balance.toString() + "\n";
                        }
                    }
                    if (countSuccess ==0){
                        text = text + "==============> Failure";
                    }
                    System.out.println("======================" + text);
                    sendMail(text);
                }
            }
            List<History> historiesRes = getHistory().getHistories();
            List<History> histories = new ArrayList<>();
            history = historiesRes.get(0);

            for (int i = 0; i< historiesRes.size(); i++){
                histories.add(historiesRes.get(i));
                if(i == 2){
                    break;
                }
            }

            List<Integer> listDC = histories.stream().map(History::getResult_cd).collect(Collectors.toList());
            List<Integer> listTK = histories.stream().map(History::getResult_tk).collect(Collectors.toList());
            historyWin.clear();
            historyWin.add(listDC.get(0));
            historyWin.add(listTK.get(0));
            coinDc = changeCoinDC(historyPlay, historyWin);
            coinTk = changeCoinTK(historyPlay, historyWin);
            Integer playDC = preditionDCTK(listDC, true, historiesRes);
            Integer playTk = preditionDCTK(listTK, false, historiesRes);
            historyPlay.clear();
            historyPlay.add(playDC);
            historyPlay.add(playTk);
            play(playDC, playTk, coinDc, coinTk);
        }catch (Exception e){
            System.out.printf("ERRORRRRRRRRRRRRRRR" + e);
        }

    }
    public Integer changeCoinDC(List<Integer> playSet, List<Integer> playWin){
        if(CollectionUtils.isEmpty(playSet) || CollectionUtils.isEmpty(playWin)) return 0;
        if(countDC == 1){
            countDC = 0;
            return 0;
        }
        if(Objects.equals(playSet.get(0), playWin.get(0))){
            countDC++;
            return coinAdd;
        }
        return 0;
    }
    public Integer changeCoinTK(List<Integer> playSet, List<Integer> playWin){
        if(CollectionUtils.isEmpty(playSet) || CollectionUtils.isEmpty(playWin)) return 0;
        if(countTK == 1){
            countTK = 0;
            return 0;
        }
        if(Objects.equals(playSet.get(1), playWin.get(1))){
            countTK++;
            return coinAdd;
        }
        return 0;
    }
    public int preditionDCTK(List<Integer> listInteger, boolean isDC, List<History> histories){
        if (isDC){
            if(listInteger.equals(list1)){
                return DctkUtils.DCTK.C;
            }else if(listInteger.equals(list2)){
                return DctkUtils.DCTK.C;
            }else if(listInteger.equals(list3)){
                return DctkUtils.DCTK.D;
            }else if(listInteger.equals(list4)){
                return DctkUtils.DCTK.D;
            }else if(listInteger.equals(list5)){
                return DctkUtils.DCTK.C;
            }else if(listInteger.equals(list6)){
                return DctkUtils.DCTK.C;
            }else if(listInteger.equals(list7)){
                return DctkUtils.DCTK.D;
            }else if (listInteger.equals(list8)){
                return DctkUtils.DCTK.D;
            }
            return 1;
        }else{
            if(listInteger.equals(list9)){
                return DctkUtils.DCTK.T;
            }else if(listInteger.equals(list10)){
                return DctkUtils.DCTK.T;
            }else if(listInteger.equals(list11)){
                return DctkUtils.DCTK.K;
            }else if(listInteger.equals(list12)){
                return DctkUtils.DCTK.K;
            }else if(listInteger.equals(list13)){
                return DctkUtils.DCTK.T;
            }else if(listInteger.equals(list14)){
                return DctkUtils.DCTK.T;
            }else if(listInteger.equals(list15)){
                return DctkUtils.DCTK.K;
            }else if (listInteger.equals(list16)){
                return DctkUtils.DCTK.K;
            }
            return 3;
        }
    }
    public List<Balance> getBalance(){
        HttpHeaders httpHeaders = new HttpHeaders();
        ResponseEntity<BalanceDTO> response = null;
        HttpEntity<String> requestEntity = null;
        try{
            httpHeaders.set("Authorization", "Bearer " + token);
            httpHeaders.set("Content-Type", "application/json");
            requestEntity = new HttpEntity<>(httpHeaders);
            response = restTemplate.exchange(urlBalance, HttpMethod.GET, requestEntity, BalanceDTO.class);
        }catch (Exception e){
            token = getToken();
            httpHeaders.set("Authorization", "Bearer " + token);
            httpHeaders.set("Content-Type", "application/json");
            requestEntity = new HttpEntity<>(httpHeaders);
            response = restTemplate.exchange(urlBalance, HttpMethod.GET, requestEntity, BalanceDTO.class);
        }
        return response.getBody().getBalances();
    }
    public void play(Integer dc, Integer tk, Integer coinDC, Integer coinTK){
        try{
            //dc
            DCTK dcApi = new DCTK(0, dc, DctkUtils.DCTK.coin + coinDC, 4);
            //tk
            DCTK tkApi = new DCTK(0, tk, DctkUtils.DCTK.coin + coinTK, 4);
            ResponseDctk responseDc = callApi(dcApi);
            System.out.println("===================Response DC: " + responseDc.getMessage());
            Thread.sleep(15  * 1000);
            ResponseDctk responseTk = callApi(tkApi);
            System.out.println("===================Response TK: " + responseTk.getMessage());

        }catch (Exception e){
            System.out.printf("===============================ERROROOOOOOOOO" + e);
        }
    }
    public ResponseDctk callApi(DCTK bodyObj){
        HttpHeaders httpHeaders = new HttpHeaders();
        ResponseEntity<ResponseDctk> response = null;
        HttpEntity<DCTK> requestEntity = null;
        try{
            httpHeaders.set("Authorization", "Bearer " + token);
            httpHeaders.set("Content-Type", "application/json");
            requestEntity = new HttpEntity<>(bodyObj, httpHeaders);
            response = restTemplate.exchange(urlPlay, HttpMethod.POST, requestEntity, ResponseDctk.class);
        }catch (Exception e){
            token = getToken();
            httpHeaders.set("Authorization", "Bearer " + token);
            httpHeaders.set("Content-Type", "application/json");
            requestEntity = new HttpEntity<>(bodyObj, httpHeaders);
            response = restTemplate.exchange(urlPlay, HttpMethod.POST, requestEntity, ResponseDctk.class);
        }
        return response.getBody();
    }
    public void sendMail(String text){
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
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Thông báo từ hệ thống phát triển bởi Nê");
            message.setText(text);

            // Gửi email
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    public void playNow() throws InterruptedException {
        isRun = true;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        DctkService dctkService  = new DctkService();
        while(isRun){
            History history = dctkService.getHistory().getHistories().get(0);
            if(dctkService.history == null || !Objects.equals(history.getId(),dctkService.history.getId()) ){
                dctkService.autoPlay();
            }
            System.out.println("Thread sleep 20s: " + dateFormat.format(new Date(System.currentTimeMillis())));
            Thread.sleep(20 * 1000);
            System.out.println("Thread start at " + dateFormat.format(new Date(System.currentTimeMillis())));
        }
        if(!isRun){
            System.out.println("================STOPPPP DCTK");
        }
    }
    public void stop(){
        isRun = false;
    }
    public String getTimeCurrent(){
        String url = "https://dctk.me/";
        String time = null;
        try {
            String serviceUrl = "http://localhost:3000/fetch-html?url=" + java.net.URLEncoder.encode(url, "UTF-8");
            String checkString = "<strong class=\"text-[#333333]\">Thời gian:</strong></div><div class=\"col-span-2 text-center\"><span class=\"text-[#333333]\">";
            boolean check = true;
            CloseableHttpClient httpClient = HttpClients.createDefault();
            while (check){
                try {
                    HttpGet request = new HttpGet(serviceUrl);
                    try (CloseableHttpResponse response = httpClient.execute(request)) {
                        String html = EntityUtils.toString(response.getEntity());
                        if(html.indexOf(checkString) != -1){
                            time = html.substring(html.indexOf(checkString) + 121, html.indexOf(checkString) + 121 + 5);
                            System.out.println("=========Time current:" + time);
                            if(isDisConnect(client)){
                                sendMail("Socket is disconnect. ReConnect socket");
                                connectSocket();
                            }
                            if (!Objects.equals(time, "02:00") && !StringUtils.isEmpty(time)){
                                List<History> historiesRes = getHistory().getHistories();
                                historyBefore =  historiesRes.get(0);
                                sycnMail(historiesRes);
                                check = false;
                            }
                            Thread.sleep(500);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return time;
    }
    public long convertToMilliseconds(String time) {
        // Kiểm tra định dạng chuỗi thời gian
        if (time == null || !time.matches("\\d{2}:\\d{2}")) {
            throw new IllegalArgumentException("Invalid time format. Use 'mm:ss'.");
        }

        // Tách phút và giây
        String[] parts = time.split(":");
        int minutes = Integer.parseInt(parts[0]);
        int seconds = Integer.parseInt(parts[1]);

        // Chuyển đổi thành mili giây
        long milliseconds = (minutes * 60 * 1000) + (seconds * 1000);

        return milliseconds;
    }
    public static Long timeEnd = null;
    public void playVersion2() throws InterruptedException {
        while (isRun && getNewSession(true)){
            Integer selectD = 0;
            Integer selectC = 0;
            Integer selectT = 0;
            Integer selectK = 0;
            Long timeCurrent = convertToMilliseconds(getTimeCurrent());
            Long timeEnd = System.currentTimeMillis() + timeCurrent - (30 * 1000);
            listMapPlayer = new HashMap<>();
            while(System.currentTimeMillis() < timeEnd){
                Thread.sleep(1000);
            }
            if(CollectionUtils.isEmpty(listMapPlayer)){
                System.out.println("========playSet is empty");
                continue;
            }
            List<Player> players = new ArrayList<>();
            for (Map.Entry<Integer, Player> entry : listMapPlayer.entrySet()) {
                players.add(entry.getValue());
            }
            System.out.println("====================Player size(): " + players.size());
            for (int i =0; i < players.size(); i++){
                if(players.get(i).getSelection() == DctkUtils.DCTK.D){
                    selectD = selectD + players.get(i).getCoin();
                }else if(players.get(i).getSelection() == DctkUtils.DCTK.C){
                    selectC = selectC + players.get(i).getCoin();
                }else if(players.get(i).getSelection() == DctkUtils.DCTK.T){
                    selectT = selectT + players.get(i).getCoin();
                }else if(players.get(i).getSelection() == DctkUtils.DCTK.K){
                    selectK = selectK + players.get(i).getCoin();
                }
            }
            DCTK dcApi;
            DCTK tkApi;
            boolean checkDC;
            boolean checkTK;
            //dc
            if((selectD + DctkUtils.DCTK.coin) > (selectC + DctkUtils.DCTK.coin)){
                checkDC = false;
                dcApi = new DCTK(0, DctkUtils.DCTK.C, DctkUtils.DCTK.coin, 4);
            }else if((selectD + DctkUtils.DCTK.coin) < (selectC + DctkUtils.DCTK.coin)){
                checkDC = true;
                dcApi = new DCTK(0, DctkUtils.DCTK.D, DctkUtils.DCTK.coin, 4);
            }else{
                continue;
            }
            //tk
            if((selectT + DctkUtils.DCTK.coin) > (selectK + DctkUtils.DCTK.coin)){
                checkTK = false;
                tkApi = new DCTK(0, DctkUtils.DCTK.K, DctkUtils.DCTK.coin, 4);
            }else if((selectT + DctkUtils.DCTK.coin) < (selectK + DctkUtils.DCTK.coin)){
                checkTK = true;
                tkApi = new DCTK(0, DctkUtils.DCTK.T, DctkUtils.DCTK.coin , 4);
            }else{
                continue;
            }
            text = "";
            String textDc = "";
            String textTk = "";
            if(checkDC){
                textDc = "D: Play D with " + formatNumber(selectD) + " < " + formatNumber(selectC) + ", chenh lech: " + formatNumber(selectC -selectD);
            }else{
                textDc = "C: Play C with " + formatNumber(selectC) + " < " + formatNumber(selectD) + ", chenh lech: " + formatNumber(selectD -selectC);
            }
            if(checkTK){
                textTk = "T: Play T with " + formatNumber(selectT) + " < " + formatNumber(selectK) + ", chenh lech: " + formatNumber(selectK -selectT);
            }else{
                textTk = "K: Play K with " + formatNumber(selectK) + " < " + formatNumber(selectT) + ", chenh lech: " + formatNumber(selectT -selectK);
            }
            System.out.println(textDc);
            System.out.println(textTk);
            text = text + textDc +"\n" + textTk +"\n";
            ResponseDctk responseDc = callApi(dcApi);
            System.out.println("===================Response DC: " + responseDc.getMessage());
            Thread.sleep(3 * 500);
            ResponseDctk responseTk = callApi(tkApi);
            System.out.println("===================Response TK: " + responseTk.getMessage());
            listMapPlayer = new HashMap<>();
        }
    }
    static SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    public Boolean getNewSession(boolean isActive){
        if(isActive){
            try {
                List<History> historiesRes = getHistory().getHistories();
                String timeEnd = historiesRes.get(0).getStop();
                Date date = formatter.parse(timeEnd);
                if(System.currentTimeMillis() > date.getTime()){
                    return true;
                }
                Thread.sleep(500);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }
    public static String formatNumber(int number) {
        DecimalFormat df;

        if (number < 1000) {
            // Không cần định dạng cho số nhỏ hơn 1000
            return String.valueOf(number);
        } else if (number < 1_000_000) {
            // Định dạng với hàng nghìn
            df = new DecimalFormat("#,###");
        } else if (number < 1_000_000_000) {
            // Định dạng với hàng triệu
            df = new DecimalFormat("#,###.##M");
            return df.format(number / 1_000_000.0);
        } else {
            // Định dạng với hàng tỷ
            df = new DecimalFormat("#,###.##B");
            return df.format(number / 1_000_000_000.0);
        }

        return df.format(number);
    }
    public static String text = "";
    public void sycnMail(List<History> histories){
        history = histories.get(0);
        if(history != null){
            Integer idPrev =  history.getId();
            if(history.getResult_cd() == DctkUtils.DCTK.D){
                text = text + "win ===> D " + "\n";
            }else{
                text = text + "win ===> C " + "\n";
            }
            if(history.getResult_tk() == DctkUtils.DCTK.T){
                text = text + "win ===> T " + "\n";
            }else{
                text = text + "win ===> K " + "\n";
            }
            List<Balance> balances = getBalance().stream().
                    filter(item -> item.getNote().indexOf(idPrev.toString()) != -1)
                    .collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(balances)){
                int countSuccess = 0;
                for (Balance balance : balances) {
                    if(balance.getNote().indexOf("Trao thưởng phiên") != -1){
                        countSuccess++;
                        text = text + "Win: "+ countSuccess + ", cảm ơn bạn: "+ balance.toString() + "\n";
                    }else{
                        text = text + "Tham gia: " + balance.toString() + "\n";
                    }
                }
                if (countSuccess ==0){
                    text = text + "==============> Failure";
                }
                System.out.println(text);
                sendMail(text);
                text = "";
            }
        }

    }
    private static WebSocketClient client = null;
    public void connectSocket(){
        URI uri = null;
        try {
            uri = new URI("wss://api.dctk.me/ws");
            client = new WebSocketScraper(uri);
            client.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean isDisConnect(WebSocketClient webSocketClient){
        if(webSocketClient.isClosed()) return true;
        return false;
    }
}
