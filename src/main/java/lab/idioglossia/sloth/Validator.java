package lab.idioglossia.sloth;

public class Validator {

    //TODO
    public static boolean isValidCollectionName(String collectionName){
        return true;
    }

    public static boolean isValidExtension(String s){
        return s.equals("") || (s.startsWith(".") && s.length() < 5);
    }

    public static boolean isValidKey(Object key){
        return key instanceof Integer || key instanceof String;
    }

    public static boolean isSupportedValue(Class aClass){
        String aClassName = aClass.getName();
        return aClassName.equals(String.class.getName()) && aClassName.equals(Byte[].class.getName());
    }

}
