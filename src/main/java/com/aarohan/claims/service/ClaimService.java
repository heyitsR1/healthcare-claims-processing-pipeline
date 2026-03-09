package com.aarohan.claims.service;

import com.aarohan.claims.model.ClaimValidator;
import com.aarohan.claims.model.ValidationResult;
import com.aarohan.claims.repository.ClaimRepository;
import com.aarohan.claims.model.Claim;
import com.aarohan.claims.model.ClaimStatus;

public class ClaimService {

    private ClaimValidator claimValidator;
    private ClaimRepository claimRepository;

    public ClaimService(ClaimValidator claimValidator, ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
        this.claimValidator = claimValidator;
    }

    public void processClaim(Claim claim) {
        ValidationResult valdiationResult = claimValidator.validate(claim);
        if (valdiationResult.isValid()) {
            claim.setStatus(ClaimStatus.VALID);
        } else {
            claim.setStatus(ClaimStatus.INVALID);
        }
        claimRepository.save(claim);
    }

}
