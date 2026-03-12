package com.aarohan.claims.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.aarohan.claims.repository.MemberRepository;
import com.aarohan.claims.repository.ProviderRepository;

public class ClaimValidator {

    private MemberRepository memberRepo;
    private ProviderRepository providerRepo;

    public ClaimValidator(MemberRepository memberRepo, ProviderRepository providerRepo) {
        this.memberRepo = memberRepo;
        this.providerRepo = providerRepo;
    }

    // to do: break this monolithic function into helper functions

    public ValidationResult validate(Claim claim) {

        ValidationResult validationResult = new ValidationResult();

        // simple checks
        if (claim.getClaimId() == null) {
            validationResult.addError(ValidationErrorCode.MISSING_CLAIM_ID, "ClaimID is missing");
        }
        if (claim.getStatus() == null)
            validationResult.addError(ValidationErrorCode.MISSING_STATUS, "Status is missing");
        // Diagnosis Code check ()
        if (claim.getDiagnosisCode() == null || claim.getDiagnosisCode().length() != Claim.DIAGNOSIS_CODE_LENGTH) {
            validationResult.addError(ValidationErrorCode.DIAGNOSIS_CODE_INVALID, "Invalid Diagnosis Code Length");
        }

        // more complex checks for other fields

        // need some kind of append method
        validationResult.merge(validateMember(claim));
        validationResult.merge(validateProvider(claim));
        validationResult.merge(validateServiceDates(claim));
        validationResult.merge(validateBilledAmount(claim));

        return validationResult;

    }

    public ValidationResult validateMember(Claim claim) {
        ValidationResult validationResult = new ValidationResult();
        if (claim.getMemberId() == null)
            validationResult.addError(ValidationErrorCode.MISSING_MEMBER_ID, "Member ID is missing");
        else {
            Member member = memberRepo.findById(claim.getMemberId());
            if (member == null) {
                validationResult.addError(ValidationErrorCode.MEMBER_NOT_FOUND, "Member not found");
            } else {
                if (member.getStatus() != MemberStatus.ACTIVE) {
                    validationResult.addError(ValidationErrorCode.MEMBER_INACTIVE, "Member not active");
                }
            }
        }
        return validationResult;
    }

    public ValidationResult validateProvider(Claim claim) {
        ValidationResult validationResult = new ValidationResult();
        if (claim.getProviderNpi() == null) {
            validationResult.addError(ValidationErrorCode.MISSING_PROVIDER_NPI, "Provider NPI is missing");
        } else {
            Provider provider = providerRepo.findByNpi(claim.getProviderNpi());
            if (provider == null) {
                validationResult.addError(ValidationErrorCode.PROVIDER_NOT_FOUND, "Provider Not Found");
            } else {
                if (provider.getNetworkStatus() != (ProviderNetworkStatus.IN_NETWORK)) {
                    validationResult.addError(ValidationErrorCode.PROVIDER_OUT_OF_NETWORK, "Provider Not in Network");
                }
            }
        }
        return validationResult;
    }

    public ValidationResult validateServiceDates(Claim claim) {
        ValidationResult validationResult = new ValidationResult();

        if (claim.getServiceDate() == null) {
            validationResult.addError(ValidationErrorCode.MISSING_SERVICE_DATE, "Service Date is missing");
        } else {
            // check if serviceDate is valid;
            if (claim.getServiceDate().isAfter(LocalDate.now())
                    || claim.getServiceDate().isBefore(LocalDate.now().minusYears(1))) {
                validationResult.addError(ValidationErrorCode.SERVICE_DATE_INVALID,
                        "Service date either ahead of Now or Before 1 year.");
            }
        }

        return validationResult;

    }

    public ValidationResult validateBilledAmount(Claim claim) {
        ValidationResult validationResult = new ValidationResult();
        if (claim.getBilledAmount() == null) {
            validationResult.addError(ValidationErrorCode.MISSING_BILLED_AMOUNT, "Billed Amount is missing");
        } else {
            // billedAmount is within bounds

            if (claim.getBilledAmount().compareTo(BigDecimal.ZERO) < 0) {
                validationResult.addError(ValidationErrorCode.BILLED_AMOUNT_INVALID, "Billed amount is negative!");
            }
            if (claim.getBilledAmount().compareTo(new BigDecimal("100000")) > 0) {
                String message = String.format("Billed amount is greater than $ %s", Claim.MAX_BILL_AMOUNT);
                validationResult.addError(ValidationErrorCode.BILLED_AMOUNT_INVALID, message);
            }
        }
        return validationResult;
    }
}
