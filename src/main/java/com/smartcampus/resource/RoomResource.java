package com.smartcampus.resource;

import com.smartcampus.model.Room;

import com.smartcampus.storage.DataStore;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.DELETE;

import java.util.ArrayList;
import java.util.List;

@Path("/rooms")
public class RoomResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Room> getRooms() {

        return new ArrayList<>(DataStore.rooms.values());

    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room) {

        DataStore.rooms.put(room.getId(), room);

        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomById(@PathParam("id") String id) {

        Room room = DataStore.rooms.get(id);

        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(room).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteRoom(@PathParam("id") String id) {

        if (!DataStore.rooms.containsKey(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        DataStore.rooms.remove(id);

        return Response.ok().build();
    }
}