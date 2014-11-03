package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkNotNull;

import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.sdk.models.users.ConsentSignature;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;


class ConsentApiCaller extends BaseApiCaller {

    private ConsentApiCaller(Session session) {
        super(session);
    }

    static ConsentApiCaller valueOf(Session session) {
        return new ConsentApiCaller(session);
    }
    
    void consentToResearch(ConsentSignature signature) {
        checkNotNull(signature, Bridge.CANNOT_BE_NULL, "ConsentSignature");
        
        String url = config.getConsentApi();
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("name", signature.getName());
        node.put("birthdate", signature.getBirthdate().toString(ISODateTimeFormat.date()));
        
        post(url, node.toString());
    }

    void suspendDataSharing() {
        String url = config.getConsentSuspendApi();
        post(url);
    }

    void resumeDataSharing() {
        String url = config.getConsentResumeApi();
        post(url);
    }

}
