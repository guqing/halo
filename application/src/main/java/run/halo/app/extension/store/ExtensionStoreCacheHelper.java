package run.halo.app.extension.store;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExtensionStoreCacheHelper {
    private final CacheManager cacheManager;

    public Map<String, ExtensionStore> getFromCache(List<String> keys, String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            throw new IllegalStateException("Cache " + cacheName + " does not exist");
        }
        var results = new LinkedHashMap<String, ExtensionStore>();
        for (String key : keys) {
            var value = cache.get(key, ExtensionStore.class);
            if (value != null) {
                results.put(key, value);
            }
        }
        return results;
    }

    public void putToCache(Map<String, ExtensionStore> entries, String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            entries.forEach(cache::put);
        }
    }
}
