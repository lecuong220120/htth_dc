package com.example.demo.Service;

import com.example.demo.DTO.BalanceDTO;
import com.example.demo.DTO.HistoryDTO;
import com.example.demo.DTO.LoginDTO;
import com.example.demo.Obj.*;
import com.example.demo.Utils.DctkUtils;
import com.example.demo.websocket.WebSocketScraper;
import com.example.demo.websocket.WebSocketThreadNso;
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
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DctkService {
    private static boolean isRun = true;
    private RestTemplate restTemplate = new RestTemplate();
    private static String url = "https://api.dctk.me/api/home/history?page=1";
    private static String urlHistoryId = "https://api.dctk.me/api/home/history?page=";
    private static String urlDrawal = "https://api.dctk.me/api/user/withdrawal";
    private static String urlToken = "https://api.dctk.me/api/login";
    private static String urlPlay = "https://api.dctk.me/api/user/join";
    private static String urlBalance = "https://api.dctk.me/api/user/balance-history?page=1&limit=20";
    private static  String userName = "animehay";
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
    //list count statistic
    public static Integer moneyChangeDC = 0;
    public static Integer moneyChangeTK = 0;
    public static Integer statisticWinDC = 0;
    public static Integer statisticLoseDC = 0;
    public static Integer statisticWinTK = 0;
    public static Integer statisticLoseTK = 0;
    public static String playDC = "";
    public static String playTK = "";
    public static Map<Integer, Integer> statistic = new HashMap<>();
    //web socket
    public static Map<Integer, Player> listMapPlayer = new HashMap<>();
    public HistoryDTO getHistory(){
        ResponseEntity<HistoryDTO> response
                = restTemplate.getForEntity(url, HistoryDTO.class);
        return  response.getBody();
    }
    public List<HistoryDTO> getHistoryAll(){
        List<HistoryDTO> result = new ArrayList<>();
        for(int i =21; i >= 1; i--) {
            String urlApi = urlHistoryId + i;
            ResponseEntity<HistoryDTO> response
                    = restTemplate.getForEntity(urlApi, HistoryDTO.class);
            result.add(response.getBody());
        }

        return result;
    }
    public String getToken(){
        LoginDTO loginDTO = new LoginDTO(userName,  password);
        ResponseEntity<LoginDTO> response
                = restTemplate.postForEntity(urlToken, loginDTO, LoginDTO.class);
        return response.getBody().getJwt();
    }
    static int coinDcbefore = 0;
    static int coinTkbefore = 0;
    public void autoPlay(){
        try{
            Integer coinDc = 0;
            Integer coinTk = 0;
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
            coinDc = changeCoinDC(historyPlay, historyWin, DctkUtils.DCTK.coin);
            coinDcbefore = coinDc;
            coinTk = changeCoinTK(historyPlay, historyWin, DctkUtils.DCTK.coin);
            coinTkbefore = coinTk;
            Integer playDC = preditionDCTK(listDC, true, historiesRes, isChangeProcess1);
            Integer playTk = preditionDCTK(listTK, false, historiesRes, isChangeProcess1);
            historyPlay.clear();
            historyPlay.add(playDC);
            historyPlay.add(playTk);
            play(playDC, playTk, coinDc, coinTk);
        }catch (Exception e){
            System.out.printf("ERRORRRRRRRRRRRRRRR" + e);
        }

    }
    public Integer changeCoinDC(List<Integer> playSet, List<Integer> playWin,Integer coin){
        if(CollectionUtils.isEmpty(playSet) || CollectionUtils.isEmpty(playWin)) return 0;
        if(Objects.equals(playSet.get(0), playWin.get(0))){
            countDC++;
            text = text + "DC Win: " +countDC+ "\n";
        }
        if(!Objects.equals(playSet.get(0), playWin.get(0))){
            countDC = 0;
            text = text + "DC lose: " +countDC+ "\n";
        }
        if(countDC == 1){
            return coin * 3;
        }else if(countDC == 2){
            return coin * 2;
        }else if(countDC == 0){
            return coin;
        }else if(countDC == 3){
            return coin * 4;
        }else{
            Random random = new Random();
            int randomNumber = random.nextInt(7) + 4;
            text = text + "Random: " + randomNumber + "\n";
            return coinDcbefore * randomNumber;
        }
    }
    public Integer changeCoinTK(List<Integer> playSet, List<Integer> playWin, Integer coin){
        if(CollectionUtils.isEmpty(playSet) || CollectionUtils.isEmpty(playWin)) return 0;
        if(Objects.equals(playSet.get(1), playWin.get(1))){
            countTK++;
            text = text + "Tk Win: " +countTK + "\n";
        }
        if(!Objects.equals(playSet.get(1), playWin.get(1))){
            countTK = 0;
            text = text + "Tk lose: " +countTK+ "\n";
        }
        if(countTK == 1){
            return coin * 3;
        }else if(countTK == 2){
            return coin * 2;
        }else if(countTK == 0){
            return coin;
        }else if(countTK == 3){
            return coin * 4;
        }else{
            Random random = new Random();
            int randomNumber = random.nextInt(7) + 4;
            text = text + "Random: " + randomNumber + "\n";
            return randomNumber * coin;
        }
    }
    public int preditionDCTK(List<Integer> listInteger, boolean isDC, List<History> histories, boolean isChangProcessLogic1){
        if(isChangProcessLogic1){
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
            }
        }else{
            if (isDC){
                if(listInteger.equals(list1)){
                    return DctkUtils.DCTK.D;
                }else if(listInteger.equals(list2)){
                    return DctkUtils.DCTK.D;
                }else if(listInteger.equals(list3)){
                    return DctkUtils.DCTK.C;
                }else if(listInteger.equals(list4)){
                    return DctkUtils.DCTK.C;
                }else if(listInteger.equals(list5)){
                    return DctkUtils.DCTK.D;
                }else if(listInteger.equals(list6)){
                    return DctkUtils.DCTK.D;
                }else if(listInteger.equals(list7)){
                    return DctkUtils.DCTK.C;
                }else if (listInteger.equals(list8)){
                    return DctkUtils.DCTK.C;
                }
            }else{
                if(listInteger.equals(list9)){
                    return DctkUtils.DCTK.K;
                }else if(listInteger.equals(list10)){
                    return DctkUtils.DCTK.K;
                }else if(listInteger.equals(list11)){
                    return DctkUtils.DCTK.T;
                }else if(listInteger.equals(list12)){
                    return DctkUtils.DCTK.T;
                }else if(listInteger.equals(list13)){
                    return DctkUtils.DCTK.K;
                }else if(listInteger.equals(list14)){
                    return DctkUtils.DCTK.K;
                }else if(listInteger.equals(list15)){
                    return DctkUtils.DCTK.T;
                }else if (listInteger.equals(list16)){
                    return DctkUtils.DCTK.T;
                }
            }
        }
        System.out.println("Sai logic roi");
        return 0;
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
            playDC = dc == DctkUtils.DCTK.C ? "C" : "D";
            playTK = tk == DctkUtils.DCTK.T ? "T" : "K";
            DCTK dcApi = new DCTK(0, dc,  coinDC, 4);
            //tk
            DCTK tkApi = new DCTK(0, tk,  coinTK, 4);
            ResponseDctk responseDc = callApi(dcApi);
            System.out.println("===================Response DC: " + responseDc.getMessage());
            text = text + "===================Response DC: " +  responseDc.getMessage() + ", tham gia: " + formatNumber(coinDC);
            ResponseDctk responseTk = callApi(tkApi);
            text = text + "===================Response TK: " +  responseDc.getMessage() + ", tham gia: " + formatNumber(coinTK);
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
    public ResponseDctk callDrawalApi(Drawl drawl){
        HttpHeaders httpHeaders = new HttpHeaders();
        ResponseEntity<ResponseDctk> response = null;
        HttpEntity<?> requestEntity = null;
        try{
            httpHeaders.set("Authorization", "Bearer " + token);
            httpHeaders.set("Content-Type", "application/json");
            requestEntity = new HttpEntity<>(drawl, httpHeaders);
            response = restTemplate.exchange(urlDrawal, HttpMethod.POST, requestEntity, ResponseDctk.class);
        }catch (Exception e){
            token = getToken();
            httpHeaders.set("Authorization", "Bearer " + token);
            httpHeaders.set("Content-Type", "application/json");
            requestEntity = new HttpEntity<>(drawl, httpHeaders);
            response = restTemplate.exchange(urlDrawal, HttpMethod.POST, requestEntity, ResponseDctk.class);
        }
        sendMailDrawal("Tao don rut xu thanh cong!!! Xu rut: "+ drawl.getCoin() + "\n" + response.getBody().toString());
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
    public void sendMailDrawal(String text){
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
            message.setSubject("WithDrawal Successfully!!!");
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
                sycnMail1(history);
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
    public void change(){
        System.out.println("Before change: "+ isChangeProcessBigger);
        isChangeProcessBigger = !isChangeProcessBigger;
        System.out.println("After change: "+ isChangeProcessBigger);
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
    public static boolean isChangeProcessBigger = false;
    public static boolean isChangeProcess1 = true;
    public static int countIsChangeProcessBigger = 0;
    public static int countIsChangeProcess1 = 0;
    public static DCTK dcBefore;
    public static DCTK ktBefore;
    public static boolean activeLogicNew1 = true;
    public String getTimeHistory(String dateTimeString){
        // Define the formatter for the input string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        // Parse the string to get the LocalDateTime object
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);

        // Extract hour, minute, and second
        int hour = dateTime.getHour();
        int minute = dateTime.getMinute();
        int second = dateTime.getSecond();

        // Create the formatted time string
        String timeString = String.format("%02d:%02d:%02d", hour, minute, second);
        return timeString;
    }
    public static String playStringDc = "";
    public static String playStringTk = "";
    public static Boolean isPlayPrevious = false;
    public void playVersion3() throws InterruptedException {
        try{
            while (isRun){
                Integer selectD = 0;
                Integer selectC = 0;
                Integer selectT = 0;
                Integer selectK = 0;
                Integer selectU = 0;
                Integer selectQ = 0;
                //get time
                getTimeCurrent1();
                int minutes = timeSocket.getTime() / 60;
                int remainingSeconds = timeSocket.getTime() % 60;
                String timeString = String.format("%d:%02d", minutes, remainingSeconds);
                System.out.println("=============Time current: " + timeString);
                Long timeCurrent = Long.valueOf(timeSocket.getTime()) * 1000;
                Long timeEnd = System.currentTimeMillis() + timeCurrent ;
                timeEnd = timeEnd - (25 * 1000);
                connectSocket();
                while(System.currentTimeMillis() < timeEnd){
                    Thread.sleep(1000);
                }
                String textDc = "";
                String textTk = "";
                DCTK dcApi = null;
                DCTK tkApi = null;
                if(CollectionUtils.isEmpty(listMapPlayer)){
                    System.out.println("========playSet is empty");
                    continue;
                }
                History history1 =  getHistory().getHistories().get(0);
                List<Player> players = new ArrayList<>();
                for (Map.Entry<Integer, Player> entry : listMapPlayer.entrySet()) {
                    if(!StringUtils.isEmpty(entry.getValue().getTime())){
                        if(entry.getValue().getTime().compareTo(getTimeHistory(history1.getStop())) > 0){
                            players.add(entry.getValue());
                        }
                    }

                }
                int countD = 0;
                int countK = 0;
                int countC = 0;
                int countT = 0;
                int countQ = 0;
                int countU = 0;
                System.out.println("====================Player size(): " + players.size());
                Map<String, Integer> stringMapDc = new HashMap<>();
                Map<String, Integer> stringMapTk = new HashMap<>();
                for (int i =0; i < players.size(); i++){
                    if(players.get(i).getSelection() == DctkUtils.DCTK.D){
                        countD++;
                        selectD = selectD + players.get(i).getCoin();
                    }else if(players.get(i).getSelection() == DctkUtils.DCTK.C){
                        countC++;
                        selectC = selectC + players.get(i).getCoin();
                    }else if(players.get(i).getSelection() == DctkUtils.DCTK.T){
                        countT++;
                        selectT = selectT + players.get(i).getCoin();
                    }else if(players.get(i).getSelection() == DctkUtils.DCTK.K){
                        countK++;
                        selectK = selectK + players.get(i).getCoin();
                    }else if(players.get(i).getSelection() == DctkUtils.DCTK.U){
                        countU++;
                        selectU = selectU + players.get(i).getCoin();
                    }else if(players.get(i).getSelection() == DctkUtils.DCTK.Q){
                        selectQ = selectQ + players.get(i).getCoin();
                        countQ++;
                    }
                }

                if(activeLogicNew1){
                    Integer idPrev =  history1.getId();
                    int countSuccess = 0;
                    List<Balance> balances = getBalance().stream().
                            filter(item -> item.getNote().indexOf(idPrev.toString()) != -1)
                            .collect(Collectors.toList());
                    if(!CollectionUtils.isEmpty(balances)){
                        isPlayPrevious = true;
                        for (Balance balance : balances) {
                            if(balance.getNote().indexOf("Tham gia phiên") != -1){
                                text = text + balance.toString() + "\n";
                            }
                            if(balance.getNote().indexOf("Trao thưởng phiên") != -1){
                                countSuccess++;
                                if(balance.getAfter() > DctkUtils.DCTK.coinDrawl){
                                    Drawl drawl = new Drawl();
                                    drawl.setCharacter("tiktokab");
                                    drawl.setCoin(DctkUtils.DCTK.coinDrawl1);
                                    drawl.setPassword("admadm");
                                    drawl.setAccount("avatarlands");
                                    drawl.setIdServer("4");
                                    callDrawalApi(drawl);
                                }
                            }else{
                            }
                        }
                        if (countSuccess ==0 ){
                            text = text + "==============> Lose van truoc" + "\n";
                        }
                    }else{
                        isPlayPrevious = false;
                    }
                    User user = getUser();
                    Integer tong = selectD + selectQ + selectT + selectU + selectK + selectC;
                    text = text + "======> Tong C  = " + formatNumber(selectC) + ", Du tinh: " +  formatNumber(selectC * 2) + "\n"
                            + "======> Tong D  = " + formatNumber(selectD) + ", Du tinh: " +  formatNumber(selectD * 2) + "\n"
                            + "======> Tong K  = " + formatNumber(selectK) + ", Du tinh: " +  formatNumber(selectK * 2) + "\n"
                            + "======> Tong T  = " + formatNumber(selectT) + ", Du tinh: " +  formatNumber(selectT * 2) + "\n"
                            + "======> Tong  = " + formatNumber(tong) + "\n";
//                    int coinPlay = getCoinLogic1(DctkUtils.DCTK.range1,DctkUtils.DCTK.range2 );
                    int coinPlay = 3000000;
                    if(countSuccess != 0){
                        coinPlay = coinPlay + 500000;
                    }
                    String playString = "";
                    String playStringDc = "";
                    String playStringTk = "";
//                    if(selectD > selectC){
//                        if (selectC != 0){
//                            int check = selectD / selectC;
//                            if (check >= DctkUtils.DCTK.check && DctkUtils.DCTK.check1 >= check){
//                                playString = "C";
//                                text = text + "=>>> bien check = " + check + ", chon: C" + "\n" ;
//                            }else{
//                                text = text + "=>>> bien check = " + check + ", bo qua chon: C" + "\n" ;
//                            }
//                        }else{
//                            playString = "C";
//                            text = text + "=>>> select C = 0, chon: C "+ "\n";
//                        }
//
//                    }else{
//                        if (selectD != 0){
//                            int check = selectC / selectD;
//                            if (check >= DctkUtils.DCTK.check && DctkUtils.DCTK.check1 >= check){
//                                playString = "D";
//                                text = text + "=>>> bien check = " + check + ", chon: D" + "\n" ;
//                            }else{
//                                text = text + "=>>> bien check = " + check + ", bo qua chon: D" + "\n" ;
//                            }
//                        }else{
//                            playString = "D";
//                            text = text + "=>>> select D = 0, chon: D "+ "\n";
//                        }
//                    }
                    stringMapDc.put( "D", countD);
                    stringMapDc.put( "C", countC);
                    stringMapTk.put( "K", countK);
                    stringMapTk.put( "T", countT);
                    if(stringMapDc.get("D") > stringMapDc.get("C") + 1 ){
                        playString = "C";
                    }else if(stringMapDc.get("C") > stringMapDc.get("D") + 1){
                        playString = "D";
                    }else{
                        if(selectD > selectC + coinPlay){
                            playString = "C";
                        }else{
                            playString = "D";
                        }
                    }
                    playStringDc = playString;
                    if (Objects.equals(playString, "D")) {
                        playDC = "D";
                        dcApi = new DCTK(0, DctkUtils.DCTK.D, DctkUtils.DCTK.coinLogicNew + coinPlay, 4);
                        ResponseDctk responseDc = callApi(dcApi);
                        dcBefore = dcApi;
                        textDc = textDc + "===================Response DC: " + responseDc.getMessage()  + " ==> " + formatNumber(dcApi.getCoin()) + "\n";
                    } else if  (Objects.equals(playString, "C")) {
                        playDC = "C";
                        dcApi = new DCTK(0, DctkUtils.DCTK.C, DctkUtils.DCTK.coinLogicNew + coinPlay, 4);
                        ResponseDctk responseDc = callApi(dcApi);
                        dcBefore = dcApi;
                        textDc = textDc + "===================Response DC: " + responseDc.getMessage()  + " ==> " + formatNumber(dcApi.getCoin()) + "\n";
                    }
//                    if(selectT > selectK){
//                        if (selectK != 0){
//                            int check = selectT / selectK;
//                            if (check >= DctkUtils.DCTK.check && DctkUtils.DCTK.check1 >= check){
//                                playString = "K";
//                                text = text + "=>>> bien check = " + check + ", chon: K" + "\n" ;
//
//                            }else{
//                                text = text + "=>>> bien check = " + check + ", bo qua chon: K" + "\n" ;
//                            }
//                        }else{
//                            playString = "K";
//                            text = text + "=>>> select K = 0, chon: K "+ "\n";
//                        }
//
//                    }else{
//                        if (selectT != 0){
//                            int check = selectK / selectT;
//                            if (check >= DctkUtils.DCTK.check && DctkUtils.DCTK.check1 >= check){
//                                playString = "T";
//                                text = text + "=>>> bien check = " + check + ", chon: T" + "\n" ;
//                            }else{
//                                text = text + "=>>> bien check = " + check + ", bo qua chon: T" + "\n" ;
//                            }
//                        }else{
//                            playString = "T";
//                            text = text + "=>>> select T = 0, chon: T "+ "\n";
//                        }
//                    }
                    if(stringMapTk.get("T") > stringMapTk.get("K") + 1){
                        playString = "K";
                    }else if(stringMapTk.get("K") > stringMapTk.get("T") + 1){
                        playString = "T";
                    }else{
                        if(selectT > selectK + coinPlay){
                            playString = "K";
                        }else{
                            playString = "T";
                        }
                    }
                    playStringTk = playString;
                     if (Objects.equals(playString, "T")) {
                        playTK = "T";
                        tkApi = new DCTK(0, DctkUtils.DCTK.T, DctkUtils.DCTK.coinLogicNew + coinPlay, 4);
                        ResponseDctk responseTk = callApi(tkApi);
                        ktBefore = tkApi;
                        textTk = textTk + "===================Response TK: " + responseTk.getMessage() + " ==> " + formatNumber(tkApi.getCoin()) + "\n";
                    } else if (Objects.equals(playString, "K")){
                        playTK = "K";
                        tkApi = new DCTK(0, DctkUtils.DCTK.K, DctkUtils.DCTK.coinLogicNew + coinPlay, 4);
                        ResponseDctk responseTk = callApi(tkApi);
                        ktBefore = tkApi;
                        textTk = textTk + "===================Response TK: " + responseTk.getMessage() + " ==> " + formatNumber(tkApi.getCoin()) + "\n";
                    }
                    text = text + textDc;
                    text = text + textTk;
                    user = getUser();
                    if(user != null){
                        text = text + "SO DU SAU KHI THAM GIA VAN #"+ history1.getId() +": " + formatNumber(user.getBalances().get(0).getBalance()) + "\n";
                    }
                    listMapPlayer.clear();
                    listMapPlayer = new HashMap<>();
                    Thread.sleep(50 * 1000);
                    History history2 = getHistory().getHistories().get(0);
                    String resultDc = history2.getResult_cd() == DctkUtils.DCTK.C ? "C" : "D";
                    String resultTk = history2.getResult_tk() == DctkUtils.DCTK.T ? "T" : "K";
                    text = text + "*********************KET QUA VAN NAY**************************" + "\n";
                    if(!StringUtils.isEmpty(playStringDc)){
                        if(playStringDc.equals("C") && resultDc.equals("C")){
                            text =text + "Chuc mung ban da thang C \n";
                        }else if(playStringDc.equals("D") && resultDc.equals("D")){
                            text =text + "Chuc mung ban da thang D \n";
                        } else {
                            text =text + "Ban da thua: \n" + playStringDc;
                        }
                    }else{
                        text = text + "Khong tham gia DC \n" ;
                    }
                    if(!StringUtils.isEmpty(playStringDc)){
                        if(playStringTk.equals("T") && resultTk.equals("T")){
                            text =text + "Chuc mung ban da thang T \n";
                        }else if(playStringTk.equals("K") && resultTk.equals("K")){
                            text =text + "Chuc mung ban da thang K \n";
                        } else {
                            text =text + "Ban da thua: \n" + playStringDc;
                        }
                    }else{
                        text = text + "Khong tham gia TK \n" ;
                    }
                    if(user != null){
                        text = text + "SO DU HIEN TAI: " + formatNumber(user.getBalances().get(0).getBalance()) + "\n";
                    }
                    sendMail(text);
                    System.out.println(text);
                    text = "";
                }
            }
        }catch (Exception e){
            System.out.println(""+e.getMessage());
        }

    }

    public static String urlUserApi = "https://api.dctk.me/api/user";
    public User getUser(){
        HttpHeaders httpHeaders = new HttpHeaders();
        ResponseEntity<User> response = null;
        HttpEntity<String> requestEntity = null;
        try{
            httpHeaders.set("Authorization", "Bearer " + token);
            httpHeaders.set("Content-Type", "application/json");
            requestEntity = new HttpEntity<>(httpHeaders);
            response = restTemplate.exchange(urlUserApi, HttpMethod.GET, requestEntity, User.class);
        }catch (Exception e){
            token = getToken();
            httpHeaders.set("Authorization", "Bearer " + token);
            httpHeaders.set("Content-Type", "application/json");
            requestEntity = new HttpEntity<>(httpHeaders);
            response = restTemplate.exchange(urlUserApi, HttpMethod.GET, requestEntity, User.class);
        }
        return response.getBody();
    }
    public String getTimeCheck(long currentTimeMillis){

        // Create a Date object using the milliseconds
        Date date = new Date(currentTimeMillis);

        // Define the desired format (HH:mm:ss for hours:minutes:seconds)
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

        // Format the date to the desired string format
        String timeString = formatter.format(date);
        return timeString;
    }
    static SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    public Boolean getNewSession(boolean isActive){
        if(isActive){
            try {
                List<History> historiesRes = getHistory().getHistories();
                historyBefore = historiesRes.get(0);
                System.out.println("=====session historyBefore.getStop() " + historyBefore.getStop());
                String timeEnd = historyBefore.getStop();
                Date date = formatter.parse(timeEnd);
                if(System.currentTimeMillis() > date.getTime()){
                    System.out.println("===================> New Sessionnn <=========================");
                    DctkService.listMapPlayer.clear();
                    DctkService.listMapPlayer = new HashMap<>();
                    return true;

                }
                Thread.sleep(500);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }
    public String formatNumber(int number) {
        DecimalFormat df;
        boolean isNegative = number < 0;
        int absNumber = Math.abs(number);

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
    public static String text = "";
    public static String textStatic = "";
    public static List<StatisticObj> lstStatisticDc = new ArrayList<>();
    public static List<StatisticObj> lstStatisticTk = new ArrayList<>();
    public void sycnMail1(History history){
        if(history != null){
            Integer idPrev =  history.getId();
            if(history.getResult_cd() == DctkUtils.DCTK.D){
                if(Objects.equals(playDC, "D")){
                    statisticWinDC = statisticWinDC + 1;
                }else if(Objects.equals(playDC, "C")){
                    statisticLoseDC = statisticLoseDC + 1;
                }
                text = text + "Ket qua DC: D " + "\n";
            }else{
                if(Objects.equals(playTK, "C")){
                    statisticWinDC = statisticWinDC + 1;
                }else  if(Objects.equals(playDC, "D")){
                    statisticLoseDC = statisticLoseDC + 1;
                }
                text = text + "Ket qua DC: C " + "\n";
            }
            if(history.getResult_tk() == DctkUtils.DCTK.T){
                if(Objects.equals(playTK, "T")){
                    statisticWinTK = statisticWinTK + 1;
                }else if(Objects.equals(playTK, "K")){
                    statisticLoseTK = statisticLoseTK + 1;
                }
                text = text + "Ket qua TK: T " + "\n";
            }else{
                if(Objects.equals(playTK, "K")){
                    statisticWinTK = statisticWinTK + 1;
                }else if(Objects.equals(playTK, "T")){
                    statisticLoseTK = statisticLoseTK + 1;
                }
                text = text + "Ket qua TK: K " + "\n";
            }
            playTK = "";
            playDC = "";
            List<Balance> balances = getBalance().stream().
                    filter(item -> item.getNote().indexOf(idPrev.toString()) != -1)
                    .collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(balances)){
                int countSuccess = 0;
                int beforeChange = 0;
                int afterChange = 0;
                for (Balance balance : balances) {
                    if(balance.getNote().indexOf("Tham gia phiên") != -1){
                        beforeChange = beforeChange + balance.getChange();
                    }
                    if(balance.getNote().indexOf("Trao thưởng phiên") != -1){
                        afterChange = afterChange + balance.getChange();
                        countSuccess++;
                        text = text + "Win: "+ countSuccess + balance.toString() + "\n";
                    }else{
                    }
                }
                if(afterChange - beforeChange < 0){
                    countIsChangeProcess1++;
                }else{
                    countIsChangeProcess1 = 0;
                }
                if(countIsChangeProcess1 > 2){
                    isChangeProcess1 = !isChangeProcess1;
                    countIsChangeProcess1 = 0;
                    text = text + " Van sau doi trang thai: " + isChangeProcess1 ;
                }
                if (countSuccess ==0){
                    text = text + "==============> Failure";
                }
                text = text + "\n\n\t********Statistic win DC: " + statisticWinDC + ", lose DC: " + statisticLoseDC
                        + "\n\n\t********Statistic win TK: " + statisticWinTK + ", lose Tk: " + statisticLoseTK;
                //                StatisticObj statisticObj = getStatisticObj();
                //                statisticObjs.add(statisticObj);
                sendMail(text);
                text = "";
            }
            //statistic dc
            StatisticObj statisticDc = new StatisticObj();
            statisticDc.setMoney(moneyChangeDC);
            statisticDc.setType(true);
            statisticDc.setCountLose(statisticLoseDC);
            statisticDc.setCountWin(statisticWinDC);
            statisticDc.setResult("" + history.getResult_cd());
            lstStatisticDc.add(statisticDc);
            //statistic tk
            StatisticObj statisticTk = new StatisticObj();
            statisticTk.setMoney(moneyChangeTK);
            statisticTk.setType(false);
            statisticTk.setCountLose(statisticLoseTK);
            statisticTk.setCountWin(statisticWinTK);
            statisticTk.setResult("" + history.getResult_tk());
            lstStatisticTk.add(statisticTk);
        }

    }
    private static WebSocketClient client = null;
    public void connectSocket(){
        if(client != null){
            if(!isDisConnect(client)){
                client.close();
            }
        }
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
    public static String findUniqueMostFrequentElement(List<String> list) {
        Map<String, Integer> frequencyMap = new HashMap<>();

        // Count the frequency of each element
        for (String element : list) {
            frequencyMap.put(element, frequencyMap.getOrDefault(element, 0) + 1);
        }

        // Find the maximum frequency and count how many elements have that frequency
        String mostFrequentElement = "";
        int maxFrequency = 0;
        int countMaxFrequency = 0;

        for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
            int frequency = entry.getValue();
            if (frequency > maxFrequency) {
                mostFrequentElement = entry.getKey();
                maxFrequency = frequency;
                countMaxFrequency = 1; // Reset count for new max frequency
            } else if (frequency == maxFrequency) {
                countMaxFrequency++; // Increment count for tie
            }
        }

        // Check if there is only one element with the maximum frequency
        return (countMaxFrequency == 1) ? mostFrequentElement : "";
    }
    public static Integer getCoinLogic(){
        Random random = new Random();

        // Xác định khoảng giá trị
        int lowerBound = 3000000;
        int upperBound = 3200000;

        int randomNumber = lowerBound + random.nextInt(upperBound - lowerBound + 1);
        return randomNumber;
    }
    public static int getCoinLogic1(int min, int max) {
        Random rand = new Random();
        int randomNumber;

        do {
            randomNumber = rand.nextInt((max - min) / 100000 + 1) * 100000 + min;
        } while (randomNumber % 100000 != 0);

        return randomNumber;
    }
}
