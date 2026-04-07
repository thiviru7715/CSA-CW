package com.smartcampus.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Root "Discovery" endpoint for the Smart Campus API.
 * 
 * Provides API metadata including version information,
 * contact details, and links to primary resource collections.
 * This follows HATEOAS principles for RESTful API discoverability.
 */
@Path("/")
public class DiscoveryResource {

    /**
     * Returns API metadata and navigation links.
     *
     * @return JSON object with API info and resource links
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApiInfo() {
        Map<String, Object> apiInfo = new LinkedHashMap<>();
        apiInfo.put("name", "Smart Campus Sensor & Room Management API");
        apiInfo.put("version", "1.0");
        apiInfo.put("description", "RESTful API for managing rooms, sensors, and sensor readings on a university smart campus.");
        apiInfo.put("contact", "admin@smartcampus.westminster.ac.uk");

        // Resource links (HATEOAS)
        Map<String, String> resources = new LinkedHashMap<>();
        resources.put("rooms", "/api/v1/rooms");
        resources.put("sensors", "/api/v1/sensors");
        apiInfo.put("resources", resources);

        return Response.ok(apiInfo).build();
    }
}
