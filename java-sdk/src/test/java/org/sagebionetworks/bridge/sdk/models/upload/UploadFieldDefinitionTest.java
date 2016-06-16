package org.sagebionetworks.bridge.sdk.models.upload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import org.sagebionetworks.bridge.sdk.exceptions.InvalidEntityException;
import org.sagebionetworks.bridge.sdk.utils.Utilities;

public class UploadFieldDefinitionTest {
    @Test(expected = InvalidEntityException.class)
    public void nullName() {
        new UploadFieldDefinition.Builder().withType(UploadFieldType.INLINE_JSON_BLOB).build();
    }

    @Test(expected = InvalidEntityException.class)
    public void emptyName() {
        new UploadFieldDefinition.Builder().withName("").withType(UploadFieldType.INLINE_JSON_BLOB).build();
    }

    @Test(expected = InvalidEntityException.class)
    public void blankName() {
        new UploadFieldDefinition.Builder().withName("   ").withType(UploadFieldType.INLINE_JSON_BLOB).build();
    }

    @Test(expected = InvalidEntityException.class)
    public void nullType() {
        new UploadFieldDefinition.Builder().withName("null-type").build();
    }

    @Test
    public void happyCase() {
        UploadFieldDefinition fieldDef = new UploadFieldDefinition.Builder().withName("happy-case")
                .withType(UploadFieldType.INLINE_JSON_BLOB).build();
        assertEquals("happy-case", fieldDef.getName());
        assertTrue(fieldDef.isRequired());
        assertEquals(UploadFieldType.INLINE_JSON_BLOB, fieldDef.getType());

        // test that toString doesn't crash and contains something reasonable
        assertTrue(fieldDef.toString().contains("happy-case"));
    }

    @Test
    public void requiredTrue() {
        UploadFieldDefinition fieldDef = new UploadFieldDefinition.Builder().withName("required-true")
                .withRequired(true).withType(UploadFieldType.STRING).build();
        assertEquals("required-true", fieldDef.getName());
        assertTrue(fieldDef.isRequired());
        assertEquals(UploadFieldType.STRING, fieldDef.getType());
    }

    @Test
    public void requiredFalse() {
        UploadFieldDefinition fieldDef = new UploadFieldDefinition.Builder().withName("required-false")
                .withRequired(false).withType(UploadFieldType.INT).build();
        assertEquals("required-false", fieldDef.getName());
        assertFalse(fieldDef.isRequired());
        assertEquals(UploadFieldType.INT, fieldDef.getType());
    }

    @Test
    public void testMultiChoiceAnswerList() {
        // set up original list
        List<String> originalAnswerList = new ArrayList<>();
        originalAnswerList.add("first");
        originalAnswerList.add("second");

        // build and validate
        List<String> expectedAnswerList = ImmutableList.of("first", "second");
        UploadFieldDefinition fieldDef = new UploadFieldDefinition.Builder().withName("multi-choice-field")
                .withType(UploadFieldType.MULTI_CHOICE).withMultiChoiceAnswerList(originalAnswerList).build();
        assertEquals(expectedAnswerList, fieldDef.getMultiChoiceAnswerList());

        // modify original list, verify that field def stays the same
        originalAnswerList.add("third");
        assertEquals(expectedAnswerList, fieldDef.getMultiChoiceAnswerList());
    }

    @Test
    public void testMultiChoiceAnswerVarargs() {
        UploadFieldDefinition fieldDef = new UploadFieldDefinition.Builder().withName("multi-choice-field")
                .withType(UploadFieldType.MULTI_CHOICE).withMultiChoiceAnswerList("aa", "bb", "cc").build();
        assertEquals(ImmutableList.of("aa", "bb", "cc"), fieldDef.getMultiChoiceAnswerList());
    }

    @Test
    public void testOptionalFields() {
        List<String> multiChoiceAnswerList = ImmutableList.of("foo", "bar", "baz");

        UploadFieldDefinition fieldDef = new UploadFieldDefinition.Builder().withFileExtension(".test")
                .withMimeType("text/plain").withMinAppVersion(10).withMaxAppVersion(13).withMaxLength(128)
                .withMultiChoiceAnswerList(multiChoiceAnswerList).withName("optional-stuff")
                .withType(UploadFieldType.STRING).withUnboundedText(true).build();
        assertEquals(".test", fieldDef.getFileExtension());
        assertEquals("text/plain", fieldDef.getMimeType());
        assertEquals(10, fieldDef.getMinAppVersion().intValue());
        assertEquals(13, fieldDef.getMaxAppVersion().intValue());
        assertEquals(128, fieldDef.getMaxLength().intValue());
        assertEquals(multiChoiceAnswerList, fieldDef.getMultiChoiceAnswerList());
        assertEquals("optional-stuff", fieldDef.getName());
        assertEquals(UploadFieldType.STRING, fieldDef.getType());
        assertTrue(fieldDef.isUnboundedText());

        // test that toString doesn't crash and contains something reasonable
        assertTrue(fieldDef.toString().contains("optional-stuff"));
    }

    @Test(expected = InvalidEntityException.class)
    public void convenienceConstructorNullName() {
        new UploadFieldDefinition(null, UploadFieldType.STRING);
    }

    @Test(expected = InvalidEntityException.class)
    public void convenienceConstructorEmptyName() {
        new UploadFieldDefinition("", UploadFieldType.STRING);
    }

    @Test(expected = InvalidEntityException.class)
    public void convenienceConstructorBlankName() {
        new UploadFieldDefinition("   ", UploadFieldType.STRING);
    }

    @Test(expected = InvalidEntityException.class)
    public void convenienceConstructorNullType() {
        new UploadFieldDefinition("null-type", null);
    }

    @Test
    public void convenienceConstructor() {
        UploadFieldDefinition fieldDef = new UploadFieldDefinition("convenience-constructor", UploadFieldType.STRING);
        assertEquals("convenience-constructor", fieldDef.getName());
        assertTrue(fieldDef.isRequired());
        assertEquals(UploadFieldType.STRING, fieldDef.getType());
    }

    @Test
    public void jsonSerialization() throws Exception {
        // start with JSON
        String jsonText = "{\n" +
                "   \"fileExtension\":\".json\",\n" +
                "   \"mimeType\":\"text/json\",\n" +
                "   \"minAppVersion\":2,\n" +
                "   \"maxAppVersion\":7,\n" +
                "   \"maxLength\":24,\n" +
                "   \"multiChoiceAnswerList\":[\"asdf\", \"jkl;\"],\n" +
                "   \"name\":\"test-field\",\n" +
                "   \"required\":false,\n" +
                "   \"type\":\"boolean\",\n" +
                "   \"unboundedText\":true\n" +
                "}";

        // convert to POJO
        List<String> expectedAnswerList = ImmutableList.of("asdf", "jkl;");
        UploadFieldDefinition fieldDef = Utilities.getMapper().readValue(jsonText, UploadFieldDefinition.class);
        assertEquals(".json", fieldDef.getFileExtension());
        assertEquals("text/json", fieldDef.getMimeType());
        assertEquals(2, fieldDef.getMinAppVersion().intValue());
        assertEquals(7, fieldDef.getMaxAppVersion().intValue());
        assertEquals(24, fieldDef.getMaxLength().intValue());
        assertEquals(expectedAnswerList, fieldDef.getMultiChoiceAnswerList());
        assertEquals("test-field", fieldDef.getName());
        assertFalse(fieldDef.isRequired());
        assertEquals(UploadFieldType.BOOLEAN, fieldDef.getType());
        assertTrue(fieldDef.isUnboundedText());

        // convert back to JSON
        String convertedJson = Utilities.getMapper().writeValueAsString(fieldDef);

        // then convert to a map so we can validate the raw JSON
        Map<String, Object> jsonMap = Utilities.getMapper().readValue(convertedJson, Utilities.TYPE_REF_RAW_MAP);
        assertEquals(10, jsonMap.size());
        assertEquals(".json", jsonMap.get("fileExtension"));
        assertEquals("text/json", jsonMap.get("mimeType"));
        assertEquals(2, jsonMap.get("minAppVersion"));
        assertEquals(7, jsonMap.get("maxAppVersion"));
        assertEquals(24, jsonMap.get("maxLength"));
        assertEquals(expectedAnswerList, jsonMap.get("multiChoiceAnswerList"));
        assertEquals("test-field", jsonMap.get("name"));
        assertFalse((boolean) jsonMap.get("required"));
        assertEquals("boolean", jsonMap.get("type"));
        assertTrue((boolean) jsonMap.get("unboundedText"));
    }

    @Test
    public void equalsVerifier() {
        EqualsVerifier.forClass(UploadFieldDefinition.class).allFieldsShouldBeUsed().verify();
    }
}
