package com.smartcampus.storage;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton in-memory data store for the Smart Campus API.
 * 
 * Uses ConcurrentHashMap for thread-safe access since JAX-RS
 * resource classes are instantiated per-request by default,
 * and the servlet container handles multiple concurrent requests.
 * 
 * This class serves as the single source of truth for all data,
 * replacing what would normally be a database layer.
 */
public class DataStore {

    // Singleton instance
    private static DataStore instance;

    // Data collections
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private final Map<String, List<SensorReading>> sensorReadings = new ConcurrentHashMap<>();

    /** Private constructor to enforce singleton pattern. */
    private DataStore() {
        initSampleData();
    }

    /**
     * Returns the singleton instance of the DataStore.
     * Uses double-checked locking for thread safety.
     *
     * @return the DataStore singleton
     */
    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    /**
     * Pre-populates the store with sample rooms and sensors for testing.
     */
    private void initSampleData() {
        // Sample Rooms
        Room room1 = new Room("LIB-301", "Library Quiet Study", 50);
        Room room2 = new Room("ENG-101", "Engineering Lab A", 30);
        Room room3 = new Room("SCI-205", "Science Lecture Hall", 120);

        rooms.put(room1.getId(), room1);
        rooms.put(room2.getId(), room2);
        rooms.put(room3.getId(), room3);

        // Sample Sensors
        Sensor sensor1 = new Sensor("TEMP-001", "Temperature", "ACTIVE", 22.5, "LIB-301");
        Sensor sensor2 = new Sensor("CO2-001",  "CO2",         "ACTIVE", 415.0, "LIB-301");
        Sensor sensor3 = new Sensor("OCC-001",  "Occupancy",   "ACTIVE", 12.0,  "ENG-101");
        Sensor sensor4 = new Sensor("TEMP-002", "Temperature", "MAINTENANCE", 0.0, "ENG-101");
        Sensor sensor5 = new Sensor("LIGHT-001","Lighting",    "ACTIVE", 75.0,  "SCI-205");

        sensors.put(sensor1.getId(), sensor1);
        sensors.put(sensor2.getId(), sensor2);
        sensors.put(sensor3.getId(), sensor3);
        sensors.put(sensor4.getId(), sensor4);
        sensors.put(sensor5.getId(), sensor5);

        // Link sensors to rooms
        room1.addSensorId("TEMP-001");
        room1.addSensorId("CO2-001");
        room2.addSensorId("OCC-001");
        room2.addSensorId("TEMP-002");
        room3.addSensorId("LIGHT-001");

        // Initialize empty reading lists for each sensor
        sensorReadings.put("TEMP-001", new ArrayList<>());
        sensorReadings.put("CO2-001",  new ArrayList<>());
        sensorReadings.put("OCC-001",  new ArrayList<>());
        sensorReadings.put("TEMP-002", new ArrayList<>());
        sensorReadings.put("LIGHT-001",new ArrayList<>());
    }

    // ======================== Room Operations ========================

    public Map<String, Room> getRooms() {
        return rooms;
    }

    public Room getRoom(String id) {
        return rooms.get(id);
    }

    public void addRoom(Room room) {
        rooms.put(room.getId(), room);
    }

    public Room removeRoom(String id) {
        return rooms.remove(id);
    }

    // ======================== Sensor Operations ========================

    public Map<String, Sensor> getSensors() {
        return sensors;
    }

    public Sensor getSensor(String id) {
        return sensors.get(id);
    }

    public void addSensor(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
        // Initialize empty readings list
        sensorReadings.putIfAbsent(sensor.getId(), new ArrayList<>());
    }

    public Sensor removeSensor(String id) {
        sensorReadings.remove(id);
        return sensors.remove(id);
    }

    // ======================== Reading Operations ========================

    public List<SensorReading> getReadings(String sensorId) {
        return sensorReadings.getOrDefault(sensorId, new ArrayList<>());
    }

    public void addReading(String sensorId, SensorReading reading) {
        sensorReadings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);
    }
}
