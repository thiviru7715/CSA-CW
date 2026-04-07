package com.smartcampus.model;

/**
 * Represents a sensor deployed on the smart campus.
 * 
 * Each sensor has a unique identifier, a type category (e.g., Temperature, CO2),
 * a status (ACTIVE, MAINTENANCE, or OFFLINE), a current measurement value,
 * and a foreign key linking it to the room where it is located.
 */
public class Sensor {

    private String id;              // Unique identifier, e.g., "TEMP-001"
    private String type;            // Category, e.g., "Temperature", "Occupancy", "CO2"
    private String status;          // Current state: "ACTIVE", "MAINTENANCE", or "OFFLINE"
    private double currentValue;    // Most recent measurement recorded
    private String roomId;          // Foreign key linking to the Room

    /** Default no-arg constructor (required for JSON deserialization). */
    public Sensor() {
    }

    /**
     * Full constructor.
     *
     * @param id           unique sensor identifier
     * @param type         sensor category
     * @param status       current operational state
     * @param currentValue most recent measurement
     * @param roomId       ID of the room this sensor is deployed in
     */
    public Sensor(String id, String type, String status, double currentValue, String roomId) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.currentValue = currentValue;
        this.roomId = roomId;
    }

    // ======================== Getters & Setters ========================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", currentValue=" + currentValue +
                ", roomId='" + roomId + '\'' +
                '}';
    }
}
