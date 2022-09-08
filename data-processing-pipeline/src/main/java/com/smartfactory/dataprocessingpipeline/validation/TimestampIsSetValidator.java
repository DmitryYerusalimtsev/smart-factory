package com.smartfactory.dataprocessingpipeline.validation;

import com.smartfactory.models.RawTelemetry;

import java.time.Instant;

public class TimestampIsSetValidator implements Validator {
    @Override
    public boolean validate(RawTelemetry telemetry) {
        return telemetry.getEventTimestamp() != Instant.EPOCH;
    }
}
