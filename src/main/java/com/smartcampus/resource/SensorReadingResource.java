package com.smartcampus.resource;

import com.smartcampus.model.SensorReading;
import com.smartcampus.storage.DataStore;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import java.util.Collections;

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

        // update sensor current value
        if (DataStore.sensors.containsKey(sensorId)) {
            DataStore.sensors.get(sensorId).setCurrentValue(reading.getValue());
        }

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SensorReading> getReadings(@PathParam("id") String sensorId) {

        return DataStore.readings.getOrDefault(sensorId, Collections.emptyList());

    }
}