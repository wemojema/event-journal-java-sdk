package com.eventjournal.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * APIKeys to sign requests using the public and private keys.
 */
public class APIKeys {

    String publicKey;
    String privateKey;
    MessageDigest digest;

    /**
     * @param publicKey  the public key provided by the event-journal.com service. A public key is not sensitive.
     * @param privateKey the private key provided by the event-journal.com service. A private key is sensitive and should never be committed to a repository.
     */
    public APIKeys(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        try {
            this.digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
