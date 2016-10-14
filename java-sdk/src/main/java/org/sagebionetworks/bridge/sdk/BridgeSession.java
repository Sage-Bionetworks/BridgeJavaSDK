package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableMap;

import org.sagebionetworks.bridge.sdk.models.accounts.SharingScope;
import org.sagebionetworks.bridge.sdk.models.accounts.StudyParticipant;
import org.sagebionetworks.bridge.sdk.models.subpopulations.ConsentStatus;
import org.sagebionetworks.bridge.sdk.models.subpopulations.SubpopulationGuid;
import org.sagebionetworks.bridge.sdk.rest.model.SignIn;

import java.util.Map;

class BridgeSession implements Session {

    private static final String NOT_AUTHENTICATED = "This session has been signed out; create a new session to retrieve a valid client.";

    private String sessionToken;
    private StudyParticipant participant;
    private Map<SubpopulationGuid,ConsentStatus> consentStatuses;

    private final SignIn signIn;
    
    BridgeSession(UserSession session, SignIn signIn) {
        checkNotNull(session, "%s cannot be null", "UserSession");
        checkNotNull(signIn);

        setUserSession(session);
        this.signIn = signIn;
        StudyParticipant studyParticipant = session.getStudyParticipant();
    }

    public SignIn getSignIn() {
        return signIn;
    }

    /**
     * Check that the client is currently authenticated, throwing an exception 
     * if it is not.
     * @throws IllegalStateException
     */
    public void checkSignedIn() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
    }
    
    @Override
    public String getSessionToken() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return sessionToken;
    }
    
    @Override
    public boolean isConsented() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return ConsentStatus.isUserConsented(consentStatuses);
    }
    
    @Override
    public StudyParticipant getStudyParticipant() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return participant;
    }
    
    @Override
    public Map<SubpopulationGuid,ConsentStatus> getConsentStatuses() {
        return consentStatuses;
    }
    
    @Override
    public UserClient getUserClient() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return new UserClient(this);
    }

    @Override
    public DeveloperClient getDeveloperClient() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return new DeveloperClient(this);
    }
    
    @Override
    public ResearcherClient getResearcherClient() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return new ResearcherClient(this);
    }

    @Override
    public AdminClient getAdminClient() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return new AdminClient(this);
    }

    /** {@inheritDoc} */
    @Override
    public WorkerClient getWorkerClient() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return new WorkerClient(this);
    }

    @Override
    public ExternalIdentifiersClient getExternalIdentifiersClient() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return new ExternalIdentifiersClient(this);
    }
    
    @Override
    public ParticipantClient getParticipantClient() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return new ParticipantClient(this);
    }
    
    @Override
    public SchedulePlanClient getSchedulePlanClient() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return new SchedulePlanClient(this);
    }
    
    @Override
    public StudyClient getStudyClient() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return new StudyClient(this);
    }
    
    @Override
    public StudyConsentClient getStudyConsentClient() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return new StudyConsentClient(this);
    }
    
    @Override
    public SubpopulationClient getSubpopulationClient() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return new SubpopulationClient(this);
    }
    
    @Override
    public SurveyClient getSurveyClient() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return new SurveyClient(this);
    }
    
    @Override
    public UploadSchemaClient getUploadSchemaClient() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return new UploadSchemaClient(this);
    }
    
    @Override
    public ReportClient getReportClient() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return new ReportClient(this);
    }

    @Override
    public boolean isSignedIn() {
        return sessionToken != null;
    }

    @Override
    public synchronized void signOut() {
        if (sessionToken != null) {
            new BaseApiCaller(this).post(ClientProvider.getConfig().getSignOutApi());
            sessionToken = null;
        }
    }
    
    void removeSession() {
        sessionToken = null;
    }
    
    void setUserSession(UserSession session) {
        this.sessionToken = session.getSessionToken();
        this.participant = session.getStudyParticipant();
        this.consentStatuses = session.getConsentStatuses();
    }
    
    void setStudyParticipant(StudyParticipant participant) {
        this.participant = participant;
    }
    
    void setSharingScope(SharingScope sharingScope) {
        this.participant = new StudyParticipant.Builder().copyOf(participant).withSharingScope(sharingScope).build();
    }
    
    void setConsentStatuses(Map<SubpopulationGuid,ConsentStatus> consentStatuses) {
        this.consentStatuses = (consentStatuses == null) ? ImmutableMap.<SubpopulationGuid,ConsentStatus>of() : 
            ImmutableMap.copyOf(consentStatuses);
    }    
    
    @Override
    public String toString() {
        return String.format("BridgeSession [sessionToken=%s, consentStatuses=%s, participant=%s]", 
                sessionToken, consentStatuses, participant);
    }

}
