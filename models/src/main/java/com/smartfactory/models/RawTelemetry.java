package com.smartfactory.models;

import java.time.Instant;

public class RawTelemetry {

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getMetricId() {
        return metricId;
    }

    public void setMetricId(Long metricId) {
        this.metricId = metricId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Instant getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(Instant eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    private Long deviceId;
    private Long metricId;
    private String value;
    private Instant eventTimestamp;

    public RawTelemetry() {
    }

    public RawTelemetry(Long deviceId,
                        Long metricId,
                        String value,
                        Instant eventTimestamp) {
        this.deviceId = deviceId;
        this.metricId = metricId;
        this.value = value;
        this.eventTimestamp = eventTimestamp;
    }
}
