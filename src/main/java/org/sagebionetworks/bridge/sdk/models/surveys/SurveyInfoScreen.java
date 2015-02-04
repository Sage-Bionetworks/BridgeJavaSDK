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
    private String imageSource;
    
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
    @Override
    public String getType() {
        return type;
    }
    public void setType(String type) {
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
    
    public String getImageSource() {
        return imageSource;
    }
    public void setImageSource(String imageSource){
        this.imageSource = imageSource;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((prompt == null) ? 0 : prompt.hashCode());
        result = prime * result + ((promptDetail == null) ? 0 : promptDetail.hashCode());
        result = prime * result + ((imageSource == null) ? 0 : imageSource.hashCode());
        result = prime * result + ((getGuid() == null) ? 0 : getGuid().hashCode());
        result = prime * result + ((getIdentifier() == null) ? 0 : getIdentifier().hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SurveyInfoScreen other = (SurveyInfoScreen) obj;
        return (Objects.equals(title, other.title) && Objects.equals(prompt, other.prompt)
                && Objects.equals(promptDetail, other.promptDetail) && Objects.equals(imageSource, other.imageSource)
                && Objects.equals(guid, other.guid) && Objects.equals(identifier, other.identifier) && Objects.equals(
                type, other.type));
    }
    @Override
    public String toString() {
        return "SurveyInfoScreen [guid=" + getGuid() + ", identifier=" + getIdentifier() + ", title=" + title
                + ", prompt=" + prompt + ", promptDetail=" + promptDetail + ", imageSource=" + imageSource + "]";
    }
    
}
