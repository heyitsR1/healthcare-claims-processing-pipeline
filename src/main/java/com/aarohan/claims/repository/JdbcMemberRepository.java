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

    public void save(Member member) {
        // check if each field exits or is valid?
        String sql = "INSERT INTO members (member_id, name, date_of_birth, plan_type, eligibility_start, eligibility_end, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, member.getMemberId());
            stmt.setString(2, member.getName());
            stmt.setDate(3, java.sql.Date.valueOf(member.getDateOfBirth()));

            if (member.getPlanType() != null) {
                stmt.setString(4, member.getPlanType().name());
            } else {
                stmt.setNull(4, java.sql.Types.VARCHAR);
            }

            // Handle optional dates

            if (member.getEligibilityStart() != null) {
                stmt.setDate(5, java.sql.Date.valueOf(member.getEligibilityStart()));
            } else {
                stmt.setNull(5, java.sql.Types.DATE);
            }
            if (member.getEligibilityEnd() != null) {
                stmt.setDate(6, java.sql.Date.valueOf(member.getEligibilityEnd()));
            } else {
                stmt.setNull(6, java.sql.Types.DATE);
            }

            // enum status
            stmt.setString(7, member.getStatus().name());

            // created at (insert or keep the current date/time)
            if (member.getCreatedAt() != null) {
                stmt.setTimestamp(8, java.sql.Timestamp.valueOf(member.getCreatedAt()));
            } else {
                stmt.setTimestamp(8, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving member to the db", e);
        }

    }

    private Member buildMemberFromResultSet(ResultSet rs) throws SQLException {
        String memberId = rs.getString("member_id");
        String name = rs.getString("name");
        LocalDate dateOfBirth = rs.getDate("date_of_birth").toLocalDate();
        MemberPlanType planType;
        LocalDate eligibilityStart;
        LocalDate eligibilityEnd;
        LocalDateTime created_at;

        // null check for optional fields
        if (rs.getString("plan_type") != null) {
            planType = MemberPlanType.valueOf(rs.getString("plan_type"));
        } else {
            planType = null;
        }
        if (rs.getDate("eligibility_start") != null) {
            eligibilityStart = rs.getDate("eligibility_start").toLocalDate();
        } else {
            eligibilityStart = null;
        }
        if (rs.getDate("eligibility_end") != null) {
            eligibilityEnd = rs.getDate("eligibility_end").toLocalDate();
        } else {
            eligibilityEnd = null;
        }
        if (rs.getTimestamp("created_at") != null) {
            created_at = rs.getTimestamp("created_at").toLocalDateTime();
        } else {
            created_at = null;
        }

        MemberStatus memberStatus = MemberStatus.valueOf(rs.getString("status"));
        Member member = new Member(memberId, name, dateOfBirth, memberStatus);
        member.setCreatedAt(created_at);
        member.setPlanType(planType);
        member.setEligibilityStart(eligibilityStart);
        member.setEligibilityEnd(eligibilityEnd);

        return member;
    }

}
