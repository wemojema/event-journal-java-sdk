package com.eventjournal.auth;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class HmacRequestSigner {

    // Included in the signature to inform Veracode of the signature version.
    protected static final String REQUEST_VERSION_STRING = "ej_request_version_1";

    // Expected format for the unencrypted data string.
    protected static final String DATA_FORMAT = "pk=%s&host=%s&url=%s&body=%s";

    // Expected format for the signature header.

    // HMAC encryption algorithm.
    protected static final String HMAC_SHA_256 = "HmacSHA256";

    // Charset to use when encrypting a string.
    protected static final String UTF_8 = "UTF-8";

    // A cryptographically secure random number generator.
    protected static final SecureRandom secureRandom = new SecureRandom();

    // Private constructor.
    protected HmacRequestSigner() {
        /*
         * This is a utility class that should only be accessed through its
         * static methods.
         */
    }

    /**
     * Entry point for HmacRequestSigner. Returns the value for the
     * Authorization header for use with Event Journal APIs when provided a public key,
     * secret key, and target URL.
     *
     * @param APIKeys     The public and private keys to use for the signature
     * @param url         The URL of the called API, including query parameters
     * @param requestBody The body of the request
     * @return The value to be put in the Authorization header
     */
    public static String signRequest(final APIKeys apiKeys, final URL url, final String requestBody) {
        final String urlPath = (url.getQuery() == null) ? url.getPath() : url.getPath().concat("?").concat(url.getQuery());
        final String data = String.format(DATA_FORMAT, apiKeys.publicKey, url.getHost(), urlPath, requestBody);
        final String timestamp = String.valueOf(System.currentTimeMillis());
        final String nonce = Base64.getEncoder().encodeToString(generateRandomBytes(16));
        final String signature;
        try {
            signature = Base64.getEncoder().encodeToString(sign(apiKeys.privateKey, data, timestamp, nonce));
            return new APIKeySignature(apiKeys.publicKey, timestamp, nonce, signature).toHeader();
        } catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    protected static byte[] sign(final String secretKey, final String data, final String timestamp, final String nonce)
            throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException {
        final byte[] keyBytes = secretKey.getBytes();
        final byte[] nonceBytes = nonce.getBytes();
        final byte[] encryptedNonce = hmacSha256(nonceBytes, keyBytes);
        final byte[] encryptedTimestamp = hmacSha256(timestamp, encryptedNonce);
        final byte[] signingKey = hmacSha256(REQUEST_VERSION_STRING, encryptedTimestamp);
        return hmacSha256(data, signingKey);
    }

    private static byte[] hmacSha256(final String data, final byte[] key)
            throws NoSuchAlgorithmException, InvalidKeyException, IllegalStateException, UnsupportedEncodingException {
        final Mac mac = Mac.getInstance(HMAC_SHA_256);
        mac.init(new SecretKeySpec(key, HMAC_SHA_256));
        return mac.doFinal(data.getBytes(UTF_8));
    }

    private static byte[] hmacSha256(final byte[] data, final byte[] key)
            throws NoSuchAlgorithmException, InvalidKeyException {
        final Mac mac = Mac.getInstance(HMAC_SHA_256);
        mac.init(new SecretKeySpec(key, HMAC_SHA_256));
        return mac.doFinal(data);
    }

    private static byte[] generateRandomBytes(final int size) {
        final byte[] key = new byte[size];
        secureRandom.nextBytes(key);
        return key;
    }

}