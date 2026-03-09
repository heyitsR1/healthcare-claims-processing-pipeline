package com.aarohan.claims.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import com.aarohan.claims.model.Claim;
import com.aarohan.claims.model.ClaimStatus;
import com.aarohan.claims.config.DatabaseConfig;

public class JdbcClaimRepository implements ClaimRepository {

    public void save(Claim claim) {
        String sql = "INSERT INTO claims (claim_id, member_id, provider_npi, service_date, billed_amount, status) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, claim.getClaimId());
            stmt.setString(2, claim.getMemberId());
            stmt.setString(3, claim.getProviderNpi());
            stmt.setDate(4, Date.valueOf(claim.getServiceDate()));
            stmt.setBigDecimal(5, claim.getBilledAmount());
            stmt.setString(6, claim.getStatus().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Claim findById(String claimId) {
        String sql = "SELECT * FROM claims WHERE claim_id = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, claimId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Claim claim = buildClaimFromResultSet(rs);
                    return claim;
                }
                return null;
            } catch (SQLException e) {
                throw new RuntimeException("Failed to find claim by ID: " + claimId, e);
                // e.printStackTrace();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find claim by ID: " + claimId, e);
            // e.printStackTrace();
        }
        // return null;
    }

    public List<Claim> findAll() {
        String sql = "SELECT * FROM claims";
        List<Claim> claims = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (

                    ResultSet rs = stmt.executeQuery();) {

                while (rs.next()) {
                    Claim claim = buildClaimFromResultSet(rs);
                    claims.add(claim);
                }
                return claims;
            } catch (SQLException e) {
                throw new RuntimeException("Failed to find all claims: ", e);
                // e.printStackTrace();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all claims: ", e);
            // e.printStackTrace();
        }
        // return claims;
    }

    private Claim buildClaimFromResultSet(ResultSet rs) throws SQLException {
        String claimId = rs.getString("claim_id");
        String memberId = rs.getString("member_id");
        String providerNpi = rs.getString("provider_npi");
        LocalDate serviceDate = rs.getDate("service_date").toLocalDate();
        BigDecimal billedAmount = rs.getBigDecimal("billed_amount");
        ClaimStatus status = ClaimStatus.valueOf(rs.getString("status"));

        Claim claim = new Claim(claimId, memberId, providerNpi, serviceDate, billedAmount, status);

        // Set optional fields
        String diagnosisCode = rs.getString("diagnosis_code");
        if (diagnosisCode != null) {
            claim.setDiagnosisCode(diagnosisCode);
        }

        String procedureCode = rs.getString("procedure_code");
        if (procedureCode != null) {
            claim.setProcedureCode(procedureCode);
        }

        java.sql.Timestamp processedAtTs = rs.getTimestamp("processed_at");
        if (processedAtTs != null) {
            claim.setProcessedAt(processedAtTs.toLocalDateTime());
        }

        return claim;
    }

}
