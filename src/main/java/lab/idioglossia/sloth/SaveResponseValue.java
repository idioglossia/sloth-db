package lab.idioglossia.sloth;

import java.io.Serializable;

public class SaveResponseValue<D extends Serializable> implements Value<D> {
    private final Value<D> input;
    private final String id;

    public SaveResponseValue(Value<D> input, String id) {
        this.input = input;
        this.id = id;
    }

    @Override
    public D getData() {
        return input.getData();
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public String id() {
        return id;
    }
}
