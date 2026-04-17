# 🏫 Smart Campus — Sensor & Room Management API

A RESTful API for managing rooms, sensors, and sensor readings on a university smart campus, built with **JAX-RS (Jersey)** and an embedded **Grizzly HTTP server**.

> **Module:** 5COSC022W — Client-Server Architectures  
> **Weight:** 60% of final grade

---

## 📁 Project Structure

```
smart-campus-api/
├── pom.xml
├── README.md
└── src/main/java/com/smartcampus/
    ├── Main.java                          ← Server entry point (Grizzly)
    ├── SmartCampusApplication.java         ← @ApplicationPath("/api/v1")
    ├── model/
    │   ├── Room.java                      ← Room POJO
    │   ├── Sensor.java                    ← Sensor POJO
    │   └── SensorReading.java             ← SensorReading POJO
    ├── storage/
    │   └── DataStore.java                 ← Singleton in-memory store (ConcurrentHashMap)
    ├── resource/
    │   ├── DiscoveryResource.java          ← GET /api/v1
    │   ├── RoomResource.java              ← /api/v1/rooms
    │   ├── SensorResource.java            ← /api/v1/sensors
    │   └── SensorReadingResource.java     ← Sub-resource (readings)
    ├── exception/
    │   ├── RoomNotEmptyException.java
    │   ├── LinkedResourceNotFoundException.java
    │   └── SensorUnavailableException.java
    ├── mapper/
    │   ├── RoomNotEmptyExceptionMapper.java    ← 409 Conflict
    │   ├── LinkedResourceNotFoundMapper.java   ← 422 Unprocessable Entity
    │   ├── SensorUnavailableMapper.java        ← 403 Forbidden
    │   └── GenericExceptionMapper.java         ← 500 Internal Server Error
    └── filter/
        └── LoggingFilter.java                 ← Request/Response logging
```

---

## 🚀 How to Build & Run

### Prerequisites
- **Java 11** or later
- **Maven 3.6+** (or use the included Maven Wrapper)

### Step 1: Clone the Repository

```bash
git clone https://github.com/thiviru7715/CSA-CW.git
cd CSA-CW
```

### Step 2: Compile the Project

```bash
./mvnw compile
```

On Windows:
```bash
.\mvnw.cmd compile
```

### Step 3: Start the Server

```bash
./mvnw compile exec:java
```

On Windows:
```bash
.\mvnw.cmd compile exec:java
```

### Step 4: Verify

Once you see the following message, the server is ready:

```
INFO: Smart Campus API started at: http://localhost:8080/api/v1/
```

The API is now available at **http://localhost:8080/api/v1/**

Press **Enter** in the terminal to stop the server.

---

## 📡 API Endpoints

| Method | Path | Description | Success | Error |
|--------|------|-------------|---------|-------|
| `GET` | `/api/v1/` | Discovery endpoint | 200 | — |
| `GET` | `/api/v1/rooms` | List all rooms | 200 | — |
| `POST` | `/api/v1/rooms` | Create a new room | 201 | 400 |
| `GET` | `/api/v1/rooms/{roomId}` | Get room by ID | 200 | 404 |
| `PUT` | `/api/v1/rooms/{roomId}` | Update a room | 200 | 404 |
| `DELETE` | `/api/v1/rooms/{roomId}` | Delete a room | 204 | 404, 409 |
| `GET` | `/api/v1/sensors` | List all sensors | 200 | — |
| `GET` | `/api/v1/sensors?type=X` | Filter sensors by type | 200 | — |
| `POST` | `/api/v1/sensors` | Create a new sensor | 201 | 400, 422 |
| `GET` | `/api/v1/sensors/{sensorId}` | Get sensor by ID | 200 | 404 |
| `PUT` | `/api/v1/sensors/{sensorId}` | Update a sensor | 200 | 404 |
| `DELETE` | `/api/v1/sensors/{sensorId}` | Delete a sensor | 204 | 404 |
| `GET` | `/api/v1/sensors/{sensorId}/readings` | Get reading history | 200 | 404 |
| `POST` | `/api/v1/sensors/{sensorId}/readings` | Add a new reading | 201 | 403, 404 |

---

## 🧪 Sample curl Commands

### 1. Discovery Endpoint

```bash
curl -X GET http://localhost:8080/api/v1/
```

**Response (200 OK):**
```json
{
  "name": "Smart Campus Sensor & Room Management API",
  "version": "1.0",
  "description": "RESTful API for managing rooms, sensors, and sensor readings on a university smart campus.",
  "contact": "admin@smartcampus.westminster.ac.uk",
  "resources": {
    "rooms": "/api/v1/rooms",
    "sensors": "/api/v1/sensors"
  }
}
```

### 2. Create a Room

```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"LIB-301","name":"Library Quiet Study","capacity":50}'
```

**Response (201 Created):**
```json
{
  "id": "LIB-301",
  "name": "Library Quiet Study",
  "capacity": 50,
  "sensorIds": []
}
```

### 3. Create a Sensor (Linked to Room)

```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"TEMP-001","type":"Temperature","status":"ACTIVE","currentValue":0.0,"roomId":"LIB-301"}'
```

**Response (201 Created):**
```json
{
  "id": "TEMP-001",
  "type": "Temperature",
  "status": "ACTIVE",
  "currentValue": 0.0,
  "roomId": "LIB-301"
}
```

### 4. Filter Sensors by Type

```bash
curl -X GET "http://localhost:8080/api/v1/sensors?type=Temperature"
```

**Response (200 OK):**
```json
[
  {
    "id": "TEMP-001",
    "type": "Temperature",
    "status": "ACTIVE",
    "currentValue": 22.5,
    "roomId": "LIB-301"
  },
  {
    "id": "TEMP-002",
    "type": "Temperature",
    "status": "MAINTENANCE",
    "currentValue": 0.0,
    "roomId": "ENG-101"
  }
]
```

### 5. Post a Sensor Reading (Sub-Resource + Side Effect)

```bash
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":23.5}'
```

**Response (201 Created):**
```json
{
  "id": "114d4c6c-2b50-4bea-937a-677f55faf9e3",
  "timestamp": 1776391367981,
  "value": 23.5
}
```

> **Side Effect:** The parent sensor's `currentValue` is automatically updated to `23.5`.

### 6. Delete a Room with Active Sensors (Error — 409 Conflict)

```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301
```

**Response (409 Conflict):**
```json
{
  "error": "CONFLICT",
  "code": 409,
  "message": "Room LIB-301 cannot be deleted. It still has 2 active sensor(s) assigned.",
  "timestamp": 1776391240799
}
```

### 7. Create Sensor with Invalid Room (Error — 422)

```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"BAD-001","type":"Pressure","status":"ACTIVE","currentValue":0.0,"roomId":"FAKE-999"}'
```

**Response (422 Unprocessable Entity):**
```json
{
  "error": "UNPROCESSABLE_ENTITY",
  "code": 422,
  "message": "Cannot create sensor: Room with ID FAKE-999 does not exist.",
  "timestamp": 1776391351061
}
```

### 8. Post Reading to Maintenance Sensor (Error — 403)

```bash
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-002/readings \
  -H "Content-Type: application/json" \
  -d '{"value":10.0}'
```

**Response (403 Forbidden):**
```json
{
  "error": "FORBIDDEN",
  "code": 403,
  "message": "Sensor TEMP-002 is undergoing maintenance and cannot accept readings.",
  "timestamp": 1776391372424
}
```

---

## 📝 Report — Answers to Coursework Questions

---

### Part 1 — Q1: JAX-RS Resource Class Lifecycle

> _Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton?_

By default, JAX-RS creates a **new instance** of a resource class for every incoming HTTP request (per-request lifecycle). This means each request gets its own object, and instance variables are not shared between requests. This design has a direct impact on in-memory data management: if you store data as instance fields of a resource class, the data would be lost after each request completes.

To persist data across requests, we use an **external shared data store** — in our case, a singleton `DataStore` class backed by `ConcurrentHashMap`. The `DataStore` class uses the singleton pattern with double-checked locking (`DataStore.getInstance()`) to ensure only one instance exists globally. We chose `ConcurrentHashMap` instead of regular `HashMap` because the servlet container processes multiple requests concurrently on different threads. Without proper synchronisation, concurrent read/write operations on a regular `HashMap` could lead to race conditions, lost updates, and data corruption. `ConcurrentHashMap` provides fine-grained locking at the segment level, allowing multiple threads to read and write safely without the performance penalty of a fully synchronised `HashMap`.

---

### Part 1 — Q2: HATEOAS & Hypermedia

> _Why is the provision of "Hypermedia" considered a hallmark of advanced RESTful design?_

Hypermedia as the Engine of Application State (HATEOAS) is the highest maturity level of REST (Richardson Maturity Model Level 3). It benefits client developers by embedding navigational links directly within API responses, enabling clients to dynamically discover available actions without hardcoding URL paths. Unlike static documentation that can become outdated, hypermedia links are always current because they are generated by the server at runtime.

This approach decouples clients from specific URL structures — if the server changes its URL scheme, clients that follow embedded links automatically adapt without needing code changes. It also improves API discoverability: a client can start at the root endpoint (`GET /api/v1`) and navigate the entire API purely through links, similar to how humans browse the web through hyperlinks. In our implementation, the discovery endpoint at `GET /api/v1` returns a `resources` map containing links to `/api/v1/rooms` and `/api/v1/sensors`, which allows clients to discover all available resource collections without prior knowledge of the URL structure.

---

### Part 2 — Q1: Full Objects vs IDs in Lists

> _What are the implications of returning only IDs versus returning the full room objects?_

Returning only IDs reduces **network bandwidth** significantly — especially for large collections — because each item is a small string rather than a full JSON object. However, it forces the client to make **N additional requests** (one per ID) to fetch complete details, creating the "N+1 query problem." This dramatically increases latency and client-side complexity, as the client must manage multiple asynchronous requests and reassemble the data.

Returning full objects provides all data in one round trip, improving client-side performance and simplifying the code. The trade-off is higher initial bandwidth usage. In practice, returning full objects is preferred for small-to-medium collections where the total payload size is manageable. For very large datasets, a hybrid approach is recommended: use **pagination** (e.g., `?page=1&size=20`) to limit the number of objects returned per request, or allow clients to specify which fields they need using **partial representations** (e.g., `?fields=id,name`). In our implementation, we return full room objects because our in-memory dataset is small enough that bandwidth is not a concern, and it provides a significantly better developer experience.

---

### Part 2 — Q2: DELETE Idempotency

> _Is the DELETE operation idempotent in your implementation?_

Yes, DELETE is **idempotent** in our implementation. The first successful DELETE request removes the room and returns `204 No Content`. Subsequent identical DELETE requests for the same room ID will return `404 Not Found` because the room no longer exists in the `DataStore`. While the response status code differs between the first call (204) and subsequent calls (404), the **server-side state** remains unchanged after the first deletion — the room is still absent from the data store.

Idempotency means that applying the same operation multiple times has the same effect on the resource state as applying it once. This is a critical property in distributed systems where network failures (e.g., timeouts, dropped connections) may cause clients to retry requests. Because DELETE is idempotent, the server won't accidentally delete a different resource or cause unintended errors if a client retries the same request. The HTTP specification (RFC 7231) explicitly requires DELETE to be idempotent, and our implementation honours this by simply returning 404 for already-deleted resources rather than throwing an error.

---

### Part 3 — Q1: @Consumes Mismatch

> _What happens if a client sends data in a different format, such as text/plain?_

When a resource method is annotated with `@Consumes(MediaType.APPLICATION_JSON)`, JAX-RS will automatically reject any request that has a `Content-Type` header that does not match `application/json`. The framework returns an **HTTP 415 Unsupported Media Type** status code without even invoking the resource method body. This applies regardless of whether the client sends `text/plain`, `application/xml`, or any other non-JSON content type.

This is part of JAX-RS's built-in **content negotiation** mechanism. The runtime inspects the `Content-Type` header of every incoming request and matches it against the `@Consumes` annotations on available resource methods. If no method can consume the provided media type, the 415 error is returned immediately. This saves developers from manually checking content types in every method and provides a standardised, predictable behaviour that clients can rely on. It also enforces a clear API contract — clients know they must always send `Content-Type: application/json` for POST and PUT requests, and any deviation is caught at the framework level before business logic executes.

---

### Part 3 — Q2: @QueryParam vs Path Segment for Filtering

> _Why is the query parameter approach generally considered superior for filtering?_

Query parameters (`?type=CO2`) are better for filtering because they represent **optional, non-hierarchical criteria** on a resource collection. The URL path `/api/v1/sensors` identifies the collection resource itself, while query parameters refine the *view* of that collection. Path segments (`/sensors/type/CO2`) would imply a hierarchical parent-child resource relationship, suggesting that "type" is a sub-resource of "sensors," which is semantically incorrect — the type is a property of a sensor, not a separate resource.

Additionally, query parameters are easily **composable** — you can combine multiple filters by adding parameters (e.g., `?type=CO2&status=ACTIVE`) without changing the URL structure. Path-based filtering would create an explosion of route combinations for every filterable field. Query parameters also allow the server to return the full, unfiltered collection when no parameters are provided, making them inherently optional. The `@QueryParam` annotation in JAX-RS naturally supports this: if the parameter is absent, the injected value is `null`, and we can gracefully fall back to returning all sensors. This approach follows the RESTful convention where the URL path identifies *what* resource you're accessing, while query parameters specify *how* you want it filtered or formatted.

---

### Part 4 — Q1: Sub-Resource Locator Pattern Benefits

> _Discuss the architectural benefits of the Sub-Resource Locator pattern._

The Sub-Resource Locator pattern promotes **separation of concerns** and **modularity** by delegating nested resource logic to dedicated classes. Instead of defining all paths like `/sensors/{id}/readings`, `/sensors/{id}/readings/{rid}` in one massive `SensorResource` class, readings-related logic is encapsulated in a separate `SensorReadingResource` class. The locator method in `SensorResource` simply validates the parent sensor exists and returns a new instance of `SensorReadingResource`:

```java
@Path("{sensorId}/readings")
public SensorReadingResource getReadings(@PathParam("sensorId") String sensorId) {
    // validate sensor exists
    return new SensorReadingResource(sensorId);
}
```

This approach improves code **maintainability** — each class handles a single domain, making it easier to understand and modify. It also enables **reuse**: the same sub-resource class could theoretically be mounted at different paths if needed. The pattern reduces class complexity, making **unit testing** easier since each class has focused responsibilities. In large APIs with deeply nested resources (e.g., `/buildings/{id}/floors/{id}/rooms/{id}/sensors`), this pattern prevents "god classes" that would become unmanageable. It also provides a natural injection point for the parent resource context — the `SensorReadingResource` constructor receives the `sensorId`, so all methods within it automatically operate within that sensor's scope.

---

### Part 5 — Q1: HTTP 422 vs 404

> _Why is HTTP 422 more semantically accurate than 404 when the issue is a missing reference inside a valid JSON payload?_

HTTP 404 means the **requested resource** (the URL endpoint) was not found — the URI `/api/v1/sensors` itself doesn't exist. However, when a client POSTs a sensor with a non-existent `roomId`, the URL `/api/v1/sensors` absolutely exists and is valid. The problem is not with the URL but with the **data within the request body**.

HTTP 422 Unprocessable Entity accurately communicates: "Your request was syntactically correct (valid JSON), but the server cannot process it due to **semantic errors** in the content." Specifically, the referenced `roomId` points to a room that doesn't exist, which is a business logic validation failure, not a missing endpoint. This distinction is critical for client developers — a 404 would lead them to check their URL construction, wasting debugging time. A 422 immediately tells them to examine their request payload for invalid references or data. In our implementation, when a sensor references `roomId: "FAKE-999"`, we throw a `LinkedResourceNotFoundException` which is mapped to 422, clearly indicating that the request body contained a reference to a non-existent linked resource.

---

### Part 5 — Q2: Security Risks of Stack Traces

> _Explain the risks of exposing internal Java stack traces to external API consumers._

Exposing stack traces is a significant **security vulnerability** classified as "Information Disclosure" by OWASP. An attacker can extract several categories of sensitive information from a Java stack trace:

1. **Framework and library versions** from package names (e.g., `org.glassfish.jersey.2.41`), enabling them to search for known CVEs (Common Vulnerabilities and Exposures) targeting those specific versions.
2. **Internal class and package structure**, revealing the application's architecture, package naming conventions, and how the code is organised.
3. **File paths and server configuration** details, potentially exposing the operating system, deployment structure, and server environment.
4. **Database connection strings or query structures** if the error originates from data access code, which could facilitate SQL injection attacks.
5. **Business logic flow** from the method call stack sequence, revealing how the application processes requests internally.

This information enables targeted attacks such as SQL injection, path traversal, remote code execution exploiting known framework vulnerabilities, or social engineering. Our `GenericExceptionMapper` prevents this by catching all unhandled exceptions and returning a generic JSON error message (`"An unexpected error occurred"`) to the client, while logging the full stack trace server-side for debugging. This follows the security principle of **minimal information disclosure**.

---

### Part 5 — Q3: Why JAX-RS Filters for Logging

> _Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging?_

JAX-RS filters implement the **cross-cutting concerns** pattern (similar to aspect-oriented programming), applying logic uniformly to all requests and responses without modifying individual resource methods. This follows the **Single Responsibility Principle** — resource methods focus purely on business logic, while the `LoggingFilter` handles the orthogonal concern of logging as a completely separate class.

Using filters ensures **consistency**: every endpoint is automatically logged without risking human error of forgetting a `Logger.info()` call in a new resource method. If we added logging manually to each method, any new endpoints added in the future could easily be missed, creating gaps in observability. Filters are also **highly maintainable**: changing the logging format, adding request timing, or switching the logging framework requires editing only one class (`LoggingFilter.java`) instead of every resource method across the entire application.

Our `LoggingFilter` implements both `ContainerRequestFilter` (to log the incoming HTTP method and URI) and `ContainerResponseFilter` (to log the outgoing status code). It operates at the container level, intercepting requests before they reach resource methods and responses after they leave, providing clean separation and comprehensive coverage. This is architecturally equivalent to the middleware pattern in frameworks like Express.js and is annotated with `@Provider` for automatic discovery by the JAX-RS runtime.

---

## ⚙️ Technology Stack

| Technology | Purpose |
|------------|---------|
| **Java 11** | Language |
| **JAX-RS 2.1 (Jersey 2.41)** | REST framework |
| **Grizzly 2** | Embedded HTTP server |
| **Jackson** | JSON serialisation/deserialisation |
| **Maven** | Build tool & dependency management |
| **ConcurrentHashMap** | Thread-safe in-memory data store |

---

## 📜 License

This project was developed as coursework for the 5COSC022W Client-Server Architectures module at the University of Westminster.
