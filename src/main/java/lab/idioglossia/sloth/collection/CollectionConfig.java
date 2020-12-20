package lab.idioglossia.sloth.collection;

public class CollectionConfig {

    public static boolean match(String config, Collection collection){
        String[] lines = config.split("\n");
        assert lines.length == 3;
        boolean typeMatch = Collection.Type.valueOf(lines[0]) == collection.getType();
        boolean classMatch = false;
        try {
            classMatch = Class.forName(lines[1]) == collection.valueClass();
        } catch (ClassNotFoundException e) {
            return false;
        }
        return classMatch && typeMatch;
    }

    public static String asString(Collection collection){
        Collection.Type type = collection.getType();
        Class valueClass = collection.valueClass();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(type.toString());
        stringBuilder.append("\n");
        stringBuilder.append(valueClass.getName());
        return stringBuilder.toString();
    }
}
