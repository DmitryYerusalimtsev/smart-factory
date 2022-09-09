package com.smartfactory.dataprocessingpipeline.rulesengine;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Job {
    public static void main(String[] args) {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.getConfig().setAutoWatermarkInterval(1000L);

        new Pipeline(env).build(
                "localhost:9092",
                "processed-telemetry",
                "rules-engine");

        try {
            env.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
