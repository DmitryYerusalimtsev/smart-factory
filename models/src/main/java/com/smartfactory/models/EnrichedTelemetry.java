package com.smartfactory.models;

import java.time.Instant;

public class EnrichedTelemetry {

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceSoftwareVersion() {
        return deviceSoftwareVersion;
    }

    public void setDeviceSoftwareVersion(String deviceSoftwareVersion) {
        this.deviceSoftwareVersion = deviceSoftwareVersion;
    }

    public Long getMetricId() {
        return metricId;
    }

    public void setMetricId(Long metricId) {
        this.metricId = metricId;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public Instant getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(Instant eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    private Long deviceId;

    private String deviceType;
    private String deviceSoftwareVersion;
    private Long metricId;
    private String metricName;
    private String value;
    private String uom;
    private Instant eventTimestamp;

    public EnrichedTelemetry(Long deviceId,
                             String deviceType,
                             String deviceSoftwareVersion,
                             Long metricId,
                             String metricName,
                             String value,
                             String uom,
                             Instant eventTimestamp) {
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.deviceSoftwareVersion = deviceSoftwareVersion;
        this.metricId = metricId;
        this.metricName = metricName;
        this.value = value;
        this.uom = uom;
        this.eventTimestamp = eventTimestamp;
    }

    public EnrichedTelemetry() {
    }
}
