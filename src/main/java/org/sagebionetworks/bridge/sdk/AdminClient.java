package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.sagebionetworks.bridge.sdk.models.users.SignUpCredentials;

public interface AdminClient {

    public boolean createUser(SignUpCredentials signUp, List<String> roles, boolean consent);

    public boolean deleteUser(String email);

}
