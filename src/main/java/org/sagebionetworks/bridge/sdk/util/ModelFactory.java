package org.sagebionetworks.bridge.sdk.util;

public final class ModelFactory {
    
    private static final String[] validClasses = { "User", "Study" };
    
    private ModelFactory() {};
    
    public static <T> T build(Class<? extends T> clazz) {
        assertValidClass(clazz);
        Object obj = null;
        try {
            obj = clazz.newInstance();
            
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return clazz.cast(obj);
    }
    
    private static void assertValidClass(Class<?> clazz) {
        if (clazz == null) {
            throw new AssertionError("class is null.");
        }
        for (int i = 0; i < validClasses.length; i++) {
            if (validClasses[i].equals(clazz.getSimpleName())) {
                return;
            }
        }
        throw new AssertionError("class is not in the list of valid classes to build.");
    }
}
