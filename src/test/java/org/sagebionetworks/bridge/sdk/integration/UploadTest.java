package org.sagebionetworks.bridge.sdk.integration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
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

    private String test = TestUserHelper.makeUserName(this.getClass());

    @Before
    public void before() {
        testUser = TestUserHelper.createAndSignInUser(HealthDataTest.class, true);
        user = testUser.getSession().getUserClient();
    }

    @After
    public void after() {
        TestUserHelper.signOut(testUser);
    }

    @Test
    public void test() {
        createTextFile();
        UploadRequest request = createRequest();
        UploadSession session = user.requestUploadSession(request);

        user.upload(session, request, test + ".txt");

        deleteTextFile();
    }

    private UploadRequest createRequest() {
        UploadRequest request = new UploadRequest()
                        .setContentLength(test.getBytes().length)
                        .setContentMd5(createMd5(test))
                        .setContentType("text/plain")
                        .setName(test);
        return request;
    }

    private void createTextFile() {
        PrintWriter writer;
        try {
            writer = new PrintWriter(test + ".txt", "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new BridgeSDKException(e);
        }
        writer.println("The first line");
        writer.println("The second line");
        writer.close();
    }

    private void deleteTextFile() {
        new File(test + ".txt").delete();
    }

    private String createMd5(String s) {
        return Base64.encodeBase64String(DigestUtils.md5(s));
    }


}

