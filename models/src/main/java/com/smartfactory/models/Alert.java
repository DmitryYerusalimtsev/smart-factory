package com.smartfactory.models;

import java.time.Instant;

public class Alert {
    private Long deviceId;
    private String type;
    private Instant timestamp;

    public Alert() {
    }

    public Alert(Long deviceId,
                 String type,
                 Instant timestamp) {

        this.deviceId = deviceId;
        this.type = type;
        this.timestamp = timestamp;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
