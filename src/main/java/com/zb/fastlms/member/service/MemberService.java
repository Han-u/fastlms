package com.zb.fastlms.member.service;

import com.zb.fastlms.member.model.MemberInput;

public interface MemberService {
    boolean register(MemberInput parameter);

    /**
     * uuid에 해당하는 계정을 활성화 함.
     */
    boolean emailAuth(String uuid);
}
