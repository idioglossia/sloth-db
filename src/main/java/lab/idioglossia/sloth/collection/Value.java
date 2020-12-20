package lab.idioglossia.sloth.collection;

import java.io.Serializable;

public interface Value<D extends Serializable> {
    D getData();
    default boolean exists() {
        return true;
    }
    default String id() {
        return null;
    }
}
