package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Locale;

import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.ResearcherClient;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.UserClient;
import org.sagebionetworks.bridge.sdk.exceptions.EntityNotFoundException;
import org.sagebionetworks.bridge.sdk.models.UploadRequest;
import org.sagebionetworks.bridge.sdk.models.UploadSession;
import org.sagebionetworks.bridge.sdk.models.upload.UploadFieldDefinition;
import org.sagebionetworks.bridge.sdk.models.upload.UploadFieldType;
import org.sagebionetworks.bridge.sdk.models.upload.UploadSchema;
import org.sagebionetworks.bridge.sdk.models.upload.UploadStatus;
import org.sagebionetworks.bridge.sdk.models.upload.UploadValidationStatus;

public class UploadTest {
    private static final Logger LOG = LoggerFactory.getLogger(UploadTest.class);

    // On a cold server, validation could take up to 8 seconds (most of this is downloading and caching the encryption
    // certs for the first time). Subsequent validation attempts take about 2 seconds. 5 second delay is a good
    // compromise between fast tests and not having to retry a bunch of times.
    private static final int UPLOAD_STATUS_DELAY_MILLISECONDS = 5000;

    // Retry up to 6 times, so we don't spend more than 30 seconds per test.
    private static final int UPLOAD_STATUS_DELAY_RETRIES = 6;

    private static TestUserHelper.TestUser researcher;
    private static TestUserHelper.TestUser user;

    @BeforeClass
    public static void beforeClass() {
        // researcher is to ensure schemas exist. user is to do uploads
        researcher = TestUserHelper.createAndSignInUser(UploadSchemaTest.class, false, Tests.RESEARCHER_ROLE);
        user = TestUserHelper.createAndSignInUser(UploadSchemaTest.class, true);

        // ensure schemas exist, so we have something to upload against
        ResearcherClient researcherClient = researcher.getSession().getResearcherClient();

        UploadSchema iosSurveySchema = null;
        try {
            iosSurveySchema = researcherClient.getUploadSchemaById("ios-survey");
        } catch (EntityNotFoundException ex) {
            // no-op
        }
        if (iosSurveySchema == null) {
            iosSurveySchema = new UploadSchema.Builder()
                    .withSchemaId("ios-survey")
                    .withName("iOS Survey Response")
                    .withFieldDefinitions(
                            new UploadFieldDefinition("answers", UploadFieldType.ATTACHMENT_JSON_TABLE),
                            new UploadFieldDefinition("item", UploadFieldType.STRING),
                            new UploadFieldDefinition("taskRunId", UploadFieldType.STRING))
                    .build();
            researcherClient.createOrUpdateUploadSchema(iosSurveySchema);
        }

        UploadSchema jsonDataSchema = null;
        try {
            jsonDataSchema = researcherClient.getUploadSchemaById("upload-test-json-data");
        } catch (EntityNotFoundException ex) {
            // no-op
        }
        if (jsonDataSchema == null) {
            jsonDataSchema = new UploadSchema.Builder()
                    .withSchemaId("upload-test-json-data")
                    .withName("Upload Test JSON Data")
                    .withFieldDefinitions(
                            new UploadFieldDefinition("string.json.string", UploadFieldType.STRING),
                            new UploadFieldDefinition("blob.json.blob", UploadFieldType.ATTACHMENT_JSON_BLOB))
                    .build();
            researcherClient.createOrUpdateUploadSchema(jsonDataSchema);
        }

        UploadSchema nonJsonSchema = null;
        try {
            nonJsonSchema = researcherClient.getUploadSchemaById("upload-test-non-json");
        } catch (EntityNotFoundException ex) {
            // no-op
        }
        if (nonJsonSchema == null) {
            nonJsonSchema = new UploadSchema.Builder()
                    .withSchemaId("upload-test-non-json")
                    // in the current version, non-JSON data matches by name, not by ID, so for testing purposes,
                    // keep schema and name the same
                    .withName("upload-test-non-json")
                    .withFieldDefinitions(
                            new UploadFieldDefinition("nonJson.txt", UploadFieldType.ATTACHMENT_BLOB),
                            new UploadFieldDefinition("jsonFile.json", UploadFieldType.ATTACHMENT_JSON_BLOB))
                    .build();
            researcherClient.createOrUpdateUploadSchema(nonJsonSchema);
        }
    }

    @AfterClass
    public static void deleteResearcher() {
        if (researcher != null) {
            researcher.signOutAndDeleteUser();
        }
    }

    @AfterClass
    public static void deleteUser() {
        if (user != null) {
            user.signOutAndDeleteUser();
        }
    }

    @Test
    public void iosSurvey() throws Exception {
        testUpload("ios-survey-encrypted");
    }

    @Test
    public void jsonData() throws Exception {
        testUpload("json-data-encrypted");
    }

    @Test
    public void nonJson() throws Exception {
        testUpload("non-json-encrypted");
    }

    private static void testUpload(String fileLeafName) throws Exception {
        // set up request
        String filePath = resolveFilePath(fileLeafName);
        UploadRequest req = makeRequest(filePath);

        // upload to server
        UserClient userClient = user.getSession().getUserClient();
        UploadSession session = userClient.requestUploadSession(req);
        String uploadId = session.getId();
        LOG.info("UploadId=" + uploadId);
        userClient.upload(session, req, filePath);

        // get validation status
        UploadValidationStatus status = null;
        for (int i = 0; i < UPLOAD_STATUS_DELAY_RETRIES; i++) {
            Thread.sleep(UPLOAD_STATUS_DELAY_MILLISECONDS);

            status = userClient.getUploadStatus(session.getId());
            if (status.getStatus() == UploadStatus.VALIDATION_FAILED) {
                // Short-circuit. Validation failed. No need to retry.
                fail("Upload validation failed, UploadId=" + uploadId);
            } else if (status.getStatus() == UploadStatus.SUCCEEDED) {
                break;
            }
        }

        assertNotNull("Upload status is not null, UploadId=" + uploadId, status);
        assertEquals("Upload succeeded, UploadId=" + uploadId, UploadStatus.SUCCEEDED, status.getStatus());
        assertTrue("Upload has no validation messages, UploadId=" + uploadId, status.getMessageList().isEmpty());
    }

    @Test
    public void cannotUploadAfterExpirationDate() throws Exception {
        // Arbitrarily choose one of the test files. It doesn't matter which. It'll never make it to the server.
        String filePath = resolveFilePath("non-json-encrypted");
        UploadRequest req = makeRequest(filePath);

        // request upload session
        UserClient userClient = user.getSession().getUserClient();
        UploadSession session = userClient.requestUploadSession(req);

        // Get number of milliseconds between now and expiration time, plus one second.
        long millis = session.getExpires()
                .minus(DateTime.now().getMillis())
                .plusSeconds(1)
                .getMillis();
        Thread.sleep(millis);

        try {
            userClient.upload(session, req, filePath);
            fail("userClient upload should have failed.");
        } catch (Exception e) {
            assertEquals("Exception thrown should be an illegal argument exception.",
                    e.getClass(), IllegalArgumentException.class);
        }
    }

    private static UploadRequest makeRequest(String filePath) throws Exception {
        File file = new File(filePath);
        return new UploadRequest.Builder().withFile(file).withContentType("application/zip").build();
    }

    // returns the path relative to the root of the project
    private static String resolveFilePath(String fileLeafName) {
        String envName = ClientProvider.getConfig().getEnvironment().name().toLowerCase(Locale.ENGLISH);
        return "src/test/resources/upload-test/" + envName + "/" + fileLeafName;
    }
}
