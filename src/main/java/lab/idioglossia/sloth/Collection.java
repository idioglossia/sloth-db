package lab.idioglossia.sloth;

import java.io.Serializable;

public interface Collection<K, D extends Serializable> {
    Type getType();
    Class<D> valueClass();
    String getName();
    int size();
    Value<D> get(K key);
    void update(K key, Value<D> value);
    void save(K key, Value<D> value);
    void save(Value<D> value);
    void remove(K key);

    enum Type {
        MAP, LIST
    }
}
