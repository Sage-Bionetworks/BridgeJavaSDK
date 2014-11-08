package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.BridgeSDKException;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.UserClient;
import org.sagebionetworks.bridge.sdk.models.UploadRequest;
import org.sagebionetworks.bridge.sdk.models.UploadSession;

public class UploadTest {

    private TestUser testUser;
    private UserClient user;

    private String test = "target/" + TestUserHelper.makeUserName(this.getClass()) + ".txt";

    @Before
    public void before() {
        testUser = TestUserHelper.createAndSignInUser(UploadTest.class, true);
        user = testUser.getSession().getUserClient();

        createTextFile();
    }

    @After
    public void after() {
        TestUserHelper.signOut(testUser);
        deleteTextFile();
    }

    @Test
    public void canRequestUploadAndUpload() {
        UploadRequest request = createRequest();
        UploadSession session = user.requestUploadSession(request);

        user.upload(session, request, test);
    }

    @Test
    public void cannotUploadAfterExpirationDate() {
        UploadRequest request = createRequest();
        UploadSession session = user.requestUploadSession(request);

        try {
            // Get number of milliseconds between now and expiration time, plus one second.
            long millis = session.getExpires()
                            .minus(DateTime.now().getMillis())
                            .plusSeconds(1)
                            .getMillis();
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new BridgeSDKException(e);
        }

        try {
            user.upload(session, request, test);
            fail("user upload should have failed.");
        } catch (Exception e) {
            assertEquals("Exception thrown should be an illegal argument exception.",
                    e.getClass(), IllegalArgumentException.class);
        }

    }

    private UploadRequest createRequest() {
        UploadRequest request = new UploadRequest()
                        .setContentLength((int) new File(test).length())
                        .setContentMd5(createMd5())
                        .setContentType("text/plain")
                        .setName(test);
        return request;
    }

    private void createTextFile() {
        PrintWriter writer;
        try {
            writer = new PrintWriter(test, "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new BridgeSDKException(e);
        }
        writer.println("The first line");
        writer.println("The second line");
        writer.close();
    }

    private void deleteTextFile() {
        try {
            Files.delete(Paths.get(test));
        } catch (IOException e) {
            throw new BridgeSDKException(e);
        }
    }

    private String createMd5() {
        try {
            byte[] b = Files.readAllBytes(Paths.get(test));
            return Base64.encodeBase64String(DigestUtils.md5(b));
        } catch (IOException e) {
            throw new BridgeSDKException(e);
        }

    }

}

