package org.sagebionetworks.bridge.sdk.util;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.sagebionetworks.bridge.sdk.user.User;

public class ModelFactoryTest {

    @Test
    public void canBuildUserAndStudy() {
        User user = ModelFactory.build(User.class);
        assertNotNull(user);
        // TODO: build study object
//        Study study = ModelFactory.build(Study.class);
    }
}
