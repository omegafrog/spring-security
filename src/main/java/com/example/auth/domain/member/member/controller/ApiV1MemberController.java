package com.example.auth.domain.member.member.controller;

import com.example.auth.domain.member.member.dto.MemberDto;
import com.example.auth.domain.member.member.entity.Member;
import com.example.auth.domain.member.member.service.MemberService;
import com.example.auth.global.dto.RsData;
import com.example.auth.global.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class ApiV1MemberController {

    private final MemberService memberService;
    private final HttpServletRequest request;

    record JoinReqBody(@NotBlank @Length(min = 3) String username,
                       @NotBlank @Length(min = 3) String password,
                       @NotBlank @Length(min = 3) String nickname) {
    }

    @PostMapping("/join")
    public RsData<MemberDto> join(@RequestBody @Valid JoinReqBody body) {

        memberService.findByUsername(body.username())
                .ifPresent(member -> {
                    throw new ServiceException("400-1", "중복된 아이디입니다.");
                });

        Member member = memberService.join(body.username(), body.password(), body.nickname());

        return new RsData<>(
                "201-1",
                "회원 가입이 완료되었습니다.",
                new MemberDto(member)
        );
    }

    record LoginReqBody(@NotBlank @Length(min = 3) String username, @NotBlank @Length(min = 3) String password) {}

    public record LoginResBody(MemberDto memberDto, String apiKey){}

    @PostMapping
    public RsData<LoginResBody> login(@RequestBody @Valid LoginReqBody body) {

        Member member = memberService.findByUsername(body.username)
                .orElseThrow(() -> new ServiceException("403-1", "login failed."));

        if(!member.getPassword().equals(body.password))
            throw new ServiceException("403-1", "login failed");

        return new RsData<>(
                "200-1",
                "welcome",
                new LoginResBody(new MemberDto(member), member.getApiKey())
        );
    }
    @GetMapping("/me")
    public RsData<MemberDto> me() {
        Member me = getAuthenticatedActor();
        return new RsData<>("200-1", "get me success.", new MemberDto(me));
    }

    private Member getAuthenticatedActor() {
        String authorization = request.getHeader("Authorization").split(" ")[1];

        String password2 = authorization.substring("Bearer ".length());

        Member actor = memberService.findByApiKey(password2)
                .orElseThrow(() -> new ServiceException("401-1", "비밀번호가 틀립니다."));

        return actor;
    }
}
