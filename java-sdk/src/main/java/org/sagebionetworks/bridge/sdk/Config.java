package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;

import org.sagebionetworks.bridge.sdk.exceptions.BridgeSDKException;
import org.sagebionetworks.bridge.sdk.models.accounts.SignInCredentials;
import org.sagebionetworks.bridge.sdk.models.reports.ReportType;
import org.sagebionetworks.bridge.sdk.models.subpopulations.SubpopulationGuid;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public final class Config {

    private static final String ASSIGNMENT_FILTER = "assignmentFilter";
    private static final String EMAIL_FILTER = "emailFilter";
    private static final String EXTERNAL_ID = "externalId";
    private static final String ID_FILTER = "idFilter";
    private static final String OFFSET_BY = "offsetBy";
    private static final String OFFSET_KEY = "offsetKey";
    private static final String PAGE_SIZE = "pageSize";
    private static final String END_DATE = "endDate";
    private static final String START_DATE = "startDate";
    private static final String TYPE = "type";

    private static final String CONFIG_FILE = "/bridge-sdk.properties";
    private static final String USER_CONFIG_FILE = System.getProperty("user.home") + "/bridge-sdk.properties";

    public enum Props {
        // These all require and entry in bridge-sdk.properties (accounts are optional).
        ACCOUNT_EMAIL(null), 
        ACCOUNT_PASSWORD(null), 
        ADMIN_EMAIL(null), 
        ADMIN_PASSWORD(null), 
        DEV_NAME(null), 
        ENV(null), 
        LOG_LEVEL(null), 
        SDK_VERSION(null), 
        STUDY_IDENTIFIER(null), 
        
        V3_ACTIVITIES("/v3/activities"), 
        V3_AUTH_REQUESTRESETPASSWORD("/v3/auth/requestResetPassword"), 
        V3_AUTH_RESENDEMAILVERIFICATION("/v3/auth/resendEmailVerification"), 
        V3_AUTH_RESETPASSWORD("/v3/auth/resetPassword"), 
        V3_AUTH_SIGNIN("/v3/auth/signIn"), 
        V3_AUTH_SIGNOUT("/v3/auth/signOut"), 
        V3_AUTH_SIGNUP("/v3/auth/signUp"), 
        V3_AUTH_VERIFYEMAIL("/v3/auth/verifyEmail"), 
        V3_BACKFILL_NAME_START("/v3/backfill/%s/start"), 
        V3_BACKFILL_NAME("/v3/backfill/%s"), 
        V3_CACHE_CACHEKEY("/v3/cache/%s"),
        V3_CACHE("/v3/cache"), 
        V3_EXTERNAL_IDS("/v3/externalIds"),
        V3_PARTICIPANT_REQUEST_RESET_PASSWORD("/v3/participants/%s/requestResetPassword"),
        V3_PARTICIPANT_SIGNOUT("/v3/participants/%s/signOut"),
        V3_PARTICIPANT("/v3/participants/%s"),
        V3_PARTICIPANTS_REPORTS_IDENTIFIER("/v3/participants/reports/%s"),
        V3_PARTICIPANTS_SELF("/v3/participants/self"),
        V3_PARTICIPANTS_USERID_REPORTS_IDENTIFIER_DATE("/v3/participants/%s/reports/%s/%s"),
        V3_PARTICIPANTS_USERID_REPORTS_IDENTIFIER("/v3/participants/%s/reports/%s"),
        V3_PARTICIPANTS("/v3/participants"),
        V3_REPORTS_IDENTIFIER_DATE("/v3/reports/%s/%s"),
        V3_REPORTS_IDENTIFIER("/v3/reports/%s"),
        V3_REPORTS("/v3/reports"),
        V3_SCHEDULEPLANS_GUID("/v3/scheduleplans/%s"),
        V3_SCHEDULEPLANS("/v3/scheduleplans"), 
        V3_STUDIES_IDENTIFIER("/v3/studies/%s"),
        V3_STUDIES_SELF("/v3/studies/self"),
        V3_STUDIES_STUDYID_SURVEYS_PUBLISHED("/v3/studies/%s/surveys/published"),
        V3_STUDIES_STUDYID_UPLOADSCHEMAS_SCHEMAID_REVISIONS_REVISION("/v3/studies/%s/uploadschemas/%s/revisions/%d"),
        V3_STUDIES("/v3/studies"), 
        V3_SUBPOPULATION("/v3/subpopulations/%s"),
        V3_SUBPOPULATIONS_CONSENTS_PUBLISHED("/v3/subpopulations/%s/consents/published"),
        V3_SUBPOPULATIONS_CONSENTS_RECENT("/v3/subpopulations/%s/consents/recent"),
        V3_SUBPOPULATIONS_CONSENTS_SIGNATURE_EMAIL("/v3/subpopulations/%s/consents/signature/email"),
        V3_SUBPOPULATIONS_CONSENTS_SIGNATURE_WITHDRAW("/v3/subpopulations/%s/consents/signature/withdraw"),
        V3_SUBPOPULATIONS_CONSENTS_SIGNATURE("/v3/subpopulations/%s/consents/signature"),
        V3_SUBPOPULATIONS_CONSENTS_TIMESTAMP_PUBLISH("/v3/subpopulations/%s/consents/%s/publish"),
        V3_SUBPOPULATIONS_CONSENTS_TIMESTAMP("/v3/subpopulations/%s/consents/%s"),
        V3_SUBPOPULATIONS_CONSENTS("/v3/subpopulations/%s/consents"),
        V3_SUBPOPULATIONS("/v3/subpopulations"),
        V3_SURVEYRESPONSES_IDENTIFIER("/v3/surveyresponses/%s"), 
        V3_SURVEYRESPONSES("/v3/surveyresponses"), 
        V3_SURVEYS_PUBLISHED("/v3/surveys/published"),
        V3_SURVEYS_RECENT("/v3/surveys/recent"),
        V3_SURVEYS_SURVEYGUID_REVISIONS_CREATEDON_PHYSICAL_TRUE("/v3/surveys/%s/revisions/%s?physical=true"),
        V3_SURVEYS_SURVEYGUID_REVISIONS_CREATEDON_PUBLISH("/v3/surveys/%s/revisions/%s/publish"), 
        V3_SURVEYS_SURVEYGUID_REVISIONS_CREATEDON_VERSION("/v3/surveys/%s/revisions/%s/version"), 
        V3_SURVEYS_SURVEYGUID_REVISIONS_CREATEDON("/v3/surveys/%s/revisions/%s"),
        V3_SURVEYS_SURVEYGUID_REVISIONS_PUBLISHED("/v3/surveys/%s/revisions/published"), 
        V3_SURVEYS_SURVEYGUID_REVISIONS_RECENT("/v3/surveys/%s/revisions/recent"), 
        V3_SURVEYS_SURVEYGUID_REVISIONS("/v3/surveys/%s/revisions"), 
        V3_SURVEYS("/v3/surveys"), 
        V3_UPLOADS_UPLOADID_COMPLETE("/v3/uploads/%s/complete"), 
        V3_UPLOADS("/v3/uploads"),
        V3_UPLOADSCHEMAS_SCHEMAID_RECENT("/v3/uploadschemas/%s/recent"),
        V3_UPLOADSCHEMAS_SCHEMAID_REVISIONS_REV("/v3/uploadschemas/%s/revisions/%s"),
        V3_UPLOADSCHEMAS_SCHEMAID("/v3/uploadschemas/%s"),
        V3_UPLOADSCHEMAS("/v3/uploadschemas"),
        V3_UPLOADSTATUSES_UPLOADID("/v3/uploadstatuses/%s"),
        V3_USER("/v3/users/%s"),
        V3_USERS_SELF_EMAILDATA("/v3/users/self/emailData"), 
        V3_USERS_SELF_REPORTS_IDENTIFIER("/v3/users/self/reports/%s"),
        V3_USERS_SELF_UNSUBSCRIBEEMAIL("/v3/users/self/unsubscribeEmail"), 
        V3_USERS("/v3/users"),
        V4_SCHEDULES("/v4/schedules"),
        V4_UPLOADSCHEMAS_SCHEMAID_REVISIONS_REV("/v4/uploadschemas/%s/revisions/%d"),
        V4_UPLOADSCHEMAS("/v4/uploadschemas");
                
        private String endpoint;
        private Props(String endpoint) {
            this.endpoint = endpoint;
        }
        public String getEndpoint() {
            return endpoint;
        }
        public String getPropertyName() {
            return this.name().replace("_", ".").toLowerCase();
        }
    }

    private Properties config;
    private Environment environment;

    Config() {
        config = new Properties();

        // Load from default configuration file
        try (InputStream in = this.getClass().getResourceAsStream(CONFIG_FILE)) {
            config.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Overwrite from user's local file
        loadProperties(USER_CONFIG_FILE, config);

        // Finally, overwrite from environment variables and system properties
        for (Props key : Props.values()) {
            String value = System.getenv(key.name());
            if (value == null) {
                value = System.getProperty(key.name());
            }
            if (value != null) {
                config.setProperty(key.getPropertyName(), value);
            }
        }
        String envName = config.getProperty("env");
        if (envName != null) {
            environment = Environment.valueOf(envName.toUpperCase());
        }
        checkNotNull(environment);
    }

    private void loadProperties(final String fileName, final Properties properties) {
        File file = new File(fileName);
        if (file.exists()) {
            try (InputStream in = new FileInputStream(file)) {
                properties.load(in);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Method to reset any of the default values that are defined in the bridge-sdk.properties configuration file.
     * 
     * @param property
     * @param value
     */
    public void set(Props property, String value) {
        checkNotNull(property, "Must specify a property");
        checkNotNull(value, "Must specify a value");
        config.setProperty(property.getPropertyName(), value);
    }

    /**
     * Method to set the environment of the SDK.
     * 
     * @param env
     */
    public void set(Environment env) {
        this.environment = env;
    }

    public SignInCredentials getAccountCredentials() {
        return new SignInCredentials(getStudyIdentifier(), getAccountEmail(), getAccountPassword());
    }

    public SignInCredentials getAdminCredentials() {
        return new SignInCredentials(getStudyIdentifier(), getAdminEmail(), getAdminPassword());
    }
    
    public String getAccountEmail() {
        return fromProperty(Props.ACCOUNT_EMAIL);
    }

    public String getAccountPassword() {
        return fromProperty(Props.ACCOUNT_PASSWORD);
    }

    public String getAdminEmail() {
        return fromProperty(Props.ADMIN_EMAIL);
    }

    public String getAdminPassword() {
        return fromProperty(Props.ADMIN_PASSWORD);
    }

    public String getCacheApi() {
        return Props.V3_CACHE.getEndpoint();
    }

    public String getCacheApi(String key) {
        checkNotNull(key);
        return String.format(Props.V3_CACHE_CACHEKEY.getEndpoint(), key);
    }

    public String getDevName() {
        return fromProperty(Props.DEV_NAME);
    }

    public Environment getEnvironment() {
        return environment;
    }

    public String getLogLevel() {
        return fromProperty(Props.LOG_LEVEL);
    }

    public String getResendEmailVerificationApi() {
        return Props.V3_AUTH_RESENDEMAILVERIFICATION.getEndpoint();
    }

    public String getSignUpApi() {
        return Props.V3_AUTH_SIGNUP.getEndpoint();
    }

    public String getSignInApi() {
        return Props.V3_AUTH_SIGNIN.getEndpoint();
    }

    public String getSignOutApi() {
        return Props.V3_AUTH_SIGNOUT.getEndpoint();
    }

    public String getVerifyEmailApi() {
        return Props.V3_AUTH_VERIFYEMAIL.getEndpoint();
    }

    public String getRequestResetPasswordApi() {
        return Props.V3_AUTH_REQUESTRESETPASSWORD.getEndpoint();
    }

    public String getResetPasswordApi() {
        return Props.V3_AUTH_RESETPASSWORD.getEndpoint();
    }

    public String getConsentsApi(SubpopulationGuid subpopGuid) {
        checkNotNull(subpopGuid);
        return String.format(Props.V3_SUBPOPULATIONS_CONSENTS.getEndpoint(), subpopGuid.getGuid());
    }

    public String getConsentApi(SubpopulationGuid subpopGuid, DateTime timestamp) {
        checkNotNull(subpopGuid);
        checkNotNull(timestamp);
        return String.format(Props.V3_SUBPOPULATIONS_CONSENTS_TIMESTAMP.getEndpoint(), subpopGuid.getGuid(),
                timestamp.toString(ISODateTimeFormat.dateTime()));
    }

    public String getConsentSignatureApi(SubpopulationGuid subpopGuid) {
        checkNotNull(subpopGuid);
        return String.format(Props.V3_SUBPOPULATIONS_CONSENTS_SIGNATURE.getEndpoint(), subpopGuid.getGuid());
    }

    public String getEmailConsentSignatureApi(SubpopulationGuid subpopGuid) {
        checkNotNull(subpopGuid);
        return String.format(Props.V3_SUBPOPULATIONS_CONSENTS_SIGNATURE_EMAIL.getEndpoint(), subpopGuid.getGuid());
    }
    
    public String getWithdrawConsentSignatureApi(SubpopulationGuid subpopGuid) {
        checkNotNull(subpopGuid);
        return String.format(Props.V3_SUBPOPULATIONS_CONSENTS_SIGNATURE_WITHDRAW.getEndpoint(), subpopGuid.getGuid());
    }

    public String getStudyIdentifier() {
        return fromProperty(Props.STUDY_IDENTIFIER);
    }

    public String getPublishedStudyConsentApi(SubpopulationGuid subpopGuid) {
        checkNotNull(subpopGuid);
        return String.format(Props.V3_SUBPOPULATIONS_CONSENTS_PUBLISHED.getEndpoint(), subpopGuid.getGuid());
    }

    public String getMostRecentStudyConsentApi(SubpopulationGuid subpopGuid) {
        checkNotNull(subpopGuid);
        return String.format(Props.V3_SUBPOPULATIONS_CONSENTS_RECENT.getEndpoint(), subpopGuid.getGuid());
    }

    public String getPublishStudyConsentApi(SubpopulationGuid subpopGuid, DateTime timestamp) {
        checkNotNull(subpopGuid);
        checkNotNull(timestamp);
        return String.format(Props.V3_SUBPOPULATIONS_CONSENTS_TIMESTAMP_PUBLISH.getEndpoint(),
                subpopGuid.getGuid(), timestamp.toString(ISODateTimeFormat.dateTime()));
    }

    public String getUploadsApi() {
        return Props.V3_UPLOADS.getEndpoint();
    }

    public String getCompleteUploadApi(String uploadId) {
        checkNotNull(uploadId);
        return String.format(Props.V3_UPLOADS_UPLOADID_COMPLETE.getEndpoint(), uploadId);
    }

    public String getUploadStatusApi(String uploadId) {
        checkNotNull(uploadId);
        return String.format(Props.V3_UPLOADSTATUSES_UPLOADID.getEndpoint(), uploadId);
    }
    
    public String getUserApi(String id) {
        checkNotNull(id);
        return String.format(Props.V3_USER.getEndpoint(), id);
    }

    public String getUsersApi() {
        return Props.V3_USERS.getEndpoint();
    }

    public String getSchedulesApi() {
        return Props.V4_SCHEDULES.getEndpoint();
    }

    public String getSurveysApi() {
        return Props.V3_SURVEYS.getEndpoint();
    }

    public String getRecentSurveysApi() {
        return Props.V3_SURVEYS_RECENT.getEndpoint();
    }

    public String getPublishedSurveysApi() {
        return Props.V3_SURVEYS_PUBLISHED.getEndpoint();
    }

    public String getPublishedSurveysForStudyApi(String studyId) {
        checkArgument(isNotBlank(studyId));
        return String.format(Props.V3_STUDIES_STUDYID_SURVEYS_PUBLISHED.getEndpoint(), studyId);
    }

    public String getUploadSchemaApi(String studyId, String schemaId, int revision) {
        checkArgument(isNotBlank(studyId));
        checkArgument(isNotBlank(schemaId));
        checkArgument(revision > 0);
        return String.format(Props.V3_STUDIES_STUDYID_UPLOADSCHEMAS_SCHEMAID_REVISIONS_REVISION.getEndpoint(), studyId,
                schemaId, revision);
    }

    public String getSurveyApi(String guid, DateTime createdOn) {
        checkArgument(isNotBlank(guid));
        checkNotNull(createdOn);
        return getSurveyApi(guid, createdOn.toString(ISODateTimeFormat.dateTime()));
    }

    public String getSurveyApi(String guid, String createdOn) {
        checkArgument(isNotBlank(guid));
        checkNotNull(createdOn);
        return String.format(Props.V3_SURVEYS_SURVEYGUID_REVISIONS_CREATEDON.getEndpoint(), guid, createdOn);
    }

    public String getDeleteSurveyPermanentlyApi(String guid, DateTime createdOn) {
        checkArgument(isNotBlank(guid));
        checkNotNull(createdOn);
        return String.format(Props.V3_SURVEYS_SURVEYGUID_REVISIONS_CREATEDON_PHYSICAL_TRUE.getEndpoint(), guid,
                createdOn.toString(ISODateTimeFormat.dateTime()));
    }

    public String getMostRecentlyPublishedSurveyRevisionApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(Props.V3_SURVEYS_SURVEYGUID_REVISIONS_PUBLISHED.getEndpoint(), guid);
    }

    public String getMostRecentSurveyRevisionApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(Props.V3_SURVEYS_SURVEYGUID_REVISIONS_RECENT.getEndpoint(), guid);
    }

    public String getSurveyAllRevisionsApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(Props.V3_SURVEYS_SURVEYGUID_REVISIONS.getEndpoint(), guid);
    }

    public String getVersionSurveyApi(String guid, DateTime createdOn) {
        checkArgument(isNotBlank(guid));
        checkNotNull(createdOn);
        return String.format(Props.V3_SURVEYS_SURVEYGUID_REVISIONS_CREATEDON_VERSION.getEndpoint(), guid,
                createdOn.toString(ISODateTimeFormat.dateTime()));
    }

    public String getPublishSurveyApi(String guid, DateTime createdOn) {
        checkArgument(isNotBlank(guid));
        checkNotNull(createdOn);
        return String.format(Props.V3_SURVEYS_SURVEYGUID_REVISIONS_CREATEDON_PUBLISH.getEndpoint(), guid,
                createdOn.toString(ISODateTimeFormat.dateTime()));
    }

    public String getRecentlyPublishedSurveyForUserApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(Props.V3_SURVEYS_SURVEYGUID_REVISIONS_PUBLISHED.getEndpoint(), guid);
    }

    public String getSurveyResponsesApi() {
        return Props.V3_SURVEYRESPONSES.getEndpoint();
    }

    public String getSurveyResponseApi(String identifier) {
        checkArgument(isNotBlank(identifier));
        return String.format(Props.V3_SURVEYRESPONSES_IDENTIFIER.getEndpoint(), identifier);
    }

    public String getSchedulePlansApi() {
        return Props.V3_SCHEDULEPLANS.getEndpoint();
    }

    public String getSchedulePlanApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(Props.V3_SCHEDULEPLANS_GUID.getEndpoint(), guid);
    }

    public String getScheduledActivitiesApi() {
        return Props.V3_ACTIVITIES.getEndpoint();
    }

    public String getUploadSchemasApi() {
        return Props.V3_UPLOADSCHEMAS.getEndpoint();
    }

    public String getUploadSchemasV4Api() {
        return Props.V4_UPLOADSCHEMAS.getEndpoint();
    }

    public String getUploadSchemaAllRevisionsApi(String schemaId) {
        checkArgument(isNotBlank(schemaId));
        return String.format(Props.V3_UPLOADSCHEMAS_SCHEMAID.getEndpoint(), schemaId);
    }

    public String getMostRecentUploadSchemaApi(String schemaId) {
        checkArgument(isNotBlank(schemaId));
        return String.format(Props.V3_UPLOADSCHEMAS_SCHEMAID_RECENT.getEndpoint(), schemaId);
    }

    public String getUploadSchemaApi(String schemaId, int revision) {
        checkArgument(isNotBlank(schemaId));
        checkArgument(revision > 0);
        return String.format(Props.V3_UPLOADSCHEMAS_SCHEMAID_REVISIONS_REV.getEndpoint(), schemaId, revision);
    }

    public String getUploadSchemaV4Api(String schemaId, int revision) {
        checkArgument(isNotBlank(schemaId));
        checkArgument(revision > 0);
        return String.format(Props.V4_UPLOADSCHEMAS_SCHEMAID_REVISIONS_REV.getEndpoint(), schemaId, revision);
    }

    public String getCurrentStudyApi() {
        return Props.V3_STUDIES_SELF.getEndpoint();
    }

    public String getStudiesApi() {
        return Props.V3_STUDIES.getEndpoint();
    }

    public String getStudyApi(String identifier) {
        checkArgument(isNotBlank(identifier));
        return String.format(Props.V3_STUDIES_IDENTIFIER.getEndpoint(), identifier);
    }

    public String getSdkVersion() {
        return fromProperty(Props.SDK_VERSION);
    }

    public String getSubpopulations() {
        return Props.V3_SUBPOPULATIONS.getEndpoint();
    }
    
    public String getSubpopulation(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(Props.V3_SUBPOPULATION.getEndpoint(), guid);
    }

    public String getUsersSignOutApi(String id) {
        checkArgument(isNotBlank(id));
        return String.format(Props.V3_PARTICIPANT_SIGNOUT.getEndpoint(), id);
    }

    public String getParticipantsApi() {
        return Props.V3_PARTICIPANTS.getEndpoint();
    }
    
    public String getParticipantsApi(int offsetBy, int pageSize, String emailFilter) {
        checkArgument(offsetBy >= 0);
        checkArgument(pageSize >= 5);
        
        List<NameValuePair> queryParams = Lists.newArrayList();
        queryParams.add(new BasicNameValuePair(OFFSET_BY, Integer.toString(offsetBy)));
        queryParams.add(new BasicNameValuePair(PAGE_SIZE, Integer.toString(pageSize)));
        if (emailFilter != null) {
            queryParams.add(new BasicNameValuePair(EMAIL_FILTER, emailFilter));
        }
        return withQueryParams(Props.V3_PARTICIPANTS.getEndpoint(), queryParams);
    }
    
    public String getParticipantApi(String id) {
        checkArgument(isNotBlank(id));
        return String.format(Props.V3_PARTICIPANT.getEndpoint(), id);
    }
    
    public String getParticipantRequestResetPasswordApi(String id) {
        checkArgument(isNotBlank(id));
        return String.format(Props.V3_PARTICIPANT_REQUEST_RESET_PASSWORD.getEndpoint(), id);
    }
    
    public String getParticipantSelfApi() {
        return Props.V3_PARTICIPANTS_SELF.getEndpoint();
    }
    
    public String getExternalIdsApi() {
        return Props.V3_EXTERNAL_IDS.getEndpoint();
    }
    
    public String getExternalIdsApi(List<String> identifiers) {
        checkNotNull(identifiers);
        
        List<NameValuePair> queryParams = Lists.newArrayList();
        for (String id : identifiers) {
            queryParams.add(new BasicNameValuePair(EXTERNAL_ID, id));
        }
        return withQueryParams(Props.V3_EXTERNAL_IDS.getEndpoint(), queryParams);
    }
    
    public String getExternalIdsApi(String offsetKey, Integer pageSize, String idFilter, Boolean assignmentFilter) {
        List<NameValuePair> queryParams = Lists.newArrayList();
        if (offsetKey != null) {
            queryParams.add(new BasicNameValuePair(OFFSET_KEY, offsetKey));
        }
        if (pageSize != null) {
            queryParams.add(new BasicNameValuePair(PAGE_SIZE, pageSize.toString()));
        }
        if (idFilter != null) {
            queryParams.add(new BasicNameValuePair(ID_FILTER, idFilter));
        }
        if (assignmentFilter != null) {
            queryParams.add(new BasicNameValuePair(ASSIGNMENT_FILTER, assignmentFilter.toString()));
        }
        return withQueryParams(Props.V3_EXTERNAL_IDS.getEndpoint(), queryParams);
    }
    
    public String getUsersSelfReportsIdentifierApi(String reportId, LocalDate startDate, LocalDate endDate) {
        checkArgument(isNotBlank(reportId));
        return startEndDateURL(Props.V3_USERS_SELF_REPORTS_IDENTIFIER, reportId, startDate, endDate);
    }
    
    public String getReportIndicesApi(ReportType reportType) {
        checkNotNull(reportType);
        
        List<NameValuePair> queryParams = Lists.newArrayList();
        queryParams.add(new BasicNameValuePair(TYPE, reportType.name().toLowerCase()));
        
        return withQueryParams(Props.V3_REPORTS.getEndpoint(), queryParams);
    }
    
    public String getReportsIdentifierApi(String reportId) {
        checkArgument(isNotBlank(reportId));
        return String.format(Props.V3_REPORTS_IDENTIFIER.getEndpoint(), reportId);
    }
    
    public String getReportsIdentifierApi(String reportId, LocalDate startDate, LocalDate endDate) {
        checkArgument(isNotBlank(reportId));
        return startEndDateURL(Props.V3_REPORTS_IDENTIFIER, reportId, startDate, endDate);
    }
    
    public String getReportsIdentifierDateApi(String reportId, LocalDate date) {
        checkArgument(isNotBlank(reportId));
        checkNotNull(date);
        
        return String.format(Props.V3_REPORTS_IDENTIFIER_DATE.getEndpoint(), reportId,
                date.toString(ISODateTimeFormat.date()));
    }
    
    public String getParticipantsUserIdReportsIdentifierApi(String userId, String reportId) {
        checkArgument(isNotBlank(reportId));
        checkArgument(isNotBlank(userId));
        return String.format(Props.V3_PARTICIPANTS_USERID_REPORTS_IDENTIFIER.getEndpoint(), userId, reportId);
    }
    
    public String getParticipantsUserIdReportsIdentifierDateApi(String userId, String reportId, LocalDate date) {
        checkArgument(isNotBlank(reportId));
        checkArgument(isNotBlank(userId));
        checkNotNull(date);
        
        return String.format(Props.V3_PARTICIPANTS_USERID_REPORTS_IDENTIFIER_DATE.getEndpoint(), userId, reportId,
                date.toString(ISODateTimeFormat.date()));
    }
    
    public String getParticipantsReportsIdentifierApi(String reportId) {
        checkArgument(isNotBlank(reportId));
        return String.format(Props.V3_PARTICIPANTS_REPORTS_IDENTIFIER.getEndpoint(), reportId);
    }
    
    private String startEndDateURL(Props prop, String reportId, LocalDate startDate, LocalDate endDate) {
        List<NameValuePair> queryParams = Lists.newArrayList();
        if (startDate != null) {
            queryParams.add(new BasicNameValuePair(START_DATE, startDate.toString()));
        }
        if (endDate != null) {
            queryParams.add(new BasicNameValuePair(END_DATE, endDate.toString()));
        }
        String url = String.format(prop.getEndpoint(), reportId);
        return withQueryParams(url, queryParams);
    }
    
    private String fromProperty(Props prop) {
        String value = config.getProperty(prop.getPropertyName());
        checkNotNull(value, "The property '" + prop.getPropertyName() + "' has not been set.");
        return value.trim();
    }
    
    private String withQueryParams(String endpoint, List<NameValuePair> queryParams) {
        try {
            URIBuilder builder = new URIBuilder(endpoint);
            builder.addParameters(queryParams);
            return builder.build().toString();
        } catch(URISyntaxException e) {
            throw new BridgeSDKException(e.getMessage(), 500);
        }        
    }

    @Override
    public String toString() {
        List<String> list = Lists.newArrayList();
        for (int i = 0; i < Props.values().length; i++) {
            String property = Props.values()[i].getPropertyName();
            String value = property.contains("password") ? "******" : config.getProperty(property);
            list.add(property + "=" + value);
        }
        return "Config[" + Joiner.on(", ").join(list) + "]";
    }
}
