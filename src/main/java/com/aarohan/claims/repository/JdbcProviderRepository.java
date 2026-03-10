package com.aarohan.claims.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import com.aarohan.claims.config.DatabaseConfig;
import com.aarohan.claims.model.Provider;
import com.aarohan.claims.model.ProviderNetworkStatus;

public class JdbcProviderRepository {

    // CREATE TABLE providers (
    // npi VARCHAR(10) PRIMARY KEY,
    // name VARCHAR(255) NOT NULL,
    // specialty VARCHAR(100),
    // network_status VARCHAR(20) NOT NULL,
    // created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    // );

    public Provider findByNpi(String providerNpi) {
        String sql = "SELECT * FROM providers WHERE npi = ?";

        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);

        ) {
            stmt.setString(1, providerNpi);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Provider provider = buildProviderFromResultSet(rs);
                    return provider;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error Fetching Provider using this NPI from database", e);
        }

    }

    private Provider buildProviderFromResultSet(ResultSet rs) throws SQLException {
        String npi = rs.getString("npi");
        String name = rs.getString("name");
        String speciality = rs.getString("specialty");
        ProviderNetworkStatus networkStatus = ProviderNetworkStatus.valueOf(rs.getString("network_status"));
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();

        Provider provider = new Provider(npi, name, networkStatus);
        provider.setCreatedAt(createdAt);
        provider.setSpecialty(speciality);

        return provider;

    }
}
