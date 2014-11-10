package org.sagebionetworks.bridge.sdk;

import org.apache.http.HttpResponse;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

class ScheduleApiCaller extends BaseApiCaller {

    private ScheduleApiCaller(Session session) {
        super(session);
    }

    static ScheduleApiCaller valueOf(Session session) {
        return new ScheduleApiCaller(session);
    }

    ResourceListImpl<Schedule> getSchedules() {
        HttpResponse response = get(config.getSchedulesApi());

        JsonNode node = getJsonNode(response);
        return mapper.convertValue(node, new TypeReference<ResourceListImpl<Schedule>>() {});
    }
}
