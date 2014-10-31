package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.sagebionetworks.bridge.sdk.models.SignUpCredentials;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

class UserManagementApiCaller extends BaseApiCaller {

    private final ObjectMapper mapper = Utilities.getMapper();

    private UserManagementApiCaller(Session session) {
        super(session);
    }

    static UserManagementApiCaller valueOf(Session session) {
        return new UserManagementApiCaller(session);
    }

    boolean createUser(SignUpCredentials signUp, List<String> roles, boolean consent) {
        checkArgument(isNotBlank(signUp.getEmail()));
        checkArgument(isNotBlank(signUp.getPassword()));
        checkArgument(isNotBlank(signUp.getEmail()));

        ObjectNode node = (ObjectNode)mapper.valueToTree(signUp);
        node.put("consent", consent);
        if (roles != null && !roles.isEmpty()) {
            node.set("roles", mapper.valueToTree(roles));
        }
        String url = config.getUserManagementApi();
        HttpResponse response = post(url, node.toString());

        return response.getStatusLine().getStatusCode() == 201;
    }
    
    boolean createUser(SignUpCredentials signUp, boolean consent) {
        return createUser(signUp, null, consent);
    }

    boolean deleteUser(String email) {
        checkArgument(isNotBlank(email));

        Map<String,String> queryParams = new HashMap<String,String>();
        queryParams.put("email", email);

        String url = config.getUserManagementApi();
        HttpResponse response = delete(url, queryParams);

        return response.getStatusLine().getStatusCode() == 200;
    }
}
