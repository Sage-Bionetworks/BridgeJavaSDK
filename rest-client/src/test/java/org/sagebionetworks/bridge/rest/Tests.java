package org.sagebionetworks.bridge.rest;

import java.lang.reflect.Field;

public class Tests {
    public static String unescapeJson(String json) {
        return json.replaceAll("'", "\"");
    }
    public static void setVariableValueInObject(Object object, String variable, Object value) {
        try {
            Field field = getFieldByNameIncludingSuperclasses(variable, object.getClass());
            field.setAccessible(true);
            field.set(object, value);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    @SuppressWarnings("rawtypes")
    private static Field getFieldByNameIncludingSuperclasses(String fieldName, Class clazz) {
        Field retValue = null;
        try {
            retValue = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class superclass = clazz.getSuperclass();
            if ( superclass != null ) {
                retValue = getFieldByNameIncludingSuperclasses( fieldName, superclass );
            }
        }
        return retValue;
    }    
}
