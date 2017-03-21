package ru.atott.mapper.dump;

import java.time.ZonedDateTime;

public class CustomDump {

    private ZonedDateTime dateTime;

    private String id;

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
