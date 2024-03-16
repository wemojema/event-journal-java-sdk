package com.eventjournal.api.impl;

import com.eventjournal.api.Envelope;
import com.eventjournal.auth.APIKeys;
import com.eventjournal.auth.HmacRequestSigner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

class Client implements EventStoreClient {
    Logger log = LoggerFactory.getLogger(Client.class);
    private static final String HOST = "https://api.event-journal.com";
    private static final URL CONNECTION_VERIFICATION_URL;
    private static final URL SAVE_URL;
    private final APIKeys keys;
    private final HttpClient httpClient;

    static {
        try {
            SAVE_URL = new URL(String.format("%s%s", HOST, "/save"));
            CONNECTION_VERIFICATION_URL = new URL(String.format("%s%s", HOST, "/verify-connection"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e); // todo make this a better exception
        }
    }

    public Client(APIKeys keys) {
        this.keys = keys;
        this.httpClient = HttpClient.newBuilder().build();
    }

    private static class Header {
        String key;
        String value;

        static Header Signature(APIKeys keys, URL url, String body) {
            return new Header("Signature", HmacRequestSigner.signRequest(keys, url, body));
        }

        private Header(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    @Override
    public void save(Envelope envelope) {
        save(List.of(envelope));
    }

    @Override
    public void save(List<Envelope> envelopeList) {
        String body = EventJournal.Toolbox.serialize(envelopeList);
        HttpRequest.BodyPublisher requestBody = HttpRequest.BodyPublishers.ofString(body);
        Header authHeader = Header.Signature(keys, SAVE_URL, body);
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header(authHeader.key, authHeader.value)
                    .uri(SAVE_URL.toURI())
                    .POST(requestBody)
                    .build();
            sendRequest(httpRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EventStream stream(String streamId) {
        return null; // todo implement this stream method
    }

    public void checkConnection() {
        try {

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(CONNECTION_VERIFICATION_URL.toURI())
                    .header("Authorization", HmacRequestSigner.signRequest(keys, CONNECTION_VERIFICATION_URL, null))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() != 200) {
                EventJournalErrorResponse errorResponse = EventJournal.Toolbox.deserialize(response.body(), EventJournalErrorResponse.class);
                log.trace("Server Response: " + errorResponse);
                throw new EventJournalConnectionFailedException("Failed to verify your connection to Event Journal. " +
                        "Make sure the API Keys provided are correct. " +
                        "The server responded with message: " +
                        errorResponse.failureReason);
            }
            log.info("Connection to Event Journal verified!");
        } catch (Exception e) {
            log.error("Failed to verify connection to Event Store.", e);
            throw new EventJournalConnectionFailedException("The connection attempt to Event Journal failed. Check your connection and proxy settings.", e);
        }
    }



    private HttpResponse<String>  sendRequest(HttpRequest httpRequest) {
        try {
            return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
