package com.aarohan.claims.repository;

import com.aarohan.claims.model.Claim;
import java.util.List;

public interface ClaimRepository {
    void save (Claim claim); 
    Claim findById(String claimId); 
    List <Claim> findAll();
    
}
