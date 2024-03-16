package com.eventjournal.api.impl;

import java.util.List;


class EventJournalErrorResponse {
    String failureReason;
    List<String> errors;
    List<String> causes;
    private EventJournalErrorResponse() {

    }

    public String getFailureReason() {
        return failureReason;
    }

    public List<String> getErrors() {
        return errors;
    }

    public List<String> getCauses() {
        return causes;
    }

    @Override
    public String toString() {
        return "EventJournalErrorResponse{" +
                "failureReason='" + failureReason + '\'' +
                ", errors=" + errors +
                ", causes=" + causes +
                '}';
    }
}
