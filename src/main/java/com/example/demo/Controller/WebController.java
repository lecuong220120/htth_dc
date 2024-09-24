package com.example.demo.Controller;

import com.example.demo.DTO.FormData;
import com.example.demo.DTO.HistoryDTO;
import com.example.demo.Obj.History;
import com.example.demo.Obj.StatisticObj;
import com.example.demo.Service.DctkService;
import com.example.demo.Utils.DctkUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class WebController {
    @Autowired
    DctkService dctkService;
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("formData", new FormData());
        return "home";
    }
    @PostMapping("/submitForm")
    public String submitForm(@ModelAttribute FormData formData, Model model) {
        // Xử lý dữ liệu từ form, có thể gọi API tại đây
        System.out.println("Input text: " + formData.getInputText());
//
//        if(!StringUtils.isEmpty(formData.getInputText())){
//            DctkUtils.DCTK.coin = Integer.parseInt(formData.getInputText());
//        }
//        if(!StringUtils.isEmpty(formData.getInputText1())){
//            DctkUtils.DCTK.coinAdd = Integer.parseInt(formData.getInputText1());
//        }

        return "home";
    }
    @GetMapping("/export-excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=data.xlsx";
        response.setHeader(headerKey, headerValue);

        List<StatisticObj> listDataDC = DctkService.lstStatisticDc;
        List<StatisticObj> listDataTK = DctkService.lstStatisticTk;

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheetDc = workbook.createSheet("DataDC");
        XSSFSheet sheetTk = workbook.createSheet("DataTK");

        // Header row dc
        Row headerRowDc = sheetDc.createRow(0);
        headerRowDc.createCell(0).setCellValue("Result");
        headerRowDc.createCell(1).setCellValue("MoneyDC");
        headerRowDc.createCell(2).setCellValue("CountWinDC");
        headerRowDc.createCell(3).setCellValue("CountLoseDC");

        // Header row tk
        Row headerRowTk = sheetTk.createRow(0);
        headerRowTk.createCell(0).setCellValue("Result");
        headerRowTk.createCell(1).setCellValue("MoneyTk");
        headerRowTk.createCell(2).setCellValue("CountWinTk");
        headerRowTk.createCell(3).setCellValue("CountLoseTk");
        // Add more columns as needed

        // Data rows dc
        int rowNum = 1;
        for (StatisticObj data : listDataDC) {
            Row row = sheetDc.createRow(rowNum++);
            row.createCell(0).setCellValue(Integer.parseInt(data.getResult()) == DctkUtils.DCTK.D  ? "D":"C");
            row.createCell(1).setCellValue(dctkService.formatNumber(data.getMoney()));
            row.createCell(2).setCellValue(data.getCountWin());
            row.createCell(3).setCellValue(data.getCountLose());
            // Add more cells as needed
        }
        // Data rows tk
        int rowNumTk = 1;
        for (StatisticObj data : listDataTK) {
            Row row = sheetTk.createRow(rowNumTk++);
            row.createCell(0).setCellValue(Integer.parseInt(data.getResult()) == DctkUtils.DCTK.T  ? "T":"K");
            row.createCell(1).setCellValue(dctkService.formatNumber(data.getMoney()));
            row.createCell(2).setCellValue(data.getCountWin());
            row.createCell(3).setCellValue(data.getCountLose());
            // Add more cells as needed
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }
    public String returnResult(StatisticObj obj){
        if(obj.isType()){
            return Integer.parseInt(obj.getResult()) == DctkUtils.DCTK.D ? "d" : "c";
        }
        if(!obj.isType()){
            return Integer.parseInt(obj.getResult()) == DctkUtils.DCTK.T ? "t" : "k";
        }
        return null;
    }
    public static void main(String[] args) {
        DctkService dctkService1 = new DctkService();
        List<History> histories = new ArrayList<>();
        List<HistoryDTO> historyDTOS = dctkService1.getHistoryAll();
        for(int i = 0; i < historyDTOS.size(); i++){
            histories.addAll(historyDTOS.get(i).getHistories());
        }

        List<Integer> listResultDc = histories.stream().sorted(Comparator.comparing(History::getStart)).collect(Collectors.toList())
                .stream().map(History::getResult_cd).toList();
        List<Integer> listResultTk = histories.stream().sorted(Comparator.comparing(History::getStart)).collect(Collectors.toList())
                .stream().map(History::getResult_tk).toList();
        System.out.println("listResultDc: " + listResultDc.size());
        listResultDc.forEach(a -> System.out.print(a == DctkUtils.DCTK.C ? "C" : "D"));
        System.out.println("\n");
        System.out.println("listResultTk ");
        listResultTk.forEach(a -> System.out.print(a == DctkUtils.DCTK.T ? "T": "K"));
        StringBuilder stringDc = new StringBuilder();
        listResultTk.forEach(a -> stringDc.append(a == DctkUtils.DCTK.T ? "T": "K"));
        listResultDc.forEach(a -> stringDc.append(a == DctkUtils.DCTK.C ? "C" : "D"));
        System.out.println("\n");
        String binaryString = String.valueOf(stringDc);

        // Đếm số lần xuất hiện liên tiếp của '0' và '1'
        Map<String, Map<Integer, Integer>> consecutiveCounts = new HashMap<>();
        consecutiveCounts.put("D", new HashMap<>());
        consecutiveCounts.put("C", new HashMap<>());
        consecutiveCounts.put("T", new HashMap<>());
        consecutiveCounts.put("K", new HashMap<>());

        // Biến để lưu ký tự hiện tại và độ dài liên tiếp
        char currentChar = binaryString.charAt(0);
        int count = 1;

        for (int i = 1; i < binaryString.length(); i++) {
            if (binaryString.charAt(i) == currentChar) {
                count++;
            } else {
                // Lưu độ dài vào Map
                consecutiveCounts.get(String.valueOf(currentChar)).put(count,
                        consecutiveCounts.get(String.valueOf(currentChar)).getOrDefault(count, 0) + 1);

                // Reset lại biến đếm cho ký tự mới
                currentChar = binaryString.charAt(i);
                count = 1;
            }
        }

        // Đừng quên lưu lần đếm cuối cùng
        consecutiveCounts.get(String.valueOf(currentChar)).put(count,
                consecutiveCounts.get(String.valueOf(currentChar)).getOrDefault(count, 0) + 1);

        // In kết quả
        System.out.println("Kết quả phân loại số lần xuất hiện liên tiếp:");
        for (Map.Entry<String, Map<Integer, Integer>> entry : consecutiveCounts.entrySet()) {
            String charType = entry.getKey();
            Map<Integer, Integer> lengths = entry.getValue();
            System.out.println("Ký tự '" + charType + "':");
            for (Map.Entry<Integer, Integer> lengthEntry : lengths.entrySet()) {
                System.out.println("  Xuất hiện " + lengthEntry.getKey() + " lần: " + lengthEntry.getValue() + " lần");
            }
        }
        int transitionsFromOneToZero = 0;
        int transitionsFromZeroToOne = 0;
        int transitionsFromTwoToThree = 0;
        int transitionsFromThreeToTwo = 0;

        // Duyệt qua từng ký tự và đếm số lần chuyển giao
        for (int i = 1; i < binaryString.length(); i++) {
            char previousChar = binaryString.charAt(i - 1);
            char currentCha = binaryString.charAt(i);

            if (previousChar == 'D' && currentCha == 'C') {
                transitionsFromOneToZero++;
            } else if (previousChar == 'C' && currentCha == 'D') {
                transitionsFromZeroToOne++;
            } else if (previousChar == 'T' && currentCha == 'K') {
                transitionsFromTwoToThree++;
            } else if (previousChar == 'K' && currentCha == 'T') {
                transitionsFromThreeToTwo++;
            }
        }

        // In kết quả
        System.out.println("Số lần chuyển giao từ 1 sang 0: " + transitionsFromOneToZero);
        System.out.println("Số lần chuyển giao từ 0 sang 1: " + transitionsFromZeroToOne);
        System.out.println("Số lần chuyển giao từ 3 sang 2: " + transitionsFromThreeToTwo);
        System.out.println("Số lần chuyển giao từ 2 sang 3: " + transitionsFromTwoToThree);
    }
}