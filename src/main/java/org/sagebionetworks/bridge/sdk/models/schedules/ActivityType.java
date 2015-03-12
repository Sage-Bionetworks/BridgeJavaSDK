package org.sagebionetworks.bridge.sdk.models.schedules;

/**
 * We use lower-case enums here because they effortlessly serialize to lower-case strings when
 * using the Jackson JSON parser, and the API uses lower-case constant strings.
 */
public enum ActivityType {
    SURVEY,
    TASK
}
