package com.smartcampus;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main entry point for the Smart Campus API server.
 * 
 * Uses Grizzly as an embedded HTTP server and Jersey as the JAX-RS implementation.
 * The server starts on http://localhost:8080/ and all API endpoints are available
 * under the /api/v1 path prefix.
 */
public class Main {

    /** Base URI the Grizzly HTTP server will listen on. */
    public static final String BASE_URI = "http://localhost:8080/api/v1/";

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    /**
     * Creates and configures the Grizzly HTTP server with Jersey resources.
     *
     * @return a configured and started HttpServer instance
     */
    public static HttpServer startServer() {
        // Create a ResourceConfig that scans the com.smartcampus package
        // for JAX-RS resources and providers
        final ResourceConfig config = new ResourceConfig()
                .packages("com.smartcampus.resource", "com.smartcampus.mapper", "com.smartcampus.filter");

        // Create and start the Grizzly HTTP server
        return GrizzlyHttpServerFactory.createHttpServer(
                URI.create(BASE_URI), config);
    }

    /**
     * Main method - starts the server and waits for shutdown signal.
     *
     * @param args command line arguments (not used)
     * @throws IOException if server fails to start
     */
    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();

        LOGGER.log(Level.INFO, "======================================================");
        LOGGER.log(Level.INFO, "Smart Campus API started at: {0}", BASE_URI);
        LOGGER.log(Level.INFO, "Discovery Endpoint: GET {0}", BASE_URI);
        LOGGER.log(Level.INFO, "Rooms Endpoint:     GET {0}rooms", BASE_URI);
        LOGGER.log(Level.INFO, "Sensors Endpoint:   GET {0}sensors", BASE_URI);
        LOGGER.log(Level.INFO, "======================================================");
        LOGGER.log(Level.INFO, "Press Enter to stop the server...");

        // Wait for user input to shut down
        System.in.read();

        // Graceful shutdown
        server.shutdownNow();
        LOGGER.log(Level.INFO, "Server stopped.");
    }
}
