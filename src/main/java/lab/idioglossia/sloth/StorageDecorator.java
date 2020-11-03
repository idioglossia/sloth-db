package lab.idioglossia.sloth;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public abstract class StorageDecorator implements Storage {
    private final Storage storage;

    protected StorageDecorator(Storage storage) {
        this.storage = storage;
    }

    @Override
    public <K, V extends Serializable> Collection<K, V> getCollectionOfType(String name, Collection.Type type, Class<V> dataClass) {
        return storage.getCollectionOfType(name, type, dataClass);
    }

    @Override
    public <K, V extends Serializable> Collection<K, V> getCollectionOfType(String name, Collection.Type type, Class<V> dataClass, String extension) {
        return storage.getCollectionOfType(name, type, dataClass, extension);
    }

    @Override
    public boolean removeCollection(String name) {
        return storage.removeCollection(name);
    }

    @Override
    public List<Collection> getCollections() {
        return storage.getCollections();
    }

    @Override
    public Set<String> getCollectionNames() {
        return storage.getCollectionNames();
    }
}
