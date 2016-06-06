package org.sagebionetworks.bridge.sdk;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import org.sagebionetworks.bridge.sdk.models.accounts.StudyParticipant;

@RunWith(MockitoJUnitRunner.class)
public class BridgeResearcherClientTest {

    @Mock
    BridgeSession session;
    
    @Test
    public void testSessionNotUpdatedWhenIdsDifferent() {
        doReturn(makeParticipant("DEF")).when(session).getStudyParticipant();
        doReturn(true).when(session).isSignedIn();
        
        ResearcherClient client = new ResearcherClient(session);
        client = spy(client);
        doReturn(null).when(client).post(anyString(), any(StudyParticipant.class));
        
        StudyParticipant participant = makeParticipant("ABC");
        client.updateStudyParticipant(participant);
        
        verify(session, never()).removeSession();
    }
    
    @Test
    public void testSessionUpdatedWhenIdsSame() {
        doReturn(makeParticipant("DEF")).when(session).getStudyParticipant();
        doReturn(true).when(session).isSignedIn();
        
        ResearcherClient client = new ResearcherClient(session);
        client = spy(client);
        doReturn(null).when(client).post(anyString(), any(StudyParticipant.class));
        
        StudyParticipant participant = makeParticipant("DEF");
        client.updateStudyParticipant(participant);
        
        verify(session).removeSession();
    }
    
    private StudyParticipant makeParticipant(String id) {
        return new StudyParticipant.Builder().withId(id).build();
    }
}
