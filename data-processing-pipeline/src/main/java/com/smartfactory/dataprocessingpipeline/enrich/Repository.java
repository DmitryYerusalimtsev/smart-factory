package com.smartfactory.dataprocessingpipeline.enrich;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface Repository<T> {
    CompletableFuture<T> get(Long id);

    CompletableFuture<Map<Long, T>> getAll(Set<Long> ids);
}
