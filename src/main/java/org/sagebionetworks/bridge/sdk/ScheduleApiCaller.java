package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.apache.http.HttpResponse;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;

import com.fasterxml.jackson.databind.JsonNode;

class ScheduleApiCaller extends BaseApiCaller {

    private ScheduleApiCaller(Session session) {
        super(session);
    }

    static ScheduleApiCaller valueOf(Session session) {
        return new ScheduleApiCaller(session);
    }

    List<Schedule> getSchedules() {
        HttpResponse response = get(config.getSchedulesApi());
        JsonNode items = getPropertyFromResponse(response, "items");

        return mapper.convertValue(items,
                mapper.getTypeFactory().constructCollectionType(List.class, Schedule.class));
    }
}
