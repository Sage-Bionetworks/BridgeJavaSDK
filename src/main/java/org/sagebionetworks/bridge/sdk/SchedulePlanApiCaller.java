package org.sagebionetworks.bridge.sdk;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.sagebionetworks.bridge.sdk.models.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

class SchedulePlanApiCaller extends BaseApiCaller {

    private SchedulePlanApiCaller(ClientProvider provider) {
        super(provider);
    }
    
    static SchedulePlanApiCaller valueOf(ClientProvider provider) {
        return new SchedulePlanApiCaller(provider);
    }
    
    List<SchedulePlan> getSchedulePlans() {
        HttpResponse response = get(provider.getConfig().getSchedulePlansApi());
        
        JsonNode items = getPropertyFromResponse(response, "items");
        
        return mapper.convertValue(items,
                mapper.getTypeFactory().constructCollectionType(List.class, SchedulePlan.class));
    }
    
    GuidVersionHolder createSchedulePlan(SchedulePlan plan) {
        try {
            HttpResponse response = post(provider.getConfig().getSchedulePlansApi(),
                    mapper.writeValueAsString(plan));
            
            String body = EntityUtils.toString(response.getEntity(), "UTF-8");
            return mapper.readValue(body, GuidVersionHolder.class);
            
        } catch(JsonProcessingException e) {
            throw new BridgeSDKException(e.getMessage(), e);
        } catch (IOException e) {
            throw new BridgeSDKException(e.getMessage(), e);
        }
    }
    
    SchedulePlan getSchedulePlan(String guid) {
        try {
            HttpResponse response = get(provider.getConfig().getSchedulePlanApi(guid));
            String body = EntityUtils.toString(response.getEntity(), "UTF-8");
            return mapper.readValue(body, SchedulePlan.class);
        } catch(JsonProcessingException e) {
            throw new BridgeSDKException(e.getMessage(), e);
        } catch (IOException e) {
            throw new BridgeSDKException(e.getMessage(), e);
        }
    }
    
    GuidVersionHolder updateSchedulePlan(SchedulePlan plan) {
        try {
            HttpResponse response = post(provider.getConfig().getSchedulePlanApi(plan.getGuid()),
                    mapper.writeValueAsString(plan));
            String body = EntityUtils.toString(response.getEntity(), "UTF-8");
            return mapper.readValue(body, GuidVersionHolder.class);
        } catch(JsonProcessingException e) {
            throw new BridgeSDKException(e.getMessage(), e);
        } catch (IOException e) {
            throw new BridgeSDKException(e.getMessage(), e);
        }
    }
    
    void deleteSchedulePlan(String guid) {
        delete(provider.getConfig().getSchedulePlanApi(guid));
    }
    
}
