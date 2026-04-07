package com.smartcampus.model;

/**
 * Represents a single reading event captured by a sensor.
 * 
 * Each reading has a unique ID (UUID recommended), a timestamp
 * in epoch milliseconds, and the actual metric value recorded
 * by the hardware.
 */
public class SensorReading {

    private String id;          // Unique reading event ID (UUID recommended)
    private long timestamp;     // Epoch time (ms) when the reading was captured
    private double value;       // The actual metric value recorded by the hardware

    /** Default no-arg constructor (required for JSON deserialization). */
    public SensorReading() {
    }

    /**
     * Full constructor.
     *
     * @param id        unique reading identifier (UUID)
     * @param timestamp epoch milliseconds when reading was captured
     * @param value     the recorded metric value
     */
    public SensorReading(String id, long timestamp, double value) {
        this.id = id;
        this.timestamp = timestamp;
        this.value = value;
    }

    // ======================== Getters & Setters ========================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SensorReading{" +
                "id='" + id + '\'' +
                ", timestamp=" + timestamp +
                ", value=" + value +
                '}';
    }
}
