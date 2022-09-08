package com.smartfactory.dataprocessingpipeline.validation;

import com.smartfactory.models.RawTelemetry;

public class MetricExistsValidator implements Validator {
    @Override
    public boolean validate(RawTelemetry telemetry) {
        return telemetry.getMetricId() != 0;
    }
}
