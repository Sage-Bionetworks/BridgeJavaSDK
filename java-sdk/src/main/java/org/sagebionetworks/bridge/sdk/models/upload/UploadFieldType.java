package org.sagebionetworks.bridge.sdk.models.upload;

/**
 * Represents field types in upload data. This is used to define upload schemas. For more information about Bridge
 * upload data and field types, see https://sagebionetworks.jira.com/wiki/display/BRIDGE/Bridge+Upload+Data+Format
 *
 * @see org.sagebionetworks.bridge.sdk.models.upload.UploadSchema
 */
public enum UploadFieldType {
    /**
     * Health Data Attachment as a non-JSON blob. The value of this field is a foreign key into the Health Data
     * Attachments table. Data stored in this format will not be subject to additional post-processing.
     */
    ATTACHMENT_BLOB,

    /**
     * Health Data Attachment as a CSV file. The value of this field is a foreign key into the Health Data Attachments
     * table. We originally planned to de-normalize this data, but that feature was punted.
     */
    ATTACHMENT_CSV,

    /**
     * Health Data Attachment as a JSON blob. The value of this field is a foreign key into the Health Data Attachments
     * table. Data stored in this format will not be subject to additional post-processing, but is tagged as JSON data
     * in the schema for convenience.
     */
    ATTACHMENT_JSON_BLOB,

    /**
     * Health Data Attachment as a JSON blob of a specific "table" format. The value of this field is a foreign key
     * into the Health Data Attachments table. We originally planned to de-normalize this data, but that feature was
     * punted.
     */
    ATTACHMENT_JSON_TABLE,

    /** Upload V2 version of attachments. Fields of this type can be associated with any file extension or MIME type */
    ATTACHMENT_V2,

    /** A boolean, expected values match Boolean.parse() */
    BOOLEAN,

    /** A calendar date in YYYY-MM-DD format */
    CALENDAR_DATE,

    /** Duration (how long something took). Used by Upload v2. */
    DURATION_V2,

    /** A floating point number, generally represented as a double in Java */
    FLOAT,

    /**
     * A JSON blob that's small enough to fit in the health data. (Generally something that's only a few hundred
     * characters at most.
     */
    INLINE_JSON_BLOB,

    /** An integer value, generally represented as a long in Java (64-bit signed integer) */
    INT,

    /** Multiple choice question with multiple answers. */
    MULTI_CHOICE,

    /** Multiple choice question with only a single answer. */
    SINGLE_CHOICE,

    /** A string value */
    STRING,

    /** Time without date or timezone. Used in Upload v2. */
    TIME_V2,

    /** A timestamp in ISO 8601 format (YYYY-MM-DDThhhh:mm:ss+/-zz:zz) */
    TIMESTAMP,
}
