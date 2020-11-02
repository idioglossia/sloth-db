package lab.idioglossia.sloth;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;

public class ICollection<K,D extends Serializable> implements Collection<K,D> {
    private final String dbPath;
    private final String collectionName;
    private Type type;
    private Class<D> valueClass;
    private final FileWriter fileWriter;
    private final FileReader fileReader;
    private String extension;
    private volatile Integer size;
    private ListCollectionFileIdGenerator listCollectionFileIdGenerator;
    private File collectionFile;

    public ICollection(String path, String collectionName, Type type, Class<D> valueClass, String extension, FileWriter fileWriter, FileReader fileReader){
        this(path, collectionName, type, valueClass, fileWriter, fileReader);
        this.extension = extension;
    }

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
        this.collectionFile = new File(dbPath + collectionName);
        if(this.collectionFile.exists()){
            if(!this.collectionFile.isDirectory()){
                throw new IllegalStateException("Collection name can not be as same as existing file name: " + collectionName);
            }
            File configFile = new File(this.collectionFile, ".config");
            if(configFile.exists()){
                readAndValidateConfig(configFile);
            }else {
                createConfigFile(configFile);
            }
        }
        if(type.equals(Type.LIST)){
            listCollectionFileIdGenerator = new ListCollectionFileIdGenerator(this.collectionFile, extension);
        }
    }

    private void createConfigFile(File configFile) {
        try {
            configFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
            try {
                size = (int) Files.list(this.collectionFile.toPath()).filter(new DBValuePathPredict(this.collectionFile)).count();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return size;
    }

    @Override
    public Value<D> get(K key) {
        File file = new File(collectionFile, getKeyAsString(key));

        if(!file.exists())
            return null;

        if(valueClass.getName().equals(String.class.getName())) {
            String value = fileReader.readAsString(file);
            return new Value<D>() {
                @Override
                public D getData() {
                    return (D) value;
                }
            };
        }else {
            byte[] bytes = fileReader.readAsByteArray(file);
            return new Value<D>() {
                @Override
                public D getData() {
                    return (D) bytes;
                }
            };
        }
    }

    @Override
    public void update(K key, Value<D> value) {
        write(getKeyAsString(key), value, false);
    }

    @Override
    public void save(K key, Value<D> value) {
        if(type.equals(Type.LIST)){
            save(value);
        }else {
            write(getKeyAsString(key), value, true);
        }
    }

    @Override
    public void save(Value<D> value) {
        if(type.equals(Type.MAP)){
            throw new RuntimeException("Calling save(value) on map collection is illegal");
        }
        write(String.valueOf(listCollectionFileIdGenerator.getId()), value, true);
        listCollectionFileIdGenerator.increase();
    }

    @Override
    public synchronized void remove(K key) {
        File file = new File(collectionFile, getKeyAsString(key));
        if(file.exists()){
            boolean delete = file.delete();
            if(delete){
                synchronized (this){
                    size--;
                }
                listCollectionFileIdGenerator.decrease();
            }

        }
    }

    private void write(String key, Value<D> value, boolean inc){
        if(valueClass.getName().equals(String.class.getName())){
            fileWriter.write(new File(collectionFile, key), (String) value.getData());
        }else {
            fileWriter.write(new File(collectionFile, key), (byte[]) value.getData());
        }
        if(inc){
            synchronized (this){
                size++;
            }
        }
    }

    private String getKeyAsString(K key){
        assert Validator.isValidKey(key);
        return key instanceof String ? (String) key : String.valueOf(key);
    }


}
