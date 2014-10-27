package org.sagebionetworks.bridge.sdk;

import static org.sagebionetworks.bridge.sdk.Preconditions.checkNotEmpty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.sagebionetworks.bridge.sdk.models.SignUpCredentials;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

class UserManagementApiCaller extends BaseApiCaller {

    private final ObjectMapper mapper = Utilities.getMapper();

    private UserManagementApiCaller(ClientProvider provider) {
        super(provider);
    }

    static UserManagementApiCaller valueOf(ClientProvider provider) {
        return new UserManagementApiCaller(provider);
    }

    boolean createUser(SignUpCredentials signUp, List<String> roles, boolean consent) {
        checkNotEmpty(signUp.getEmail());
        checkNotEmpty(signUp.getPassword());
        checkNotEmpty(signUp.getEmail());

        ObjectNode node = (ObjectNode)mapper.valueToTree(signUp);
        node.put("consent", consent);
        if (roles != null && !roles.isEmpty()) {
            node.set("roles", mapper.valueToTree(roles));
        }
        String url = provider.getConfig().getUserManagementApi();
        HttpResponse response = post(url, node.toString());

        return response.getStatusLine().getStatusCode() == 201;
    }
    
    boolean createUser(SignUpCredentials signUp, boolean consent) {
        return createUser(signUp, null, consent);
    }

    boolean deleteUser(String email) {
        checkNotEmpty(email);

        Map<String,String> queryParams = new HashMap<String,String>();
        queryParams.put("email", email);

        String url = provider.getConfig().getUserManagementApi();
        HttpResponse response = delete(url, queryParams);

        return response.getStatusLine().getStatusCode() == 200;
    }

    boolean revokeAllConsentRecords(String email) {
        checkNotEmpty(email);
        
        Map<String,String> queryParams = new HashMap<String,String>();
        queryParams.put("email", email);

        String url = provider.getConfig().getUserManagementConsentApi();
        HttpResponse response = delete(url, queryParams);

        return response.getStatusLine().getStatusCode() == 200;
    }

}
