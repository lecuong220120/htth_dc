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

        List<StatisticObj> listData = dctkService.statisticObjs;

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Data");

        // Header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ResultDC");
        headerRow.createCell(1).setCellValue("ResultTK");
        headerRow.createCell(2).setCellValue("ChangeMoneyDC");
        headerRow.createCell(3).setCellValue("ChangeMoneyTK");
        headerRow.createCell(4).setCellValue("CountWinDC");
        headerRow.createCell(5).setCellValue("CountLoseDC");
        headerRow.createCell(6).setCellValue("CountWinTK");
        headerRow.createCell(7).setCellValue("CountLostTK");
        // Add more columns as needed

        // Data rows
        int rowNum = 1;
        for (StatisticObj data : listData) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(data.getResultDC());
            row.createCell(1).setCellValue(data.getResultTK());
            row.createCell(2).setCellValue(dctkService.formatNumber(data.getMoneyChangeDC()));
            row.createCell(3).setCellValue(dctkService.formatNumber(data.getMoneyChangeTK()));
            row.createCell(4).setCellValue(data.getCountWinDC());
            row.createCell(5).setCellValue(data.getCountLoseDC());
            row.createCell(6).setCellValue(data.getCountWinTK());
            row.createCell(7).setCellValue(data.getCountLoseTK());
            // Add more cells as needed
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}