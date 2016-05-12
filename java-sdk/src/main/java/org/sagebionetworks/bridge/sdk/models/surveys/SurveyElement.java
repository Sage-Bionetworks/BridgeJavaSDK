package org.sagebionetworks.bridge.sdk.models.surveys;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @Type(name="SurveyQuestion", value=SurveyQuestion.class),
    @Type(name="SurveyInfoScreen", value=SurveyInfoScreen.class)
})
public interface SurveyElement {
    
    public String getGuid();
    public void setGuid(String guid);
    public String getIdentifier();
    public void setIdentifier(String identifier);
    
}
