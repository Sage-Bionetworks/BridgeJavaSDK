package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.sagebionetworks.bridge.IntegrationSmokeTest;
import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.Session;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.Utilities;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.UserClient;
import org.sagebionetworks.bridge.sdk.exceptions.ConsentRequiredException;
import org.sagebionetworks.bridge.sdk.exceptions.EntityAlreadyExistsException;
import org.sagebionetworks.bridge.sdk.exceptions.InvalidEntityException;
import org.sagebionetworks.bridge.sdk.models.users.ConsentSignature;
import org.sagebionetworks.bridge.sdk.models.users.SharingScope;
import org.sagebionetworks.bridge.sdk.models.users.SignInCredentials;

import com.fasterxml.jackson.databind.ObjectMapper;

@Category(IntegrationSmokeTest.class)
@SuppressWarnings("unchecked")
public class ConsentTest {
    private static final String FAKE_IMAGE_DATA = "VGhpcyBpc24ndCBhIHJlYWwgaW1hZ2Uu";

    @Test
    public void canToggleDataSharing() {
        TestUser testUser = TestUserHelper.createAndSignInUser(ConsentTest.class, true);
        UserClient client = testUser.getSession().getUserClient();
        Session session = testUser.getSession();
        try {
            // starts out with no sharing
            assertEquals(SharingScope.NO_SHARING, session.getSharingScope());
            
            // Change, verify in-memory session changed, verify after signing in again that server state has changed
            client.changeSharingScope(SharingScope.SPONSORS_AND_PARTNERS);
            assertEquals(SharingScope.SPONSORS_AND_PARTNERS, testUser.getSession().getSharingScope());
            session.signOut();
            session = ClientProvider.signIn(new SignInCredentials(Tests.TEST_KEY, testUser.getEmail(), testUser.getPassword()));
            client = session.getUserClient();
            assertEquals(SharingScope.SPONSORS_AND_PARTNERS, session.getSharingScope());
            
            // Do the same thing in reverse, setting to no sharing
            client.changeSharingScope(SharingScope.NO_SHARING);
            session.signOut();

            session = ClientProvider.signIn(new SignInCredentials(Tests.TEST_KEY, testUser.getEmail(), testUser.getPassword()));
            assertEquals(SharingScope.NO_SHARING, session.getSharingScope());

            session.signOut();
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
            client.consentToResearch(new ConsentSignature(user.getUsername(), date, null, null), SharingScope.SPONSORS_AND_PARTNERS);
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
            client.consentToResearch(new ConsentSignature(user.getUsername(), date, null, null), SharingScope.ALL_QUALIFIED_RESEARCHERS);
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
        ObjectMapper jsonObjectMapper = Utilities.getMapper();

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
        ConsentSignature sig = new ConsentSignature(name, birthdate, imageData, imageMimeType);
        try {
            UserClient client = testUser.getSession().getUserClient();
            assertFalse("User has not consented", testUser.getSession().isConsented());

            // get consent should fail if the user hasn't given consent
            try {
                client.getConsentSignature();
                fail("ConsentRequiredException not thrown");
            } catch (ConsentRequiredException ex) {
                // expected
            }

            // give consent
            client.consentToResearch(sig, SharingScope.ALL_QUALIFIED_RESEARCHERS);
            
            // The local session should reflect the sharing scope
            assertEquals(SharingScope.ALL_QUALIFIED_RESEARCHERS, testUser.getSession().getSharingScope());
            
            // get consent and validate that it's the same consent
            ConsentSignature sigFromServer = client.getConsentSignature();
            
            assertEquals("name matches", name, sigFromServer.getName());
            assertEquals("birthdate matches", birthdate, sigFromServer.getBirthdate());
            assertEquals("imageData matches", imageData, sigFromServer.getImageData());
            assertEquals("imageMimeType matches", imageMimeType, sigFromServer.getImageMimeType());
            
            // giving consent again will throw
            try {
                client.consentToResearch(sig, SharingScope.ALL_QUALIFIED_RESEARCHERS);
                fail("EntityAlreadyExistsException not thrown");
            } catch (EntityAlreadyExistsException ex) {
                // expected
            }

            // The remote session should also reflect the sharing scope
            testUser.getSession().signOut();
            Session session = ClientProvider.signIn(new SignInCredentials("api", testUser.getUsername(), testUser.getPassword()));
            assertEquals(SharingScope.ALL_QUALIFIED_RESEARCHERS, session.getSharingScope());
            
        } finally {
            testUser.signOutAndDeleteUser();
        }
    }
    
    @Test
    public void canEmailConsentAgreement() {
        TestUser testUser = TestUserHelper.createAndSignInUser(ConsentTest.class, true);
        try {
            UserClient client = testUser.getSession().getUserClient();
            client.emailConsentSignature(); // just verify it throws no errors
        } finally {
            testUser.signOutAndDeleteUser();
        }
    }
}
