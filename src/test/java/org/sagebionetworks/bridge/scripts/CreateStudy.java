package org.sagebionetworks.bridge.scripts;

import org.sagebionetworks.bridge.sdk.AdminClient;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.Config;
import org.sagebionetworks.bridge.sdk.Session;
import org.sagebionetworks.bridge.sdk.models.studies.Study;

public class CreateStudy {

    public static void main(String[] args) {
        Config config = ClientProvider.getConfig();
        config.set(Config.Props.HOST, "http://localhost:9000");
        Session session = ClientProvider.signIn(config.getAdminCredentials());
        
        AdminClient client = session.getAdminClient();
        Study study = new Study();
        study.setName("mPower");
        study.setIdentifier("parkinson");
        client.createStudy(study);
        /*
        Study study = new Study();
        study.setName("Share The Journey");
        study.setIdentifier("breastcancer");
        client.createStudy(study);

        study = new Study();
        study.setName("Asthma Study");
        study.setIdentifier("asthma");
        client.createStudy(study);
        
        study = new Study();
        study.setName("My Heart Counts");
        study.setIdentifier("cardiovascular");
        client.createStudy(study);
        
        study = new Study();
        study.setIdentifier("diabetes");
        study.setName("Diabetes Study");
        client.createStudy(study);
        
        // YOU MUST REPLACE THE VALUES BELOW EACH TIME YOU RUN THIS!
        
        config.set(Config.Props.ADMIN_STUDIES_API, "/admin/v2/studies");
        study = new Study();
        study.setName("Test Study");
        study.setIdentifier("api");
        study.getTrackers().add("pb-tracker");
        study.getTrackers().add("med-tracker");
        study.setHostname("api.sagebridge.org");
        study.setResearcherRole("api_researcher");
        study.setStormpathHref("https://api.stormpath.com/v1/directories/4Jb1NU1Y02Kj90AmQNvwOk");
        client.createStudy(study);
        
        study = new Study();
        study.setName("Parkinson's Disease Mobile Study");
        study.setIdentifier("pd");
        study.setHostname("pd.sagebridge.org");
        study.setResearcherRole("pd_researcher");
        study.setStormpathHref("https://api.stormpath.com/v1/directories/ojPkm9y0sxo8lH8PuZrId");
        client.createStudy(study);
        */
    }
    
}
