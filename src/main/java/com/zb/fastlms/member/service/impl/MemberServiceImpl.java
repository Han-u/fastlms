package com.zb.fastlms.member.service.impl;

import com.zb.fastlms.components.MailComponents;
import com.zb.fastlms.member.entity.Member;
import com.zb.fastlms.member.model.MemberInput;
import com.zb.fastlms.member.repository.MemberRepository;
import com.zb.fastlms.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MailComponents mailComponents;

    @Override
    public boolean register(MemberInput parameter) {
        Optional<Member> optionalMember = memberRepository.findById(parameter.getUserId());
        if(optionalMember.isPresent()){
            // 현재 userId에 해당하는 데이터 존재
            return false;
        }

        String uuid = UUID.randomUUID().toString();
        System.out.println(parameter);
        Member member = Member.builder()
                        .userId(parameter.getUserId())
                        .userName(parameter.getUserName())
                        .phone(parameter.getPhone())
                        .password(parameter.getPassword())
                        .regDt(LocalDateTime.now())
                        .emailAuthYn(false)
                        .emailAuthKey(uuid)
                        .build();
        memberRepository.save(member);
        System.out.println("Done");
        String email = parameter.getUserId();
        String subject = "사이트 가입 ㅊㅋㅊㅋㅋ";
        String text=  "<p>사이트 가입 축하</p> <p>링크클릭 앤 가입완료</p>"+"<div><a href='http://localhost:8080/member/email-auth?id=" + uuid+"'>가입완료하기</a></div>";
        mailComponents.sendMail(email, subject, text);

       return true;
    }

    @Override
    public boolean emailAuth(String uuid) {
        Optional<Member> optionalMember = memberRepository.findByEmailAuthKey(uuid);

        if(!optionalMember.isPresent()){
            return false;
        }
        Member member = optionalMember.get();
        member.setEmailAuthYn(true);
        member.setEmailAuthDt(LocalDateTime.now());
        memberRepository.save(member);

        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> optionalMember = memberRepository.findById(username);
        if (!optionalMember.isPresent()){
            throw new UsernameNotFoundException("회원정보가 전재하지 않습니다.");
        }

        Member member = optionalMember.get();

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new User(member.getUserId(), member.getPassword(), grantedAuthorities);
    }
}
