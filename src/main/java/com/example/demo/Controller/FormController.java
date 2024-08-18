//package com.example.demo.Controller;
//
//import com.example.demo.DTO.FormData;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Controller
//public class FormController {
//    @GetMapping("/home")
//    public String showForm(Model model) {
//        model.addAttribute("formData", new FormData());
//        return "home";
//    }
//
//    @PostMapping("/submitForm")
//    public String submitForm(@ModelAttribute FormData formData, Model model) {
//        // Xử lý dữ liệu từ form, có thể gọi API tại đây
//        System.out.println("Input text: " + formData.getInputText());
//
//        // Ví dụ: gọi API và lưu kết quả vào model
//        String apiResponse = callApi(formData.getInputText());
//        model.addAttribute("apiResponse", apiResponse);
//
//        return "result";
//    }
//    // Hàm giả lập gọi API
//    private String callApi(String input) {
//        // Gọi API với input và trả về kết quả (ví dụ)
//        return "Kết quả từ API cho giá trị '" + input + "'";
//    }
//}
//
//
