package com.eventjournal.api.impl;

import com.eventjournal.api.Message;

import java.util.List;

public record EventStream(List<Message.Event> events) {
}
