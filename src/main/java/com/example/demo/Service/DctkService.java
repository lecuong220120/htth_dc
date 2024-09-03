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
import java.util.*;
import java.util.stream.Collectors;

import static com.example.demo.Utils.DctkUtils.DCTK.coinAdd;

@Service
public class DctkService {
    private static boolean isRun = true;
    private RestTemplate restTemplate = new RestTemplate();
    private static String url = "https://api.dctk.me/api/home/history?page=1";
    private static String urlDrawal = "https://api.dctk.me/api/user/withdrawal";
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
    static int coinBefore = 0;
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
        sendMail("Tao don rut xu thanh cong!!! Xu rut: "+ drawl.getCoin() + "\n" + response.getBody().toString());
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
    public static boolean isFirstRun = false;
    public String getTimeCurrent(){
        String url = "https://dctk.me/";
        String time = null;
        try {
            String serviceUrl = "http://localhost:3000/fetch-html?url=" + URLEncoder.encode(url, "UTF-8");
            String checkString = "<strong class=\"text-[#333333]\">Thời gian:</strong></div><div class=\"col-span-2 text-center\"><span class=\"text-[#333333]\">";
            boolean check = true;
            CloseableHttpClient httpClient = HttpClients.createDefault();
            Integer count = 0;
            while (check){
                try {
                    HttpGet request = new HttpGet(serviceUrl);
                    try (CloseableHttpResponse response = httpClient.execute(request)) {
                        String html = EntityUtils.toString(response.getEntity());
                        if(html.indexOf(checkString) != -1){
                            time = html.substring(html.indexOf(checkString) + 121, html.indexOf(checkString) + 121 + 5);
                            if(isDisConnect(client)){
                                while (isDisConnect(client)){
                                    sendMail("Socket is disconnect. ReConnect socket");
                                    connectSocket();
                                    Thread.sleep(1000);
                                }
                            }
                            System.out.println("Time current " + count++ +": "+ time);
                            if (!Objects.equals(time, "02:00") && !StringUtils.isEmpty(time)){
                                System.out.println("Time current: "+ time);
                                check = false;
//                                sycnMail(historyBefore);
                                moneyChangeDC = 0;
                                moneyChangeTK = 0;
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
    public static TimeSocket timeSocket = null;
    public static WebSocketThreadNso clientNso = null;

    public void getTimeCurrent1(){
        try {
            timeSocket = null;
            URI uri = new URI("wss://game-server.nsocltx.com/socket.io/?EIO=3&transport=websocket");
            clientNso = new WebSocketThreadNso(uri);
            clientNso.connect();
            if(clientNso.isClosed()){
                System.out.println("========retry connected");
                clientNso.connect();
            }
            boolean check = true;
            int count = 0;
            while (check){
                if(timeSocket == null || timeSocket.getTime() == 120 || timeSocket.getTime() < 25){
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
    public static boolean isPlayDc = true;
    public static boolean isPlayTk = true;
    public static boolean isChangeProcessBigger = false;
    public static boolean isChangeProcess1 = true;
    public static int countIsChangeProcessBigger = 0;
    public static int countIsChangeProcess1 = 0;
    public static DCTK dcBefore;
    public static DCTK ktBefore;
    public static boolean checkDc;
    public static boolean checkTk;
    public static boolean activeLogicNew = false;
    public static boolean activeLogicNew1 = true;
    public static List<String> DQT = new ArrayList<>(List.of("D", "T", "Q"));
    public static List<String> UCK = new ArrayList<>(List.of("C", "K", "U"));
    public static List<String> CT = new ArrayList<>(List.of("C", "T"));
    public static List<String> DK = new ArrayList<>(List.of("D", "K"));
    public void playVersion2() throws InterruptedException {
        try{
            while (isRun){
                Integer selectD = 0;
                Integer selectC = 0;
                Integer selectT = 0;
                Integer selectK = 0;
                Integer selectU = 0;
                Integer selectQ = 0;

                List<String> listPredictMost = new ArrayList<>();
                if(!getNewSession(true)){
                    continue;
                }
                Long timeCurrent = convertToMilliseconds(getTimeCurrent());
                Long timeEnd = System.currentTimeMillis() + timeCurrent - (26 * 1000);
                while(System.currentTimeMillis() < timeEnd){
                    Thread.sleep(500);
                }
                String textDc = "";
                String textTk = "";
                DCTK dcApi = null;
                DCTK tkApi = null;
                if(CollectionUtils.isEmpty(listMapPlayer)){
                    System.out.println("========playSet is empty");
                    continue;
                }
                List<Player> players = new ArrayList<>();
                for (Map.Entry<Integer, Player> entry : listMapPlayer.entrySet()) {
                    if(WebSocketScraper.isBefore(entry.getValue().getTime(), DctkService.historyBefore)){
                        players.add(entry.getValue());
//                        System.out.println("player: " + entry.getValue().getName() + ", coin: "+ entry.getValue().getCoin());
                    }
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
                    }else if(players.get(i).getSelection() == DctkUtils.DCTK.U){
                        selectU = selectU + players.get(i).getCoin();
                    }else if(players.get(i).getSelection() == DctkUtils.DCTK.Q){
                        selectQ = selectQ + players.get(i).getCoin();
                    }
                }
                System.out.println("==========Active logic new :" + activeLogicNew);

                if(activeLogicNew1){
                    Integer idPrev =  getHistory().getHistories().get(0).getId();
                    int countSuccess = 0;
                    List<Balance> balances = getBalance().stream().
                            filter(item -> item.getNote().indexOf(idPrev.toString()) != -1)
                            .collect(Collectors.toList());
                    if(!CollectionUtils.isEmpty(balances)){
                        for (Balance balance : balances) {
                            if(balance.getNote().indexOf("Tham gia phiên") != -1){
                            }
                            if(balance.getNote().indexOf("Trao thưởng phiên") != -1){
                                countSuccess++;
                                text = text + balance.toString() + "\n";
                                if(balance.getAfter() > DctkUtils.DCTK.coinDrawl){
                                    Drawl drawl = new Drawl();
                                    drawl.setCharacter("tiktokab");
                                    drawl.setCoin(20000000);
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
                    }
                    List<String> strSum = new ArrayList<>();
                    Integer tongDQT = selectD + selectQ + selectT;
                    Integer tongWinDQT = selectD * 2 + selectQ * 3 + selectT * 2;
                    Integer tongUCK = selectU + selectC + selectK;
                    Integer tongWinUCK = selectU * 3 + selectC * 2+ selectK * 2;
                    Integer tongDK = selectD + selectK;
                    Integer tongWinDK = selectD * 2 + selectK * 2;
                    Integer tongCT = selectC + selectT ;
                    Integer tongWinCT = selectC * 2 + selectT * 2;
                    Integer tong = selectD + selectQ + selectT + selectU + selectK + selectC;

//                    System.out.println("======> Tong (D + T + Q) = " + formatNumber(tongDQT) + ", du tinh: " + formatNumber(tongWinDQT));
//                    System.out.println("======> Tong (D + K) = " + formatNumber(tongDK) + ", du tinh: " + formatNumber(tongWinDK));
//                    System.out.println("======> Tong (C + K + U) = " + formatNumber(tongUCK) + ", du tinh: " + formatNumber(tongWinUCK));
//                    System.out.println("======> Tong (C + T) = " + formatNumber(tongCT) + ", du tinh: " + formatNumber(tongWinCT));
                    System.out.println("======> Tong C  = " + formatNumber(selectC) + ", Du tinh: " +  formatNumber(selectC * 2));
                    System.out.println("======> Tong D  = " + formatNumber(selectD) + ", Du tinh: " +  formatNumber(selectD * 2));
                    System.out.println("======> Tong K  = " + formatNumber(selectK) + ", Du tinh: " +  formatNumber(selectK * 2));
                    System.out.println("======> Tong T  = " + formatNumber(selectT) + ", Du tinh: " +  formatNumber(selectT * 2));
                    System.out.println("======> Tong  = " + formatNumber(tong) );
                    text = text + "======> Tong C  = " + formatNumber(selectC) + ", Du tinh: " +  formatNumber(selectC * 2) + "\n"
                            + "======> Tong D  = " + formatNumber(selectD) + ", Du tinh: " +  formatNumber(selectD * 2) + "\n"
                            + "======> Tong K  = " + formatNumber(selectK) + ", Du tinh: " +  formatNumber(selectK * 2) + "\n"
                            + "======> Tong T  = " + formatNumber(selectT) + ", Du tinh: " +  formatNumber(selectT * 2) + "\n"
                            + "======> Tong  = " + formatNumber(tong);
                    List<Integer> listSum = new ArrayList<>(List.of(selectC, selectD, selectT, selectK));
                    Collections.sort(listSum);
                    if(selectD == 0){
                        dcApi = new DCTK(0, DctkUtils.DCTK.D, 3000000, 4);
                        ResponseDctk responseDc = callApi(dcApi);
                        dcBefore = dcApi;
                        textDc = textDc + "===================Response DC: " + responseDc.getMessage()  + " ==> " + formatNumber(dcApi.getCoin()) + "\n";
                        text = text + "selectD = 0, play D" + "\n";
                        text = text + textDc + "\n";
                    }
                    if(selectC == 0){
                        dcApi = new DCTK(0, DctkUtils.DCTK.C, 3000000, 4);
                        ResponseDctk responseDc = callApi(dcApi);
                        dcBefore = dcApi;
                        textDc = textDc + "===================Response DC: " + responseDc.getMessage()  + " ==> " + formatNumber(dcApi.getCoin()) + "\n";
                        text = text + "selectC = 0, play C" + "\n";
                        text = text + textDc + "\n";
                    }
                    if(selectT == 0){
                        tkApi = new DCTK(0, DctkUtils.DCTK.T, 3000000, 4);
                        ResponseDctk responseTk = callApi(tkApi);
                        ktBefore = tkApi;
                        textTk = textTk + "===================Response TK: " + responseTk.getMessage() + " ==> " + formatNumber(tkApi.getCoin()) + "\n";
                        text = text + "selectT = 0, play T" + "\n";
                        text = text + textTk + "\n";
                    }
                    if(selectK == 0){
                        tkApi = new DCTK(0, DctkUtils.DCTK.K, 3000000, 4);
                        ResponseDctk responseTk = callApi(tkApi);
                        ktBefore = tkApi;
                        textTk = textTk + "===================Response TK: " + responseTk.getMessage() + " ==> " + formatNumber(tkApi.getCoin()) + "\n";
                        text = text + "selectK = 0, play K" + "\n";
                        text = text + textTk + "\n";
                    }
                    int playSelect =listSum.get(1);
                    int checkSelect =listSum.get(2);
                    int coinPlay = genCoin(playSelect, checkSelect);
                    String playString = "";
                    if(playSelect == selectC){
                        playString = "C";
                    }else if(playSelect == selectD){
                        playString = "D";
                    }else if(playSelect == selectT){
                        playString = "T";
                    }else if(playSelect == selectK){
                        playString = "K";
                    }
                    boolean activeCount = false;
                    if(countSuccess == 0){
                        activeCount = true;
                    }

//                    text = text + "======> Tong (D + T + Q) = " + formatNumber(tongDQT) + ", du tinh: " + formatNumber(tongWinDQT) + "\n"
//                            + "======> Tong (D + K) = " + formatNumber(tongDK) + ", du tinh: " + formatNumber(tongWinDK) + "\n"
//                            + "======> Tong (C + K + U) = " + formatNumber(tongUCK) + ", du tinh: " + formatNumber(tongWinUCK) + "\n"
//                            + "======> Tong (C + T) = " + formatNumber(tongCT) + ", du tinh: " + formatNumber(tongWinCT) + "\n"
//                            + "======> Tong  = " + formatNumber(tong) + "\n"
//                            + "============Predict play: "+ playString + "\n";
                    if (Objects.equals(playString, "D")) {
                        playDC = "D";
                        dcApi = new DCTK(0, DctkUtils.DCTK.D, DctkUtils.DCTK.coinLogicNew + coinPlay, 4);
                        ResponseDctk responseDc = callApi(dcApi);
                        dcBefore = dcApi;
                        textDc = textDc + "===================Response DC: " + responseDc.getMessage()  + " ==> " + formatNumber(dcApi.getCoin()) + "\n";
                        text = text + textDc + "\n";
                    } else if (Objects.equals(playString, "C")) {
                        playDC = "C";
                        dcApi = new DCTK(0, DctkUtils.DCTK.C, DctkUtils.DCTK.coinLogicNew + coinPlay, 4);
                        ResponseDctk responseDc = callApi(dcApi);
                        dcBefore = dcApi;
                        textDc = textDc + "===================Response DC: " + responseDc.getMessage()  + " ==> " + formatNumber(dcApi.getCoin()) + "\n";
                        text = text + textDc + "\n";
                    } else if (Objects.equals(playString, "T")) {
                        playTK = "T";
                        tkApi = new DCTK(0, DctkUtils.DCTK.T, DctkUtils.DCTK.coinLogicNew + coinPlay, 4);
                        ResponseDctk responseTk = callApi(tkApi);
                        ktBefore = tkApi;
                        textTk = textTk + "===================Response TK: " + responseTk.getMessage() + " ==> " + formatNumber(tkApi.getCoin()) + "\n";
                        text = text + textTk + "\n";
                    } else if (Objects.equals(playString, "K")) {
                        playTK = "K";
                        tkApi = new DCTK(0, DctkUtils.DCTK.K, DctkUtils.DCTK.coinLogicNew + coinPlay, 4);
                        ResponseDctk responseTk = callApi(tkApi);
                        ktBefore = tkApi;
                        textTk = textTk + "===================Response TK: " + responseTk.getMessage() + " ==> " + formatNumber(tkApi.getCoin()) + "\n";
                        text = text + textTk + "\n";
                    } else {
                        Thread.sleep(30 * 1000);
                        sendMail("Next continue");
                        continue;
                    }
                    sendMail(text);
                    System.out.println(text);
                    text = "";
                    Thread.sleep(30 * 1000);
                    continue;
                }

                if(activeLogicNew){
                    Integer idPrev =  getHistory().getHistories().get(0).getId();
                    int countSuccess = 0;
                    List<Balance> balances = getBalance().stream().
                            filter(item -> item.getNote().indexOf(idPrev.toString()) != -1)
                            .collect(Collectors.toList());
                    if(!CollectionUtils.isEmpty(balances)){
                        for (Balance balance : balances) {
                            if(balance.getNote().indexOf("Tham gia phiên") != -1){
                            }
                            if(balance.getNote().indexOf("Trao thưởng phiên") != -1){
                                countSuccess++;
                                text = text + "Win van truoc: " + balance.toString() + "\n";
                            }else{
                            }
                        }
                        if (countSuccess ==0 ){
                            text = text + "==============> Lose van truoc" + "\n";
                        }
                    }
                    List<String> strSum = new ArrayList<>();
                    Integer tongDQT = selectD + selectQ + selectT;
                    Integer tongWinDQT = selectD * 2 + selectQ * 3 + selectT * 2;
                    Integer tongUCK = selectU + selectC + selectK;
                    Integer tongWinUCK = selectU * 3 + selectC * 2+ selectK * 2;
                    Integer tongDK = selectD + selectK;
                    Integer tongWinDK = selectD * 2 + selectK * 2;
                    Integer tongCT = selectC + selectT ;
                    Integer tongWinCT = selectC * 2 + selectT * 2;
                    Integer tong = selectD + selectQ + selectT + selectU + selectK + selectC;

//                    System.out.println("======> Tong (D + T + Q) = " + formatNumber(tongDQT) + ", du tinh: " + formatNumber(tongWinDQT));
//                    System.out.println("======> Tong (D + K) = " + formatNumber(tongDK) + ", du tinh: " + formatNumber(tongWinDK));
//                    System.out.println("======> Tong (C + K + U) = " + formatNumber(tongUCK) + ", du tinh: " + formatNumber(tongWinUCK));
//                    System.out.println("======> Tong (C + T) = " + formatNumber(tongCT) + ", du tinh: " + formatNumber(tongWinCT));
                    System.out.println("======> Tong C  = " + formatNumber(selectC) + ", Du tinh: " +  formatNumber(selectC * 2));
                    System.out.println("======> Tong D  = " + formatNumber(selectD) + ", Du tinh: " +  formatNumber(selectD * 2));
                    System.out.println("======> Tong K  = " + formatNumber(selectK) + ", Du tinh: " +  formatNumber(selectK * 2));
                    System.out.println("======> Tong T  = " + formatNumber(selectT) + ", Du tinh: " +  formatNumber(selectT * 2));
                    System.out.println("======> Tong  = " + formatNumber(tong) );

                    List<Integer> listSum = new ArrayList<>(List.of(tongWinDK, tongWinCT, tongWinDQT, tongWinUCK));
                    Collections.sort(listSum);
                    List<Integer> listSumSort = new ArrayList<>(List.of(listSum.get(0), listSum.get(1)));
                    int countsize = 0;
                    if(selectD == 0){
                        dcApi = new DCTK(0, DctkUtils.DCTK.D, 3000000, 4);
                        ResponseDctk responseDc = callApi(dcApi);
                        dcBefore = dcApi;
                        textDc = textDc + "===================Response DC: " + responseDc.getMessage()  + " ==> " + formatNumber(dcApi.getCoin()) + "\n";
                        text = text + "selecctD = 0, play D" + "\n";
                        text = text + textDc + "\n";
                    }
                    if(selectC == 0){
                        dcApi = new DCTK(0, DctkUtils.DCTK.C, 3000000, 4);
                        ResponseDctk responseDc = callApi(dcApi);
                        dcBefore = dcApi;
                        textDc = textDc + "===================Response DC: " + responseDc.getMessage()  + " ==> " + formatNumber(dcApi.getCoin()) + "\n";
                        text = text + "selecctC = 0, play C" + "\n";
                        text = text + textDc + "\n";
                    }
                    if(selectT == 0){
                        tkApi = new DCTK(0, DctkUtils.DCTK.T, 3000000, 4);
                        ResponseDctk responseTk = callApi(tkApi);
                        ktBefore = tkApi;
                        textTk = textTk + "===================Response TK: " + responseTk.getMessage() + " ==> " + formatNumber(tkApi.getCoin()) + "\n";
                        text = text + "selecctT = 0, play T" + "\n";
                        text = text + textTk + "\n";
                    }
                    if(selectK == 0){
                        tkApi = new DCTK(0, DctkUtils.DCTK.K, 3000000, 4);
                        ResponseDctk responseTk = callApi(tkApi);
                        ktBefore = tkApi;
                        textTk = textTk + "===================Response TK: " + responseTk.getMessage() + " ==> " + formatNumber(tkApi.getCoin()) + "\n";
                        text = text + "selecctK = 0, play K" + "\n";
                        text = text + textTk + "\n";
                    }
                    if(tong > tongWinDQT && listSumSort.contains(tongWinDQT)){
                        countsize++;
                        strSum.addAll(DQT);
                        System.out.println("Add tongWinDQT");
                    }
                    if(tong > tongWinUCK && listSumSort.contains(tongWinUCK)){
                        countsize++;
                        strSum.addAll(UCK);
                        System.out.println("Add tongWinUCK");
                    }
                    if(tong > tongWinCT && listSumSort.contains(tongWinCT)){
                        countsize++;
                        System.out.println("Add tongWinCT");
                        strSum.addAll(CT);
                    }
                    if(tong >tongWinDK && listSumSort.contains(tongWinDK)){
                        countsize++;
                        System.out.println("Add tongWinDK");
                        strSum.addAll(DK);
                    }
                    String playString = findUniqueMostFrequentElement(strSum);
                    if(countsize == 1 && StringUtils.isEmpty(playString)){
                        playString = strSum.get(0);
                    }
                    System.out.println("============Predict play: "+ playString);
                    text = text + "======> Tong (D + T + Q) = " + formatNumber(tongDQT) + ", du tinh: " + formatNumber(tongWinDQT) + "\n"
                            + "======> Tong (D + K) = " + formatNumber(tongDK) + ", du tinh: " + formatNumber(tongWinDK) + "\n"
                            + "======> Tong (C + K + U) = " + formatNumber(tongUCK) + ", du tinh: " + formatNumber(tongWinUCK) + "\n"
                            + "======> Tong (C + T) = " + formatNumber(tongCT) + ", du tinh: " + formatNumber(tongWinCT) + "\n"
                            + "======> Tong  = " + formatNumber(tong) + "\n"
                            + "============Predict play: "+ playString + "\n";
                    int coinPlay = getCoinLogic();
                    if(countSuccess == 0){
                        coinPlay = 100000;
                    }
                    if (Objects.equals(playString, "D")) {
                        playDC = "D";
                        dcApi = new DCTK(0, DctkUtils.DCTK.D, DctkUtils.DCTK.coinLogicNew + coinPlay, 4);
                        ResponseDctk responseDc = callApi(dcApi);
                        dcBefore = dcApi;
                        textDc = textDc + "===================Response DC: " + responseDc.getMessage()  + " ==> " + formatNumber(dcApi.getCoin()) + "\n";
                        text = text + textDc + "\n";
                    } else if (Objects.equals(playString, "C")) {
                        playDC = "C";
                        dcApi = new DCTK(0, DctkUtils.DCTK.C, DctkUtils.DCTK.coinLogicNew + coinPlay, 4);
                        ResponseDctk responseDc = callApi(dcApi);
                        dcBefore = dcApi;
                        textDc = textDc + "===================Response DC: " + responseDc.getMessage()  + " ==> " + formatNumber(dcApi.getCoin()) + "\n";
                        text = text + textDc + "\n";
                    } else if (Objects.equals(playString, "T")) {
                        playTK = "T";
                        tkApi = new DCTK(0, DctkUtils.DCTK.T, DctkUtils.DCTK.coinLogicNew + coinPlay, 4);
                        ResponseDctk responseTk = callApi(tkApi);
                        ktBefore = tkApi;
                        textTk = textTk + "===================Response TK: " + responseTk.getMessage() + " ==> " + formatNumber(tkApi.getCoin()) + "\n";
                        text = text + textTk + "\n";
                    } else if (Objects.equals(playString, "K")) {
                        playTK = "K";
                        tkApi = new DCTK(0, DctkUtils.DCTK.K, DctkUtils.DCTK.coinLogicNew + coinPlay, 4);
                        ResponseDctk responseTk = callApi(tkApi);
                        ktBefore = tkApi;
                        textTk = textTk + "===================Response TK: " + responseTk.getMessage() + " ==> " + formatNumber(tkApi.getCoin()) + "\n";
                        text = text + textTk + "\n";
                    } else {
                        Thread.sleep(30 * 1000);
                        sendMail("Next continue");
                        continue;
                    }
                    sendMail(text);
                    System.out.println(text);
                    text = "";
                    Thread.sleep(30 * 1000);
                    continue;
                }



                //dc
                System.out.println("===========isChangeProcessBigger: " + isChangeProcessBigger);
                Integer compareMoneyChangeDC = (selectC -selectD) > 0 ? selectC - selectD : selectD -selectC;
                Integer compareMoneyChangeTK = (selectK -selectT) > 0 ? selectK - selectT : selectT -selectK;
                if(selectD> selectC){
                    if(isChangeProcessBigger){
                        playDC = "D";
                        textDc = textDc + "Bigger => D > C chenh lech: " + formatNumber(compareMoneyChangeDC) + "===> predict: D\n";
                        dcApi = new DCTK(0, DctkUtils.DCTK.D, DctkUtils.DCTK.coin, 4);
                    }else{
                        playDC = "C";
                        textDc = textDc + "Smaller => D > C chenh lech: " + formatNumber(compareMoneyChangeDC) + "===> predict: C\n";
                        dcApi = new DCTK(0, DctkUtils.DCTK.C, DctkUtils.DCTK.coin, 4);
                    }
                }else if(selectD  < selectC ){
                    if(isChangeProcessBigger){
                        playDC = "C";
                        textDc = textDc + "Bigger => C > D chenh lech: " + formatNumber(compareMoneyChangeDC) + "===> predict: C\n";
                        dcApi = new DCTK(0, DctkUtils.DCTK.C, DctkUtils.DCTK.coin, 4);
                    }else{
                        playDC = "D";
                        textDc = textDc + "Smaller => C > D chenh lech: " + formatNumber(compareMoneyChangeDC) + "===> predict: D\n";
                        dcApi = new DCTK(0, DctkUtils.DCTK.D, DctkUtils.DCTK.coin, 4);
                    }
                }else{
                    continue;
                }
                //tk
                if(selectT > selectK ){
                    if(isChangeProcessBigger){
                        playTK = "T";
                        textDc = textDc + "Bigger => T > K chenh lech: " + formatNumber(compareMoneyChangeTK) + "===> predict: T\n";
                        tkApi = new DCTK(0, DctkUtils.DCTK.T, DctkUtils.DCTK.coin, 4);
                    }else{
                        playTK = "K";
                        textDc = textDc + "Smaller => T > K chenh lech: " + formatNumber(compareMoneyChangeTK) + "===> predict: K\n";
                        tkApi = new DCTK(0, DctkUtils.DCTK.K, DctkUtils.DCTK.coin, 4);
                    }
                }else if(selectT < selectK){
                    if(isChangeProcessBigger){
                        playTK = "K";
                        textDc = textDc + "Bigger => K > T chenh lech: " + formatNumber(compareMoneyChangeTK) + "===> predict: K\n";
                        tkApi = new DCTK(0, DctkUtils.DCTK.K, DctkUtils.DCTK.coin , 4);
                    }else{
                        playTK = "T";
                        textDc = textDc + "Smaller => K > T chenh lech: " + formatNumber(compareMoneyChangeTK) + "===> predict: T\n";
                        tkApi = new DCTK(0, DctkUtils.DCTK.T, DctkUtils.DCTK.coin , 4);
                    }
                }else{
                    continue;
                }
                text = "";
                moneyChangeDC = selectC -selectD;
                moneyChangeTK = selectK -selectT;

                Integer rangeDC = 3;
                Integer rangeTk = 3;
                if(compareMoneyChangeDC > compareMoneyChangeTK){
                    rangeDC = 1;
                }
                if(compareMoneyChangeTK > compareMoneyChangeDC){
                    rangeTk = 1;
                }
                Integer coinPlayDc = compareMoneyChangeDC < DctkUtils.DCTK.rangePlay ? 200000 : (Integer) ((compareMoneyChangeDC * 20) / 1000);
                Integer coinPlayTk = compareMoneyChangeTK < DctkUtils.DCTK.rangePlay ? 200000 : (Integer)((compareMoneyChangeTK * 20) / 1000);

                coinPlayDc = coinPlayDc * rangeDC;
                coinPlayTk = coinPlayTk * rangeTk;
                if(compareMoneyChangeDC > 100000000){
                    if(!isChangeProcessBigger){
                        coinPlayDc = 3000000;
                    }else{
                        coinPlayDc = 1000000;
                    }
                }
                if(compareMoneyChangeTK > 100000000){
                    if(!isChangeProcessBigger){
                        coinPlayTk = 3000000;
                    }else{
                        coinPlayTk = 1000000;
                    }
                }
                if(countIsChangeProcessBigger > 2){
                    coinPlayTk = 200000;
                    coinPlayDc = 200000;
                    countIsChangeProcessBigger = 0;
                }

                if(compareMoneyChangeDC > compareMoneyChangeTK){
                    dcApi.setCoin(coinPlayDc);
                    ResponseDctk responseDc = callApi(dcApi);
                    dcBefore = dcApi;
                    textDc = textDc + "===================Response DC: " + responseDc.getMessage() + " ==> " + formatNumber(dcApi.getCoin()) + ", coin after: " + formatNumber(coinPlayDc/rangeDC) + "\n";
                    text = text + textDc +"\n";
                }
                if(compareMoneyChangeTK > compareMoneyChangeDC){
                    tkApi.setCoin(coinPlayTk);
                    ResponseDctk responseTk = callApi(tkApi);
                    ktBefore = tkApi;
                    textTk = textTk + "===================Response TK: " + responseTk.getMessage() + " ==> " + formatNumber(tkApi.getCoin()) + ", coin after: " + formatNumber(coinPlayTk/rangeTk) + "\n";
                    text = text + textTk +"\n";
                }
                System.out.println(textDc);
                System.out.println(textTk);
                Thread.sleep(50 * 1000);
                if(isFirstRun){
                    isFirstRun = false;
                }
            }
        }catch (Exception e){
            System.out.println(""+e.getMessage());
        }

    }
    public void playVersion3() throws InterruptedException {
        try{
            while (isRun){
                Integer selectD = 0;
                Integer selectC = 0;
                Integer selectT = 0;
                Integer selectK = 0;
                Integer selectU = 0;
                Integer selectQ = 0;

                if(!getNewSession(true)){
                    continue;
                }
                //get time
                getTimeCurrent1();
                int minutes = timeSocket.getTime() / 60;
                int remainingSeconds = timeSocket.getTime() % 60;
                String timeString = String.format("%d:%02d", minutes, remainingSeconds);
                System.out.println("=============Time current: " + timeString);
                Long timeCurrent = Long.valueOf(timeSocket.getTime()) * 1000;
                Long timeEnd = System.currentTimeMillis() + timeCurrent - (23 * 1000);
                connectSocket();
                while(System.currentTimeMillis() < timeEnd){
                    Thread.sleep(500);
                }
                String textDc = "";
                String textTk = "";
                DCTK dcApi = null;
                DCTK tkApi = null;
                if(CollectionUtils.isEmpty(listMapPlayer)){
                    System.out.println("========playSet is empty");
                    continue;
                }
                List<Player> players = new ArrayList<>();
                for (Map.Entry<Integer, Player> entry : listMapPlayer.entrySet()) {
                    if(WebSocketScraper.isBefore(entry.getValue().getTime(), DctkService.historyBefore)){
                        players.add(entry.getValue());
                    }
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
                    }else if(players.get(i).getSelection() == DctkUtils.DCTK.U){
                        selectU = selectU + players.get(i).getCoin();
                    }else if(players.get(i).getSelection() == DctkUtils.DCTK.Q){
                        selectQ = selectQ + players.get(i).getCoin();
                    }
                }
                System.out.println("==========Active logic new 1:" + activeLogicNew1);

                if(activeLogicNew1){
                    Integer idPrev =  getHistory().getHistories().get(0).getId();
                    int countSuccess = 0;
                    List<Balance> balances = getBalance().stream().
                            filter(item -> item.getNote().indexOf(idPrev.toString()) != -1)
                            .collect(Collectors.toList());
                    if(!CollectionUtils.isEmpty(balances)){
                        for (Balance balance : balances) {
                            if(balance.getNote().indexOf("Tham gia phiên") != -1){
                            }
                            if(balance.getNote().indexOf("Trao thưởng phiên") != -1){
                                countSuccess++;
                                text = text + balance.toString() + "\n";
                                if(balance.getAfter() > DctkUtils.DCTK.coinDrawl){
                                    Drawl drawl = new Drawl();
                                    drawl.setCharacter("tiktokab");
                                    drawl.setCoin(20000000);
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
                    }
                    List<String> strSum = new ArrayList<>();
                    Integer tongDQT = selectD + selectQ + selectT;
                    Integer tongWinDQT = selectD * 2 + selectQ * 3 + selectT * 2;
                    Integer tongUCK = selectU + selectC + selectK;
                    Integer tongWinUCK = selectU * 3 + selectC * 2+ selectK * 2;
                    Integer tongDK = selectD + selectK;
                    Integer tongWinDK = selectD * 2 + selectK * 2;
                    Integer tongCT = selectC + selectT ;
                    Integer tongWinCT = selectC * 2 + selectT * 2;
                    Integer tong = selectD + selectQ + selectT + selectU + selectK + selectC;

//                    System.out.println("======> Tong (D + T + Q) = " + formatNumber(tongDQT) + ", du tinh: " + formatNumber(tongWinDQT));
//                    System.out.println("======> Tong (D + K) = " + formatNumber(tongDK) + ", du tinh: " + formatNumber(tongWinDK));
//                    System.out.println("======> Tong (C + K + U) = " + formatNumber(tongUCK) + ", du tinh: " + formatNumber(tongWinUCK));
//                    System.out.println("======> Tong (C + T) = " + formatNumber(tongCT) + ", du tinh: " + formatNumber(tongWinCT));
                    System.out.println("======> Tong C  = " + formatNumber(selectC) + ", Du tinh: " +  formatNumber(selectC * 2));
                    System.out.println("======> Tong D  = " + formatNumber(selectD) + ", Du tinh: " +  formatNumber(selectD * 2));
                    System.out.println("======> Tong K  = " + formatNumber(selectK) + ", Du tinh: " +  formatNumber(selectK * 2));
                    System.out.println("======> Tong T  = " + formatNumber(selectT) + ", Du tinh: " +  formatNumber(selectT * 2));
                    System.out.println("======> Tong  = " + formatNumber(tong) );
                    text = text + "======> Tong C  = " + formatNumber(selectC) + ", Du tinh: " +  formatNumber(selectC * 2) + "\n"
                            + "======> Tong D  = " + formatNumber(selectD) + ", Du tinh: " +  formatNumber(selectD * 2) + "\n"
                            + "======> Tong K  = " + formatNumber(selectK) + ", Du tinh: " +  formatNumber(selectK * 2) + "\n"
                            + "======> Tong T  = " + formatNumber(selectT) + ", Du tinh: " +  formatNumber(selectT * 2) + "\n"
                            + "======> Tong  = " + formatNumber(tong);
                    List<Integer> listSum = new ArrayList<>(List.of(selectC, selectD, selectT, selectK));
                    Collections.sort(listSum);
                    if(selectD == 0){
                        dcApi = new DCTK(0, DctkUtils.DCTK.D, 3000000, 4);
                        ResponseDctk responseDc = callApi(dcApi);
                        dcBefore = dcApi;
                        textDc = textDc + "===================Response DC: " + responseDc.getMessage()  + " ==> " + formatNumber(dcApi.getCoin()) + "\n";
                        text = text + "selectD = 0, play D" + "\n";
                        text = text + textDc + "\n";
                    }
                    if(selectC == 0){
                        dcApi = new DCTK(0, DctkUtils.DCTK.C, 3000000, 4);
                        ResponseDctk responseDc = callApi(dcApi);
                        dcBefore = dcApi;
                        textDc = textDc + "===================Response DC: " + responseDc.getMessage()  + " ==> " + formatNumber(dcApi.getCoin()) + "\n";
                        text = text + "selectC = 0, play C" + "\n";
                        text = text + textDc + "\n";
                    }
                    if(selectT == 0){
                        tkApi = new DCTK(0, DctkUtils.DCTK.T, 3000000, 4);
                        ResponseDctk responseTk = callApi(tkApi);
                        ktBefore = tkApi;
                        textTk = textTk + "===================Response TK: " + responseTk.getMessage() + " ==> " + formatNumber(tkApi.getCoin()) + "\n";
                        text = text + "selectT = 0, play T" + "\n";
                        text = text + textTk + "\n";
                    }
                    if(selectK == 0){
                        tkApi = new DCTK(0, DctkUtils.DCTK.K, 3000000, 4);
                        ResponseDctk responseTk = callApi(tkApi);
                        ktBefore = tkApi;
                        textTk = textTk + "===================Response TK: " + responseTk.getMessage() + " ==> " + formatNumber(tkApi.getCoin()) + "\n";
                        text = text + "selectK = 0, play K" + "\n";
                        text = text + textTk + "\n";
                    }
                    int playSelect =listSum.get(1);
                    int checkSelect =listSum.get(2);
                    int coinPlay = genCoin(playSelect, checkSelect);
                    String playString = "";
                    if(playSelect == selectC){
                        playString = "C";
                    }else if(playSelect == selectD){
                        playString = "D";
                    }else if(playSelect == selectT){
                        playString = "T";
                    }else if(playSelect == selectK){
                        playString = "K";
                    }
                    if (Objects.equals(playString, "D")) {
                        playDC = "D";
                        dcApi = new DCTK(0, DctkUtils.DCTK.D, DctkUtils.DCTK.coinLogicNew + coinPlay, 4);
                        ResponseDctk responseDc = callApi(dcApi);
                        dcBefore = dcApi;
                        textDc = textDc + "===================Response DC: " + responseDc.getMessage()  + " ==> " + formatNumber(dcApi.getCoin()) + "\n";
                        text = text + textDc + "\n";
                    } else if (Objects.equals(playString, "C")) {
                        playDC = "C";
                        dcApi = new DCTK(0, DctkUtils.DCTK.C, DctkUtils.DCTK.coinLogicNew + coinPlay, 4);
                        ResponseDctk responseDc = callApi(dcApi);
                        dcBefore = dcApi;
                        textDc = textDc + "===================Response DC: " + responseDc.getMessage()  + " ==> " + formatNumber(dcApi.getCoin()) + "\n";
                        text = text + textDc + "\n";
                    } else if (Objects.equals(playString, "T")) {
                        playTK = "T";
                        tkApi = new DCTK(0, DctkUtils.DCTK.T, DctkUtils.DCTK.coinLogicNew + coinPlay, 4);
                        ResponseDctk responseTk = callApi(tkApi);
                        ktBefore = tkApi;
                        textTk = textTk + "===================Response TK: " + responseTk.getMessage() + " ==> " + formatNumber(tkApi.getCoin()) + "\n";
                        text = text + textTk + "\n";
                    } else if (Objects.equals(playString, "K")) {
                        playTK = "K";
                        tkApi = new DCTK(0, DctkUtils.DCTK.K, DctkUtils.DCTK.coinLogicNew + coinPlay, 4);
                        ResponseDctk responseTk = callApi(tkApi);
                        ktBefore = tkApi;
                        textTk = textTk + "===================Response TK: " + responseTk.getMessage() + " ==> " + formatNumber(tkApi.getCoin()) + "\n";
                        text = text + textTk + "\n";
                    } else {
                        Thread.sleep(30 * 1000);
                        sendMail("Next continue");
                        continue;
                    }
                    sendMail(text);
                    System.out.println(text);
                    text = "";
                    Thread.sleep(30 * 1000);
                }
            }
        }catch (Exception e){
            System.out.println(""+e.getMessage());
        }

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
    public void sycnMail(History history1){
        history = getHistory().getHistories().get(0);
        if(isFirstRun) return;
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
                if(afterChange + beforeChange < DctkUtils.DCTK.coinRange){
                    countIsChangeProcessBigger++;
                }else{
                    countIsChangeProcessBigger = 0;
                }
//                text = text + "Gia tri doi: " +countIsChangeProcessBigger+ "/n";
//                if(countIsChangeProcessBigger >= DctkUtils.DCTK.count){
//                    isChangeProcessBigger = !isChangeProcessBigger;
//                    countIsChangeProcessBigger = 0;
//                    text = text + " Van sau doi trang thai: "+ (isChangeProcessBigger == true ? "lon" :"nho");
//                }
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
    // Hàm tìm phần tử xuất hiện ở nhiều danh sách nhất dùng varargs
    public static List<String> findMostFrequentElements(List<List<String>> lists) {
        // Sử dụng HashMap để lưu số lần xuất hiện của từng phần tử
        Map<String, Integer> countMap = new HashMap<>();

        // Đếm số lần xuất hiện trong các danh sách
        for (List<String> list : lists) {
            for (String element : list) {
                countMap.put(element, countMap.getOrDefault(element, 0) + 1);
            }
        }

        // Tìm các phần tử có số lần xuất hiện nhiều nhất
        int maxCount = 0;
        List<String> mostFrequentElements = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                mostFrequentElements.clear(); // Xóa danh sách cũ
                mostFrequentElements.add(entry.getKey());
                maxCount = entry.getValue();
            } else if (entry.getValue() == maxCount) {
                mostFrequentElements.add(entry.getKey());
            }
        }

        // Kiểm tra nếu tất cả phần tử có số lần xuất hiện bằng nhau
        boolean allEqual = mostFrequentElements.size() == countMap.size();

        // Nếu tất cả phần tử xuất hiện với số lần bằng nhau hoặc số lần xuất hiện lớn nhất là 1 thì trả về danh sách rỗng
        if (maxCount == 1 || allEqual) {
            return new ArrayList<>(); // Trả về danh sách rỗng
        }

        return mostFrequentElements;
    }
    public static String timPhanTuXuatHienNhieuNhat(List<String> danhSach) {
        // Sử dụng HashMap để đếm số lần xuất hiện của mỗi phần tử
        Map<String, Integer> mapDem = new HashMap<>();

        for (String phanTu : danhSach) {
            mapDem.put(phanTu, mapDem.getOrDefault(phanTu, 0) + 1);
        }

        // Biến để lưu trữ phần tử xuất hiện nhiều nhất và số lần xuất hiện của nó
        String phanTuXuatHienNhieuNhat = null;
        int soLanXuatHienMax = 0;

        // Duyệt qua map để tìm phần tử có số lần xuất hiện nhiều nhất
        for (Map.Entry<String, Integer> entry : mapDem.entrySet()) {
            if (entry.getValue() > soLanXuatHienMax) {
                soLanXuatHienMax = entry.getValue();
                phanTuXuatHienNhieuNhat = entry.getKey();
            }
        }

        return phanTuXuatHienNhieuNhat;
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
        int lowerBound = 1500000;
        int upperBound = 2500000;

        // Tạo số ngẫu nhiên trong khoảng từ 900000 đến 1200000
        int randomNumber = lowerBound + random.nextInt(upperBound - lowerBound + 1);
        return randomNumber;
    }
    public int genCoin(int coin, int coinCheck){
        int check = coinCheck - coin;
        if(check < 1000000){
            return 500000;
        }
        if(check < 2000000 && check > 1000000){
            return 1000000;
        }
        if(check < 3000000 && check > 2000000){
            return 1500000;
        }
        int result;
        int coinRandom;
        do{
            coinRandom = getCoinLogic();
            result = coin + coinRandom;
        }while (result > coinCheck);
        return coinRandom;
    }
}
