package com.smartcampus.resource;

import com.smartcampus.model.Room;

import com.smartcampus.storage.DataStore;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.List;

@Path("/rooms")
public class RoomResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Room> getRooms() {

        return new ArrayList<>(DataStore.rooms.values());

    }
}