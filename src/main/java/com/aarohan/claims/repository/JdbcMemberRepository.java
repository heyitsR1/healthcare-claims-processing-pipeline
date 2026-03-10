package com.aarohan.claims.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.aarohan.claims.config.DatabaseConfig;
import com.aarohan.claims.model.Member;
import com.aarohan.claims.model.MemberPlanType;
import com.aarohan.claims.model.MemberStatus;

public class JdbcMemberRepository implements MemberRepository {

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

    public Member findById(String memberId) {
        String sql = "SELECT * FROM members WHERE member_id LIKE ?";
        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);) {

            stmt.setString(1, memberId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Member member = buildMemberFromResultSet(rs);
                    return member;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding member by Id", e);
        }
        return null;
    }

    private Member buildMemberFromResultSet(ResultSet rs) throws SQLException {
        String memberId = rs.getString("member_id");
        String name = rs.getString("name");
        LocalDate dateOfBirth = rs.getDate("date_of_birth").toLocalDate();
        MemberPlanType planType = MemberPlanType.valueOf(rs.getString("plan_type"));
        // would be better if it was enum no?
        LocalDate eligibilityStart = rs.getDate("eligibility_start").toLocalDate();
        LocalDate eligibilityEnd = rs.getDate("eligibility_end").toLocalDate();
        MemberStatus memberStatus = MemberStatus.valueOf(rs.getString("status"));
        LocalDateTime created_at = rs.getTimestamp("created_at").toLocalDateTime();

        Member member = new Member(memberId, name, dateOfBirth, memberStatus);
        member.setCreatedAt(created_at);
        member.setPlanType(planType);
        member.setEligibilityStart(eligibilityStart);
        member.setEligibilityEnd(eligibilityEnd);

        return member;
    }

}
