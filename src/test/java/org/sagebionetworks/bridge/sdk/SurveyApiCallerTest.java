package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.models.SignInCredentials;
import org.sagebionetworks.bridge.sdk.models.SignUpCredentials;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;

public class SurveyApiCallerTest {

    private static ClientProvider provider;
    private static SurveyApiCaller surveyApi;
    private static UserManagementApiCaller userManagementApi;
    private static SignInCredentials user;
    private static SignInCredentials admin;

    @BeforeClass
    public static void initialSetup() {
        provider = ClientProvider.valueOf();
        surveyApi= SurveyApiCaller.valueOf(provider);
        userManagementApi = UserManagementApiCaller.valueOf(provider);

        // create admin sign in
        String adminEmail = provider.getConfig().getAdminEmail();
        String adminPassword = provider.getConfig().getAdminPassword();
        admin = SignInCredentials.valueOf().setUsername(adminEmail).setPassword(adminPassword);

        provider.signIn(admin);

        // create user and user sign in
        String userEmail = "testtest@sagebase.org";
        String userPassword = "password";
        String username= "test";
        boolean consent = true;
        SignUpCredentials signUp = SignUpCredentials.valueOf().setUsername(username).setEmail(userEmail).setPassword(userPassword);
        userManagementApi.createUser(signUp, consent);
        user = SignInCredentials.valueOf().setUsername(userEmail).setPassword(userPassword);

        provider.signOut();
    }

    @AfterClass
    public static void after() {
        userManagementApi.deleteUser(user.getUsername());
    }

    @Before
    public void before() {
        List<Survey> surveys = surveyApi.getAllVersionsOfAllSurveys();
        for (Survey survey : surveys) {
            surveyApi.closeSurvey(survey.getGuid(), survey.getVersionedOn());
        }
    }

    @Test
    public void cannotSubmitAsNormalUser() {
    }

}
