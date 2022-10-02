package com.zb.fastlms.member.controller;

import com.zb.fastlms.member.entity.Member;
import com.zb.fastlms.member.model.MemberInput;
import com.zb.fastlms.member.repository.MemberRepository;
import com.zb.fastlms.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Controller
public class MemberController {

    private final MemberService memberService;
    @RequestMapping(value = "/member/register", method = RequestMethod.GET)
    public String register(){
        return "member/register";
    }

    @RequestMapping(value = "/member/register", method = RequestMethod.POST)
    public String registerSubmit(HttpServletRequest request, HttpServletResponse response, MemberInput parameter){

        boolean result = memberService.register(parameter);

        return "member/register_complete";
    }
}
