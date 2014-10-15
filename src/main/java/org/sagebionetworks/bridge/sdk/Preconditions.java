package org.sagebionetworks.bridge.sdk;

import org.apache.commons.lang3.StringUtils;

/**
 * We wanted to use Guava's Preconditions class, but it is not extensible for some 
 * common checks, like verifying a String is neither null nor blank. So we do that 
 * here by wrapping all the Guava Precondition calls.
 */
class Preconditions {
    
    static void checkArgument(boolean expression) {
        com.google.common.base.Preconditions.checkArgument(expression);
    }
    static void checkArgument(boolean expression, Object errorMessage) {
        com.google.common.base.Preconditions.checkArgument(expression, errorMessage);
    }
    static void checkArgument(boolean expression, String errorMessageTemplate, Object... errorMessageArgs) {
        com.google.common.base.Preconditions.checkArgument(expression, errorMessageTemplate, errorMessageArgs);
    }
    static int checkElementIndex(int index, int size) {
        return com.google.common.base.Preconditions.checkElementIndex(index, size);
    }
    static int checkElementIndex(int index, int size, String desc) {
        return com.google.common.base.Preconditions.checkElementIndex(index, size, desc);
    }
    static <T> T checkNotNull(T reference) {
        return com.google.common.base.Preconditions.checkNotNull(reference);
    }
    static <T> T checkNotNull(T reference, Object errorMessage) {
        return com.google.common.base.Preconditions.checkNotNull(reference, errorMessage);
    }
    static <T> T checkNotNull(T reference, String errorMessageTemplate, Object... errorMessageArgs) {
        return com.google.common.base.Preconditions.checkNotNull(reference, errorMessageTemplate, errorMessageArgs);
    }
    static int checkPositionIndex(int index, int size) {
        return com.google.common.base.Preconditions.checkPositionIndex(index, size);
    }
    static int checkPositionIndex(int index, int size, String desc) {
        return com.google.common.base.Preconditions.checkPositionIndex(index, size, desc);
    }
    static void checkPositionIndexes(int start, int end, int size) {
        com.google.common.base.Preconditions.checkPositionIndexes(start, end, size);
    }
    static void checkState(boolean expression) {
        com.google.common.base.Preconditions.checkState(expression);
    }
    static void checkState(boolean expression, Object errorMessage) {
        com.google.common.base.Preconditions.checkState(expression, errorMessage);
    }
    static void checkState(boolean expression, String errorMessageTemplate, Object... errorMessageArgs) {
        com.google.common.base.Preconditions.checkState(expression, errorMessageTemplate, errorMessageArgs);
    }
    static String checkNotEmpty(String reference) {
        if (StringUtils.isBlank(reference)) {
            throw new IllegalArgumentException();
        }
        return reference;
    }
    static String checkNotEmpty(String reference, String message) {
        if (StringUtils.isBlank(reference)) {
            throw new IllegalArgumentException(message);
        }
        return reference;
    }
    static String checkNotEmpty(String reference, String errorMessageTemplate, Object... errorMessageArgs) {
        if (StringUtils.isBlank(reference)) {
            throw new IllegalArgumentException(String.format(errorMessageTemplate, errorMessageArgs));
        }
        return reference;
    }
}
