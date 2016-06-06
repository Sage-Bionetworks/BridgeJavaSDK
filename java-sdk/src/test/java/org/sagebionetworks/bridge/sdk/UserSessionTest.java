package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.models.accounts.SharingScope;
import org.sagebionetworks.bridge.sdk.models.accounts.StudyParticipant;
import org.sagebionetworks.bridge.sdk.models.subpopulations.ConsentStatus;
import org.sagebionetworks.bridge.sdk.models.subpopulations.SubpopulationGuid;
import org.sagebionetworks.bridge.sdk.utils.Utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class UserSessionTest {

    private static final String SESSION_TOKEN = "sessionToken";
    private static final String ID = "ABC";
    private static final HashSet<String> DATA_GROUPS = Sets.newHashSet("group1", "group2");
    private static final SubpopulationGuid SUBPOP_GUID = new SubpopulationGuid("study");
    private static final ConsentStatus CONSENT_STATUS = new ConsentStatus("Consent Name", "study", true, true, true);
    
    private static ObjectMapper mapper = Utilities.getMapper();

    @Test
    public void canConstructUserSessionFromJson() throws Exception {
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
        
        UserSession session = mapper.readValue(json, UserSession.class);
        assertEquals(SESSION_TOKEN, session.getSessionToken());
        assertTrue(session.isAuthenticated());
        assertTrue(session.isConsented());
        assertEquals(CONSENT_STATUS, session.getConsentStatuses().get(SUBPOP_GUID));
        StudyParticipant participant = session.getStudyParticipant();
        assertEquals(ID, participant.getId());
        assertEquals(DATA_GROUPS, participant.getDataGroups());
        assertEquals(SharingScope.NO_SHARING, participant.getSharingScope());
        assertEquals("FirstName", participant.getFirstName());
        assertEquals("LastName", participant.getLastName());
        assertEquals("email@email.com", participant.getEmail());
        List<String> langs = Lists.newArrayList(participant.getLanguages());
        assertEquals("en", langs.get(0));
        assertEquals("fr", langs.get(1));
    }
}
