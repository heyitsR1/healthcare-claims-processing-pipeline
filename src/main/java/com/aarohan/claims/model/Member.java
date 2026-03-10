package com.aarohan.claims.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Member {

    // CREATE TABLE members (
    // member_id VARCHAR(50) PRIMARY KEY,
    // name VARCHAR(255) NOT NULL,
    // date_of_birth DATE NOT NULL,
    // plan_type VARCHAR(50),
    // eligibility_start DATE,
    // eligibility_end DATE,
    // status VARCHAR(20) NOT NULL,
    // created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    // );

    private String memberId;
    private String name;
    private LocalDate dateOfBirth;
    private MemberPlanType planType;
    private LocalDate eligibilityStart;
    private LocalDate eligibilityEnd;
    private MemberStatus status;

    // DB generated
    private LocalDateTime createdAt;

    public Member(String memberId,
            String name,
            LocalDate dateOfBirth,
            MemberStatus status) {

        this.memberId = memberId;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
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

    public MemberPlanType getPlanType() {
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

    // kinda bad design ; creation date can be overriden; can be fixed using a
    // builder Pattern
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setPlanType(MemberPlanType planType) {
        this.planType = planType;
    }

    public void setEligibilityStart(LocalDate eligibilityStart) {
        this.eligibilityStart = eligibilityStart;
    }

    public void setEligibilityEnd(LocalDate eligibilityEnd) {
        this.eligibilityEnd = eligibilityEnd;
    }

}