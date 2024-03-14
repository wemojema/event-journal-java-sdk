package com.eventjournal.auth;

import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

public class APIKeys {

    String publicKey;
    String privateKey;
    MessageDigest digest;

    public APIKeys(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        try {
            this.digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String sign(HttpRequest.BodyPublisher body, Instant timestamp) {
        digest.digest(new String(timestamp.toString() + body.toString() + privateKey).getBytes(StandardCharsets.UTF_8));
        return null;
    }
}
