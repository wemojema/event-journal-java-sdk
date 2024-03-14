package com.eventjournal.auth;


public class ConnectionVerification {
    boolean authenticated;
    boolean authorized;

    public ConnectionVerification(boolean authenticated, boolean authorized) {
        this.authenticated = authenticated;
        this.authorized = authorized;
    }
}
