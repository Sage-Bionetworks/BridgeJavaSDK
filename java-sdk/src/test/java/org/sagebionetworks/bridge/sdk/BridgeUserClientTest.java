package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
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
import org.sagebionetworks.bridge.sdk.utils.BridgeUtils;

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
        
        UserSession session = BridgeUtils.getMapper().readValue(json, UserSession.class);
        
        //doReturn(makeParticipant("DEF")).when(session).getStudyParticipant();
        
        BridgeSession bridgeSession = spy(new BridgeSession(session));
        UserClient client = new UserClient(bridgeSession);
        client = spy(client);
        doReturn(session).when(client).post(anyString(), any(StudyParticipant.class), eq(UserSession.class));
        
        StudyParticipant participant = makeParticipant("DEF"); // id is ignored
        client.saveStudyParticipant(participant);
        
        verify(bridgeSession).setUserSession(sessionCaptor.capture());
        UserSession updatedSession = sessionCaptor.getValue();
        assertEquals("ABC", updatedSession.getStudyParticipant().getId());
    }
    
    private StudyParticipant makeParticipant(String id) {
        return new StudyParticipant.Builder().withId(id).build();
    }
}
