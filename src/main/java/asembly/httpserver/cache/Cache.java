package asembly.httpserver.cache;

import asembly.httpserver.exception.ResourceNotFoundException;

public interface Cache<K, V> {

    V get(K key) throws ResourceNotFoundException;
    void put(K key, V value);

}
