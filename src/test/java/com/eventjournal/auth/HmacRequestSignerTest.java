package com.eventjournal.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.Base64;

class HmacRequestSignerTest {


    public static class HmacRequestSignerTester extends HmacRequestSigner {
        public static byte[] signRequest(final String secretKey, final String data, final String timestamp, final String nonce) {
            try {
                return sign(secretKey, data, timestamp, nonce);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    void should_sign_an_empty_body_request_without_any_problems() {
        byte[] result = HmacRequestSignerTester.signRequest("456", "", "0", "654");
        String s = new String(Base64.getEncoder().encode(result));
        Assertions.assertEquals("1vuKvNtLnbDE6W9d6KEz08TfbK8KXcjSkVet6EJl210=", s);
    }

    @Test
    void should_sign_a_request_with_a_body() {
        byte[] result = HmacRequestSignerTester.signRequest("456", "data", "0", "654");
        String s = new String(Base64.getEncoder().encode(result));
        Assertions.assertEquals("xDKWDDm05PP8t2+qlSvrqywqNcxdhGC4s5eeE0jGlEc=", s);
    }

    @Test
    void should_generate_the_data_to_sign_correctly() {
        String result = HmacRequestSignerTester.formatSigningData("123", "host", "url", "body");
        Assertions.assertEquals("pk=123&host=host&url=url&body=body", result);
    }

    @Test
    void should_format_signing_data_the_same_every_time() {
        String result = HmacRequestSignerTester.formatSigningData("123", "host", "url", null);
        Assertions.assertEquals("pk=123&host=host&url=url&body=", result);
    }

    @Test
    void should_sign_the_full_request() throws MalformedURLException {
        HmacRequestSigner.signRequest(new APIKeys("456","123"), new URL("https://api.event-journal.com"), Instant.ofEpochMilli(0), null , "99");
        String result = HmacRequestSignerTester.formatSigningData("123", "host", "url", null);
        Assertions.assertEquals("pk=123&host=host&url=url&body=", result);
    }

}