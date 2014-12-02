package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.exceptions.ConsentRequiredException;
import org.sagebionetworks.bridge.sdk.exceptions.EntityAlreadyExistsException;
import org.sagebionetworks.bridge.sdk.models.users.ConsentSignature;
import org.sagebionetworks.bridge.sdk.UserClient;

public class ConsentTest {

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
        giveAndGetConsentHelper("Eggplant McTester", DateTime.parse("1970-01-01", ISODateTimeFormat.date()), null,
                null);
    }

    @Test
    public void giveAndGetConsentWithSignatureImage() {
        String fakeImageData = "VGhpcyBpc24ndCBhIHJlYWwgaW1hZ2Uu";
        giveAndGetConsentHelper("Eggplant McTester", DateTime.parse("1970-01-01", ISODateTimeFormat.date()),
                fakeImageData, "image/fake");
    }

    // helper method to test consent with and without images
    private static void giveAndGetConsentHelper(String name, DateTime birthdate, String imageData,
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
                client.consentToResearch(new ConsentSignature("bad name", DateTime.now(), null, null));
                fail("expected EntityAlreadyExistsException");
            } catch (EntityAlreadyExistsException ex) {
                thrownAlreadyExists = ex;
            }
            assertNotNull("expected EntityAlreadyExistsException", thrownAlreadyExists);
        } finally {
            testUser.signOutAndDeleteUser();
        }
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
            client.consentToResearch(new ConsentSignature(user.getUsername(), DateTime.now(), null, null));
            assertTrue("User has not consented", user.getSession().isConsented());
            client.getSchedules();
        } finally {
            user.signOutAndDeleteUser();
        }
    }
}
