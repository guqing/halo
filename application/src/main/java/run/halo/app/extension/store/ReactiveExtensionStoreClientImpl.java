package run.halo.app.extension.store;

import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.infra.exception.DuplicateNameException;

@Component
@RequiredArgsConstructor
public class ReactiveExtensionStoreClientImpl implements ReactiveExtensionStoreClient {
    private final ConcurrentMap<String, Mono<List<ExtensionStore>>> lockMap =
        new ConcurrentHashMap<>();
    static final String CACHE_NAME = "extensionStore";

    private final ExtensionStoreRepository repository;
    private final ExtensionStoreCacheHelper cacheHelper;

    @Override
    public Flux<ExtensionStore> listByNamePrefix(String prefix) {
        return repository.findAllByNameStartingWith(prefix);
    }

    @Override
    public Mono<Page<ExtensionStore>> listByNamePrefix(String prefix, Pageable pageable) {
        return this.repository.findAllByNameStartingWith(prefix, pageable)
            .collectList()
            .zipWith(this.repository.countByNameStartingWith(prefix))
            .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    }

    @Override
    public Mono<Long> countByNamePrefix(String prefix) {
        return this.repository.countByNameStartingWith(prefix);
    }

    @Override
    public Flux<ExtensionStore> listByNames(List<String> names) {
        ToIntFunction<ExtensionStore> comparator =
            store -> names.indexOf(store.getName());

        // batch get from cache
        Map<String, ExtensionStore> cachedResults =
            cacheHelper.getFromCache(names, CACHE_NAME);

        // get missing names
        List<String> missingNames = names.stream()
            .filter(name -> !cachedResults.containsKey(name))
            .toList();

        if (missingNames.isEmpty()) {
            // if all names are cached, return the result directly
            return Flux.fromIterable(
                names.stream()
                    .map(cachedResults::get)
                    .sorted(Comparator.comparingInt(comparator))
                    .toList()
            );
        }

        // batch query missing names
        Mono<List<ExtensionStore>> dbResultsMono = lockMap.computeIfAbsent(
            generateLockKey(missingNames),
            key -> Mono.defer(() -> repository.findByNameIn(missingNames)
                    .collectList()
                    .doOnNext(stores -> {
                        // update cache
                        var toCache = stores.stream()
                            .collect(Collectors.toMap(ExtensionStore::getName, store -> store));
                        cacheHelper.putToCache(toCache, CACHE_NAME);
                    })
                    // remove lock
                    .doFinally(signal -> lockMap.remove(key))
                )
                // ensure only one query executed
                .cache(Duration.ofSeconds(10))
        );

        return dbResultsMono.flatMapMany(dbResults -> {
            var allResults = new HashMap<>(cachedResults);
            dbResults.forEach(store -> allResults.put(store.getName(), store));

            // return results sorted by input order
            return Flux.fromIterable(
                names.stream()
                    .map(allResults::get)
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparingInt(comparator))
                    .toList()
            );
        });
    }

    private String generateLockKey(List<String> names) {
        return names.stream()
            .sorted()
            .collect(Collectors.joining(","));
    }

    @Override
    @Cacheable(cacheNames = CACHE_NAME, key = "#name")
    public Mono<ExtensionStore> fetchByName(String name) {
        return repository.findById(name);
    }

    @Override
    @CachePut(value = CACHE_NAME, key = "#name")
    public Mono<ExtensionStore> create(String name, byte[] data) {
        return repository.save(new ExtensionStore(name, data))
            .onErrorMap(DuplicateKeyException.class,
                t -> new DuplicateNameException("Duplicate name detected.", t));
    }

    @Override
    @CachePut(cacheNames = CACHE_NAME, key = "#name")
    public Mono<ExtensionStore> update(String name, Long version, byte[] data) {
        return repository.save(new ExtensionStore(name, data, version));
    }

    @Override
    @CacheEvict(cacheNames = CACHE_NAME, key = "#name")
    public Mono<ExtensionStore> delete(String name, Long version) {
        return repository.findById(name)
            .flatMap(extensionStore -> {
                // reset the version
                extensionStore.setVersion(version);
                return repository.delete(extensionStore).thenReturn(extensionStore);
            });
    }
}
