package com.smartfactory.dataprocessingpipeline;

import com.google.gson.*;
import com.smartfactory.dataprocessingpipeline.enrich.Enricher;
import com.smartfactory.dataprocessingpipeline.validation.Validation;
import com.smartfactory.models.EnrichedTelemetry;
import com.smartfactory.models.RawTelemetry;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.OutputTag;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

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

    private KafkaSink<String> kafkaSink(String brokers, String topic) {
        return KafkaSink.<String>builder()
                .setBootstrapServers(brokers)
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic(topic)
                        .setValueSerializationSchema(new SimpleStringSchema())
                        .build()
                )
                .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();
    }

    public void build(String brokers,
                      String sourceTopics,
                      String groupId,
                      String sinkTopic) {

        var source = kafkaSource(brokers, sourceTopics, groupId);
        var sink = kafkaSink(brokers, sinkTopic);
        var invalidSink = kafkaSink(brokers, sinkTopic + "-invalid");

        SingleOutputStreamOperator<RawTelemetry> telemetry =
                env.fromSource(source, WatermarkStrategy.noWatermarks(), "Telemetry")
                        .map(t -> {
                            Gson gson = new GsonBuilder()
                                    .registerTypeAdapter(Instant.class, new InstantDeserializer())
                                    .create();

                            return gson.fromJson(t, RawTelemetry.class);
                        })
                        .process(new Validation());

        DataStream<EnrichedTelemetry> enriched = AsyncDataStream.unorderedWait(telemetry, new Enricher(),
                1000, TimeUnit.MILLISECONDS, 100);

        DataStream<String> output = enriched.map(t -> {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Instant.class, new InstantSerializer())
                    .create();

            return gson.toJson(t);
        });
        output.sinkTo(sink);

        final OutputTag<String> outputTag = new OutputTag<>("invalid") {};
        DataStream<String> invalid = telemetry.getSideOutput(outputTag);
        invalid.sinkTo(invalidSink);
    }
}
