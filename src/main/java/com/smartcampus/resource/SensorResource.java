package com.smartcampus.resource;

import com.smartcampus.model.Sensor;
import com.smartcampus.storage.DataStore;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Room;

/**
 * REST controller for managing Sensor resources.
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final DataStore dataStore = DataStore.getInstance();

    /**
     * Retrieve all sensors or filter by type.
     */
    @GET
    public Response getAllSensors(@QueryParam("type") String type) {
        List<Sensor> sensorList = new ArrayList<>(dataStore.getSensors().values());
        if (type != null && !type.trim().isEmpty()) {
            sensorList = sensorList.stream()
                    .filter(s -> type.equalsIgnoreCase(s.getType()))
                    .collect(Collectors.toList());
        }
        return Response.ok(sensorList).build();
    }

    /**
     * Retrieve a specific sensor by its ID.
     */
    @GET
    @Path("/{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = dataStore.getSensor(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Sensor not found\"}")
                    .build();
        }
        return Response.ok(sensor).build();
    }

    /**
     * Create a new sensor.
     */
    @POST
    public Response createSensor(Sensor sensor) {
        if (sensor == null || sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Invalid sensor data: ID is required\"}")
                    .build();
        }

        if (dataStore.getSensor(sensor.getId()) != null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Sensor with this ID already exists\"}")
                    .build();
        }

        if (sensor.getRoomId() == null || sensor.getRoomId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Invalid sensor data: roomId is required\"}")
                    .build();
        }

        Room room = dataStore.getRoom(sensor.getRoomId());
        if (room == null) {
            throw new LinkedResourceNotFoundException(
                "Cannot create sensor: Room with ID " + sensor.getRoomId() + " does not exist."
            );
        }

        dataStore.addSensor(sensor);
        
        // Add sensor ID to the Room's sensorIds list
        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<>());
        }
        room.addSensorId(sensor.getId());
        
        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

}
