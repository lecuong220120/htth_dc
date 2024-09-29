package com.example.demo.Service;

import com.example.demo.NsoObj.GameData;
import com.example.demo.NsoObj.PlayerNSO;
import com.example.demo.Obj.TimeSocket;
import com.example.demo.Utils.DctkUtils;
import com.example.demo.websocket.WebSocketThreadNso;
import org.hibernate.annotations.Synchronize;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.example.demo.Utils.DctkUtils.CLNSO.coin;

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
    public  static String urlHistory = "https://nsocltx.com/lich-su?server=4&page=";
    public  static String userName = "thanbai";
    public  static String password = "Abc123@@";
    public  static String token = "";
    public static Map<String, Long> statisticMap = new HashMap<>();
    public static List<String> dcHistory = new ArrayList<>();
    public static List<String> tkHistory = new ArrayList<>();
    public static boolean checkTime(String time){
        // Định dạng thời gian
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");

        // Chuyển đổi chuỗi thành LocalDateTime
        LocalDateTime givenDateTime = LocalDateTime.parse(time, formatter);

        // Lấy thời gian hiện tại và thiết lập là 2 giờ sáng
        LocalDateTime twoAMToday = LocalDateTime.now().with(LocalTime.of(0, 0)).withSecond(0).withNano(0);

        // So sánh thời gian
        int comparison = twoAMToday.compareTo(givenDateTime);

        // In kết quả so sánh
        if (comparison < 0) {
            return true;
        } else if (comparison > 0) {
           return false;
        } else {
            return true;
        }
    }
    public void getHistory(){
        dcHistory = new ArrayList<>();
        tkHistory = new ArrayList<>();
        outerloop: for(int i = 1; i <= 50; i++){
            String url = urlHistory + i;
            ResponseEntity<?> response
                    = restTemplate.getForEntity(url, String.class);
            String res = String.valueOf(response.getBody());
            if(res != null){
                Document document = Jsoup.parse(res);
                Elements paragraphs = document.select("p");
                for (Element p : paragraphs) {
                    if(p.text().contains("Bắt đầu")){
                       String sub = p.text();
                       String text = "Bắt đầu: ";
                       String bd = sub.substring(sub.indexOf(text,0) + text.length(), sub.length());
                        if(!checkTime(bd)){
                            System.out.println(bd);
                            break outerloop;
                        }
                    }
                    if (p.text().contains("CD:")) {
                        // Lấy nội dung bên trong thẻ <span> và in ra
                        Element span = p.selectFirst("span");
                        if (span != null) {
                            String cdValue = span.text();
                            if(StringUtils.endsWithIgnoreCase(cdValue, "cung")){
                                dcHistory.add("C");
                            }else{
                                dcHistory.add("D");
                            }
                        }
                    }
                    if (p.text().contains("TK:")) {
                        // Lấy nội dung bên trong thẻ <span> và in ra
                        Element span = p.selectFirst("span");
                        if (span != null) {
                            String tkValue = span.text();
                            if(StringUtils.endsWithIgnoreCase(tkValue, "kiếm")){
                                tkHistory.add("K");
                            }else{
                                tkHistory.add("T");
                            }
                        }
                    }
            }
        }
    }

    }
    public static char predictNext(String input) {
        // Iterate through the string to find the last "t" -> "k" transition
        for (int i = 1; i < input.length(); i++) {
            if (input.charAt(i - 1) == 'T' && input.charAt(i) == 'K') {
                // Get the substring before the transition
                String subStr = input.substring(0, i);

                // Reverse the substring to get the opposite character pattern
                String reversed = new StringBuilder(subStr).reverse().toString();

                // Return the first character of the reversed substring
                return reversed.charAt(0);
            }
        }

        // If no "t" -> "k" transition found, return a default character (e.g. '?')
        return '?';
    }
    public static String subString(String s, char c){
        String result = "";
        int count = 0;
        int check = 0;
        String left = "";
        String right = "";
        String mid = "";
        for(int i = 0; i< s.length();i++){
            char ch = s.charAt(i);;
            if(ch != c){
                check++;
                c = ch;
            }
            if(check == 0){
                left = left + ch;
            }
            if(check == 1){
                mid = mid + ch;
            }
            if(check == 2){
                right = right + ch;
            }
            count++;

            if(check == 3){
                result = s.substring(0, count);
                break;
            }

        }
        System.out.println(left);
        System.out.println(mid);
        System.out.println(right);
        System.out.println(result);
        if (left.length() < right.length()){
            return String.valueOf(right.charAt(0));
        }
        return null;
    }

    public static char predictNextCharacter(String str) {
        int length = str.length();
        int mid = length / 2;

        // Nếu độ dài là số lẻ, lấy ký tự giữa
        char centerChar = str.charAt(mid);

        // Kiểm tra các ký tự đối xứng
        for (int i = 0; i < mid; i++) {
            if (str.charAt(mid - 1 - i) != str.charAt(mid + i)) {
                break;
            }
            // Nếu đối xứng, tiếp tục
            if (i == mid - 1) {
                return centerChar; // Ký tự tiếp theo là ký tự giữa
            }
        }

        // Nếu không tìm thấy đối xứng, trả về ký tự đầu tiên (hoặc có thể thay đổi logic)
        return str.charAt(0);
    }
    public static char predictNextTk(String input) {
        // Iterate through the string to find the last "t" -> "k" transition
        for (int i = 1; i < input.length(); i++) {
            if (input.charAt(i - 1) == 'C' && input.charAt(i) == 'D') {
                // Get the substring before the transition
                String subStr = input.substring(0, i);

                // Reverse the substring to get the opposite character pattern
                String reversed = new StringBuilder(subStr).reverse().toString();

                // Return the first character of the reversed substring
                return reversed.charAt(0);
            }
        }

        // If no "t" -> "k" transition found, return a default character (e.g. '?')
        return '?';
    }
    static int choosedc = 0;
    static int choosetk = 3;
    public static AtomicBoolean isPlayDc =  new AtomicBoolean(true);
    public static AtomicBoolean isPlayTk = new AtomicBoolean(false);
    public static String playTkAgo =  "";
    public static String playDcAgo =  "";
    public static AtomicBoolean isDone = new AtomicBoolean(false);
    Long selectC = 0L;
    public static Integer time = 0;
    public static Integer countPlayDc = 0;
    public static Integer countPlayDc1 = 0;
    public static boolean isWinDc = true;
    public static Integer countPlayTk = 0;
    public static Integer countPlayTk1 = 0;
    public static boolean isWinTK = true;
    @Synchronize({"playVersion4NSO"})
    public void playVersion4NSO() throws InterruptedException {
        while (isRun){
            try{
                Random random = new Random();
                time = random.nextInt(20);
                Long selectD = 0L;
                Long selectC = 0L;
                Long selectT = 0L;
                Long selectK = 0L;
                long coinPlayTk = coin;
                long coinPlayDc = coin + 100;
                //get time
                getTimeCurrent1();
                int minutes = timeSocket.getTime() / 60;
                int remainingSeconds =  timeSocket.getTime() % 60;
                String timeString = String.format("%d:%02d", minutes, remainingSeconds);
                System.out.println("=============Time current NSO: " + timeString);
                Long timeCurrent = Long.valueOf(timeSocket.getTime()) * 1000;
                Long timeEnd = System.currentTimeMillis() + timeCurrent ;
                timeEnd = timeEnd - (30 * 1000)-500;
                while(System.currentTimeMillis() < timeEnd){
                    Thread.sleep(1000);
                }
                if(activeLogicNew1){
                    Map<Long, Integer> playMap = new HashMap<>();

                    String text1 = "**************NSO SITE****************" + "\n\n";
                    System.out.println(text1);
                    String playStringDc = "";
                    String playStringTk = "";
                    getHistory();
                    List<String> dcList = dcHistory.subList(0, 50);
                    List<String> tkList = tkHistory.subList(0, 50);
                    String dcString = "";
                    String tkString = "";
                    for (String s : dcList) {
                        dcString = dcString + s;
                    }
                    for (String s : tkList) {
                        tkString = tkString + s;
                    }
                    playStringDc = String.valueOf(subString(dcString, dcString.charAt(0)));
                    playStringTk = String.valueOf(subString(tkString, tkString.charAt(0)));
                    if (Objects.equals(playStringDc, "D")) {
                        playMap.put(coinPlayDc, DctkUtils.CLNSO.D);
                    }else if (Objects.equals(playStringDc, "C")){
                        playMap.put(coinPlayDc, DctkUtils.CLNSO.C);
                    }else{
                        playStringDc = "";
                    }
                    if (Objects.equals(playStringTk, "T")) {
                        playMap.put(coinPlayTk, DctkUtils.CLNSO.T);
                    }else if (Objects.equals(playStringTk, "K")){
                        playMap.put(coinPlayTk, DctkUtils.CLNSO.K);
                    }else{
                        playStringTk = "";
                    }
                    callApi(playMap);
                    Thread.sleep(40 * 1000);
                    getHistory();
                    if(playStringDc.equals(dcHistory.get(0))){
                        System.out.println("Ban da thang " + playStringDc);
                    }else if(playStringDc == ""){
                        System.out.println("Ko tham gia DC");
                    } else{
                        System.out.println("Ban da thua "  + playStringDc );
                    }
                    if(playStringTk.equals(tkHistory.get(0))){
                        System.out.println("Ban da thang " + playStringTk);
                    }else if(playStringTk == ""){
                        System.out.println("Ko tham gia Tk");
                    }
                    else{
                        System.out.println("Ban da thua "+ playStringTk );
                    }
                }
            }catch (Exception e){
                System.out.println(""+e.getMessage());
            }
        }


    }

    public static void main(String[] args) {
        NsoService nsoService = new NsoService();
        nsoService.getHistory();
        System.out.println("size dc: "+dcHistory.size());
        System.out.println("size tk: "+tkHistory.size());
        boolean isCheck = true;
        boolean isCheck1 = false;
        int subdcsize = 0;
        int subdcsize1 = 10;
        AtomicInteger countD = new AtomicInteger();
        AtomicInteger countC = new AtomicInteger();
        AtomicInteger countT = new AtomicInteger();
        AtomicInteger countK = new AtomicInteger();
        while (isCheck){
            AtomicInteger countDc = new AtomicInteger();
            AtomicInteger countDc1 = new AtomicInteger();
            AtomicInteger countTk = new AtomicInteger();
            AtomicInteger countTk1 = new AtomicInteger();
            List<String> sub1 = new ArrayList<>();
            List<String> sub2 = new ArrayList<>();

            sub1 = dcHistory.subList(subdcsize, subdcsize1);
            sub2 = tkHistory.subList(subdcsize, subdcsize1);
            sub1.forEach(a -> {
                if(a == "D"){
                    countD.getAndIncrement();
                    countDc.getAndIncrement();
                }else{
                    countC.getAndIncrement();
                    countDc1.getAndIncrement();
                }
                System.out.print(a);

            });
            System.out.println("\n");
            System.out.println("D: " + countDc);
            System.out.println("C: " + countDc1);
            System.out.println("\n");
            sub2.forEach(a -> {
                if(a == "T"){
                    countT.getAndIncrement();
                    countTk.getAndIncrement();
                }else{
                    countK.getAndIncrement();
                    countTk1.getAndIncrement();
                }
                System.out.print(a);
            });
            System.out.println("\n");
            System.out.println("T: " + countTk);
            System.out.println("K: " + countTk1);
            if(isCheck1){
                break;
            }
            if(tkHistory.size() < subdcsize1 + 10){
                int check = tkHistory.size() - subdcsize1;
                subdcsize = 0;
                subdcsize1 = check;
                isCheck1 = true;
            }else{
                subdcsize = subdcsize + 10;
                subdcsize1 = subdcsize1 + 10;
            }
        }
        System.out.println("countD: " + countD);
        System.out.println("countC: " + countC);
        System.out.println("countT: " + countT);
        System.out.println("countK: " + countK);

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
            System.out.println("========playMap size: " + playMap.size());
            for (Map.Entry<Long, Integer> entry : playMap.entrySet()) {
                Long coin = entry.getKey();
                if(coin < 100000){
                    coin = 100000l;
                }
                MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
                body.add("game", "0");
                body.add("server", "4");
                body.add("game_type", "1");
                body.add("money", coin+"");
                body.add("selection", entry.getValue() +"");
                body.add("_token", token);
                requestEntity = new HttpEntity<>(body, httpHeaders);
                response = restTemplate.exchange(urlPushGame, HttpMethod.POST, requestEntity, String.class);
                String responseString = response.getBody();
                if(responseString.indexOf("money") != -1){
                    Integer sodu = Integer.valueOf(responseString.substring(responseString.indexOf("money") + 7, responseString.indexOf(",\"role\"")));
                    System.out.println("SO DU HIEN TAI: " + formatNumber(sodu) );
                }
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
