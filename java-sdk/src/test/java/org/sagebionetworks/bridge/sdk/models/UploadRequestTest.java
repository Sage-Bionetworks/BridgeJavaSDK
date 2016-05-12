package org.sagebionetworks.bridge.sdk.models;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URL;
import java.util.Map;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.exceptions.InvalidEntityException;
import org.sagebionetworks.bridge.sdk.models.upload.UploadRequest;
import org.sagebionetworks.bridge.sdk.utils.Utilities;

public class UploadRequestTest {
    @Test(expected = InvalidEntityException.class)
    public void nullName() {
        new UploadRequest.Builder().withContentLength(42).withContentMd5("dummy md5").withContentType("text/plain")
                .build();
    }

    @Test(expected = InvalidEntityException.class)
    public void emptyName() {
        new UploadRequest.Builder().withName("").withContentLength(42).withContentMd5("dummy md5")
                .withContentType("text/plain").build();
    }

    @Test(expected = InvalidEntityException.class)
    public void blankName() {
        new UploadRequest.Builder().withName("   ").withContentLength(42).withContentMd5("dummy md5")
                .withContentType("text/plain").build();
    }

    @Test(expected = InvalidEntityException.class)
    public void negativeLength() {
        new UploadRequest.Builder().withName("test-file").withContentLength(-1).withContentMd5("dummy md5")
                .withContentType("text/plain").build();
    }

    @Test(expected = InvalidEntityException.class)
    public void nullMd5() {
        new UploadRequest.Builder().withName("test-file").withContentLength(42).withContentType("text/plain").build();
    }

    @Test(expected = InvalidEntityException.class)
    public void emptyMd5() {
        new UploadRequest.Builder().withName("test-file").withContentLength(42).withContentMd5("")
                .withContentType("text/plain").build();
    }

    @Test(expected = InvalidEntityException.class)
    public void blankMd5() {
        new UploadRequest.Builder().withName("test-file").withContentLength(42).withContentMd5("   ")
                .withContentType("text/plain").build();
    }

    @Test(expected = InvalidEntityException.class)
    public void nullType() {
        new UploadRequest.Builder().withName("test-file").withContentLength(42).withContentMd5("dummy md5").build();
    }

    @Test(expected = InvalidEntityException.class)
    public void emptyType() {
        new UploadRequest.Builder().withName("test-file").withContentLength(42).withContentMd5("dummy md5")
                .withContentType("").build();
    }

    @Test(expected = InvalidEntityException.class)
    public void blankType() {
        new UploadRequest.Builder().withName("test-file").withContentLength(42).withContentMd5("dummy md5")
                .withContentType("   ").build();
    }

    @Test
    public void happyCaseZeroLength() {
        UploadRequest req = new UploadRequest.Builder().withName("empty-file").withContentLength(0)
                .withContentMd5("dummy md5").withContentType("text/empty").build();
        assertEquals("empty-file", req.getName());
        assertEquals(0, req.getContentLength());
        assertEquals("dummy md5", req.getContentMd5());
        assertEquals("text/empty", req.getContentType());
    }

    @Test
    public void happyCasePositiveLength() {
        UploadRequest req = new UploadRequest.Builder().withName("test-file").withContentLength(42)
                .withContentMd5("dummy md5").withContentType("text/plain").build();
        assertEquals("test-file", req.getName());
        assertEquals(42, req.getContentLength());
        assertEquals("dummy md5", req.getContentMd5());
        assertEquals("text/plain", req.getContentType());
    }

    @Test
    public void fromFile() throws Exception {
        URL resource = this.getClass().getResource("/upload-test/production/non-json-encrypted");
        File file = new File(resource.toURI());
        UploadRequest req = new UploadRequest.Builder().withFile(file).withContentType("application/zip").build();
        assertEquals("non-json-encrypted", req.getName());
        assertEquals(1224, req.getContentLength());
        assertEquals("+nRdR9Pg6voPdlZQj80iHA==", req.getContentMd5());
        assertEquals("application/zip", req.getContentType());
    }

    @Test
    public void jsonSerialization() throws Exception {
        // start with JSON
        String jsonText = "{\n" +
                "   \"name\":\"test-from-json\",\n" +
                "   \"contentLength\":1337,\n" +
                "   \"contentMd5\":\"dummy md5\",\n" +
                "   \"contentType\":\"text/plain\"\n" +
                "}";

        // convert to POJO
        UploadRequest req = Utilities.getMapper().readValue(jsonText, UploadRequest.class);
        assertEquals("test-from-json", req.getName());
        assertEquals(1337, req.getContentLength());
        assertEquals("dummy md5", req.getContentMd5());
        assertEquals("text/plain", req.getContentType());

        // convert back to JSON
        String convertedJson = Utilities.getMapper().writeValueAsString(req);

        // then convert to a map so we can validate the raw JSON
        Map<String, Object> jsonMap = Utilities.getMapper().readValue(convertedJson, Utilities.TYPE_REF_RAW_MAP);
        assertEquals(4, jsonMap.size());
        assertEquals("test-from-json", jsonMap.get("name"));
        assertEquals(1337, (int) jsonMap.get("contentLength"));
        assertEquals("dummy md5", jsonMap.get("contentMd5"));
        assertEquals("text/plain", jsonMap.get("contentType"));
    }

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(UploadRequest.class).allFieldsShouldBeUsed().verify();
    }
}
