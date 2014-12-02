package org.sagebionetworks.bridge.scripts;

import java.util.LinkedList;

import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestionOption;

@SuppressWarnings("serial")
public class UnitedStatesList extends LinkedList<SurveyQuestionOption> {

    private void addState(String name, String code) {
        add(new SurveyQuestionOption(name, code));
    }
    
    public UnitedStatesList() {
        addState("Alabama","AL");
        addState("Alaska","AK");
        addState("Arizona","AZ");
        addState("Arkansas","AR");
        addState("California","CA");
        addState("Colorado","CO");
        addState("Connecticut","CT");
        addState("Delaware","DE");
        addState("Florida","FL");
        addState("Georgia","GA");
        addState("Hawaii","HI");
        addState("Idaho","ID");
        addState("Illinois","IL");
        addState("Indiana","IN");
        addState("Iowa","IA");
        addState("Kansas","KS");
        addState("Kentucky[C]","KY");
        addState("Louisiana","LA");
        addState("Maine","ME");
        addState("Maryland","MD");
        addState("Massachusetts[D]","MA");
        addState("Michigan","MI");
        addState("Minnesota","MN");
        addState("Mississippi","MS");
        addState("Missouri","MO");
        addState("Montana","MT");
        addState("Nebraska","NE");
        addState("Nevada","NV");
        addState("New Hampshire","NH");
        addState("New Jersey","NJ");
        addState("New Mexico","NM");
        addState("New York","NY");
        addState("North Carolina","NC");
        addState("North Dakota","ND");
        addState("Ohio","OH");
        addState("Oklahoma","OK");
        addState("Oregon","OR");
        addState("Pennsylvania[E]","PA");
        addState("Rhode Island[F]","RI");
        addState("South Carolina","SC");
        addState("South Dakota","SD");
        addState("Tennessee","TN");
        addState("Texas","TX");
        addState("Utah","UT");
        addState("Vermont","VT");
        addState("Virginia[G]","VA");
        addState("Washington","WA");
        addState("Washington, D.C.","DC");
        addState("West Virginia","WV");
        addState("Wisconsin","WI");
        addState("Wyoming","WY");
    }
    
}
