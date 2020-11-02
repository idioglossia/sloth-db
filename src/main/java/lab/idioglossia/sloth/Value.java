package lab.idioglossia.sloth;

import java.io.Serializable;

public interface Value<D extends Serializable> {
    D getData();
    void setData(D d);
}
