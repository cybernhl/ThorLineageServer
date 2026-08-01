package javafx.util;

import java.io.Serializable;
import java.util.AbstractMap;

/**
 * Shim class for javafx.util.Pair using JDK standard AbstractMap.SimpleImmutableEntry.
 * This allows removing the JavaFX dependency while maintaining source compatibility.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class Pair<K, V> extends AbstractMap.SimpleImmutableEntry<K, V> implements Serializable {

    private static final long serialVersionUID = 1L;

    public Pair(K key, V value) {
        super(key, value);
    }
}
