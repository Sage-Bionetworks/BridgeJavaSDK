package org.sagebionetworks.bridge.sdk;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;

public class GlobalCleanup {

    private static TestUser admin;

    @BeforeClass
    public void setup() {
        admin = TestUserHelper.getSignedInAdmin();
    }

    @AfterClass
    public void teardown() {
        admin.getSession().signOut();
    }

    @Test
    public void deleteAllAccruedTestUsers() {
        admin.getSession().getAdminClient().deleteAllTestUsers();
    }
}
