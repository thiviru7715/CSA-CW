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

    @POST
    public javax.ws.rs.core.Response addReading(SensorReading reading) {
        com.smartcampus.model.Sensor sensor = dataStore.getSensor(sensorId);
        
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException("Sensor " + sensorId + " is undergoing maintenance and cannot accept readings.");
        }
        
        reading.setId(UUID.randomUUID().toString());
        reading.setTimestamp(System.currentTimeMillis());
        
        dataStore.addReading(sensorId, reading);
        
        // Side effect: update sensor's current value
        sensor.setCurrentValue(reading.getValue());
        dataStore.addSensor(sensor);
        
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.CREATED).entity(reading).build();
    }

}
