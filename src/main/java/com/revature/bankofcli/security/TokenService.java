package com.revature.bankofcli.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.revature.bankofcli.domain.Account;

@Service
public class TokenService {

    private final JwtEncoder encoder;
    private final long expirationMinutes;

    public TokenService(JwtEncoder encoder, @Value("${jwt.expiration-minutes}") long expirationMinutes) {
        this.encoder = encoder;
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(Account account) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("bank-of-cli")
                .issuedAt(now)
                .expiresAt(now.plus(expirationMinutes, ChronoUnit.MINUTES))
                .subject(String.valueOf(account.getId()))
                .claim("email", account.getEmail())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build(); // required: default is RS256
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
