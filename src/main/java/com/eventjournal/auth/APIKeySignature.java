package com.eventjournal.auth;

public record APIKeySignature(String id, String ts, String nonce, String sig) {
    public static final String HEADER_KEY = "ej-request-signature";
    private static final String HEADER_FORMAT = HEADER_KEY + ": id:%s,ts:%s,nonce:%s,sig:%s";

    public static APIKeySignature from(String signature) {
        String[] parts = signature.split(",");
        String id = parts[0].split(":")[1];
        String ts = parts[1].split(":")[1];
        String nonce = parts[2].split(":")[1];
        String sig = parts[3].split(":")[1];
        return new APIKeySignature(id, ts, nonce, sig);
    }

    public String toHeader() {
        return String.format(HEADER_FORMAT, id, ts, nonce, sig);
    }

}
