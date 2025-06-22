package com.yoyodev.starter.AOP.Jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    @Value("${jwt.expiration-time-in-second}")
    private Integer expirationTimeInSecond;
    @Value("${spring.application.name}")
    private String issuer;
    @Value("${jwt.clock-skew-in-second}")
    private Integer clockSkewInSecond;

    public String generateToken(String username) throws Exception {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(issuer)
                .subject(username)
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + expirationTimeInSecond * 1000))
                .build();
        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.RS256), claims.toPayload());
        JWSSigner signer = new RSASSASigner(privateKey);
        jwsObject.sign(signer);
        return jwsObject.serialize();
    }

    public String parseSubject(String token) throws Exception {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier rsaVerifier = new RSASSAVerifier((RSAPublicKey) publicKey);
        signedJWT.verify(rsaVerifier);

        JWTClaimsSet exactMatchClaims = new JWTClaimsSet.Builder()
                .issuer(issuer)
                .build();
        Set<String> requiredClaims = new HashSet<>(Arrays.asList("sub", "iat", "exp"));
        DefaultJWTClaimsVerifier<?> claimsVerifier = new DefaultJWTClaimsVerifier<>(exactMatchClaims, requiredClaims);
        claimsVerifier.setMaxClockSkew(clockSkewInSecond);
        claimsVerifier.verify(signedJWT.getJWTClaimsSet(), null);

        return signedJWT.getJWTClaimsSet().getSubject();
    }
}
