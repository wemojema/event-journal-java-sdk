package com.eventjournal.api.impl;

import com.eventjournal.api.Envelope;
import com.eventjournal.auth.APIKeys;
import com.eventjournal.auth.ConnectionVerification;
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
    private static final URL SAVE_URL;
    private final APIKeys keys;
    private final HttpClient httpClient;

    static {
        try {
            SAVE_URL = new URL(String.format("%s%s", HOST, "/save"));
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
        return null;
    }

    public ConnectionVerification checkConnection() {
        try {
            URL url = new URL("https://api.event-journal.com/health");

            String requestBody = EventJournal.Toolbox.serialize(new ConnectionVerification(true, true));
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(url.toURI())
                    .header("Authorization", HmacRequestSigner.signRequest(keys, url, requestBody))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println("Connection to the Event Store is: " + response.statusCode());
            return new ConnectionVerification(response.statusCode() != 403, response.statusCode() != 401);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("A problem occurred while checking the connection to the Event Store.");
            return new ConnectionVerification(false, false);
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
