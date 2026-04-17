package com.smartcampus.storage;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;

import java.util.HashMap;
import java.util.Map;
import com.smartcampus.model.SensorReading;
import java.util.List;

public class DataStore {

    public static Map<String, Room> rooms = new HashMap<>();
    public static Map<String, Sensor> sensors = new HashMap<>();
    public static Map<String, List<SensorReading>> readings = new HashMap<>();
}