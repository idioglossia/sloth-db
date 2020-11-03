package lab.idioglossia.sloth;

import java.io.Serializable;

public class QueryResponseValue<D extends Serializable> implements Value<D> {
    private D data;
    private boolean exists = true;
    private String id;

    public QueryResponseValue() {
    }

    public QueryResponseValue(D data, boolean exists, String id) {
        this.data = data;
        this.exists = exists;
        this.id = id;
    }

    public QueryResponseValue(D data, boolean exists) {
        this.data = data;
        this.exists = exists;
    }

    public QueryResponseValue(D data, String id) {
        this.data = data;
        this.id = id;
    }

    @Override
    public D getData() {
        return data;
    }

    @Override
    public boolean exists() {
        return exists;
    }

    @Override
    public String id() {
        return id;
    }
}
