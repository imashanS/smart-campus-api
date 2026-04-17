package com.smartcampus.resource;

import com.smartcampus.model.SensorReading;
import com.smartcampus.storage.DataStore;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

@Path("/sensors/{id}/readings")
public class SensorReadingResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addReading(@PathParam("id") String sensorId, SensorReading reading) {

        List<SensorReading> sensorReadings =
                DataStore.readings.getOrDefault(sensorId, new ArrayList<>());

        sensorReadings.add(reading);

        DataStore.readings.put(sensorId, sensorReadings);

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}