package com.smartfactory.dataprocessingpipeline;

import com.smartfactory.models.Device;
import com.smartfactory.models.Metric;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;

import java.util.Collections;

public class Job {
    public static void main(String[] args) {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Ignite ignite = ignition();

        setupCaches(ignite);

        new Pipeline(env).build(
                "localhost:9092",
                "raw-telemetry",
                "processing-pipeline",
                "processed-telemetry");

        try {
            env.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Ignite ignition() {
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setPeerClassLoadingEnabled(true);
        cfg.setClientMode(true);

        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setAddresses(Collections.singletonList("127.0.0.1:47500..47509"));
        cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));

        return Ignition.getOrStart(cfg);
    }

    private static void setupCaches(Ignite ignite) {
        IgniteCache<Long, Device> devices = ignite.getOrCreateCache(Constants.DEVICES);
        devices.put(1L, new Device(1L, "Slitter", "This device is for slitting", "1.2.5"));
        devices.put(2L, new Device(2L, "Winder", "This device is for winding", "4.5.2"));

        IgniteCache<Long, Metric> metrics = ignite.getOrCreateCache(Constants.METRICS);
        metrics.put(1L, new Metric(1L, "Temperature", "C"));
        metrics.put(2L, new Metric(2L, "Pressure", "Pa"));
    }
}
