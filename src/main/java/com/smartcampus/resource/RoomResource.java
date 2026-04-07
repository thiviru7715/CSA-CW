package com.smartcampus.resource;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;
import com.smartcampus.storage.DataStore;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for managing Room resources.
 */
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private final DataStore dataStore = DataStore.getInstance();

    /**
     * Retrieve all rooms.
     */
    @GET
    public Response getAllRooms() {
        List<Room> roomList = new ArrayList<>(dataStore.getRooms().values());
        return Response.ok(roomList).build();
    }

    /**
     * Retrieve a specific room by its ID.
     */
    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = dataStore.getRoom(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Room not found\"}")
                    .build();
        }
        return Response.ok(room).build();
    }

    /**
     * Create a new room.
     */
    @POST
    public Response createRoom(Room room) {
        if (room == null || room.getId() == null || room.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Invalid room data: ID is required\"}")
                    .build();
        }

        if (dataStore.getRoom(room.getId()) != null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Room with this ID already exists\"}")
                    .build();
        }

        // Ensure new room starts with empty sensors list
        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<>());
        }

        dataStore.addRoom(room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    /**
     * Update an existing room.
     */
    @PUT
    @Path("/{roomId}")
    public Response updateRoom(@PathParam("roomId") String roomId, Room updatedRoom) {
        Room existingRoom = dataStore.getRoom(roomId);
        if (existingRoom == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Room not found\"}")
                    .build();
        }

        // Update fields but preserve id and sensorIds
        existingRoom.setName(updatedRoom.getName());
        existingRoom.setCapacity(updatedRoom.getCapacity());
        
        // Use DataStore explicitly (though the object is already mutated above)
        dataStore.addRoom(existingRoom);

        return Response.ok(existingRoom).build();
    }

    /**
     * Delete a room. Block if the room still has sensors assigned.
     */
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = dataStore.getRoom(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Room not found\"}")
                    .build();
        }

        // Safety Logic: block deletion if sensors exist
        if (!room.getSensorIds().isEmpty()) {
            // Throwing exception as per Part 5 constraints. 
            // This will result in a 500 error until the ExceptionMapper is implemented to convert it to 409
            throw new RoomNotEmptyException(
                "Room " + roomId + " cannot be deleted. It still has " + room.getSensorIds().size() + " active sensor(s) assigned."
            );
        }

        dataStore.removeRoom(roomId);
        return Response.noContent().build(); // 204 No Content
    }
}
