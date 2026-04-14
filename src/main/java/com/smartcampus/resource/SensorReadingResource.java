package com.smartcampus.resource;

import com.smartcampus.storage.DataStore;
import com.smartcampus.model.SensorReading;
import com.smartcampus.exception.SensorUnavailableException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

/**
 * Sub-resource for handling sensor readings.
 * Note: Not annotated with @Path at the class level because it's a sub-resource
 * accessed via SensorResource's locator method.
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;
    private final DataStore dataStore = DataStore.getInstance();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public javax.ws.rs.core.Response getReadings() {
        return javax.ws.rs.core.Response.ok(dataStore.getReadings(sensorId)).build();
    }

}
