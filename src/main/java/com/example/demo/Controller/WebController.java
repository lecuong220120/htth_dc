package com.example.demo.Controller;

import com.example.demo.DTO.FormData;
import com.example.demo.Utils.DctkUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class WebController {

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
}