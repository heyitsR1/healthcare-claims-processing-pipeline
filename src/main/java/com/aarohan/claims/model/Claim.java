import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;

public class Claim {
    private String claimId; 
    private String memberId; 
    private String providerNpi; 
    private LocalDate serviceDate; 
    private String diagnosisCode; 
    private String procedureCode;
    private BigDecimal billedAmount; 
    private ClaimStatus status;
    private LocalDateTime createdAt; 
    private LocalDateTime processedAt; 
    
    public Claim (String claimId, String memberId, String providerNpi, 
        LocalDate serviceDate, BigDecimal billedAmount, 
        ClaimStatus status){
        
            this.claimId = claimId; 
            this.memberId = memberId; 
            this.providerNpi = providerNpi; 
            this.serviceDate = serviceDate;
            this.status = status;
            this.createdAt = LocalDateTime.now();

    };

    // public Claim () {};

    @Override
    public String toString() { 
        return "Claim {" +
                "claimId = '" + getClaimId()+ '\'' + 
                ",memberId='" + getMemberId()+ '\'' + 
                ",status=" + getStatus() + 
                '}';
    }

    public String getClaimId (){ 
        return claimId; 
    }
    public void setClaimId (String claimId) { 
        this.claimId = claimId;
    }

    public String getMemberId () { 
        return memberId; 
    }
    public void setMemberId (String memberId) { 
        this.memberId = memberId;
    }

    public String getProviderNpi () { 
        return providerNpi; 
    }
    public void setProviderNpi(String providerNpi) { 
        this.providerNpi = providerNpi;
    }
    public LocalDate getServiceDate() { 
        return serviceDate; 
    }
    public void setServiceDate (LocalDate serviceDate) { 
        this.serviceDate = serviceDate;
    }
    public String getDiagnosisCode () { 
        return diagnosisCode; 
    }
    public void setDiagnosisCode (String diagnosisCode) { 
        this.diagnosisCode = diagnosisCode;
    }
    public String getProcedureCode () { 
        return procedureCode; 
    }
    public void setProcedureCode (String procedureCode) { 
        this.procedureCode = procedureCode;
    }
    public BigDecimal getBilledAmount () { 
        return billedAmount; 
    }
    public void setBilledAmount (BigDecimal billedAmount) { 
        this.billedAmount = billedAmount;
    }
    public ClaimStatus getStatus () { 
        return status; 
    }
    public void setStatus (ClaimStatus status) { 
        this.status = status;
    }
    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    // public void setCreatedAt (LocalDateTime createdAt) { 
    //     this.createdAt = createdAt;
    // }
    public LocalDateTime getProcessedAt() { 
        return processedAt; 
    }
    
    public void setProcessedAt (LocalDateTime processedAt) { 
        this.processedAt = processedAt;
    }
}
 