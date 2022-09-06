package com.smartfactory.dataprocessingpipeline.enrich;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class IgniteRepository<T> implements Repository<T> {

    private final IgniteCache<Long, T> cache;

    public IgniteRepository(Ignite ignite, String cacheName) {
        this.cache = ignite.cache(cacheName);
    }

    @Override
    public CompletableFuture<T> get(Long id) {
        return CompletableFuture.supplyAsync(() -> cache.get(id));
    }

    @Override
    public CompletableFuture<Map<Long, T>> getAll(Set<Long> ids) {
        return CompletableFuture.supplyAsync(() -> cache.getAll(ids));
    }
}
