package org.sagebionetworks.bridge.sdk.models.studies;

import static org.junit.Assert.assertEquals;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;

import org.sagebionetworks.bridge.sdk.models.studies.EmailTemplate.MimeType;
import org.sagebionetworks.bridge.sdk.utils.Utilities;

import com.fasterxml.jackson.databind.JsonNode;

public class StudyTest {
    
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(Study.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
    @Test
    public void testRoundtripSerialization() throws Exception {
        Study study = new Study();
        study.setName("Test Name");
        study.setSponsorName("Sponsor Name");
        study.setIdentifier("foo");
        study.setVersion(2L);
        study.setSupportEmail("bridge-testing+support@sagebase.org");
        study.setSynapseDataAccessTeamId(1337L);
        study.setSynapseProjectId("test-project");
        study.setTechnicalEmail("bridge-testing+technical@sagebase.org");
        study.setConsentNotificationEmail("bridge-testing+consent@sagebase.org");
        study.getUserProfileAttributes().add("test");
        study.getTaskIdentifiers().add("taskA");
        study.getDataGroups().add("beta_users");
        study.setPasswordPolicy(new PasswordPolicy(7, false, true, true, false));
        study.setVerifyEmailTemplate(new EmailTemplate("subject", "body ${url}", MimeType.TEXT));
        study.setResetPasswordTemplate(new EmailTemplate("subject", "<p>body ${url}</p>", MimeType.HTML));
        study.setStrictUploadValidationEnabled(true);
        study.setHealthCodeExportEnabled(true);
        study.setEmailVerificationEnabled(true);
        study.getMinSupportedAppVersions().put(OperatingSystem.IOS, 12);
        study.getMinSupportedAppVersions().put(OperatingSystem.ANDROID, 14);
        
        String json = Utilities.getMapper().writeValueAsString(study);
        JsonNode node = Utilities.getMapper().readTree(json);
        
        assertEquals("Test Name", node.get("name").asText());
        assertEquals("Sponsor Name", node.get("sponsorName").asText());
        assertEquals("foo", node.get("identifier").asText());
        assertEquals(2L, node.get("version").asLong());
        assertEquals(0, node.get("minAgeOfConsent").asInt());
        assertEquals(0, node.get("maxNumOfParticipants").asInt());
        assertEquals("bridge-testing+support@sagebase.org", node.get("supportEmail").asText());
        assertEquals(1337L, node.get("synapseDataAccessTeamId").longValue());
        assertEquals("test-project", node.get("synapseProjectId").textValue());
        assertEquals("bridge-testing+technical@sagebase.org", node.get("technicalEmail").asText());
        assertEquals("bridge-testing+consent@sagebase.org", node.get("consentNotificationEmail").asText());
        assertEquals("test", node.get("userProfileAttributes").get(0).asText());
        assertEquals("taskA", node.get("taskIdentifiers").get(0).asText());
        assertEquals("beta_users", node.get("dataGroups").get(0).asText());
        assertEquals(true, node.get("strictUploadValidationEnabled").asBoolean());
        assertEquals(true, node.get("healthCodeExportEnabled").asBoolean());
        assertEquals(true, node.get("emailVerificationEnabled").asBoolean());
        assertEquals(12, node.get("minSupportedAppVersions").get("iPhone OS").asInt());
        assertEquals(14, node.get("minSupportedAppVersions").get("Android").asInt());
        
        JsonNode passwordPolicyNode = node.get("passwordPolicy");
        assertEquals(7, passwordPolicyNode.get("minLength").asInt());
        assertEquals(false, passwordPolicyNode.get("numericRequired").asBoolean());
        assertEquals(true, passwordPolicyNode.get("symbolRequired").asBoolean());
        assertEquals(true, passwordPolicyNode.get("lowerCaseRequired").asBoolean());
        assertEquals(false, passwordPolicyNode.get("upperCaseRequired").asBoolean());
        
        JsonNode veTemplate = node.get("verifyEmailTemplate");
        assertEquals("subject", veTemplate.get("subject").asText());
        assertEquals("body ${url}", veTemplate.get("body").asText());
        assertEquals("text", veTemplate.get("mimeType").asText());
        
        JsonNode rpTemplate = node.get("resetPasswordTemplate");
        assertEquals("subject", rpTemplate.get("subject").asText());
        assertEquals("<p>body ${url}</p>", rpTemplate.get("body").asText());
        assertEquals("html", rpTemplate.get("mimeType").asText());
        
        // And the resultant object is equal to the original (comparing by value)
        Study newStudy = Utilities.getMapper().readValue(json, Study.class);
        
        assertEquals(study, newStudy);
    }
}
