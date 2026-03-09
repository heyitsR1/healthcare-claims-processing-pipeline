package com.aarohan.claims.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.aarohan.claims.repository.MemberRepository;
import com.aarohan.claims.repository.ProviderRespository;

public class ClaimValidator {

    private MemberRepository memberRepo;
    private ProviderRespository providerRepo;

    public ClaimValidator(MemberRepository memberRepo, ProviderRespository providerRepo) {
        this.memberRepo = memberRepo;
        this.providerRepo = providerRepo;
    }

    public ValidationResult validate(Claim claim) {

        ValidationResult valdiationResult = new ValidationResult();

        // fields missing checks
        if (claim.getClaimId() == null) {
            valdiationResult.addError(ValidationErrorCode.MISSING_CLAIM_ID, "ClaimID is missing");
        }

        if (claim.getMemberId() == null)
            valdiationResult.addError(ValidationErrorCode.MISSING_MEMBER_ID, "Member ID is missing");
        else {
            Member member = memberRepo.findById(claim.getMemberId());
            if (member == null) {
                valdiationResult.addError(ValidationErrorCode.MEMBER_NOT_FOUND, "Member not found");
            } else {

                // check eligibility here but member repo isn't enough made right now so ig
                // first have to do that?
            }
        }

        if (claim.getProviderNpi() == null) {
            valdiationResult.addError(ValidationErrorCode.MISSING_PROVIDER_NPI, "Provider NPI is missing");
        } else {
            Provider provider = providerRepo.findByNpi(claim.getProviderNpi());
            if (provider == null) {
                valdiationResult.addError(ValidationErrorCode.PROVIDER_NOT_FOUND, "Provider Not Found");
            } else {
                // check if in network (provider repo not set)
            }
        }

        if (claim.getBilledAmount() == null) {
            valdiationResult.addError(ValidationErrorCode.MISSING_BILLED_AMOUNT, "Billed Amount is missing");
        } else {

            // billedAmount is within bounds

            if (claim.getBilledAmount().compareTo(new BigDecimal("0")) < 0) {
                valdiationResult.addError(ValidationErrorCode.BILLED_AMOUNT_INVALID, "Billed amount is negative!");
            }
            if (claim.getBilledAmount().compareTo(new BigDecimal("100000")) > 0) {
                String message = String.format("Billed amount is greater than $ %s", Claim.MAX_BILL_AMOUNT);
                valdiationResult.addError(ValidationErrorCode.BILLED_AMOUNT_INVALID, message);
            }
        }

        if (claim.getStatus() == null)
            valdiationResult.addError(ValidationErrorCode.MISSING_STATUS, "Status is missing");

        if (claim.getServiceDate() == null) {
            valdiationResult.addError(ValidationErrorCode.MISSING_SERVICE_DATE, "Service Date is missing");
        } else {

            // check if serviceDate is valid;

            if (claim.getServiceDate().isAfter(LocalDate.now())
                    || claim.getServiceDate().isBefore(LocalDate.now().minusYears(1))) {
                valdiationResult.addError(ValidationErrorCode.SERVICE_DATE_INVALID,
                        "Service date either ahead of Now or Before 1 year.");
            }
        }

        // Diagnosis Code check ()

        if (claim.getDiagnosisCode() == null || claim.getDiagnosisCode().length() != Claim.DIAGNOSIS_CODE_LENGTH) {
            valdiationResult.addError(ValidationErrorCode.DIAGNOSIS_CODE_INVALID, "Invalid Diagnosis Code Length");
        }

        return valdiationResult;

    }

}
