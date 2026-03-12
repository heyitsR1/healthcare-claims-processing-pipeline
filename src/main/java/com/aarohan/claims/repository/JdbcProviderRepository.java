package com.aarohan.claims.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import com.aarohan.claims.config.DatabaseConfig;
import com.aarohan.claims.model.Provider;
import com.aarohan.claims.model.ProviderNetworkStatus;

public class JdbcProviderRepository implements ProviderRepository {

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

    public void save(Provider provider) {
        String sql = "INSERT INTO providers (npi,name,specialty,network_status,created_at) VALUES (?,?,?,?,?)";

        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setString(1, provider.getNpi());
            stmt.setString(2, provider.getName());
            stmt.setString(3, provider.getSpecialty());
            stmt.setString(4, provider.getNetworkStatus().name());

            if (provider.getCreatedAt() != null) {
                stmt.setTimestamp(5, java.sql.Timestamp.valueOf(provider.getCreatedAt()));
            } else {
                stmt.setTimestamp(5, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            }
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error saving provider to the database");
        }

    }

    private Provider buildProviderFromResultSet(ResultSet rs) throws SQLException {
        String npi = rs.getString("npi");
        String name = rs.getString("name");
        ProviderNetworkStatus networkStatus = ProviderNetworkStatus.valueOf(rs.getString("network_status"));
        String specialty;
        LocalDateTime createdAt;

        // null check for optional fields

        if (rs.getTimestamp("created_at") != null) {
            createdAt = rs.getTimestamp("created_at").toLocalDateTime();
        } else {
            createdAt = null;
        }
        if (rs.getString("specialty") != null) {
            specialty = rs.getString("specialty");
        } else {
            specialty = null;
        }

        Provider provider = new Provider(npi, name, networkStatus);
        provider.setCreatedAt(createdAt);
        provider.setSpecialty(specialty);

        return provider;

    }
}
