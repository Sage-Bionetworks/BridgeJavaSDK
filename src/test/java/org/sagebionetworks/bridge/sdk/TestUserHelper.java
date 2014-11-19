package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.exceptions.ConsentRequiredException;
import org.sagebionetworks.bridge.sdk.models.users.SignInCredentials;
import org.sagebionetworks.bridge.sdk.models.users.SignUpCredentials;

import com.google.common.collect.Lists;

public class TestUserHelper {

    public static class TestUser {
        private final AdminClient adminClient;
        private final Session userSession;
        private final String username;
        private final String email;
        private final String password;
        private final List<String> roles;

        public TestUser(AdminClient client, Session userSession, String username, String email, String password,
                List<String> roleList) {
            this.adminClient = client;
            this.userSession = userSession;
            this.username = username;
            this.email = email;
            this.password = password;
            this.roles = roleList;
        }
        public Session getSession() {
            return userSession;
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
            userSession.signOut();
            return adminClient.deleteUser(email);
        }
        public boolean isSignedIn() {
            return userSession.isSignedIn();
        }
    }
    
    public static TestUser getSignedInAdmin() {
        Config config = ClientProvider.getConfig();
        Session session = ClientProvider.signIn(config.getAdminCredentials());
        AdminClient adminClient = session.getAdminClient();

        return new TestUserHelper.TestUser(adminClient, session, "", "", "", Lists.newArrayList(Tests.ADMIN_ROLE));
    }
    
    public static void signOut(TestUser testUser) {
        testUser.getSession().signOut();
    }

    public static TestUser createAndSignInUser(Class<?> cls, boolean consent, String... roles) {
        checkNotNull(cls);

        Config config = ClientProvider.getConfig();
        Session session = ClientProvider.signIn(config.getAdminCredentials());
        AdminClient adminClient = session.getAdminClient();

        List<String> rolesList = (roles == null) ? Collections.<String>emptyList() : Arrays.asList(roles);
        String name = makeUserName(cls);
        SignUpCredentials signUp = new SignUpCredentials(name, name + "@sagebridge.org", "P4ssword");
        adminClient.createUser(signUp, rolesList, consent);
        
        Session userSession = null;
        try {
            SignInCredentials signIn = new SignInCredentials(name, "P4ssword");
            userSession = ClientProvider.signIn(signIn);
        } catch(ConsentRequiredException e) {
            userSession = e.getSession();
            // Do nothing. Some tests want to play around with a user who has not yet consented.
        }
        return new TestUserHelper.TestUser(adminClient, userSession, signUp.getUsername(), signUp.getEmail(),
                signUp.getPassword(), rolesList);
    }

    public static String makeUserName(Class<?> cls) {
        Config config = ClientProvider.getConfig();
        String devName = config.getDevName();
        String clsPart = cls.getSimpleName();
        String rndPart = RandomStringUtils.randomAlphabetic(4);
        return String.format("%s-%s-%s", devName, clsPart, rndPart);
    }
}
