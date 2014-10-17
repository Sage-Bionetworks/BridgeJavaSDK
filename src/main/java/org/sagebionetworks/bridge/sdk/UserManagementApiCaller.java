package org.sagebionetworks.bridge.sdk;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
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

    boolean createUser(String email, String username, String password, boolean consent) {
        assert provider.isSignedIn();
        assert email != null && username != null && password != null;

        ObjectNode node = mapper.createObjectNode();
        node.put("email", email);
        node.put("username", username);
        node.put("password", password);
        node.put("consent", consent);

        String json;
        try {
            json = mapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            throw new BridgeSDKException(
                    "Problem constructing string out of arguments. Are you sure they aren't malformed or incorrect? email="
                            + email + ", username=" + username + ", password=" + password + "consent=" + consent, e);
        }

        String url = provider.getConfig().getUserManagementApi();
        HttpResponse response = post(url, json);

        return response.getStatusLine().getStatusCode() == 201;
    }

    boolean deleteUser(String email) {
        assert provider.isSignedIn();
        assert email != null;

        Map<String,String> queryParams = new HashMap<String,String>();
        queryParams.put("email", email);

        String url = provider.getConfig().getUserManagementApi();
        HttpResponse response = delete(url, queryParams);

        return response.getStatusLine().getStatusCode() == 200;
    }

    boolean revokeAllConsentRecords(String email) {
        assert provider.isSignedIn();

        Map<String,String> queryParams = new HashMap<String,String>();
        queryParams.put("email", email);

        String url = provider.getConfig().getUserManagementConsentApi();
        HttpResponse response = delete(url, queryParams);

        return response.getStatusLine().getStatusCode() == 200;
    }

}
