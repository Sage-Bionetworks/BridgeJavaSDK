package org.sagebionetworks.bridge.sdk.models;

final class Study {

    private final String name;
    private final int minAge;

    private Study(String name, int minAge) {
        this.name = name;
        this.minAge = minAge;
    }

    public static Study valueOf(String name, int minAge) {
        return new Study(name, minAge);
    }

    public String getName() { return this.name; }
    public int getMinAge() { return this.minAge; }

}
