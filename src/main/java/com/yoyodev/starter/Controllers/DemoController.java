package com.yoyodev.starter.Controllers;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.yoyodev.starter.Common.Constants.EndpointConstants;
import com.yoyodev.starter.Common.Utils.AlgorithmHelper;
import com.yoyodev.starter.Entities.RefreshToken;
import com.yoyodev.starter.Model.Response.AuthModel;
import com.yoyodev.starter.Model.Response.ResponseWrapper;
import com.yoyodev.starter.Repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(EndpointConstants.API_V1 + "/demo")
@RequiredArgsConstructor
public class DemoController extends BaseController {
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final RefreshTokenRepository refreshTokenRepository;
    @GetMapping
    public ResponseEntity<ResponseWrapper<String>> demo() {
        return getSuccess();
    }

    @PostMapping("/get-error-jwt")
    public ResponseEntity<?> getErrorJwt(@RequestBody Map<String, String> body) throws JOSEException {
        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder();
        body.forEach(
                (key, value) -> {
                    if(key.equals("iat") || key.equals("exp")) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = null;
                        try {
                            date = sdf.parse(value);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        claimsBuilder.claim(key, date);
                    }
                    else {
                        claimsBuilder.claim(key, value);
                    }
                }
        );

        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.RS256), claimsBuilder.build().toPayload());
        JWSSigner signer = new RSASSASigner(privateKey);
        jwsObject.sign(signer);
        String jwt = jwsObject.serialize();


        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(-1L);
        String randomToken = UUID.randomUUID().toString();
        refreshToken.setToken(randomToken);
        refreshToken.setExpirationTime(Timestamp.valueOf(LocalDateTime.now().plusDays(1)));
        refreshTokenRepository.save(refreshToken);

        return getSuccess(new AuthModel(jwt, AlgorithmHelper.generateMD5(refreshToken.getToken())));
    }
}
