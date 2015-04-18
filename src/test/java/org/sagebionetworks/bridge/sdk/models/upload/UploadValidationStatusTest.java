package org.sagebionetworks.bridge.sdk.models.upload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import org.sagebionetworks.bridge.sdk.exceptions.BridgeSDKException;

public class UploadValidationStatusTest {
    @Test(expected = BridgeSDKException.class)
    public void nullId() {
        new UploadValidationStatus.Builder().withMessageList(ImmutableList.<String>of())
                .withStatus(UploadStatus.UNKNOWN).build();
    }

    @Test(expected = BridgeSDKException.class)
    public void emptyId() {
        new UploadValidationStatus.Builder().withId("").withMessageList(ImmutableList.<String>of())
                .withStatus(UploadStatus.UNKNOWN).build();
    }

    @Test(expected = BridgeSDKException.class)
    public void blankId() {
        new UploadValidationStatus.Builder().withId("   ").withMessageList(ImmutableList.<String>of())
                .withStatus(UploadStatus.UNKNOWN).build();
    }

    @Test(expected = BridgeSDKException.class)
    public void nullMessageList() {
        new UploadValidationStatus.Builder().withId("test-upload-id").withStatus(UploadStatus.UNKNOWN).build();
    }

    @Test(expected = BridgeSDKException.class)
    public void nullStatus() {
        new UploadValidationStatus.Builder().withId("test-upload-id").withMessageList(ImmutableList.<String>of())
                .build();
    }

    @Test(expected = BridgeSDKException.class)
    public void nullMessage() {
        List<String> messageList = new ArrayList<>();
        messageList.add("first");
        messageList.add(null);
        messageList.add("last");

        new UploadValidationStatus.Builder().withId("test-upload-id").withMessageList(messageList)
                .withStatus(UploadStatus.UNKNOWN).build();
    }

    @Test(expected = BridgeSDKException.class)
    public void emptyMessage() {
        List<String> messageList = new ArrayList<>();
        messageList.add("first");
        messageList.add("");
        messageList.add("last");

        new UploadValidationStatus.Builder().withId("test-upload-id").withMessageList(messageList)
                .withStatus(UploadStatus.UNKNOWN).build();
    }

    @Test(expected = BridgeSDKException.class)
    public void blankMessage() {
        List<String> messageList = new ArrayList<>();
        messageList.add("first");
        messageList.add("   ");
        messageList.add("last");

        new UploadValidationStatus.Builder().withId("test-upload-id").withMessageList(messageList)
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
    public void happyCaseWithmessages() {
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

    // TODO json serialization

    @Test
    public void equalsVerifier() {
        EqualsVerifier.forClass(UploadValidationStatus.class).allFieldsShouldBeUsed().verify();
    }
}
