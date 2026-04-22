package com.smartcampus;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public static final String BASE_URI = "http://localhost:8080/api/v1/";
    private static final Logger LOGGER = Logger.getLogger(SmartCampusApplication.class.getName());

    public static HttpServer startServer() {
        final ResourceConfig config = new ResourceConfig()
                .packages("com.smartcampus.resource", "com.smartcampus.mapper", "com.smartcampus.filter");
        return GrizzlyHttpServerFactory.createHttpServer(
                URI.create(BASE_URI), config);
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        LOGGER.log(Level.INFO, "======================================================");
        LOGGER.log(Level.INFO, "Smart Campus API started at: {0}", BASE_URI);
        LOGGER.log(Level.INFO, "Discovery Endpoint: GET {0}", BASE_URI);
        LOGGER.log(Level.INFO, "Rooms Endpoint:     GET {0}rooms", BASE_URI);
        LOGGER.log(Level.INFO, "Sensors Endpoint:   GET {0}sensors", BASE_URI);
        LOGGER.log(Level.INFO, "======================================================");
        LOGGER.log(Level.INFO, "Press Enter to stop the server...");
        System.in.read();
        server.shutdownNow();
        LOGGER.log(Level.INFO, "Server stopped.");
    }
}
