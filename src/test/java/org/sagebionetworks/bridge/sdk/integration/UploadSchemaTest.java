package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.google.common.collect.ImmutableList;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.ResearcherClient;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.exceptions.EntityNotFoundException;
import org.sagebionetworks.bridge.sdk.exceptions.UnauthorizedException;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.upload.UploadFieldDefinition;
import org.sagebionetworks.bridge.sdk.models.upload.UploadFieldType;
import org.sagebionetworks.bridge.sdk.models.upload.UploadSchema;

public class UploadSchemaTest {
    private static final String TEST_SCHEMA_ID = "integration-test-schema";
    private static final String TEST_SCHEMA_NAME = "Upload Schema Integration Tests";

    private static TestUserHelper.TestUser researcher;
    private static TestUserHelper.TestUser user;

    @BeforeClass
    public static void beforeClass() {
        researcher = TestUserHelper.createAndSignInUser(UploadSchemaTest.class, false, Tests.RESEARCHER_ROLE);
        user = TestUserHelper.createAndSignInUser(UploadSchemaTest.class, true);
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
    public void test() {
        ResearcherClient researcherClient = researcher.getSession().getResearcherClient();

        // TODO: Don't update the schema until we have a delete API. This will prevent the integration tests from
        // creating an explosion of schemas. Once we do that, we can do more in-depth testing of these APIs.

        // Step 1: check if the integration test schema already exists
        boolean foundSchema;
        try {
            UploadSchema schema = researcherClient.getUploadSchemaById(TEST_SCHEMA_ID);
            assertNotNull(schema);
            foundSchema = true;
        } catch (EntityNotFoundException ex) {
            foundSchema = false;
        }

        if (!foundSchema) {
            // Step 2: Schema doesn't exist, so create it.
            List<UploadFieldDefinition> submittedFieldDefList = ImmutableList.of(
                    new UploadFieldDefinition.Builder().withName("foo").withRequired(true)
                            .withType(UploadFieldType.STRING).build(),
                    new UploadFieldDefinition.Builder().withName("bar").withRequired(false)
                            .withType(UploadFieldType.INT).build());
            UploadSchema submittedSchema = new UploadSchema.Builder().withName(TEST_SCHEMA_NAME)
                    .withSchemaId(TEST_SCHEMA_ID).withFieldDefinitions(submittedFieldDefList).build();
            UploadSchema createdSchema = researcherClient.createOrUpdateUploadSchema(submittedSchema);

            assertEquals(TEST_SCHEMA_NAME, createdSchema.getName());
            assertEquals(1, createdSchema.getRevision().intValue());
            assertEquals(TEST_SCHEMA_ID, createdSchema.getSchemaId());
            assertEquals(submittedFieldDefList, createdSchema.getFieldDefinitions());

            // Step 3: test the getUploadSchemaById() method, by getting the same schema back from the server and
            // comparing for equality
            UploadSchema returnedSchema = researcherClient.getUploadSchemaById(TEST_SCHEMA_ID);
            assertEquals(createdSchema, returnedSchema);
        }

        // Step 4: List schemas. The schema may have been created by a previous version of the integration tests, so
        // the only thing we know for sure is that it has the same Schema ID. Iterate through all the schemas until
        // you find it.
        //
        // We do this instead of just calling getById because we want to test the list API.
        boolean foundListedSchema = false;
        ResourceList<UploadSchema> schemaList = researcherClient.getUploadSchemaForStudy();
        for (UploadSchema oneListedSchema : schemaList) {
            if (TEST_SCHEMA_ID.equals(oneListedSchema.getSchemaId())) {
                foundListedSchema = true;
                break;
            }
        }
        assertTrue(foundListedSchema);
    }

    @Test(expected=UnauthorizedException.class)
    public void unauthorizedTest() {
        user.getSession().getResearcherClient().getUploadSchemaForStudy();
    }
}
