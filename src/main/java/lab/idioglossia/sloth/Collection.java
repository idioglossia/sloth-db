package lab.idioglossia.sloth;

import java.io.Serializable;

public interface Collection<K, D extends Serializable> {
    Type getType();
    String getName();
    int size();
    Value<D> get(K key);
    void update(K key, Value<D> value);
    void save(K key, Value<D> value);

    enum Type {
        MAP, LIST
    }
}
