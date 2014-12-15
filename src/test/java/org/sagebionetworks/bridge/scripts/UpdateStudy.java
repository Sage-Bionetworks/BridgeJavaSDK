package org.sagebionetworks.bridge.scripts;

import org.sagebionetworks.bridge.sdk.AdminClient;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.Config;
import org.sagebionetworks.bridge.sdk.Session;

public class UpdateStudy {
    public static void main(String[] args) {
        Config config = ClientProvider.getConfig();
        config.set(Config.Props.HOST, "https://parkinson.sagebridge.org");
        Session session = ClientProvider.signIn(config.getAdminCredentials());

        AdminClient client = session.getAdminClient();
        /*
        ResourceList<Study> studies = client.getAllStudies();
        for (Study study : studies) {
            study.setMinAgeOfConsent(18);
            client.updateStudy(study);
        }*/
    }
}
