package asembly.httpserver.cache;

import asembly.httpserver.exception.ResourceNotFoundException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public class LazyCache<K, V> implements Cache<K, V>{

    private final Function<K, V> function;
    private final ConcurrentMap<K, V> cache = new ConcurrentHashMap<>();

    public LazyCache(Function<K, V> function)
    {
       this.function = function;
    }

    @Override
    public V get(K key) throws ResourceNotFoundException {
        var value = cache.get(key);
        if(value != null) return value;

        value = function.apply(key);

        if(value == null)
           throw new ResourceNotFoundException();

        cache.put(key, value);

        return value;
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }
}
