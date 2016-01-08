package org.sagebionetworks.bridge.sdk.models.schedules;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Schedule plans contain a strategy object that describes an algorithm for assigning 
 * participants to schedules. The implementing classes of this interface take different
 * approaches to this assignment and construction of a schedule plan requires selecting 
 * one of these strategies.
 *
 */
@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @Type(name="SimpleScheduleStrategy", value=SimpleScheduleStrategy.class),
    @Type(name="ABTestScheduleStrategy", value=ABTestScheduleStrategy.class),
    @Type(name="CriteriaScheduleStrategy", value=CriteriaScheduleStrategy.class)
})
public interface ScheduleStrategy {

    // There's no implementation of this strategy on the client, only sub-classes with 
    // data models that support the server-side implementations.
    
}
