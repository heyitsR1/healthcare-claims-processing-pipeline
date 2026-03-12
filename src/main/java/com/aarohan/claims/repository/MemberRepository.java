package com.aarohan.claims.repository;

import com.aarohan.claims.model.Member;

public interface MemberRepository {

    public Member findById(String memberId);

    public void save(Member member);

}
