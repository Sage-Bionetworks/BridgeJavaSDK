package org.sagebionetworks.bridge.sdk.models.healthData;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.utils.BridgeUtils;

public class RecordExportStatusRequestTest {

    @Test
    public void canDeserialize() throws Exception {
        String json = Tests.unescapeJson("{'recordIds':['test record id'],"+
                "'synapseExporterStatus':'SUCCEEDED'}");

        RecordExportStatusRequest deser = BridgeUtils.getMapper().readValue(json, RecordExportStatusRequest.class);
        assertEquals(RecordExportStatusRequest.ExporterStatus.SUCCEEDED, deser.getSynapseExporterStatus());
        assertEquals("test record id", deser.getRecordIds().get(0));
    }
}
