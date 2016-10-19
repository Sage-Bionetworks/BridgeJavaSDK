package org.sagebionetworks.bridge.sdk.models.surveys;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @Type(name="SurveyQuestion", value=SurveyQuestion.class),
    @Type(name="SurveyInfoScreen", value=SurveyInfoScreen.class)
})
public interface SurveyElement {
    
    String getGuid();
    void setGuid(String guid);
    String getIdentifier();
    void setIdentifier(String identifier);
    
}
