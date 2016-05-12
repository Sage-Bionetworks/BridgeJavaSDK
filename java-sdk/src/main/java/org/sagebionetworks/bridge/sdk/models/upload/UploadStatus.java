package org.sagebionetworks.bridge.sdk.models.upload;

/** Represents the lifecycle of an upload object. */
public enum UploadStatus {
    /** Upload status is unknown. */
    UNKNOWN,

    /** Initial state. Upload is requested. User needs to upload to specified URL and call uploadComplete. */
    REQUESTED,

    /** User has called uploadComplete. Upload validation is currently taking place. */
    VALIDATION_IN_PROGRESS,

    /** Upload validation has failed. */
    VALIDATION_FAILED,

    /** Upload has succeeded, including validation. */
    SUCCEEDED,
}
