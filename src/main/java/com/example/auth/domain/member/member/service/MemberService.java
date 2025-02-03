package com.example.auth.domain.member.member.service;

import com.example.auth.domain.member.member.entity.Member;
import com.example.auth.domain.member.member.repository.MemberRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member join(String username, String password, String nickname) {
        Member member = Member.builder()
                .username(username)
                .password(password)
                .apiKey(UUID.randomUUID().toString())
                .nickname(nickname)
                .build();

        return memberRepository.save(member);
    }

    public long count() {
        return memberRepository.count();
    }

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public Optional<Member> findById(@NotNull Long memberId) {
        return memberRepository.findById(memberId);
    }
    public Optional<Member> findByPassword2(String password2){
        return memberRepository.findByApiKey(password2);
    }
}