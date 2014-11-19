package org.sagebionetworks.bridge.sdk;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.sagebionetworks.bridge.sdk.exceptions.BridgeSDKException;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

class SchedulePlanApiCaller extends BaseApiCaller {

    private SchedulePlanApiCaller(Session session) {
        super(session);
    }

    static SchedulePlanApiCaller valueOf(Session session) {
        return new SchedulePlanApiCaller(session);
    }

    ResourceList<SchedulePlan> getSchedulePlans() {
        HttpResponse response = get(config.getSchedulePlansApi());

        JsonNode node = getJsonNode(response);
        return mapper.convertValue(node, new TypeReference<ResourceListImpl<SchedulePlan>>() {});
    }

    GuidVersionHolder createSchedulePlan(SchedulePlan plan) {
        try {
            HttpResponse response = post(config.getSchedulePlansApi(),
                    mapper.writeValueAsString(plan));

            String body = EntityUtils.toString(response.getEntity(), "UTF-8");
            return mapper.readValue(body, SimpleGuidVersionHolder.class);

        } catch(JsonProcessingException e) {
            throw new BridgeSDKException(e.getMessage(), e);
        } catch (IOException e) {
            throw new BridgeSDKException(e.getMessage(), e);
        }
    }

    SchedulePlan getSchedulePlan(String guid) {
        try {
            HttpResponse response = get(config.getSchedulePlanApi(guid));
            String body = EntityUtils.toString(response.getEntity(), "UTF-8");
            
            System.out.println(body);
            
            return mapper.readValue(body, SchedulePlan.class);
        } catch(JsonProcessingException e) {
            throw new BridgeSDKException(e.getMessage(), e);
        } catch (IOException e) {
            throw new BridgeSDKException(e.getMessage(), e);
        }
    }

    GuidVersionHolder updateSchedulePlan(SchedulePlan plan) {
        try {
            HttpResponse response = post(config.getSchedulePlanApi(plan.getGuid()),
                    mapper.writeValueAsString(plan));
            String body = EntityUtils.toString(response.getEntity(), "UTF-8");
            return mapper.readValue(body, SimpleGuidVersionHolder.class);
        } catch(JsonProcessingException e) {
            throw new BridgeSDKException(e.getMessage(), e);
        } catch (IOException e) {
            throw new BridgeSDKException(e.getMessage(), e);
        }
    }

    void deleteSchedulePlan(String guid) {
        delete(config.getSchedulePlanApi(guid));
    }

}
