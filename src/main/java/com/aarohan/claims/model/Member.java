package com.aarohan.claims.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Member {

    private String memberId;
    private String name;
    private LocalDate dateOfBirth;
    private String planType;
    private LocalDate eligibilityStart;
    private LocalDate eligibilityEnd;
    private MemberStatus status;

    // DB generated
    private LocalDateTime createdAt;

    public Member(String memberId,
                  String name,
                  LocalDate dateOfBirth,
                  String planType,
                  LocalDate eligibilityStart,
                  LocalDate eligibilityEnd,
                  MemberStatus status) {

        this.memberId = memberId;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.planType = planType;
        this.eligibilityStart = eligibilityStart;
        this.eligibilityEnd = eligibilityEnd;
        this.status = status;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPlanType() {
        return planType;
    }

    public LocalDate getEligibilityStart() {
        return eligibilityStart;
    }

    public LocalDate getEligibilityEnd() {
        return eligibilityEnd;
    }

    public MemberStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}