package org.sagebionetworks.bridge.sdk.models.upload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import org.sagebionetworks.bridge.sdk.exceptions.InvalidEntityException;
import org.sagebionetworks.bridge.sdk.utils.Utilities;

@SuppressWarnings("unchecked")
public class UploadValidationStatusTest {
    @Test(expected = InvalidEntityException.class)
    public void nullId() {
        new UploadValidationStatus.Builder().withMessageList(ImmutableList.<String>of())
                .withStatus(UploadStatus.UNKNOWN).build();
    }

    @Test(expected = InvalidEntityException.class)
    public void emptyId() {
        new UploadValidationStatus.Builder().withId("").withMessageList(ImmutableList.<String>of())
                .withStatus(UploadStatus.UNKNOWN).build();
    }

    @Test(expected = InvalidEntityException.class)
    public void blankId() {
        new UploadValidationStatus.Builder().withId("   ").withMessageList(ImmutableList.<String>of())
                .withStatus(UploadStatus.UNKNOWN).build();
    }

    @Test(expected = InvalidEntityException.class)
    public void nullMessageList() {
        new UploadValidationStatus.Builder().withId("test-upload-id").withStatus(UploadStatus.UNKNOWN).build();
    }

    @Test(expected = InvalidEntityException.class)
    public void nullStatus() {
        new UploadValidationStatus.Builder().withId("test-upload-id").withMessageList(ImmutableList.<String>of())
                .build();
    }

    @Test(expected = InvalidEntityException.class)
    public void nullMessage() {
        List<String> messageList = new ArrayList<>();
        messageList.add("first");
        messageList.add(null);
        messageList.add("last");

        new UploadValidationStatus.Builder().withId("test-upload-id").withMessageList(messageList)
                .withStatus(UploadStatus.UNKNOWN).build();
    }

    @Test(expected = InvalidEntityException.class)
    public void emptyMessage() {
        List<String> messageList = new ArrayList<>();
        messageList.add("first");
        messageList.add("");
        messageList.add("last");

        new UploadValidationStatus.Builder().withId("test-upload-id").withMessageList(messageList)
                .withStatus(UploadStatus.UNKNOWN).build();
    }

    @Test(expected = InvalidEntityException.class)
    public void blankMessage() {
        List<String> messageList = new ArrayList<>();
        messageList.add("first");
        messageList.add("   ");
        messageList.add("last");

        new UploadValidationStatus.Builder().withId("test-upload-id").withMessageList(messageList)
                .withStatus(UploadStatus.UNKNOWN).build();
    }

    // This actually triggers an error on withMessages() from ImmutableList not accepting null elements. As per
    // Guava docs, this is a null pointer exception.
    @Test(expected = NullPointerException.class)
    public void nullMessageVarargs() {
        new UploadValidationStatus.Builder().withMessages("first", null, "last");
    }

    @Test(expected = InvalidEntityException.class)
    public void emptyMessageVarargs() {
        new UploadValidationStatus.Builder().withId("test-upload-id").withMessages("first", "", "last")
                .withStatus(UploadStatus.UNKNOWN).build();
    }

    @Test(expected = InvalidEntityException.class)
    public void blankMessageVarargs() {
        new UploadValidationStatus.Builder().withId("test-upload-id").withMessages("first", "   ", "last")
                .withStatus(UploadStatus.UNKNOWN).build();
    }

    @Test
    public void happyCase() {
        List<String> originalMessageList = new ArrayList<>();

        UploadValidationStatus status = new UploadValidationStatus.Builder().withId("test-upload-id")
                .withMessageList(originalMessageList).withStatus(UploadStatus.SUCCEEDED).build();
        assertEquals("test-upload-id", status.getId());
        assertTrue(status.getMessageList().isEmpty());
        assertEquals(UploadStatus.SUCCEEDED, status.getStatus());

        // check that modifying the original message list doesn't modify the result message list
        originalMessageList.add("This message was added afterwards");
        assertTrue(status.getMessageList().isEmpty());
    }

    @Test
    public void happyCaseWithMessages() {
        UploadValidationStatus status = new UploadValidationStatus.Builder().withId("failed-upload-id")
                .withMessageList(ImmutableList.of("foo", "bar", "baz")).withStatus(UploadStatus.VALIDATION_FAILED)
                .build();
        assertEquals("failed-upload-id", status.getId());
        assertEquals(UploadStatus.VALIDATION_FAILED, status.getStatus());

        List<String> resultMessageList = status.getMessageList();
        assertEquals(3, resultMessageList.size());
        assertEquals("foo", resultMessageList.get(0));
        assertEquals("bar", resultMessageList.get(1));
        assertEquals("baz", resultMessageList.get(2));
    }

    @Test
    public void happyCaseWithMessageVarargs() {
        UploadValidationStatus status = new UploadValidationStatus.Builder().withId("varargs-upload-id")
                .withMessages("qwerty", "asdf", "jkl;").withStatus(UploadStatus.VALIDATION_FAILED).build();
        assertEquals("varargs-upload-id", status.getId());
        assertEquals(UploadStatus.VALIDATION_FAILED, status.getStatus());

        List<String> resultMessageList = status.getMessageList();
        assertEquals(3, resultMessageList.size());
        assertEquals("qwerty", resultMessageList.get(0));
        assertEquals("asdf", resultMessageList.get(1));
        assertEquals("jkl;", resultMessageList.get(2));
    }

    @Test
    public void serialization() throws Exception {
        // start with JSON
        String jsonText = "{\n" +
                "   \"id\":\"json-upload\",\n" +
                "   \"status\":\"succeeded\",\n" +
                "   \"messageList\":[\n" +
                "       \"foo\",\n" +
                "       \"bar\",\n" +
                "       \"baz\"\n" +
                "   ]\n" +
                "}";

        // convert to POJO
        UploadValidationStatus status = Utilities.getMapper().readValue(jsonText, UploadValidationStatus.class);
        assertEquals("json-upload", status.getId());
        assertEquals(UploadStatus.SUCCEEDED, status.getStatus());

        List<String> messageList = status.getMessageList();
        assertEquals(3, messageList.size());
        assertEquals("foo", messageList.get(0));
        assertEquals("bar", messageList.get(1));
        assertEquals("baz", messageList.get(2));

        // convert back to JSON
        String convertedJson = Utilities.getMapper().writeValueAsString(status);

        // then convert to a map so we can validate the raw JSON
        Map<String, Object> jsonMap = Utilities.getMapper().readValue(convertedJson, Utilities.TYPE_REF_RAW_MAP);
        assertEquals(3, jsonMap.size());
        assertEquals("json-upload", jsonMap.get("id"));
        assertEquals("succeeded", jsonMap.get("status"));

        List<String> messageJsonList = (List<String>) jsonMap.get("messageList");
        assertEquals(3, messageJsonList.size());
        assertEquals("foo", messageJsonList.get(0));
        assertEquals("bar", messageJsonList.get(1));
        assertEquals("baz", messageJsonList.get(2));
    }

    @Test
    public void equalsVerifier() {
        EqualsVerifier.forClass(UploadValidationStatus.class).allFieldsShouldBeUsed().verify();
    }
}
