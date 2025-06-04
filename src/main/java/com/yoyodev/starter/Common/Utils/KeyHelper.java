package com.yoyodev.starter.Common.Utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@Slf4j
public class KeyHelper {
    @Value("${jwt.rsa.path.private-key}")
    private String privateKeyPath;
    @Value("${jwt.rsa.path.public-key}")
    private String publicKeyPath;

    @Bean
    public PublicKey getPublicKey() throws Exception {
        log.info("Loading public key, path: {}", publicKeyPath);
        try{
            return getPublicKey(publicKeyPath);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new Exception("Error while loading public key from file");
        }
    }

    @Bean
    public PrivateKey getPrivateKey() throws Exception {
        log.info("Loading private key, path: {}", privateKeyPath);
        try {
            return getPrivateKey(privateKeyPath);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new Exception("Error while loading private key from file");
        }
    }

    private PrivateKey getPrivateKey(String filepath) throws Exception {
        String privateKeyPEM = extractKeyFromFile(filepath);
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] decoded = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    private PublicKey getPublicKey(String filepath) throws Exception {
        String publicKeyPEM = extractKeyFromFile(filepath);
        publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----", "").
                replace("-----END PUBLIC KEY-----", "").
                replaceAll("\\s+", "");
        byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    private String extractKeyFromFile(String filepath) throws IOException {
        File file = new File(filepath);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] keyBytes = new byte[(int) file.length()];
        fileInputStream.read(keyBytes);
        fileInputStream.close();
        return new String(keyBytes);
    }
}
