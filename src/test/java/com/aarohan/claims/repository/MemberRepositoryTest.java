package com.aarohan.claims.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.management.MemoryMXBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aarohan.claims.config.DatabaseConfig;
import com.aarohan.claims.model.Member;
import com.aarohan.claims.model.MemberPlanType;
import com.aarohan.claims.model.MemberStatus;

public class MemberRepositoryTest {

    private MemberRepository memberRepository;
    private Connection testConnection;

    @Before
    public void setUp() throws SQLException {
        memberRepository = new JdbcMemberRepository();
        testConnection = DatabaseConfig.getConnection();
        testConnection.setAutoCommit(false); // have to dig deeper into this choice

        // clean up test data first
        String deleteSql = "DELETE FROM members WHERE member_id LIKE 'TEST_%'";
        try (PreparedStatement stmt = testConnection.prepareStatement(deleteSql)) {
            stmt.executeUpdate();
        }
        // upload new data?
        String insertSQL = "INSERT INTO members (member_id,name,date_of_birth,plan_type,eligibility_start, eligibility_end,status,created_at) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = testConnection.prepareStatement(insertSQL);) {
            stmt.setString(1, "TEST_1");
            stmt.setString(2, "Test Member");
            stmt.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            stmt.setString(4, MemberPlanType.HMO.name());
            stmt.setDate(5, java.sql.Date.valueOf(LocalDate.now().minusYears(1)));
            stmt.setDate(6, java.sql.Date.valueOf(LocalDate.now().plusYears(2)));
            stmt.setString(7, MemberStatus.ACTIVE.name());
            stmt.setTimestamp(8, java.sql.Timestamp.valueOf(LocalDateTime.now()));

        }
        testConnection.commit();

    }

    @After
    public void tearDown() throws SQLException {
        String deleteMemberSql = "DELETE FROM members WHERE member_id LIKE 'TEST_%'";
        try (PreparedStatement stmt = testConnection.prepareStatement(deleteMemberSql);) {
            stmt.executeUpdate();
        }
        testConnection.commit();
        if (testConnection != null && !testConnection.isClosed()) {
            testConnection.close();
        }

    }

    @Test
    public void shouldSaveValidMemberWithOptionalFields() throws SQLException {
        Member member = new Member(
                "TEST_1",
                "Test Member",
                LocalDate.of(2004, 12, 23),
                MemberStatus.ACTIVE

        );
        memberRepository.save(member);

        Member retrievedMember = memberRepository.findById(member.getMemberId());
        assertNotNull(retrievedMember);
        assertEquals(retrievedMember.getMemberId(), member.getMemberId());
        assertEquals(retrievedMember.getDateOfBirth(), member.getDateOfBirth());

    }

    @Test
    public void shouldSaveValidMemberWithAllFields() throws SQLException {
        Member member = new Member(
                "TEST_2",
                "Test Member 2",
                LocalDate.of(2003, 2, 13),
                MemberStatus.ACTIVE);
        member.setEligibilityStart(LocalDate.now());
        member.setEligibilityEnd(LocalDate.now());
        member.setPlanType(MemberPlanType.PPO);

        memberRepository.save(member);

        Member retrievedMember = memberRepository.findById("TEST_2");

        assertNotNull(retrievedMember);
        assertEquals(member.getMemberId(), retrievedMember.getMemberId());
    }

    @Test
    public void shouldReturnNullWhenMemberNotFound() throws SQLException {
        Member member = memberRepository.findById("NO_EXIST");
        assertNull(member);
    }
}
