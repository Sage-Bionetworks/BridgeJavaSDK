package org.sagebionetworks.bridge.scripts;

import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.Config;
import org.sagebionetworks.bridge.sdk.ResearcherClient;
import org.sagebionetworks.bridge.sdk.Session;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;
import org.sagebionetworks.bridge.sdk.models.schedules.SimpleScheduleStrategy;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;

public class CreateSurveys {
    public static void main(String[] args) throws Exception {
        Config config = ClientProvider.getConfig();
        config.set(Config.Props.HOST, "https://parkinson.sagebridge.org");
        Session session = ClientProvider.signIn(config.getAdminCredentials());

        ResearcherClient client = session.getResearcherClient();
        ResourceList<SchedulePlan> plans = client.getSchedulePlans();
        
        for (SchedulePlan plan : plans) {
            SimpleScheduleStrategy strategy = (SimpleScheduleStrategy)plan.getStrategy();
            
            GuidCreatedOnVersionHolder keys = ScriptUtils.getSurveyActivityKeys(strategy.getSchedule());
            Survey survey = client.getSurvey(keys);
            if ("Parkinson Enrollment Survey".equals(survey.getName())) {
                // System.out.println(survey);
                updateSurvey(client, plan, survey, new ParkinsonEnrollmentSurvey());
            } else if ("Parkinson Monthly Survey".equals(survey.getName())) {
                // System.out.println(survey);
                updateSurvey(client, plan, survey, new ParkinsonMonthlySurvey());
            }
        }
        /*
        createSurveyAndPlan(client, new ParkinsonDemographicSurvey());
        createSurveyAndPlan(client, new ParkinsonMonthlySurvey());
        createSurveyAndPlan(client, new ParkinsonWeeklySurvey());
        createSurveyAndPlan(client, new BreastcancerEnrollmentSurvey());
        createSurveyAndPlan(client, new BreastcancerMonthlySurvey());
        createSurveyAndPlan(client, new BreastcancerWeeklySurvey());
         */
    }
    
    private static void updateSurvey(ResearcherClient client, SchedulePlan plan, Survey survey, Survey update) {
        GuidCreatedOnVersionHolder keys = client.versionSurvey(survey);
        
        update.setGuidCreatedOnVersionHolder(keys);
        keys = client.updateSurvey(update);
        client.publishSurvey(keys);
        
        SimpleScheduleStrategy strategy = (SimpleScheduleStrategy)plan.getStrategy();
        ScriptUtils.setSurveyActivity(strategy.getSchedule(), keys);
        
        client.updateSchedulePlan(plan);
    }

    /* private static void createSurveyAndPlan(ResearcherClient client, Survey survey) {
        GuidCreatedOnVersionHolder keys = client.createSurvey(survey);
        survey.setGuidCreatedOnVersionHolder(keys);
        client.publishSurvey(keys);

        ScheduleHolder holder = (ScheduleHolder) survey;
        SchedulePlan sp = new SchedulePlan();
        sp.setStrategy(new SimpleScheduleStrategy(holder.getSchedule(survey)));
        client.createSchedulePlan(sp);
    } */
}
