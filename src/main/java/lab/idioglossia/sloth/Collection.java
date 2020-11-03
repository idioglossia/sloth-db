package lab.idioglossia.sloth;

import java.io.Serializable;

public interface Collection<K, D extends Serializable> {
    Type getType();
    Class<D> valueClass();
    default String extension() {
        return "";
    }
    String getName();
    int size();
    Value<D> get(K key);
    void update(K key, Value<D> value);
    Value<D> save(K key, Value<D> value);
    Value<D> save(Value<D> value);
    void remove(K key);
    void stop();

    enum Type {
        MAP, LIST
    }
}
