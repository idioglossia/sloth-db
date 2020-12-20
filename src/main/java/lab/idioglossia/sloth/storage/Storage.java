package lab.idioglossia.sloth.storage;

import lab.idioglossia.sloth.collection.Collection;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public interface Storage {
    <K, V extends Serializable> Collection<K, V> getCollectionOfType(String name, Collection.Type type, Class<V> dataClass);
    <K, V extends Serializable> Collection<K, V> getCollectionOfType(String name, Collection.Type type, Class<V> dataClass, String extension);
    boolean removeCollection(String name);
    List<Collection> getCollections();
    Set<String> getCollectionNames();
}
