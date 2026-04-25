package asembly.httpserver.cache;

import java.io.IOException;

public interface Cache<K, V> {

    V get(K key) throws IOException;
    void put(K key, V value);

}
