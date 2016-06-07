package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.joda.time.LocalDate;

import org.sagebionetworks.bridge.sdk.models.DateRangeResourceList;
import org.sagebionetworks.bridge.sdk.models.reports.ReportData;
import org.sagebionetworks.bridge.sdk.utils.Utilities;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Client for working with the report API, an API for saving and retrieving JSON-based data for 
 * reports, either for a study or for individual participants in a study. Reports are organized by ID, and 
 * IDs can be added "at will" (you do not need to specify the ID before you start adding data for that 
 * report). However, report IDs can only contain upper- and lower-case letters, numbers, a dash or 
 * an underscore.
 */
public class ReportClient extends BaseApiCaller {

    private static final TypeReference<DateRangeResourceList<ReportData>> REPORT_DATA_LIST_TYPE = 
            new TypeReference<DateRangeResourceList<ReportData>>() {};

    ReportClient(BridgeSession session) {
        super(session);
    }

    /**
     * Retrieve a set of report records for a report about the currently authenticated and consented user. 
     * The date range can span up to 45 days.
     * 
     * @param reportId
     *      the ID of the report in the study
     * @param startDate
     *      the start date of the records to return (optional, defaults to yesterday)
     * @param endDate
     *      the end date of the records to return (optional, default to yesterday)
     */
    public DateRangeResourceList<ReportData> getParticipantReport(String reportId, LocalDate startDate,
            LocalDate endDate) {
        session.checkSignedIn();
        checkArgument(isNotBlank(reportId), CANNOT_BE_BLANK, "reportId");
        
        return get(config.getUsersSelfReportsIdentifierApi(reportId, startDate, endDate), REPORT_DATA_LIST_TYPE);
    }
    
    /**
     * Save a single day of report data for a participant, using the participant's ID.
     * 
     * @param reportId
     *      the ID of the report in the study
     * @param userId
     *      the ID of the participant
     * @param reportData
     *      the data for a single day of the report.
     */
    public void saveParticipantReportByUserId(String reportId, String userId, ReportData reportData) {
        session.checkSignedIn();
        checkArgument(isNotBlank(reportId), CANNOT_BE_BLANK, "reportId");
        checkArgument(isNotBlank(userId), CANNOT_BE_BLANK, "userId");
        checkNotNull(reportData, CANNOT_BE_NULL, "reportData");
        
        post(config.getReportsIdentifierUsersUserIdApi(reportId, userId), reportData);
    }
    
    /**
     * Save a single day of report data for a participant, using the participant's anonymous health code (necessary 
     * for reports based on the anonymized data set). Available to accounts with the worker role only.
     * 
     * @param reportId
     *      the ID of the report in the study
     * @param healthCode
     *      the anonymous health code of a participant
     * @param reportData
     *      the data for a single day of the report.
     */
    public void saveParticipantReportByHealthCode(String reportId, String healthCode, ReportData reportData) {
        session.checkSignedIn();
        checkArgument(isNotBlank(reportId), CANNOT_BE_BLANK, "reportId");
        checkArgument(isNotBlank(healthCode), CANNOT_BE_BLANK, "healthCode");
        checkNotNull(reportData, CANNOT_BE_NULL, "reportData");
        
        ObjectNode node = (ObjectNode)Utilities.getMapper().valueToTree(reportData);
        node.put("healthCode", healthCode);
        post(config.getReportsIdentifierUsersApi(reportId), node);
    }
    
    /**
     * Delete all records for all days of a report for a single participant.
     * 
     * @param reportId
     *      the ID of the report in the study
     * @param userId
     *      the ID of the participant
     */
    public void deleteParticipantReport(String reportId, String userId) {
        session.checkSignedIn();
        checkArgument(isNotBlank(reportId), CANNOT_BE_BLANK, "reportId");
        checkArgument(isNotBlank(userId), CANNOT_BE_BLANK, "healthCode");
        
        delete(config.getReportsIdentifierUsersUserIdApi(reportId, userId));
    }
    
    /**
     * 
     * @param reportId
     *      the ID of the report in the study
     * @param startDate
     *      the start date of the records to return (optional, defaults to yesterday)
     * @param endDate
     *      the end date of the records to return (optional, default to yesterday)
     */
    public DateRangeResourceList<ReportData> getStudyReport(String reportId, LocalDate startDate, LocalDate endDate) {
        session.checkSignedIn();
        checkArgument(isNotBlank(reportId), CANNOT_BE_BLANK, "reportId");
        
        return get(config.getReportsIdentifierApi(reportId, startDate, endDate), REPORT_DATA_LIST_TYPE);
    }

    /**
     * Save a single day of report data for study-wide report. This report is available to all consented participants 
     * in the study.
     * 
     * @param reportId
     *      the ID of the report in the study
     * @param reportData
     *      the data for a single day of the report.
     */
    public void saveStudyReport(String reportId, ReportData reportData) {
        session.checkSignedIn();
        checkArgument(isNotBlank(reportId), CANNOT_BE_BLANK, "reportId");
        checkNotNull(reportData, CANNOT_BE_NULL, "reportData");
        
        post(config.getReportsIdentifierApi(reportId), reportData);
    }
    
    /**
     * Delete all records for all days of a study-wide report.
     * 
     * @param reportId
     *      the ID of the report in the study
     */
    public void deleteStudyReport(String reportId) {
        session.checkSignedIn();
        checkArgument(isNotBlank(reportId), CANNOT_BE_BLANK, "reportId");

        delete(config.getReportsIdentifierApi(reportId));
    }    
}
