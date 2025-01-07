package com.parkit.parkingsystem;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;

import java.util.ArrayList;
import java.util.List;

class TestAppender extends AbstractAppender {

    private final List<String> messages = new ArrayList<>();

    protected TestAppender(String name) {
        super(name, null, null, true, null);
    }

    @Override
    public void append(LogEvent event) {
        messages.add(event.getMessage().getFormattedMessage());
    }

    public List<String> getMessages() {
        return messages;
    }
}