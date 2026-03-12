package com.aarohan.claims.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aarohan.claims.config.DatabaseConfig;
import com.aarohan.claims.model.Provider;
import com.aarohan.claims.model.ProviderNetworkStatus;

public class ProviderRepositoryTest {
    private Connection testConnection;
    private ProviderRepository providerRepository;

    @Before()
    public void setUp() throws SQLException {
        testConnection = DatabaseConfig.getConnection();
        testConnection.setAutoCommit(false);

        // clean up. old data if remaining (redudnacy check)
        String deletePrevious = "DELETE FROM providers WHERE npi LIKE 'TEST_%' ";
        try (PreparedStatement stmt = testConnection.prepareStatement(deletePrevious)) {
            stmt.executeUpdate();
        }

        // insert new
        String setUPString = "INSERT INTO providers (npi, name, specialty, network_status, created_at) VALUES (?,?,?,?,?)";
        try (PreparedStatement stmt = testConnection.prepareStatement(setUPString)) {
            stmt.setString(1, "TEST_NPI");
            stmt.setString(2, "TEST PROVIDER");
            stmt.setString(3, "TEST SPECIALTY");
            stmt.setString(4, ProviderNetworkStatus.IN_NETWORK.name());
            stmt.setTimestamp(5, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();
        }
        testConnection.commit();

    }

    @After()
    public void tearDown() throws SQLException {
        String deletePrevious = "DELETE FROM providers WHERE npi LIKE 'TEST_%' ";
        try (PreparedStatement stmt = testConnection.prepareStatement(deletePrevious)) {
            stmt.executeUpdate();
        }
        testConnection.commit();
        if (testConnection != null && !testConnection.isClosed()) {
            testConnection.close();
        }

    }

    @Test
    public void shouldSaveAndFindProvider() throws SQLException {
        Provider provider = new Provider("TEST_001", "Test Provider", ProviderNetworkStatus.OUT_OF_NETWORK);

        providerRepository.save(provider);
        Provider retrievedProvider = providerRepository.findByNpi("TEST_001");

        assertNotNull(retrievedProvider);
        assertEquals(provider.getNpi(), retrievedProvider.getNpi());
        assertEquals(provider.getCreatedAt(), retrievedProvider.getCreatedAt());
    }

    @Test
    public void shouldReturnNullWhenProviderNotFound() throws SQLException {
        Provider retrievedProvider = providerRepository.findByNpi("NO_EZXIT");
        assertNull(retrievedProvider);
    }
}
