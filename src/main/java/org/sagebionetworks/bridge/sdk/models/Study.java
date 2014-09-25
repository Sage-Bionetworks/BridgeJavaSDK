package org.sagebionetworks.bridge.sdk.models;

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
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null.");
        } else if (minAge <= 0 || minAge >= 100) {
            throw new IllegalArgumentException("minAge is outside the valid range (0 < minAge < 100 years old: "
                    + minAge);
        }
        return new Study(name, minAge);
    }
}
