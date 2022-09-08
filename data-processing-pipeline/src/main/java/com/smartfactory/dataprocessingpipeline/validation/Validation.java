package com.smartfactory.dataprocessingpipeline.validation;

import com.smartfactory.models.RawTelemetry;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

public class Validation extends ProcessFunction<RawTelemetry, RawTelemetry> {
    @Override
    public void processElement(RawTelemetry telemetry,
                               ProcessFunction<RawTelemetry, RawTelemetry>.Context context,
                               Collector<RawTelemetry> collector) {

        final OutputTag<String> outputTag = new OutputTag<>("invalid") {};

        var metricExists = new MetricExistsValidator().validate(telemetry);
        var timestampIsSet = new TimestampIsSetValidator().validate(telemetry);

        if (metricExists && timestampIsSet) {
            collector.collect(telemetry);
        } else {
            String invalid = String.format("Invalid record: deviceId = %d, metricId = %d",
                    telemetry.getDeviceId(), telemetry.getMetricId());
            context.output(outputTag, invalid);
        }
    }
}
