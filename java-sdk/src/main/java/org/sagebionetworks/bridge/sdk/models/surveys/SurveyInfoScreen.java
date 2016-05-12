package org.sagebionetworks.bridge.sdk.models.surveys;

import java.util.Objects;

import org.sagebionetworks.bridge.sdk.models.holders.GuidHolder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as=SurveyInfoScreen.class)
public final class SurveyInfoScreen implements SurveyElement, GuidHolder {

    private String guid;
    private String identifier;
    private String type;
    private String title;
    private String prompt;
    private String promptDetail;
    private Image image;
    
    public SurveyInfoScreen() {
        setType("SurveyInfoScreen");
    }
    @Override
    public String getGuid() {
        return guid;
    }
    public void setGuid(String guid) {
        this.guid = guid;
    }
    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    String getType() {
        return type;
    }
    void setType(String type) {
        this.type = type;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getPrompt() {
        return prompt;
    }
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
    
    public String getPromptDetail() {
        return promptDetail;
    }
    public void setPromptDetail(String promptDetail) {
        this.promptDetail = promptDetail;
    }
    
    public Image getImage() {
        return image;
    }
    public void setImage(Image image){
        this.image = image;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(title);
        result = prime * result + Objects.hashCode(prompt);
        result = prime * result + Objects.hashCode(promptDetail);
        result = prime * result + Objects.hashCode(image);
        result = prime * result + Objects.hashCode(guid);
        result = prime * result + Objects.hashCode(identifier);
        result = prime * result + Objects.hashCode(type);
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        SurveyInfoScreen other = (SurveyInfoScreen) obj;
        return (Objects.equals(title, other.title) && Objects.equals(prompt, other.prompt)
                && Objects.equals(promptDetail, other.promptDetail) && Objects.equals(image, other.image)
                && Objects.equals(guid, other.guid) && Objects.equals(identifier, other.identifier) && Objects.equals(
                type, other.type));
    }
    @Override
    public String toString() {
        return String.format("SurveyInfoScreen [guid=%s, identifier=%s, title=%s, prompt=%s, promptDetail=%s, image=%s]", 
                    guid, identifier, title, prompt, promptDetail, image);
    }
    
}
