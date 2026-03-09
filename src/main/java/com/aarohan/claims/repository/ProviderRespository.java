package com.aarohan.claims.repository;

import com.aarohan.claims.model.Provider;

public interface ProviderRespository {
    public Provider findByNpi(String providerNpi);

}
