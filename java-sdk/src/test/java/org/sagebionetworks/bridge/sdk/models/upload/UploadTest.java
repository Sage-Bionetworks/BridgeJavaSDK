package org.sagebionetworks.bridge.sdk.models.upload;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;

import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.utils.Utilities;

import nl.jqno.equalsverifier.EqualsVerifier;

public class UploadTest {
    
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(Upload.class).allFieldsShouldBeUsed().verify();
    }
    
    @Test
    public void canDeserialize() throws Exception {
        String json = Tests.unescapeJson("{'contentLength':10000,"+
                "'status':'succeeded','requestedOn':'2016-07-26T22:43:10.392Z',"+
                "'completedOn':'2016-07-26T22:43:10.468Z','completedBy':'s3_worker',"+
                "'uploadDate':'2016-10-10','uploadId':'DEF','validationMessageList':"+
                    "['message 1','message 2'],'schemaId':'schemaId','schemaRevision':2,'type':'Upload'}");
        
        Upload deser = Utilities.getMapper().readValue(json, Upload.class);
        assertEquals(10000, deser.getContentLength());
        assertEquals(UploadStatus.SUCCEEDED, deser.getStatus());
        assertEquals(DateTime.parse("2016-07-26T22:43:10.392Z"), deser.getRequestedOn());
        assertEquals(DateTime.parse("2016-07-26T22:43:10.468Z"), deser.getCompletedOn());
        assertEquals(UploadCompletionClient.S3_WORKER, deser.getCompletedBy());
        assertEquals(LocalDate.parse("2016-10-10"), deser.getUploadDate());
        assertEquals("DEF", deser.getUploadId());
        assertEquals("message 1", deser.getValidationMessageList().get(0));
        assertEquals("message 2", deser.getValidationMessageList().get(1));
    }

}
