package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import javax.annotation.Nonnull;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleGuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleGuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.VersionHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;
import org.sagebionetworks.bridge.sdk.models.studies.Study;
import org.sagebionetworks.bridge.sdk.models.studies.StudyConsent;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;

import com.fasterxml.jackson.core.type.TypeReference;

class BridgeResearcherClient extends BaseApiCaller implements ResearcherClient {
    
    private static final TypeReference<ResourceListImpl<StudyConsent>> scType =
            new TypeReference<ResourceListImpl<StudyConsent>>() {};
    private static final TypeReference<ResourceListImpl<Survey>> sType =
            new TypeReference<ResourceListImpl<Survey>>() {};
    private static final TypeReference<ResourceListImpl<SchedulePlan>> spType =
            new TypeReference<ResourceListImpl<SchedulePlan>>() {};

    BridgeResearcherClient(@Nonnull BridgeSession session, @Nonnull ClientProvider clientProvider) {
        super(session, clientProvider);
    }

    @Override
    public ResourceList<StudyConsent> getAllStudyConsents() {
        session.checkSignedIn();

        return get(clientProvider.getConfig().getStudyConsentsApi(), scType);
    }
    @Override
    public StudyConsent getMostRecentlyActivatedStudyConsent() {
        session.checkSignedIn();

        return get(clientProvider.getConfig().getActiveStudyConsentApi(), StudyConsent.class);
    }
    @Override
    public StudyConsent getStudyConsent(DateTime createdOn) {
        session.checkSignedIn();
        checkNotNull(createdOn, Bridge.CANNOT_BE_NULL, "createdOn");

        return get(clientProvider.getConfig().getStudyConsentApi(createdOn), StudyConsent.class);
    }
    @Override
    public void createStudyConsent(StudyConsent consent) {
        session.checkSignedIn();
        checkNotNull(consent, Bridge.CANNOT_BE_NULL, "consent");

        post(clientProvider.getConfig().getStudyConsentsApi(), consent, StudyConsent.class);
    }
    @Override
    public void activateStudyConsent(DateTime createdOn) {
        session.checkSignedIn();
        checkNotNull(createdOn, Bridge.CANNOT_BE_NULL, "createdOn");

        post(clientProvider.getConfig().getVersionStudyConsentApi(createdOn));
    }

    @Override
    public Survey getSurvey(String guid, DateTime createdOn) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), Bridge.CANNOT_BE_BLANK, "guid");
        checkNotNull(createdOn, Bridge.CANNOT_BE_NULL, "createdOn");
        return get(clientProvider.getConfig().getSurveyApi(guid, createdOn), Survey.class);
    }
    @Override
    public Survey getSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, Bridge.CANNOT_BE_NULL, "guid/createdOn keys");
        return get(clientProvider.getConfig().getSurveyApi(keys.getGuid(), keys.getCreatedOn()), Survey.class);
    }
    @Override
    public ResourceList<Survey> getSurveyAllVersions(String guid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), Bridge.CANNOT_BE_BLANK, "guid");
        return get(clientProvider.getConfig().getSurveyVersionsApi(guid), sType);
    }
    @Override
    public Survey getSurveyMostRecentlyPublishedVersion(String guid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), Bridge.CANNOT_BE_BLANK, "guid");
        return get(clientProvider.getConfig().getSurveyMostRecentlyPublishedVersionApi(guid), Survey.class);
    }
    @Override
    public Survey getSurveyMostRecentVersion(String guid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), Bridge.CANNOT_BE_BLANK, "guid");
        return get(clientProvider.getConfig().getSurveyMostRecentVersionApi(guid), Survey.class);
    }
    @Override
    public ResourceList<Survey> getAllSurveysMostRecentlyPublishedVersion() {
        session.checkSignedIn();
        return get(clientProvider.getConfig().getSurveysPublishedApi(), sType);
    }
    @Override
    public ResourceList<Survey> getAllSurveysMostRecentVersion() {
        session.checkSignedIn();
        return get(clientProvider.getConfig().getSurveysRecentApi(), sType);
    }
    @Override
    public GuidCreatedOnVersionHolder createSurvey(Survey survey) {
        session.checkSignedIn();
        checkNotNull(survey, Bridge.CANNOT_BE_NULL,"Survey object");
        return post(clientProvider.getConfig().getSurveysApi(), survey, SimpleGuidCreatedOnVersionHolder.class);
    }
    @Override
    public GuidCreatedOnVersionHolder versionSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, Bridge.CANNOT_BE_NULL, "guid/createdOn keys");
        checkArgument(isNotBlank(keys.getGuid()), Bridge.CANNOT_BE_BLANK, "guid");
        checkNotNull(keys.getCreatedOn(), Bridge.CANNOT_BE_NULL, "createdOn");
        return post(clientProvider.getConfig().getSurveyNewVersionApi(keys.getGuid(), keys.getCreatedOn()), null,
                SimpleGuidCreatedOnVersionHolder.class);
    }
    @Override
    public GuidCreatedOnVersionHolder updateSurvey(Survey survey) {
        session.checkSignedIn();
        checkNotNull(survey, Bridge.CANNOT_BE_NULL,"Survey object");
        return post(clientProvider.getConfig().getSurveyApi(survey.getGuid(), new DateTime(survey.getCreatedOn())),
                survey, SimpleGuidCreatedOnVersionHolder.class);
    }
    @Override
    public void publishSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, Bridge.CANNOT_BE_NULL, "guid/createdOn keys");
        checkArgument(isNotBlank(keys.getGuid()), Bridge.CANNOT_BE_BLANK, "guid");
        checkNotNull(keys.getCreatedOn(), Bridge.CANNOT_BE_NULL, "createdOn");
        post(clientProvider.getConfig().getPublishSurveyApi(keys.getGuid(), keys.getCreatedOn()));
    }
    @Override
    public GuidCreatedOnVersionHolder versionUpdateAndPublishSurvey(Survey survey, boolean publish) {
        session.checkSignedIn();
        checkNotNull(survey, Bridge.CANNOT_BE_NULL, "survey");
        
        // in essence, updating new version to hold all the data of the supplied survey.
        GuidCreatedOnVersionHolder keys = versionSurvey(survey);
        survey.setGuidCreatedOnVersionHolder(keys);
        keys = updateSurvey(survey); 
        if (publish) {
            publishSurvey(survey);
        }
        return keys;
    }
    @Override
    public void closeSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, Bridge.CANNOT_BE_NULL, "guid/createdOn keys");
        checkArgument(isNotBlank(keys.getGuid()), Bridge.CANNOT_BE_BLANK, "guid");
        checkNotNull(keys.getCreatedOn(), Bridge.CANNOT_BE_NULL, "createdOn");
        post(clientProvider.getConfig().getCloseSurveyApi(keys.getGuid(), keys.getCreatedOn()));
    }
    @Override
    public void deleteSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, Bridge.CANNOT_BE_NULL, "guid/createdOn keys");
        checkArgument(isNotBlank(keys.getGuid()), Bridge.CANNOT_BE_BLANK, "guid");
        checkNotNull(keys.getCreatedOn(), Bridge.CANNOT_BE_NULL, "createdOn");
        delete(clientProvider.getConfig().getSurveyApi(keys.getGuid(), keys.getCreatedOn()));
    }
    @Override
    public ResourceList<SchedulePlan> getSchedulePlans() {
        session.checkSignedIn();
        return get(clientProvider.getConfig().getSchedulePlansApi(), spType);
    }
    @Override
    public GuidVersionHolder createSchedulePlan(SchedulePlan plan) {
        session.checkSignedIn();
        checkNotNull(plan, Bridge.CANNOT_BE_NULL, "SchedulePlan");
        return post(clientProvider.getConfig().getSchedulePlansApi(), plan, SimpleGuidVersionHolder.class);
    }
    @Override
    public SchedulePlan getSchedulePlan(String guid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), Bridge.CANNOT_BE_BLANK, "guid");
        return get(clientProvider.getConfig().getSchedulePlanApi(guid), SchedulePlan.class);
    }
    @Override
    public GuidVersionHolder updateSchedulePlan(SchedulePlan plan) {
        session.checkSignedIn();
        checkNotNull(plan, Bridge.CANNOT_BE_NULL, "SchedulePlan");
        return post(clientProvider.getConfig().getSchedulePlanApi(plan.getGuid()), plan,
                SimpleGuidVersionHolder.class);
    }
    @Override
    public void deleteSchedulePlan(String guid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), Bridge.CANNOT_BE_BLANK, "guid");
        delete(clientProvider.getConfig().getSchedulePlanApi(guid));
    }
    @Override
    public Study getStudy() {
        session.checkSignedIn();
        return get(clientProvider.getConfig().getResearcherStudyApi(), Study.class);
    }
    @Override
    public VersionHolder updateStudy(Study study) {
        session.checkSignedIn();
        checkNotNull(study, Bridge.CANNOT_BE_NULL, "study");
        checkNotNull(isNotBlank(study.getIdentifier()), Bridge.CANNOT_BE_BLANK, "study identifier");
        
        return post(clientProvider.getConfig().getResearcherStudyApi(), study, SimpleVersionHolder.class);
    }
}
