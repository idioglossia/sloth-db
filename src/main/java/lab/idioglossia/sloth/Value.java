package lab.idioglossia.sloth;

import java.io.Serializable;

public interface Value<D extends Serializable> {
    D getData();
    default boolean exists() {return true;}
}
