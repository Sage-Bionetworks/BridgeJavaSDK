package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.apache.http.HttpResponse;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;

import com.fasterxml.jackson.databind.JsonNode;

class ScheduleApiCaller extends BaseApiCaller {

    private ScheduleApiCaller(ClientProvider provider) {
        super(provider);
    }
    
    static ScheduleApiCaller valueOf(ClientProvider provider) {
        return new ScheduleApiCaller(provider);
    }
    
    List<Schedule> getSchedules() {
        HttpResponse response = authorizedGet(provider.getConfig().getSchedulesApi());
        
        JsonNode items = getPropertyFromResponse(response, "items");
        
        return mapper.convertValue(items,
                mapper.getTypeFactory().constructCollectionType(List.class, Schedule.class));
    }
    
}
