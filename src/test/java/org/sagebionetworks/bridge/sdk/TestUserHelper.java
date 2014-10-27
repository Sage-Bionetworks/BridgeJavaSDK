package org.sagebionetworks.bridge.sdk;

import static org.sagebionetworks.bridge.sdk.Preconditions.checkNotEmpty;
import static org.sagebionetworks.bridge.sdk.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.sagebionetworks.bridge.sdk.models.SignInCredentials;
import org.sagebionetworks.bridge.sdk.models.SignUpCredentials;

public class TestUserHelper {

    public class TestUser {
        private final String sessionToken;
        private final String username;
        private final String email;
        private final String password;
        private final List<String> roles;

        public TestUser(String sessionToken, String username, String email, String password, List<String> roleList) {
            this.sessionToken = sessionToken;
            this.username = username;
            this.email = email;
            this.password = password;
            this.roles = roleList;
        }
        public String getSessionToken() {
            return sessionToken;
        }
        public String getUsername() {
            return username;
        }
        public String getEmail() {
            return email;
        }
        public String getPassword() {
            return password;
        }
        public List<String> getRoles() {
            return roles;
        }
        public boolean signOutAndDeleteUser() {
            provider.signOut();
            
            signInAsAdmin();
            
            boolean success = client.deleteUser(email);
            provider.signOut();
            return success;
        }
    }
    
    private ClientProvider provider;
    private BridgeAdminClient client;
    private String devName;
    
    private TestUserHelper(ClientProvider provider) {
        this.provider = provider;
        signInAsAdmin();
        this.client = provider.getAdminClient();
        this.devName = provider.getConfig().getDevName();
    }
    
    public static TestUserHelper valueOf(ClientProvider provider) {
        checkNotNull(provider, "Provider is required.");
        return new TestUserHelper(provider);
    }
    
    public TestUser createAndSignInUser(Class<?> cls, boolean consent, String... roles) {
        checkNotNull(cls);
        
        provider.signOut();
        signInAsAdmin();
        List<String> rolesList = (roles == null) ? Collections.<String>emptyList() : Arrays.asList(roles);
        String name = makeUserName(cls);
        SignUpCredentials signUp = SignUpCredentials.valueOf()
                .setUsername(name)
                .setEmail(name + "@sagebridge.org")
                .setPassword("P4ssword");
        client.createUser(signUp, rolesList, consent);

        provider.signOut();
        provider.signIn(SignInCredentials.valueOf().setUsername(signUp.getUsername()).setPassword(signUp.getPassword()));
        
        return new TestUser(provider.getSessionToken(), signUp.getUsername(), signUp.getEmail(), signUp.getPassword(), rolesList);
    }
    
    public boolean signOutAndDeleteUser(TestUser testUser) {
        checkNotNull(testUser);
        
        provider.signOut();
        signInAsAdmin();
        
        boolean result = client.deleteUser(testUser.getEmail());
        
        provider.signOut();
        return result;
    }
    
    public String makeUserName(Class<?> cls) {
        String clsPart = cls.getSimpleName();
        String rndPart = RandomStringUtils.randomAlphabetic(4);
        return String.format("%s-%s-%s", devName, clsPart, rndPart);
    }
    
    private void signInAsAdmin() {
        String adminEmail = provider.getConfig().getAdminEmail();
        String adminPassword = provider.getConfig().getAdminPassword();
        checkNotEmpty(adminEmail, "admin.email property is not set");
        checkNotEmpty(adminPassword, "admin.password property is not set");
        
        SignInCredentials adminSignIn = SignInCredentials.valueOf().setUsername(adminEmail).setPassword(adminPassword);
        provider.signIn(adminSignIn);
    }
    
}
