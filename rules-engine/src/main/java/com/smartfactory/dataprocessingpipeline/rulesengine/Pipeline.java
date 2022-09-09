package com.smartfactory.dataprocessingpipeline.rulesengine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smartfactory.models.Alert;
import com.smartfactory.models.EnrichedTelemetry;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.functions.PatternProcessFunction;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class Pipeline {
    private final StreamExecutionEnvironment env;

    public Pipeline(StreamExecutionEnvironment env) {
        this.env = env;
    }

    private KafkaSource<String> kafkaSource(String brokers,
                                            String topics,
                                            String groupId) {
        return KafkaSource.<String>builder()
                .setBootstrapServers(brokers)
                .setTopics(topics)
                .setGroupId(groupId)
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
    }

    private KafkaSink<String> kafkaSink(String brokers) {
        return KafkaSink.<String>builder()
                .setBootstrapServers(brokers)
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic("alerts")
                        .setValueSerializationSchema(new SimpleStringSchema())
                        .build()
                )
                .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();
    }

    public void build(String brokers,
                      String sourceTopics,
                      String groupId) {
        var source = kafkaSource(brokers, sourceTopics, groupId);
        var alertsSink = kafkaSink(brokers);

        DataStream<EnrichedTelemetry> telemetry =
                env.fromSource(source, WatermarkStrategy.noWatermarks(), "Telemetry")
                        .map(t -> {
                            Gson gson = new GsonBuilder()
                                    .registerTypeAdapter(Instant.class, new InstantDeserializer()).create();

                            return gson.fromJson(t, EnrichedTelemetry.class);
                        });

        DataStream<String> alerts = simplePattern(telemetry)
                .map(t -> {
                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(Instant.class, new InstantSerializer()).create();
                    return gson.toJson(t);
                });

        alerts.sinkTo(alertsSink);
    }

    private DataStream<Alert> simplePattern(DataStream<EnrichedTelemetry> telemetry) {
        Pattern<EnrichedTelemetry, ?> pattern = Pattern.<EnrichedTelemetry>begin("start").where(
                new SimpleCondition<>() {
                    @Override
                    public boolean filter(EnrichedTelemetry event) {
                        return event.getMetricId() == 1;
                    }
                }
        ).followedBy("next").where(new SimpleCondition<>() {
            @Override
            public boolean filter(EnrichedTelemetry value) {
                return value.getDeviceId() == 1;
            }
        }).oneOrMore();

        PatternStream<EnrichedTelemetry> patternStream = CEP.pattern(telemetry, pattern).inProcessingTime();

        return patternStream.process(
                new PatternProcessFunction<>() {
                    @Override
                    public void processMatch(
                            Map<String, List<EnrichedTelemetry>> pattern1,
                            Context ctx,
                            Collector<Alert> out) {

                        var event = pattern1.get("start").get(0);

                        var alert = new Alert(
                                event.getDeviceId(),
                                "ALERT!!! PLEASE CHECK THE SYSTEM.",
                                Instant.now());

                        out.collect(alert);
                    }
                });
    }
}

