package com.zb.fastlms.main.controller;

// 목적: 주소와 물리적인 파일 매핑하기 위해서

// 주소에 대해서 어디서 매핑하고 누가 매핑하느냐
// 클래스, 속성, 메소드

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class MainController {

    @RequestMapping("/")
    public String index(){
        return "index";
    }

    // request -> WEB -> SERVER
    // response -> SERVER -> WEB

    @RequestMapping("/hello")
    public void hello(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter printWriter = response.getWriter();

        String msg = "<html>" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "</head>" +
                "<body>" +
                "<p>hello</p> <p>fastlms website!!!</p>" +
                "<p>안녕하세용</p>" +
                "</body>" +
                "</html>";
        printWriter.write(msg);
        printWriter.close();
    }

    @RequestMapping("/error/denied")
    public String errorDenied(){
        return "error/denied";
    }

}
