package com.aarohan.claims.repository;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.aarohan.claims.model.Claim;
import com.aarohan.claims.model.ClaimStatus;
import com.aarohan.claims.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ClaimRepositoryTest {

    private ClaimRepository repository;
    private Connection testConnection;

    @Before
    public void setUp() throws SQLException {
        repository = new JdbcClaimRepository();
        testConnection = DatabaseConfig.getDataSource().getConnection();
        testConnection.setAutoCommit(false);

        // Clean up test data first
        String deleteClaimsSql = "DELETE FROM claims WHERE claim_id LIKE 'TEST_%'";
        try (PreparedStatement stmt = testConnection.prepareStatement(deleteClaimsSql)) {
            stmt.executeUpdate();
        }

        // Insert test provider if it doesn't exist
        String insertProviderSql = "INSERT INTO providers (npi, name, specialty, network_status) VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING";
        try (PreparedStatement stmt = testConnection.prepareStatement(insertProviderSql)) {
            stmt.setString(1, "1234567890");
            stmt.setString(2, "Test Provider");
            stmt.setString(3, "General");
            stmt.setString(4, "ACTIVE");
            stmt.executeUpdate();
        } catch (SQLException e) {
            // Handle databases that don't support ON CONFLICT
            try {
                String checkProvider = "SELECT 1 FROM providers WHERE npi = ?";
                try (PreparedStatement checkStmt = testConnection.prepareStatement(checkProvider)) {
                    checkStmt.setString(1, "1234567890");
                    if (!checkStmt.executeQuery().next()) {
                        String insertNoConflict = "INSERT INTO providers (npi, name, specialty, network_status) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement insertStmt = testConnection.prepareStatement(insertNoConflict)) {
                            insertStmt.setString(1, "1234567890");
                            insertStmt.setString(2, "Test Provider");
                            insertStmt.setString(3, "General");
                            insertStmt.setString(4, "ACTIVE");
                            insertStmt.executeUpdate();
                        }
                    }
                }
            } catch (SQLException ignored) {
            }
        }

        // Insert test member if it doesn't exist
        String insertMemberSql = "INSERT INTO members (member_id, name, date_of_birth, plan_type, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = testConnection.prepareStatement(insertMemberSql)) {
            stmt.setString(1, "MEM123");
            stmt.setString(2, "Test Member");
            stmt.setDate(3, Date.valueOf(LocalDate.of(1990, 1, 1)));
            stmt.setString(4, "HMO");
            stmt.setString(5, "ACTIVE");
            stmt.executeUpdate();
        } catch (SQLException e) {
            // Member may already exist, which is fine
        }

        testConnection.commit();
    }

    @After
    public void tearDown() throws SQLException {
        // Clean up test claims
        String deleteClaimsSql = "DELETE FROM claims WHERE claim_id LIKE 'TEST_%'";
        try (PreparedStatement stmt = testConnection.prepareStatement(deleteClaimsSql)) {
            stmt.executeUpdate();
        }
        testConnection.commit();
        if (testConnection != null && !testConnection.isClosed()) {
            testConnection.close();
        }
    }

    /**
     * TEST CASE 1: Save a valid claim and verify it's inserted
     * 
     * Pattern (Given-When-Then):
     * - Given: A valid claim object
     * - When: We call save()
     * - Then: The claim should exist in database
     */
    @Test
    public void shouldSaveValidClaim() throws SQLException {
        // GIVEN: A valid test claim
        Claim testClaim = new Claim(
                "TEST_CLM001", // claim ID (prefixed with TEST_ for cleanup)
                "MEM123", // member ID (must exist in members table or FK will fail)
                "1234567890", // provider NPI (must exist in providers table or FK will fail)
                LocalDate.of(2025, 3, 1), // service date
                new BigDecimal("1500.00"), // billed amount
                ClaimStatus.PENDING // status
        );

        // WHEN: We save the claim
        repository.save(testClaim);

        // THEN: We should be able to retrieve it and verify it matches
        Claim retrieved = repository.findById("TEST_CLM001");
        assertNotNull("Claim should exist in database", retrieved);
        assertEquals("Claim ID should match", testClaim.getClaimId(), retrieved.getClaimId());
        assertEquals("Member ID should match", testClaim.getMemberId(), retrieved.getMemberId());
        assertEquals("Status should match", testClaim.getStatus(), retrieved.getStatus());
    }

    /**
     * TEST CASE 2: FindById should return null when claim doesn't exist
     * 
     * Pattern:
     * - When: We search for a claim that doesn't exist
     * - Then: It should return null (not throw exception)
     */
    @Test
    public void shouldReturnNullWhenClaimNotFound() {
        // WHEN: We search for a claim that doesn't exist
        Claim result = repository.findById("NONEXISTENT_CLAIM");

        // THEN: Should return null (not throw exception)
        assertNull("Should return null when claim not found", result);
    }

    /**
     * TEST CASE 3: FindAll should return all claims
     * 
     * Pattern:
     * - Given: Multiple claims in database
     * - When: We call findAll()
     * - Then: List should contain all our test claims
     * 
     * IMPORTANT: This test assumes other tests might leave data or
     * you might count only TEST_ prefixed claims
     */
    @Test
    public void shouldReturnAllClaims() throws SQLException {
        // GIVEN: Insert multiple test claims
        Claim claim1 = new Claim("TEST_CLM002", "MEM123", "1234567890",
                LocalDate.of(2025, 3, 1), new BigDecimal("1500.00"), ClaimStatus.PENDING);
        Claim claim2 = new Claim("TEST_CLM003", "MEM123", "1234567890",
                LocalDate.of(2025, 3, 2), new BigDecimal("2000.00"), ClaimStatus.VALID);

        repository.save(claim1);
        repository.save(claim2);

        // WHEN: We call findAll()
        List<Claim> allClaims = repository.findAll();

        // THEN: Should contain at least our 2 test claims
        assertNotNull("Claim list should not be null", allClaims);
        assertTrue("Should have at least 2 claims", allClaims.size() >= 2);
    }

    @Test
    public void shouldHandleSaveWithOptionalFields() {

        Claim testClaim = new Claim("TEST_CLM_004", "MEM123", "1234567890", LocalDate.of(2026, 03, 07),
                new BigDecimal("169.420"), ClaimStatus.PENDING);
        testClaim.setDiagnosisCode("extra");
        testClaim.setProcedureCode("PROC_T");

        repository.save(testClaim);

        Claim retrievedClaim = repository.findById("TEST_CLM_004");
        assertNotNull("Claim is saved to the db", retrievedClaim);
        assertEquals("Claim with optional fields should save to the database", testClaim.getMemberId(),
                retrievedClaim.getMemberId());
        assertEquals("service date should match", testClaim.getServiceDate(), retrievedClaim.getServiceDate());

    }

    @Test
    public void shouldHandleNullDiagnosisCode() {
        Claim testClaim = new Claim("TEST_CLM_005", "MEM123", "1234567890", LocalDate.of(2026, 03, 07),
                new BigDecimal("124.344"), ClaimStatus.VALID);
        testClaim.setDiagnosisCode(null);

        repository.save(testClaim);

        Claim retrievedClaim = repository.findById("TEST_CLM_005");
        assertNotNull("Save was successful", retrievedClaim);
        assertEquals("Saved Diagnostic code is null", null, retrievedClaim.getDiagnosisCode());

    }

    @Test
    public void shouldHandleLargeBilledAmount() {
        Claim testClaim = new Claim("TEST_CLM_006", "MEM123", "1234567890", LocalDate.of(2026, 03, 07),
                new BigDecimal("99999999.99"), ClaimStatus.VALID);
        repository.save(testClaim);

        Claim retrievedClaim = repository.findById("TEST_CLM_006");
        assertEquals("Large Billed Amount is saved correctly", retrievedClaim.getBilledAmount(),
                testClaim.getBilledAmount());

    }

}
