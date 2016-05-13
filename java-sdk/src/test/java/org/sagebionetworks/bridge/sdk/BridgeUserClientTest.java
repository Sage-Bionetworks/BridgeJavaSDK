package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.models.accounts.StudyParticipant;
import org.sagebionetworks.bridge.sdk.utils.Utilities;

import com.fasterxml.jackson.databind.JsonNode;

@RunWith(MockitoJUnitRunner.class)
public class BridgeUserClientTest {

    @Mock
    BridgeSession session;
    
    @Captor
    ArgumentCaptor<UserSession> sessionCaptor;

    @Test
    public void updateParticipantSelfUpdatesSession() throws Exception {
        String json = Tests.unescapeJson("{'sessionToken':'sessionToken',"+
                "'authenticated':true,"+
                "'languages':['en','fr'],"+
                "'sharingScope':'no_sharing',"+
                "'firstName': 'FirstName'," +
                "'lastName': 'LastName'," +
                "'email': 'email@email.com'," +
                "'dataGroups':['group1','group2'],"+
                "'id':'ABC',"+
                "'consentStatuses':{'study':{'name':'Consent Name',"+
                    "'subpopulationGuid':'study',"+
                    "'required':true,"+
                    "'consented':true,"+
                    "'mostRecentConsent':true}},"+
                "'consented':true}");
        
        JsonNode node = Utilities.getMapper().readTree(json);
        
        doReturn(makeParticipant("DEF")).when(session).getStudyParticipant();
        doReturn(true).when(session).isSignedIn();
        
        BridgeUserClient client = new BridgeUserClient(session);
        client = spy(client);
        doReturn(node).when(client).post(anyString(), any(StudyParticipant.class), eq(JsonNode.class));
        
        StudyParticipant participant = makeParticipant("ABC");
        client.saveStudyParticipant(participant);
        
        verify(session).setUserSession(sessionCaptor.capture());
        UserSession session = sessionCaptor.getValue();
        assertEquals("ABC", session.getStudyParticipant().getId());
    }
    
    private StudyParticipant makeParticipant(String id) {
        return new StudyParticipant.Builder().withId(id).build();
    }
}
