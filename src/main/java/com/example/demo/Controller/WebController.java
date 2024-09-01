package com.example.demo.Controller;

import com.example.demo.DTO.FormData;
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
import java.util.ArrayList;
import java.util.List;

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

        if(!StringUtils.isEmpty(formData.getInputText())){
            DctkUtils.DCTK.coin = Integer.parseInt(formData.getInputText());
        }
        if(!StringUtils.isEmpty(formData.getInputText1())){
            DctkUtils.DCTK.coinAdd = Integer.parseInt(formData.getInputText1());
        }

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
}