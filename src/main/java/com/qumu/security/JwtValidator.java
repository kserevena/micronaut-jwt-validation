package com.qumu.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import io.micronaut.context.annotation.Value;
import io.micronaut.security.token.jwt.signature.SignatureConfiguration;
import jakarta.inject.Singleton;

@Singleton
public class JwtValidator implements SignatureConfiguration {

    @Value("${security.hs256.secret}")
    private String securityToken;

    @Override
    public String supportedAlgorithmsMessage() {
        return null;
    }

    @Override
    public boolean supports(JWSAlgorithm algorithm) {
        return false;
    }

    @Override
    public boolean verify(SignedJWT jwt) throws JOSEException {
        MACVerifier verifier = new MACVerifier(securityToken);
        return jwt.verify(verifier);
    }
}
