import java.time.LocalDateTime;

public class Provider {

    private String npi;
    private String name;
    private String specialty;
    private ProviderNetworkStatus networkStatus;

    // DB generated
    private LocalDateTime createdAt;

    public Provider(String npi,
                    String name,
                    String specialty,
                    ProviderNetworkStatus networkStatus) {

        this.npi = npi;
        this.name = name;
        this.specialty = specialty;
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
}