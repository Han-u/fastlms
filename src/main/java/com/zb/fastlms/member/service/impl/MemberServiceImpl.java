package com.zb.fastlms.member.service.impl;

import com.zb.fastlms.admin.dto.MemberDto;
import com.zb.fastlms.admin.mapper.MemberMapper;
import com.zb.fastlms.admin.model.MemberParam;
import com.zb.fastlms.components.MailComponents;
import com.zb.fastlms.member.entity.Member;
import com.zb.fastlms.member.exception.MemberNotEmailAuthException;
import com.zb.fastlms.member.model.MemberInput;
import com.zb.fastlms.member.model.ResetPasswordInput;
import com.zb.fastlms.member.repository.MemberRepository;
import com.zb.fastlms.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MailComponents mailComponents;

    private final MemberMapper memberMapper;

    @Override
    public boolean register(MemberInput parameter) {
        Optional<Member> optionalMember = memberRepository.findById(parameter.getUserId());
        if(optionalMember.isPresent()){
            // 현재 userId에 해당하는 데이터 존재
            return false;
        }

        String encPassword = BCrypt.hashpw(parameter.getPassword(), BCrypt.gensalt());

        String uuid = UUID.randomUUID().toString();
        System.out.println(parameter);
        Member member = Member.builder()
                        .userId(parameter.getUserId())
                        .userName(parameter.getUserName())
                        .phone(parameter.getPhone())
                        .password(encPassword)
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

        if(member.isEmailAuthYn()){
            return false;
        }

        member.setEmailAuthYn(true);
        member.setEmailAuthDt(LocalDateTime.now());
        memberRepository.save(member);

        return true;
    }

    @Override
    public boolean sendResetPassword(ResetPasswordInput parameter) {

        Optional<Member> optionalMember = memberRepository.findByUserIdAndUserName(parameter.getUserId(), parameter.getUserName());
        if(!optionalMember.isPresent()){
            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
        }

        Member member = optionalMember.get();

        String uuid = UUID.randomUUID().toString();

        member.setResetPasswordKey(uuid);
        member.setResetPasswordLimitDt(LocalDateTime.now().plusDays(1));
        memberRepository.save(member);


        String email = parameter.getUserId();
        String subject = "[fastlms] 비밀번호 초기화 메일 입니다.";
        String text=  "<p>fastlms 비밀번호 초기화 메일 입니다.</p> " +
                "<p>아래 링크 클릭해 비밀번호 초기화해주세요</p>"+
                "<div><a target='_blank' href='http://localhost:8080/member/reset/password?id=" + uuid+"'>비밀번호 초기화 링크</a></div>";
        mailComponents.sendMail(email, subject, text);

        return false;
    }

    @Override
    public boolean resetPassword(String uuid, String password) {
        Optional<Member> optionalMember = memberRepository.findByResetPasswordKey(uuid);
        if (!optionalMember.isPresent()){
            throw new UsernameNotFoundException("회원정보가 전재하지 않습니다.");
        }

        Member member = optionalMember.get();

        // 초기화 날짜 유효 체크
        if(member.getResetPasswordLimitDt() == null){
            throw new RuntimeException("유효한 날짜가 아닙니다.");
        }

        if(member.getResetPasswordLimitDt().isBefore(LocalDateTime.now())){
            throw new RuntimeException("유효한 날짜가 아닙니다.");
        }

        String encPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        member.setPassword(encPassword);
        member.setResetPasswordKey("");
        member.setResetPasswordLimitDt(null);
        memberRepository.save(member);


        return true;
    }

    @Override
    public boolean checkResetPassword(String uuid) {
        Optional<Member> optionalMember = memberRepository.findByResetPasswordKey(uuid);
        if (!optionalMember.isPresent()){
            return false;
        }

        Member member = optionalMember.get();

        // 초기화 날짜 유효 체크
        if(member.getResetPasswordLimitDt() == null){
            throw new RuntimeException("유효한 날짜가 아닙니다.");
        }

        if(member.getResetPasswordLimitDt().isBefore(LocalDateTime.now())){
            throw new RuntimeException("유효한 날짜가 아닙니다.");
        }

        return true;
    }

    @Override
    public List<MemberDto> list(MemberParam parameter) {

        long totalCount = memberMapper.selectListCount(parameter);

        List<MemberDto> list = memberMapper.selectList(parameter);
        if(!CollectionUtils.isEmpty(list)){
            int i = 0;
            for(MemberDto x: list){
                x.setTotalCount(totalCount);
                x.setSeq(totalCount - parameter.getPageStart());
                i++;
            }
        }

        return list;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> optionalMember = memberRepository.findById(username);
        if (!optionalMember.isPresent()){
            throw new UsernameNotFoundException("회원정보가 전재하지 않습니다.");
        }

        Member member = optionalMember.get();

        if (!member.isEmailAuthYn()){
            throw new MemberNotEmailAuthException("이메일 활성화 이후에 로그인을 해주세요");
        }

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        if (member.isAdminYn()){
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        return new User(member.getUserId(), member.getPassword(), grantedAuthorities);
    }
}
