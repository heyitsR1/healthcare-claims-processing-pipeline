-- providers table
CREATE TABLE providers (
    npi VARCHAR(10) PRIMARY KEY,
    name VARCHAR(255) NOT NULL, 
    specialty VARCHAR(100), 
    network_status VARCHAR(20) NOT NULL, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- members table
CREATE TABLE members (
    member_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL, 
    date_of_birth DATE NOT NULL, 
    plan_type VARCHAR(50),
    eligibility_start DATE,  
    eligibility_end DATE,
    status VARCHAR(20) NOT NULL, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- claims table
CREATE TABLE claims (
    claim_id VARCHAR(50) PRIMARY KEY, 
    member_id VARCHAR(50) NOT NULL, 
    provider_npi VARCHAR(10) NOT NULL, 
    service_date DATE NOT NULL,
    diagnosis_code VARCHAR(10), 
    procedure_code VARCHAR(10), 
    billed_amount DECIMAL(10,2),
    status VARCHAR(20) NOT NULL, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES members(member_id),
    FOREIGN KEY (provider_npi) REFERENCES providers(npi)
);

-- claim_errors table
CREATE TABLE claim_errors (
    id SERIAL PRIMARY KEY, 
    claim_id VARCHAR(50) NOT NULL, 
    error_code VARCHAR(20) NOT NULL, 
    error_message TEXT NOT NULL,
    severity VARCHAR(20) NOT NULL, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_claims_status ON claims(status); 
CREATE INDEX idx_claims_member ON claims(member_id); 
CREATE INDEX idx_claims_provider ON claims(provider_npi); 
CREATE INDEX idx_claims_service_date ON claims(service_date);

