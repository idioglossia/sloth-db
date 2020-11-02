package lab.idioglossia.sloth;

public class Validator {

    //TODO
    public static boolean isValidCollectionName(String collectionName){
        return true;
    }

    public static boolean isSupportedValue(Class aClass){
        String aClassName = aClass.getName();
        return aClassName.equals(String.class.getName()) && aClassName.equals(Byte[].class.getName());
    }

}
