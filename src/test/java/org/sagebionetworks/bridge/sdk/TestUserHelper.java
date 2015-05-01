package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.exceptions.ConsentRequiredException;
import org.sagebionetworks.bridge.sdk.models.users.SignInCredentials;
import org.sagebionetworks.bridge.sdk.models.users.SignUpByAdmin;

import com.google.common.collect.Sets;

public class TestUserHelper {

    public static class TestUser {
        private final AdminClient adminClient;
        private final Session userSession;
        private final String username;
        private final String email;
        private final String password;
        private final Set<String> roles;

        public TestUser(AdminClient client, Session userSession, String username, String email, String password,
                Set<String> roleList) {

            this.adminClient = client;
            this.userSession = userSession;
            this.username = username;
            this.email = email;
            this.password = password;
            this.roles = (roleList == null) ? new HashSet<String>() : roleList;
            roles.add("test_users");
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
        public Set<String> getRoles() {
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

        return new TestUserHelper.TestUser(adminClient, session, "", "", "", Sets.newHashSet(Tests.ADMIN_ROLE));
    }

    public static TestUser createAndSignInUser(Class<?> cls, boolean consent, String... roles) {
        checkNotNull(cls);

        ClientProvider.getClientInfo().withAppName("Integration Tests");

        Config config = ClientProvider.getConfig();
        Session session = ClientProvider.signIn(config.getAdminCredentials());
        AdminClient adminClient = session.getAdminClient();

        Set<String> rolesList = (roles == null) ? Sets.<String>newHashSet() : Sets.newHashSet(roles);
        rolesList.add("test_users");
        String name = makeUserName(cls);

        // For email address, we don't want consent emails to bounce or SES will get mad at us. All test user email
        // addresses should be in the form bridge-testing+[semi-unique token]@sagebase.org. This directs all test
        // email to bridge-testing@sagebase.org.
        String emailAddress = makeEmail(name);

        SignUpByAdmin signUp = new SignUpByAdmin(name, emailAddress, "P4ssword", rolesList, consent);
        adminClient.createUser(signUp);

        Session userSession = null;
        try {
            SignInCredentials signIn = new SignInCredentials(Tests.TEST_KEY, name, "P4ssword");
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
    
    public static String makeEmail(String userName) {
        return String.format("bridge-testing+%s@sagebase.org", userName);
    }
}
