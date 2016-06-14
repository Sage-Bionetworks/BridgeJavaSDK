package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.joda.time.LocalDate;

import org.sagebionetworks.bridge.sdk.models.DateRangeResourceList;
import org.sagebionetworks.bridge.sdk.models.ReportTypeResourceList;
import org.sagebionetworks.bridge.sdk.models.reports.ReportData;
import org.sagebionetworks.bridge.sdk.models.reports.ReportIndex;
import org.sagebionetworks.bridge.sdk.models.reports.ReportType;
import org.sagebionetworks.bridge.sdk.utils.Utilities;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Client for working with the report API, an API for saving and retrieving JSON-based data for reports, either for a
 * study or for individual participants in a study. Reports are organized by ID, and IDs can be added "at will" (you do
 * not need to specify the ID before you start adding data for that report). However, report IDs can only contain upper-
 * and lower-case letters, numbers, a dash or an underscore.
 */
public class ReportClient extends BaseApiCaller {

    private static final TypeReference<DateRangeResourceList<ReportData>> REPORT_DATA_LIST_TYPE = 
            new TypeReference<DateRangeResourceList<ReportData>>() {};

    private static final TypeReference<ReportTypeResourceList<ReportIndex>> REPORT_INDEX_LIST_TYPE = 
            new TypeReference<ReportTypeResourceList<ReportIndex>>() {};
                    
    ReportClient(BridgeSession session) {
        super(session);
    }
    
    public ReportTypeResourceList<ReportIndex> getReportIndices(ReportType type) {
        session.checkSignedIn();
        checkNotNull(type);
        
        return get(config.getReportIndicesApi(type), REPORT_INDEX_LIST_TYPE);
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
     * @param userId
     *      the ID of the participant
     * @param reportId
     *      the ID of the report in the study
     * @param reportData
     *      the data for a single day of the report.
     */
    public void saveParticipantReportByUserId(String userId, String reportId, ReportData reportData) {
        session.checkSignedIn();
        checkArgument(isNotBlank(userId), CANNOT_BE_BLANK, "userId");
        checkArgument(isNotBlank(reportId), CANNOT_BE_BLANK, "reportId");
        checkNotNull(reportData, CANNOT_BE_NULL, "reportData");
        
        post(config.getParticipantsUserIdReportsIdentifierApi(userId, reportId), reportData);
    }
    
    /**
     * Save a single day of report data for a participant, using the participant's anonymous health code (necessary 
     * for reports based on the anonymized data set). Available to accounts with the worker role only.
     * 
     * @param healthCode
     *      the anonymous health code of a participant
     * @param reportId
     *      the ID of the report in the study
     * @param reportData
     *      the data for a single day of the report.
     */
    public void saveParticipantReportByHealthCode(String healthCode, String reportId, ReportData reportData) {
        session.checkSignedIn();
        checkArgument(isNotBlank(healthCode), CANNOT_BE_BLANK, "healthCode");
        checkArgument(isNotBlank(reportId), CANNOT_BE_BLANK, "reportId");
        checkNotNull(reportData, CANNOT_BE_NULL, "reportData");
        
        ObjectNode node = (ObjectNode)Utilities.getMapper().valueToTree(reportData);
        node.put("healthCode", healthCode);
        post(config.getParticipantsReportsIdentifierApi(reportId), node);
    }
    
    /**
     * Delete all records for all days of a report for a single participant.
     * 
     * @param userId
     *      the ID of the participant
     * @param reportId
     *      the ID of the report in the study
     */
    public void deleteParticipantReport(String userId, String reportId) {
        session.checkSignedIn();
        checkArgument(isNotBlank(userId), CANNOT_BE_BLANK, "healthCode");
        checkArgument(isNotBlank(reportId), CANNOT_BE_BLANK, "reportId");
        
        delete(config.getParticipantsUserIdReportsIdentifierApi(userId, reportId));
    }
    
    /**
     * Delete a single participant report record.
     * 
     * @param userId
     *      the ID of the participant
     * @param reportId
     *      the ID of the report in the study
     * @param date
     *      the date of the single report record to delete
     */
    public void deleteParticipantReportRecord(String userId, String reportId, LocalDate date) {
        session.checkSignedIn();
        checkArgument(isNotBlank(userId), CANNOT_BE_BLANK, "healthCode");
        checkArgument(isNotBlank(reportId), CANNOT_BE_BLANK, "reportId");
        checkNotNull(date, CANNOT_BE_NULL, "date");
        
        delete(config.getParticipantsUserIdReportsIdentifierDateApi(userId, reportId, date));
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
    
    /**
     * Delete one report record by its date.
     * 
     * @param reportId
     *      the ID of the study report to delete
     * @param date
     *      the date of the single record to delete
     */
    public void deleteStudyReportRecord(String reportId, LocalDate date) {
        session.checkSignedIn();
        checkArgument(isNotBlank(reportId), CANNOT_BE_BLANK, "reportId");
        checkNotNull(date, CANNOT_BE_NULL, "date");
        
        delete(config.getReportsIdentifierDateApi(reportId, date));
    }
    
    /** A method for an admin user to force the deletion of a participant report index. 
     * These cannot normally be removed because of the expense of determining whether or 
     * not they are still in use for any participant. 
     * 
     * @param reportId
     */
    public void forceDeleteOfParticipantReportIndex(String reportId) {
        session.checkSignedIn();
        checkArgument(isNotBlank(reportId), CANNOT_BE_BLANK, "reportId");
        
        delete(config.getParticipantsReportsIdentifierApi(reportId));
    }
}
