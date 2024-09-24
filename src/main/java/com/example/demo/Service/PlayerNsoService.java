package com.example.demo.Service;

import com.example.demo.NsoObj.GameData;
import com.example.demo.NsoObj.PlayerNSO;
import com.example.demo.Obj.Player;
import com.example.demo.Repository.PlayerNsoRepository;
import com.example.demo.Utils.DctkUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlayerNsoService {
    @Autowired
    PlayerNsoRepository playerNsoRepository;
    static SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    public void save() throws InterruptedException {
        while (true){
            try {
                List<PlayerNSO> playerNSOS = getPlayerNSO();
                if(!CollectionUtils.isEmpty(playerNSOS)){
                    String date = formatter.format(new Date());
                    for(int i = 0; i < playerNSOS.size(); i++){
                        Optional<PlayerNSO> playerNSO = playerNsoRepository.findByNameAndTimeAndSelection(playerNSOS.get(i).getName(),playerNSOS.get(i).getTime(), playerNSOS.get(i).getSelection());
                        if(playerNSO.isPresent()){

                        }else{
                            playerNSOS.get(i).setCreateAt(date);
                            playerNSOS.get(i).setXuWin(playerNSOS.get(i).getXuWin() - playerNSOS.get(i).getXu());
                            String selectStr = getString(playerNSOS, i);
                            playerNSOS.get(i).setSelectStr(selectStr);
                            playerNsoRepository.save(playerNSOS.get(i));
                        }
                    }
                    playerNSOS.clear();
                    playerNSOS = new ArrayList<>();
                }
                Thread.sleep(2000);
            }catch (Exception e){
                System.out.println(e);
            }
        }
    }

    private static String getString(List<PlayerNSO> playerNSOS, int i) {
        String selectStr = "";
        if(playerNSOS.get(i).getSelection() == DctkUtils.CLNSO.C){
            selectStr = "C";
        }else if(playerNSOS.get(i).getSelection() == DctkUtils.CLNSO.D){
            selectStr = "D";
        }else if(playerNSOS.get(i).getSelection() == DctkUtils.CLNSO.T){
            selectStr = "T";
        }else if(playerNSOS.get(i).getSelection() == DctkUtils.CLNSO.K){
            selectStr = "K";
        }else if(playerNSOS.get(i).getSelection() == DctkUtils.CLNSO.CK){
            selectStr = "CK";
        }else if(playerNSOS.get(i).getSelection() == DctkUtils.CLNSO.DK){
            selectStr = "DK";
        }else if(playerNSOS.get(i).getSelection() == DctkUtils.CLNSO.CT){
            selectStr = "CT";
        }else if(playerNSOS.get(i).getSelection() == DctkUtils.CLNSO.DT){
            selectStr = "DT";
        }
        return selectStr;
    }

    public List<PlayerNSO> getPlayerNSO(){
        try{
            ResponseEntity<GameData> responseGameData
                    = restTemplate.getForEntity(urlGetPlay, GameData.class);
            List<PlayerNSO> playerNSOS = responseGameData.getBody().getPlayers();
            List<PlayerNSO> result = new ArrayList<>();
            if(!CollectionUtils.isEmpty(playerNSOS)){
                result = playerNSOS.stream().filter(item -> item.getStatus() != 0).collect(Collectors.toList());
            }else{
                return null;
            }
            return result;
        }catch (Exception e){
            return null;
        }
    }
    private RestTemplate restTemplate = new RestTemplate();
    private String urlGetPlay = "https://nsocltx.com/get-players?server=4&key=&limit=100";
}
