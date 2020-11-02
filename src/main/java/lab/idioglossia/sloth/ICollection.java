package lab.idioglossia.sloth;

import java.io.File;
import java.io.Serializable;

public class ICollection<K,D extends Serializable> implements Collection<K,D> {
    private Class<D> valueClass;
    private Type type;
    private final FileWriter fileWriter;
    private final FileReader fileReader;
    private final String dbPath;
    private final String collectionName;
    private volatile Integer size;

    public ICollection(String path, String collectionName, Type type, Class<D> valueClass, FileWriter fileWriter, FileReader fileReader){
        this.dbPath = path;
        this.fileWriter = fileWriter;
        this.fileReader = fileReader;
        assert Validator.isValidCollectionName(collectionName);
        assert Validator.isSupportedValue(valueClass);
        this.collectionName = collectionName;
        this.type = type;
        this.valueClass = valueClass;
        init();
    }

    private void init() {
        File f = new File(dbPath + collectionName);
        if(f.exists()){
            if(!f.isDirectory()){
                throw new IllegalStateException("Collection name can not be as same as existing file name: " + collectionName);
            }
            File configFile = new File(f, ".config");
            if(configFile.exists()){
                readAndValidateConfig(configFile);
            }else {
                createConfigFile(configFile);
            }
        }
    }

    private void createConfigFile(File configFile) {
        String configString = CollectionConfig.asString(this);
        fileWriter.write(configFile, configString);
    }

    private void readAndValidateConfig(File configFile) {
        String config = fileReader.readAsString(configFile);
        if (!CollectionConfig.match(config, this)) {
            throw new IllegalStateException("Collection already exists under different configuration");
        }
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Class<D> valueClass() {
        return valueClass;
    }

    @Override
    public String getName() {
        return collectionName;
    }

    @Override
    public int size() {
        if(size == null){
            size = new File(dbPath + collectionName).list().length;
        }
        return size;
    }

    @Override
    public Value<D> get(K key) {
        return null;
    }

    @Override
    public void update(K key, Value<D> value) {

    }

    @Override
    public void save(K key, Value<D> value) {

    }

    @Override
    public void save(Value<D> value) {

    }

    @Override
    public void remove(K key) {

    }
}
