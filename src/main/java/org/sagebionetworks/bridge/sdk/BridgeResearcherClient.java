package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.VersionHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;
import org.sagebionetworks.bridge.sdk.models.studies.Study;
import org.sagebionetworks.bridge.sdk.models.studies.StudyConsent;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;

import com.fasterxml.jackson.core.type.TypeReference;

class BridgeResearcherClient extends BaseApiCaller implements ResearcherClient {
    
    private final TypeReference<ResourceListImpl<StudyConsent>> scType = new TypeReference<ResourceListImpl<StudyConsent>>() {};
    private final TypeReference<ResourceListImpl<Survey>> sType = new TypeReference<ResourceListImpl<Survey>>() {};
    private final TypeReference<ResourceListImpl<SchedulePlan>> spType = new TypeReference<ResourceListImpl<SchedulePlan>>() {};

    private BridgeResearcherClient(BridgeSession session) {
        super(session);
    }

    static BridgeResearcherClient valueOf(BridgeSession session) {
        return new BridgeResearcherClient(session);
    }

    @Override
    public ResourceList<StudyConsent> getAllStudyConsents() {
        session.checkSignedIn();

        return get(config.getStudyConsentsApi(), scType);
    }
    @Override
    public StudyConsent getMostRecentlyActivatedStudyConsent() {
        session.checkSignedIn();

        return get(config.getActiveStudyConsentApi(), StudyConsent.class);
    }
    @Override
    public StudyConsent getStudyConsent(DateTime createdOn) {
        session.checkSignedIn();
        checkNotNull(createdOn, Bridge.CANNOT_BE_NULL, "createdOn");

        return get(config.getStudyConsentApi(createdOn), StudyConsent.class);
    }
    @Override
    public void createStudyConsent(StudyConsent consent) {
        session.checkSignedIn();
        checkNotNull(consent, Bridge.CANNOT_BE_NULL, "consent");

        post(config.getStudyConsentsApi(), consent, StudyConsent.class);
    }
    @Override
    public void activateStudyConsent(DateTime createdOn) {
        session.checkSignedIn();
        checkNotNull(createdOn, Bridge.CANNOT_BE_NULL, "createdOn");

        post(config.getVersionStudyConsentApi(createdOn));
    }

    @Override
    public Survey getSurvey(String guid, DateTime createdOn) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), Bridge.CANNOT_BE_BLANK, "guid");
        checkNotNull(createdOn, Bridge.CANNOT_BE_NULL, "createdOn");
        return get(config.getSurveyApi(guid, createdOn), Survey.class);
    }
    @Override
    public Survey getSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, Bridge.CANNOT_BE_NULL, "guid/createdOn keys");
        return get(config.getSurveyApi(keys.getGuid(), keys.getCreatedOn()), Survey.class);
    }
    @Override
    public ResourceList<Survey> getAllVersionsOfAllSurveys() {
        session.checkSignedIn();
        return get(config.getSurveysApi(), sType);
    }
    @Override
    public ResourceList<Survey> getPublishedVersionsOfAllSurveys() {
        session.checkSignedIn();
        return get(config.getSurveysPublishedApi(), sType);
    }
    @Override
    public ResourceList<Survey> getRecentVersionsOfAllSurveys() {
        session.checkSignedIn();
        return get(config.getRecentSurveysApi(), sType);
    }
    @Override
    public ResourceList<Survey> getAllVersionsOfASurvey(String guid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), Bridge.CANNOT_BE_BLANK, "guid");
        return get(config.getSurveyVersionsApi(guid), sType);
    }
    @Override
    public GuidCreatedOnVersionHolder createSurvey(Survey survey) {
        session.checkSignedIn();
        checkNotNull(survey, Bridge.CANNOT_BE_NULL,"Survey object");
        return post(config.getSurveysApi(), survey, SimpleGuidCreatedOnVersionHolder.class);
    }
    @Override
    public GuidCreatedOnVersionHolder versionSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, Bridge.CANNOT_BE_NULL, "guid/createdOn keys");
        checkArgument(isNotBlank(keys.getGuid()), Bridge.CANNOT_BE_BLANK, "guid");
        checkNotNull(keys.getCreatedOn(), Bridge.CANNOT_BE_NULL, "createdOn");
        return post(config.getSurveyNewVersionApi(keys.getGuid(), keys.getCreatedOn()), null, SimpleGuidCreatedOnVersionHolder.class);
    }
    @Override
    public GuidCreatedOnVersionHolder updateSurvey(Survey survey) {
        session.checkSignedIn();
        checkNotNull(survey, Bridge.CANNOT_BE_NULL,"Survey object");
        return post(config.getSurveyApi(survey.getGuid(), new DateTime(survey.getCreatedOn())), survey,
                SimpleGuidCreatedOnVersionHolder.class);
    }
    @Override
    public void publishSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, Bridge.CANNOT_BE_NULL, "guid/createdOn keys");
        checkArgument(isNotBlank(keys.getGuid()), Bridge.CANNOT_BE_BLANK, "guid");
        checkNotNull(keys.getCreatedOn(), Bridge.CANNOT_BE_NULL, "createdOn");
        post(config.getPublishSurveyApi(keys.getGuid(), keys.getCreatedOn()));
    }
    @Override
    public void closeSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, Bridge.CANNOT_BE_NULL, "guid/createdOn keys");
        checkArgument(isNotBlank(keys.getGuid()), Bridge.CANNOT_BE_BLANK, "guid");
        checkNotNull(keys.getCreatedOn(), Bridge.CANNOT_BE_NULL, "createdOn");
        post(config.getCloseSurveyApi(keys.getGuid(), keys.getCreatedOn()));
    }
    @Override
    public void deleteSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, Bridge.CANNOT_BE_NULL, "guid/createdOn keys");
        checkArgument(isNotBlank(keys.getGuid()), Bridge.CANNOT_BE_BLANK, "guid");
        checkNotNull(keys.getCreatedOn(), Bridge.CANNOT_BE_NULL, "createdOn");
        delete(config.getSurveyApi(keys.getGuid(), keys.getCreatedOn()));
    }
    @Override
    public ResourceList<SchedulePlan> getSchedulePlans() {
        session.checkSignedIn();
        return get(config.getSchedulePlansApi(), spType);
    }
    @Override
    public GuidVersionHolder createSchedulePlan(SchedulePlan plan) {
        session.checkSignedIn();
        checkNotNull(plan, Bridge.CANNOT_BE_NULL, "SchedulePlan");
        return post(config.getSchedulePlansApi(), plan, SimpleGuidVersionHolder.class);
    }
    @Override
    public SchedulePlan getSchedulePlan(String guid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), Bridge.CANNOT_BE_BLANK, "guid");
        return get(config.getSchedulePlanApi(guid), SchedulePlan.class);
    }
    @Override
    public GuidVersionHolder updateSchedulePlan(SchedulePlan plan) {
        session.checkSignedIn();
        checkNotNull(plan, Bridge.CANNOT_BE_NULL, "SchedulePlan");
        return post(config.getSchedulePlanApi(plan.getGuid()), plan, SimpleGuidVersionHolder.class);
    }
    @Override
    public void deleteSchedulePlan(String guid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), Bridge.CANNOT_BE_BLANK, "guid");
        delete(config.getSchedulePlanApi(guid));
    }
    @Override
    public Study getStudy() {
        session.checkSignedIn();
        return get(config.getResearcherStudyApi(), Study.class);
    }
    @Override
    public VersionHolder updateStudy(Study study) {
        session.checkSignedIn();
        checkNotNull(study, Bridge.CANNOT_BE_NULL, "study");
        checkNotNull(isNotBlank(study.getIdentifier()), Bridge.CANNOT_BE_BLANK, "study identifier");
        
        return post(config.getResearcherStudyApi(), study, SimpleVersionHolder.class);
    }
}
