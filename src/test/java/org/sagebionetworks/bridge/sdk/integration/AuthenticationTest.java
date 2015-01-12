package org.sagebionetworks.bridge.sdk.integration;

import org.junit.Test;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.Config;
import org.sagebionetworks.bridge.sdk.Session;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.exceptions.BridgeServerException;
import org.sagebionetworks.bridge.sdk.models.users.SignUpCredentials;

public class AuthenticationTest {

    @Test
    public void canResendEmailVerification() {
        String username = TestUserHelper.makeUserName(AuthenticationTest.class);
        String email = username + "@sagebase.org";
        String password = "P4ssword";
        
        try {
            
            SignUpCredentials signUp = new SignUpCredentials(username, email, password);
            ClientProvider.signUp(signUp);
            
            // Beyond an exception being thrown, there's not a lot you can test here.
            ClientProvider.resendEmailVerification(email);
            
        } finally {
            Config config = ClientProvider.getConfig();
            Session session = ClientProvider.signIn(config.getAdminCredentials());
            session.getAdminClient().deleteUser(email);
        }
    }
    
    @Test(expected = BridgeServerException.class)
    public void cannotSendToAnyRandomEmail() throws Exception {
        ClientProvider.resendEmailVerification("fooboo-sagebridge@antwerp.com");
    }
    
}
