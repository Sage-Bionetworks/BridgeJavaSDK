package org.sagebionetworks.bridge.sdk.models.reports;

import static org.junit.Assert.assertEquals;

import org.joda.time.LocalDate;
import org.junit.Test;

import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.utils.BridgeUtils;

import com.fasterxml.jackson.databind.JsonNode;

public class ReportDataTest {

    private static final LocalDate REPORT_DATE = LocalDate.parse("2016-06-10");
    
    @Test
    public void canSerialize() throws Exception {
        String json = Tests.unescapeJson("{'date': '2016-06-10', 'data':{'foo':'baz','bar':'bam'}}");
        ReportData report = BridgeUtils.getMapper().readValue(json, ReportData.class);
        
        assertEquals(REPORT_DATE, report.getDate());
        assertEquals("baz", report.getReportData().get("foo").asText());
        assertEquals("bam", report.getReportData().get("bar").asText());
        
        JsonNode serReport = BridgeUtils.getMapper().readTree(
                BridgeUtils.getMapper().writeValueAsString(report));
        
        assertEquals(REPORT_DATE.toString(), serReport.get("date").asText());
        assertEquals("baz", serReport.get("data").get("foo").asText());
        assertEquals("bam", serReport.get("data").get("bar").asText());
    }
}
