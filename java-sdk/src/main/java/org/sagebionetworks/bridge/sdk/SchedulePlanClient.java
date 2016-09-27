package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleGuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;

import com.fasterxml.jackson.core.type.TypeReference;

public class SchedulePlanClient extends BaseApiCaller {
    
    private final TypeReference<ResourceList<SchedulePlan>> SCHEDULEPLAN_LIST_TYPE = new TypeReference<ResourceList<SchedulePlan>>() {};

    SchedulePlanClient(BridgeSession session) {
        super(session);
    }
    
    /**
     * Get all schedule plans.
     *
     * @return
     *      a list of all schedule plans in the study
     */
    public ResourceList<SchedulePlan> getSchedulePlans() {
        session.checkSignedIn();
        return get(config.getSchedulePlansApi(), SCHEDULEPLAN_LIST_TYPE);
    }
    
    /**
     * Create a schedule plan.
     *
     * @param plan
     *      The plan object Bridge will use to create a SchedulePlan.
     * @return
     *      a holder containing the the guid and version of the created SchedulePlan.
     */
    public GuidVersionHolder createSchedulePlan(SchedulePlan plan) {
        session.checkSignedIn();
        checkNotNull(plan, CANNOT_BE_NULL, "SchedulePlan");
        
        GuidVersionHolder holder = post(config.getSchedulePlansApi(), plan, SimpleGuidVersionHolder.class);
        plan.setGuid(holder.getGuid());
        plan.setVersion(holder.getVersion());
        return holder;
    }
    
    /**
     * Get a schedule plan.
     *
     * @param guid
     *      GUID identifying the schedule plan to retrieve.
     * @return
     *      The schedule plan with the given GUID
     */
    public SchedulePlan getSchedulePlan(String guid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), CANNOT_BE_BLANK, "guid");
        return get(config.getSchedulePlanApi(guid), SchedulePlan.class);
    }
    
    /**
     * Update a schedule plan.
     *
     * @param plan
     *      The plan object Bridge will use to update it's Schedule Plan.
     * @return
     *      holder object containing the GUID and version of the updated schedule plan
     */
    public GuidVersionHolder updateSchedulePlan(SchedulePlan plan) {
        session.checkSignedIn();
        checkNotNull(plan, CANNOT_BE_NULL, "SchedulePlan");
        GuidVersionHolder holder = post(config.getSchedulePlanApi(plan.getGuid()), plan, SimpleGuidVersionHolder.class);
        plan.setVersion(holder.getVersion());
        return holder;
    }
    
    /**
     * Delete a schedule plan.
     *
     * @param guid
     *      GUID identifying the schedule plan to delete.
     */
    public void deleteSchedulePlan(String guid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), CANNOT_BE_BLANK, "guid");
        delete(config.getSchedulePlanApi(guid));
    }
}
