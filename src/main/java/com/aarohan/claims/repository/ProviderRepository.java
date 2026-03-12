package com.aarohan.claims.repository;

import com.aarohan.claims.model.Provider;

public interface ProviderRepository {
    public Provider findByNpi(String providerNpi);

    public void save(Provider provider);

}
