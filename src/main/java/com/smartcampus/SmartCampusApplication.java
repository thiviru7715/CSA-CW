package com.smartcampus;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

import com.smartcampus.resource.DiscoveryResource;
import com.smartcampus.resource.RoomResource;
import com.smartcampus.resource.SensorResource;

import com.smartcampus.mapper.RoomNotEmptyExceptionMapper;
import com.smartcampus.mapper.LinkedResourceNotFoundMapper;
import com.smartcampus.mapper.SensorUnavailableMapper;
import com.smartcampus.mapper.GenericExceptionMapper;
import com.smartcampus.filter.LoggingFilter;

/**
 * JAX-RS Application configuration class.
 * 
 * Sets the base URI path for all REST resources to "/api/v1".
 * All resource classes must be registered here or discovered
 * through package scanning.
 */
@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        // Resource classes
        classes.add(DiscoveryResource.class);
        classes.add(RoomResource.class);
        classes.add(SensorResource.class);

        // Exception mappers
        classes.add(RoomNotEmptyExceptionMapper.class);
        classes.add(LinkedResourceNotFoundMapper.class);
        classes.add(SensorUnavailableMapper.class);
        classes.add(GenericExceptionMapper.class);

        // Filters
        classes.add(LoggingFilter.class);

        return classes;
    }
}
