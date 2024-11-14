package com.eventjournal.auth;

import com.wemojema.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

class HmacRequestSignerTest extends BaseTest {

    private APIKeys apiKeys;

    @BeforeEach
    public void setup() {
        apiKeys = new APIKeys("public-key","secret-key");
    }

    @Test
    void should_sign_a_request_the_same_everytime() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] signature = HmacRequestSigner.sign(apiKeys.privateKey, "{\"foo\":\"bar\"}", "1731438802975", "dZJSKDdf40saED8Idy4uEg==");
        Assertions.assertEquals("Lqsc/iaW6dgfVeHnC+ZxZxbWRZSoVPgDmT4u4cDkiDA=", Base64.getEncoder().encodeToString(signature));
    }

}