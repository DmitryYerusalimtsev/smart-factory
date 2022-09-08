package com.smartfactory.dataprocessingpipeline.validation;

import com.smartfactory.models.RawTelemetry;

public interface Validator {
    boolean validate(RawTelemetry telemetry);
}
