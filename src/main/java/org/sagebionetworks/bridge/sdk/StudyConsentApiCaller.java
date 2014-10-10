package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.sdk.models.StudyConsent;

class StudyConsentApiCaller extends BaseApiCaller {

    private final String STUDY_CONSENT = provider.getConfig().getStudyConsentApi();
    private final String ACTIVE = STUDY_CONSENT + "/active";

    private StudyConsentApiCaller(ClientProvider provider) {
        super(provider);
    }

    static StudyConsentApiCaller valueOf(ClientProvider provider) {
        return new StudyConsentApiCaller(provider);
    }

    List<StudyConsent> getAllStudyConsents() {

        return null;
    }

    StudyConsent getStudyConsent(DateTime timestamp) {
        assert timestamp != null;
        return null;
    }

    StudyConsent getActiveStudyConsent() {
        return null;
    }

    StudyConsent setActiveStudyConsent(DateTime timestamp) {
        return null;
    }

    StudyConsent addStudyConsent(StudyConsent studyConsent) {
        return null;
    }

    private String timestamp(DateTime date) {
        return "/" + date.toString(ISODateTimeFormat.dateTime());
    }
}
