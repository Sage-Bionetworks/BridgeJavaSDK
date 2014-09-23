package org.sagebionetworks.bridge.sdk;

final class Study {

    private final String name;
    private final int minAge;

    public Study(String name, int minAge) {
        this.name = name;
        this.minAge = minAge;
    }

    public String getName() { return this.name; }
    public int getMinAge() { return this.minAge; }

    public Study setActiveStudy(String name, int minAge) {
        return new Study(name, minAge);
    }
}
