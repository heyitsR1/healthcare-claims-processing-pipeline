package com.aarohan.claims.model;

import java.time.LocalDateTime;

public class Provider {

    // CREATE TABLE providers (
    // npi VARCHAR(10) PRIMARY KEY,
    // name VARCHAR(255) NOT NULL,
    // specialty VARCHAR(100),
    // network_status VARCHAR(20) NOT NULL,
    // created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    // );

    private String npi;
    private String name;
    private String specialty;
    private ProviderNetworkStatus networkStatus;

    // DB generated
    private LocalDateTime createdAt;

    public Provider(String npi,
            String name,
            ProviderNetworkStatus networkStatus) {

        this.npi = npi;
        this.name = name;
        this.networkStatus = networkStatus;
    }

    public String getNpi() {
        return npi;
    }

    public String getName() {
        return name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public ProviderNetworkStatus getNetworkStatus() {
        return networkStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // kinda bad design ; creation date can be overriden; can be fixed using a
    // builder Pattern
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
}