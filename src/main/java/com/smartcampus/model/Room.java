package com.smartcampus.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a physical room on the smart campus.
 * 
 * Each room has a unique identifier, a human-readable name,
 * a maximum occupancy capacity, and a collection of sensor IDs
 * for sensors deployed within it.
 */
public class Room {

    private String id;          // Unique identifier, e.g., "LIB-301"
    private String name;        // Human-readable name, e.g., "Library Quiet Study"
    private int capacity;       // Maximum occupancy for safety regulations
    private List<String> sensorIds = new ArrayList<>();  // IDs of sensors in this room

    /** Default no-arg constructor (required for JSON deserialization). */
    public Room() {
    }

    /**
     * Full constructor.
     *
     * @param id       unique room identifier
     * @param name     human-readable room name
     * @param capacity maximum occupancy
     */
    public Room(String id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.sensorIds = new ArrayList<>();
    }

    // ======================== Getters & Setters ========================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<String> getSensorIds() {
        return sensorIds;
    }

    public void setSensorIds(List<String> sensorIds) {
        this.sensorIds = sensorIds;
    }

    // ======================== Helper Methods ========================

    /**
     * Adds a sensor ID to this room's sensor list.
     *
     * @param sensorId the sensor ID to add
     */
    public void addSensorId(String sensorId) {
        if (!this.sensorIds.contains(sensorId)) {
            this.sensorIds.add(sensorId);
        }
    }

    /**
     * Removes a sensor ID from this room's sensor list.
     *
     * @param sensorId the sensor ID to remove
     */
    public void removeSensorId(String sensorId) {
        this.sensorIds.remove(sensorId);
    }

    @Override
    public String toString() {
        return "Room{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                ", sensorIds=" + sensorIds +
                '}';
    }
}
