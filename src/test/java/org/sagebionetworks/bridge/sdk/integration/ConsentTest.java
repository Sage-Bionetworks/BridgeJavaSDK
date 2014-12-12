package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.UserClient;
import org.sagebionetworks.bridge.sdk.exceptions.ConsentRequiredException;
import org.sagebionetworks.bridge.sdk.exceptions.EntityAlreadyExistsException;
import org.sagebionetworks.bridge.sdk.exceptions.InvalidEntityException;
import org.sagebionetworks.bridge.sdk.models.users.ConsentSignature;

import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("unchecked")
public class ConsentTest {
    private static final String FAKE_IMAGE_DATA = "VGhpcyBpc24ndCBhIHJlYWwgaW1hZ2Uu";

    @Test
    public void canToggleDataSharing() {
        TestUser testUser = TestUserHelper.createAndSignInUser(ConsentTest.class, true);
        try {
            UserClient client = testUser.getSession().getUserClient();

            assertTrue("Initially sharing data", testUser.getSession().isDataSharing());
            client.suspendDataSharing();
            assertFalse("Not sharing data", testUser.getSession().isDataSharing());
            client.resumeDataSharing();
            assertTrue("Sharing data", testUser.getSession().isDataSharing());
        } finally {
            testUser.signOutAndDeleteUser();
        }
    }

    @Test
    public void giveAndGetConsent() {
        giveAndGetConsentHelper("Eggplant McTester", new LocalDate(1970, 1, 1), null, null);
    }

    @Test
    public void giveAndGetConsentWithSignatureImage() {
        giveAndGetConsentHelper("Eggplant McTester", new LocalDate(1970, 1, 1), FAKE_IMAGE_DATA, "image/fake");
    }

    @Test
    public void signedInUserMustGiveConsent() {
        TestUser user = TestUserHelper.createAndSignInUser(ConsentTest.class, false);
        try {
            UserClient client = user.getSession().getUserClient();
            assertFalse("User has not consented", user.getSession().isConsented());
            try {
                client.getSchedules();
                fail("Should have required consent.");
            } catch(ConsentRequiredException e) {
                assertEquals("Exception is a 412 Precondition Failed", 412, e.getStatusCode());
            }
            LocalDate date = new LocalDate(1970, 10, 10);
            client.consentToResearch(new ConsentSignature(user.getUsername(), date, null, null));
            assertTrue("User has consented", user.getSession().isConsented());
            client.getSchedules();
        } finally {
            user.signOutAndDeleteUser();
        }
    }
    
    @Test(expected=InvalidEntityException.class)
    public void userMustMeetMinAgeRequirements() {
        TestUser user = TestUserHelper.createAndSignInUser(ConsentTest.class, false);
        try {
            UserClient client = user.getSession().getUserClient();
            LocalDate date = LocalDate.now(); // impossibly young.
            client.consentToResearch(new ConsentSignature(user.getUsername(), date, null, null));
        } finally {
            user.signOutAndDeleteUser();
        }
    }

    @Test
    public void jsonSerialization() throws Exception {
        // setup
        String sigJson = "{\n" +
                "   \"name\":\"Jason McSerializer\",\n" +
                "   \"birthdate\":\"1985-12-31\",\n" +
                "   \"imageData\":\"" + FAKE_IMAGE_DATA + "\",\n" +
                "   \"imageMimeType\":\"image/fake\"\n" +
                "}";
        ObjectMapper jsonObjectMapper = new ObjectMapper();

        // de-serialize and validate
        ConsentSignature sig = jsonObjectMapper.readValue(sigJson, ConsentSignature.class);
        assertEquals("(ConsentSignature instance) name matches", "Jason McSerializer", sig.getName());
        assertEquals("(ConsentSignature instance) birthdate matches", "1985-12-31",
                sig.getBirthdate().toString(ISODateTimeFormat.date()));
        assertEquals("(ConsentSignature instance) imageData matches", FAKE_IMAGE_DATA, sig.getImageData());
        assertEquals("(ConsentSignature instance) imageMimeType matches", "image/fake", sig.getImageMimeType());

        // re-serialize, then parse as a raw map to validate the JSON
        String reserializedJson = jsonObjectMapper.writeValueAsString(sig);
        Map<String, String> jsonAsMap = jsonObjectMapper.readValue(reserializedJson, Map.class);
        assertEquals("JSON map has exactly 4 elements", 4, jsonAsMap.size());
        assertEquals("(JSON map) name matches", "Jason McSerializer", jsonAsMap.get("name"));
        assertEquals("(JSON map) birthdate matches", "1985-12-31", jsonAsMap.get("birthdate"));
        assertEquals("(JSON map) imageData matches", FAKE_IMAGE_DATA, jsonAsMap.get("imageData"));
        assertEquals("(JSON map) imageMimeType matches", "image/fake", jsonAsMap.get("imageMimeType"));
    }

    // helper method to test consent with and without images
    private static void giveAndGetConsentHelper(String name, LocalDate birthdate, String imageData,
            String imageMimeType) {
        TestUser testUser = TestUserHelper.createAndSignInUser(ConsentTest.class, false);
        try {
            UserClient client = testUser.getSession().getUserClient();
            assertFalse("User has not consented", testUser.getSession().isConsented());

            // get consent should fail if the user hasn't given consent
            ConsentRequiredException thrownConsentRequired = null;
            try {
                client.getConsentSignature();
                fail("expected ConsentRequiredException");
            } catch (ConsentRequiredException ex) {
                thrownConsentRequired = ex;
            }
            assertNotNull("expected ConsentRequiredException", thrownConsentRequired);

            // give consent
            client.consentToResearch(new ConsentSignature(name, birthdate, imageData, imageMimeType));

            // get consent and validate that it's the same consent
            // For birthdate, we convert it to a formatted string, since DateTime.equals() can be wonky sometimes.
            ConsentSignature sigFromServer = client.getConsentSignature();
            assertEquals("name matches", name, sigFromServer.getName());
            assertEquals("birthdate matches", birthdate.toString(ISODateTimeFormat.date()),
                    sigFromServer.getBirthdate().toString(ISODateTimeFormat.date()));
            assertEquals("imageData matches", imageData, sigFromServer.getImageData());
            assertEquals("imageMimeType matches", imageMimeType, sigFromServer.getImageMimeType());

            // giving consent again will throw
            EntityAlreadyExistsException thrownAlreadyExists = null;
            try {
                client.consentToResearch(new ConsentSignature("bad name", LocalDate.now(), null, null));
                fail("expected EntityAlreadyExistsException");
            } catch (EntityAlreadyExistsException ex) {
                thrownAlreadyExists = ex;
            }
            assertNotNull("expected EntityAlreadyExistsException", thrownAlreadyExists);
        } finally {
            testUser.signOutAndDeleteUser();
        }
    }
}
