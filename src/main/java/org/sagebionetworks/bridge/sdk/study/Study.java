package org.sagebionetworks.bridge.sdk.study;

import org.sagebionetworks.bridge.sdk.user.User;
import org.sagebionetworks.bridge.sdk.util.ModelFactory;

public final class Study {

    private final String name;
    private final int minAge;
    
    public Study(String name, int minAge) {
        this.name = name;
        this.minAge = minAge;
    }
    
    public String getName() { return this.name; }
    public int getMinAge() { return this.minAge; }
    
    public User getUser(String username, String password) {
        User user = ModelFactory.build(User.class);
        user.set(AuthenticationClient.signIn(username, password));
        // TODO user.setProfile(UserProfileClient.getUser(user.getSession().getSessionToken()));
        return user;
    }
    
    public void registerNewUser(String email, String username, String password) {
        AuthenticationClient.signUp(email, username, password);
    }
}
