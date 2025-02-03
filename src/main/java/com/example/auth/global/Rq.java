package com.example.auth.global;

import com.example.auth.domain.member.member.entity.Member;
import com.example.auth.domain.member.member.service.MemberService;
import com.example.auth.global.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@RequiredArgsConstructor
@Component
@RequestScope
public class Rq {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final MemberService memberService;

    public Member getAuthenticatedActor() {
        String authorization = request.getHeader("Authorization");

        String apiKey = authorization.substring("Bearer ".length());

        Member actor = memberService.findByApiKey(apiKey)
                .orElseThrow(() -> new ServiceException("401-1", "비밀번호가 틀립니다."));

        return actor;
    }

}
