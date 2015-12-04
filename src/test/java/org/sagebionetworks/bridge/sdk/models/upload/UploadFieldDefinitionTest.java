package org.sagebionetworks.bridge.sdk.models.upload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

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
                "   \"name\":\"test-field\",\n" +
                "   \"required\":false,\n" +
                "   \"type\":\"boolean\"\n" +
                "}";

        // convert to POJO
        UploadFieldDefinition fieldDef = Utilities.getMapper().readValue(jsonText, UploadFieldDefinition.class);
        assertEquals("test-field", fieldDef.getName());
        assertFalse(fieldDef.isRequired());
        assertEquals(UploadFieldType.BOOLEAN, fieldDef.getType());

        // convert back to JSON
        String convertedJson = Utilities.getMapper().writeValueAsString(fieldDef);

        // then convert to a map so we can validate the raw JSON
        Map<String, Object> jsonMap = Utilities.getMapper().readValue(convertedJson, Utilities.TYPE_REF_RAW_MAP);
        assertEquals(3, jsonMap.size());
        assertEquals("test-field", jsonMap.get("name"));
        assertFalse((boolean) jsonMap.get("required"));
        assertEquals("boolean", jsonMap.get("type"));
    }

    @Test
    public void equalsVerifier() {
        EqualsVerifier.forClass(UploadFieldDefinition.class).allFieldsShouldBeUsed().verify();
    }
}
