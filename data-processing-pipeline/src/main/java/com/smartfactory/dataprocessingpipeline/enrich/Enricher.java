package com.smartfactory.dataprocessingpipeline.enrich;

import com.smartfactory.dataprocessingpipeline.Constants;
import com.smartfactory.models.Device;
import com.smartfactory.models.EnrichedTelemetry;
import com.smartfactory.models.Metric;
import com.smartfactory.models.RawTelemetry;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;

import java.util.Collections;

public class Enricher extends RichAsyncFunction<RawTelemetry, EnrichedTelemetry> {

    private transient Repository<Device> devicesRepository;
    private transient Repository<Metric> metricsRepository;

    @Override
    public void open(Configuration parameters) throws Exception {
        var cfg = new IgniteConfiguration();
        cfg.setPeerClassLoadingEnabled(true);
        cfg.setClientMode(true);

        var ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setAddresses(Collections.singletonList("127.0.0.1:47500..47509"));
        cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));

        var ignite = Ignition.getOrStart(cfg);

        devicesRepository = new IgniteRepository<>(ignite, Constants.DEVICES);
        metricsRepository = new IgniteRepository<>(ignite, Constants.METRICS);
    }

    @Override
    public void asyncInvoke(RawTelemetry rawTelemetry, ResultFuture<EnrichedTelemetry> resultFuture) throws Exception {
        var device = devicesRepository.get(rawTelemetry.getDeviceId());
        var metric = metricsRepository.get(rawTelemetry.getMetricId());

        device.thenCombine(metric, (d, m) -> new EnrichedTelemetry(
                rawTelemetry.getDeviceId(),
                d.getType(),
                d.getSoftwareVersion(),
                rawTelemetry.getMetricId(),
                m.getName(),
                rawTelemetry.getValue(),
                m.getUom(),
                rawTelemetry.getEventTimestamp()
        )).thenAccept((EnrichedTelemetry result) -> resultFuture.complete(Collections.singleton(result)));
    }
}
