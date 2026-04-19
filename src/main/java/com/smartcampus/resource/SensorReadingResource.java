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
import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;

import java.util.ArrayList;
import java.util.List;

@Path("/sensors/{id}/readings")
public class SensorReadingResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addReading(@PathParam("id") String sensorId, SensorReading reading) {

        Sensor sensor = DataStore.sensors.get(sensorId);

        // check if sensor exists
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // maintenance rule
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException("Sensor is under maintenance.");
        }

        List<SensorReading> sensorReadings =
                DataStore.readings.getOrDefault(sensorId, new ArrayList<>());

        sensorReadings.add(reading);
        DataStore.readings.put(sensorId, sensorReadings);

        // update current value
        sensor.setCurrentValue(reading.getValue());

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SensorReading> getReadings(@PathParam("id") String sensorId) {

        return DataStore.readings.getOrDefault(sensorId, Collections.emptyList());

    }
}