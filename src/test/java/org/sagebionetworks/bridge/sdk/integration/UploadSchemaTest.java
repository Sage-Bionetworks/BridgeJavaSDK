package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sagebionetworks.bridge.sdk.DeveloperClient;
import org.sagebionetworks.bridge.sdk.Roles;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.exceptions.EntityNotFoundException;
import org.sagebionetworks.bridge.sdk.exceptions.UnauthorizedException;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.upload.UploadFieldDefinition;
import org.sagebionetworks.bridge.sdk.models.upload.UploadFieldType;
import org.sagebionetworks.bridge.sdk.models.upload.UploadSchema;
import org.sagebionetworks.bridge.sdk.models.upload.UploadSchemaType;

public class UploadSchemaTest {
    private static final Logger LOG = LoggerFactory.getLogger(UploadSchemaTest.class);

    private static final String TEST_SCHEMA_ID_PREFIX = "integration-test-schema-";

    private static TestUserHelper.TestUser developer;
    private static TestUserHelper.TestUser user;

    @BeforeClass
    public static void beforeClass() {
        developer = TestUserHelper.createAndSignInUser(UploadSchemaTest.class, false, Roles.DEVELOPER);
        user = TestUserHelper.createAndSignInUser(UploadSchemaTest.class, true);
    }

    @AfterClass
    public static void deleteResearcher() {
        if (developer != null) {
            developer.signOutAndDeleteUser();
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
        DeveloperClient developerClient = developer.getSession().getDeveloperClient();
        String schemaId = TEST_SCHEMA_ID_PREFIX + RandomStringUtils.randomAlphabetic(4);
        LOG.info("schemaId=" + schemaId);

        // set up some field defs
        UploadFieldDefinition fooFieldDef = new UploadFieldDefinition.Builder().withName("foo").withRequired(true)
                .withType(UploadFieldType.STRING).build();
        UploadFieldDefinition barFieldDef = new UploadFieldDefinition.Builder().withName("bar").withRequired(false)
                .withType(UploadFieldType.INT).build();
        UploadFieldDefinition bazFieldDef = new UploadFieldDefinition.Builder().withName("baz").withRequired(true)
                .withType(UploadFieldType.BOOLEAN).build();

        // Step 1: Create initial version of schema.
        UploadSchema schemaV1 = new UploadSchema.Builder().withFieldDefinitions(fooFieldDef)
                .withName("Upload Schema Integration Tests").withSchemaId(schemaId)
                .withSchemaType(UploadSchemaType.IOS_DATA).build();
        UploadSchema createdSchemaV1 = createOrUpdateSchemaAndVerify(schemaV1);

        // Step 2: Update to v2
        UploadSchema schemaV2 = new UploadSchema.Builder().copyOf(createdSchemaV1)
                .withFieldDefinitions(fooFieldDef, barFieldDef).withName("Schema Test II: The Sequel").build();
        UploadSchema updatedSchemaV2 = createOrUpdateSchemaAndVerify(schemaV2);

        // Step 3: Another update. Having multiple versions helps test the delete API.
        UploadSchema schemaV3 = new UploadSchema.Builder().copyOf(updatedSchemaV2)
                .withFieldDefinitions(fooFieldDef, barFieldDef, bazFieldDef).withName("Schema Test v3").build();
        createOrUpdateSchemaAndVerify(schemaV3);

        // Step 4: Delete v3 and verify the getter returns v2.
        developerClient.deleteUploadSchema(schemaId, 3);
        UploadSchema returnedAfterDelete = developerClient.getMostRecentUploadSchemaRevision(schemaId);
        assertEquals(updatedSchemaV2, returnedAfterDelete);

        // Step 4a: Use list API to verify v1 and v2 are both still present
        boolean v1Found = false;
        boolean v2Found = false;
        ResourceList<UploadSchema> schemaList = developerClient.getUploadSchema(schemaId);
        for (UploadSchema oneSchema : schemaList) {
            if (oneSchema.getSchemaId().equals(schemaId)) {
                int rev = oneSchema.getRevision();
                if (rev == 1) {
                    assertEquals(createdSchemaV1, oneSchema);
                    v1Found = true;
                } else if (rev == 2) {
                    assertEquals(updatedSchemaV2, oneSchema);
                    v2Found = true;
                } else {
                    fail("Unexpected schema revision: " + rev);
                }
            }
        }
        assertTrue(v1Found);
        assertTrue(v2Found);

        // Step 5: Delete all schemas with the test schema ID
        developerClient.deleteUploadSchemaAllRevisions(schemaId);

        // Step 5a: Get API should throw
        Exception thrownEx = null;
        try {
            developerClient.getUploadSchema(schemaId);
            fail("expected exception");
        } catch (EntityNotFoundException ex) {
            thrownEx = ex;
        }
        assertNotNull(thrownEx);

        // Step 5b: Use list API to verify no schemas with this ID
        ResourceList<UploadSchema> schemaList2 = developerClient.getAllUploadSchemas();
        for (UploadSchema oneSchema : schemaList2) {
            if (oneSchema.getSchemaId().equals(schemaId)) {
                fail("Found schema with ID " + schemaId + " even though it should have been deleted");
            }
        }
    }

    private static UploadSchema createOrUpdateSchemaAndVerify(UploadSchema schema) {
        DeveloperClient developerClient = developer.getSession().getDeveloperClient();
        UploadSchema returnedSchema = developerClient.createOrUpdateUploadSchema(schema);

        // all fields should match, except revision which is incremented
        assertEquals(schema.getFieldDefinitions(), returnedSchema.getFieldDefinitions());
        assertEquals(schema.getName(), returnedSchema.getName());
        assertEquals(schema.getSchemaId(), returnedSchema.getSchemaId());
        assertEquals(schema.getSchemaType(), returnedSchema.getSchemaType());

        if (schema.getRevision() == null) {
            assertEquals(1, returnedSchema.getRevision().intValue());
        } else {
            assertEquals(schema.getRevision() + 1, returnedSchema.getRevision().intValue());
        }

        return returnedSchema;
    }

    @Test(expected=UnauthorizedException.class)
    public void unauthorizedTest() {
        user.getSession().getDeveloperClient().getAllUploadSchemas();
    }
}
