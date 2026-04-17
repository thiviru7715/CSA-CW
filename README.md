# Smart Campus — Sensor & Room Management API

A RESTful API for managing rooms, sensors, and sensor readings across a university smart campus, built with **JAX-RS (Jersey)** and an embedded **Grizzly HTTP server**.

> **Module:** 5COSC022W — Client-Server Architectures
> **Weight:** 60% of final grade

---

## Project Structure

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
    │   └── SensorReadingResource.java     ← Sub-resource for readings
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

## How to Build & Run

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

### Step 4: Verify the Server is Running

Once you see the following message in the terminal, the server is ready:

```
INFO: Smart Campus API started at: http://localhost:8080/api/v1/
```

The API is now live at **http://localhost:8080/api/v1/**

Press **Enter** in the terminal to gracefully stop the server.

---

## API Endpoints

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

## Sample curl Commands

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

### 3. Create a Sensor (Linked to a Room)

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

### 6. Delete a Room with Active Sensors (409 Conflict)

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

### 7. Create a Sensor with an Invalid Room (422 Unprocessable Entity)

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

### 8. Post a Reading to a Sensor Under Maintenance (403 Forbidden)

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

## Report — Answers to Coursework Questions

---

### Part 1 — Q1: JAX-RS Resource Class Lifecycle

> *Is a new instance created for every incoming request, or does the runtime treat it as a singleton?*

By default, JAX-RS spins up a **fresh instance** of a resource class for every single HTTP request — this is called the per-request lifecycle. Each request gets its own isolated object, which means instance variables are never shared between concurrent requests. At first glance this sounds wasteful, but it's actually a deliberate design choice: it eliminates an entire class of concurrency bugs where one request accidentally reads or overwrites another's in-progress state.

The implication for in-memory data management is significant. If you store your data as instance fields inside the resource class, that data evaporates the moment the request finishes. To keep data alive between requests, you need to push it somewhere that outlives a single request — in our case, a singleton `DataStore` backed by `ConcurrentHashMap`.

The `DataStore` class uses the singleton pattern with double-checked locking (`DataStore.getInstance()`) to guarantee exactly one shared instance exists for the entire server process. We deliberately chose `ConcurrentHashMap` over a plain `HashMap` because the Grizzly server handles requests on multiple threads simultaneously. A regular `HashMap` is not thread-safe — concurrent reads and writes can corrupt its internal structure, cause lost updates, or produce completely wrong results. `ConcurrentHashMap` solves this with fine-grained bucket-level locking, allowing many threads to read and write in parallel without contention, which is exactly the behaviour needed for a high-throughput campus API.

---

### Part 1 — Q2: HATEOAS & Hypermedia

> *Why is "Hypermedia" considered a hallmark of advanced RESTful design?*

HATEOAS — Hypermedia as the Engine of Application State — sits at the top of the Richardson Maturity Model (Level 3), and for good reason. The core idea is simple but powerful: instead of forcing developers to read external documentation to discover what URLs they can call next, the API embeds those links directly inside every response.

Think of it like the web itself. When you land on a webpage, you don't need a separate guide telling you what links exist — they're right there on the page. A HATEOAS-compliant API works the same way. A client can start at a single known entry point (say, `GET /api/v1`) and navigate the entire API surface by following the links embedded in each response, without any hardcoded knowledge of URL structures.

This decoupling has a tangible developer benefit: if the server-side URL scheme changes — say `/rooms` becomes `/campus-rooms` — clients that navigate via embedded links adapt automatically, with zero code changes on the client side. Static documentation, on the other hand, would be instantly outdated. In our implementation, the discovery endpoint at `GET /api/v1` returns a `resources` map containing links to `/api/v1/rooms` and `/api/v1/sensors`, giving any client an immediate, self-contained map of the API's capabilities from a single request.

---

### Part 2 — Q1: Full Objects vs IDs in Lists

> *What are the implications of returning only IDs versus returning the full room objects?*

Returning only IDs keeps each list item small — just a short string — which significantly reduces network bandwidth, especially when the collection is large. The trade-off is that any client that actually needs room details must then fire off a separate `GET /rooms/{id}` for every ID in the list. With a hundred rooms, that's a hundred round trips — the classic "N+1 problem" — each one adding latency and consuming server resources. The client code also gets more complex because it has to juggle multiple asynchronous requests and stitch the results back together.

Returning full objects eliminates all of that. One request, one response, complete data. The cost is a larger initial payload, but for collections of any reasonable size the developer experience and latency improvement easily outweigh the extra bytes. For genuinely huge datasets, the right answer is neither extreme — instead, use **pagination** (e.g., `?page=1&size=20`) to cap the response size, or support **sparse fieldsets** (e.g., `?fields=id,name`) so clients can request only the properties they care about.

In our implementation we return full room objects. Our dataset is small and in-memory, so bandwidth is not a concern, and the simplified client code is a clear win.

---

### Part 2 — Q2: DELETE Idempotency

> *Is the DELETE operation idempotent in your implementation?*

Yes — DELETE is idempotent, and our implementation honours that contract correctly. The first successful `DELETE /rooms/{id}` removes the room from the `DataStore` and returns `204 No Content`. Any subsequent identical request for the same room ID returns `404 Not Found` because the room is already gone.

The key thing to understand is that idempotency is about **server-side state**, not response codes. After the very first deletion, the server state is: "this room does not exist." Every subsequent DELETE leaves the server in exactly the same state — the room still does not exist. The fact that the status code shifts from `204` to `404` on repeat calls does not break idempotency; the resource's state on the server is unchanged.

This matters enormously in distributed systems. Networks drop packets. Clients time out. Retry logic kicks in. If DELETE were not idempotent, a client retrying after a dropped response might accidentally delete a different resource or trigger unexpected side effects. Because DELETE is idempotent, the server calmly returns 404 on retries with no harm done. The HTTP specification (RFC 7231) explicitly requires this behaviour, and our implementation follows it faithfully.

---

### Part 3 — Q1: @Consumes Mismatch

> *What happens if a client sends data in a different format, such as text/plain?*

When a resource method carries `@Consumes(MediaType.APPLICATION_JSON)`, the JAX-RS runtime intercepts every incoming request and checks its `Content-Type` header *before* the method body ever runs. If the header says anything other than `application/json` — whether that's `text/plain`, `application/xml`, or something else entirely — the framework immediately rejects the request with **HTTP 415 Unsupported Media Type** and returns that error to the client without invoking any of our code.

This is JAX-RS's built-in content negotiation at work. The runtime maintains a registry of which media types each method can consume and performs this match automatically. The practical benefit is that we never need to write manual `Content-Type` checks inside our resource methods — the framework enforces the contract at the boundary, keeping business logic clean. It also gives clients a clear, standardised signal: `415` means "fix your `Content-Type` header," which is unambiguous and easy to act on. This behaviour is part of the formal JAX-RS specification, so every compliant implementation handles it the same way.

---

### Part 3 — Q2: @QueryParam vs Path Segment for Filtering

> *Why is the query parameter approach generally considered superior for filtering?*

The distinction comes down to semantics. In REST, URL path segments identify *resources* — the things you're interacting with. Query parameters describe *how you want to see* a resource or collection. When you write `/api/v1/sensors`, the path identifies the sensors collection. A `?type=CO2` parameter then says "give me a filtered view of that collection." The collection itself hasn't changed; you're just asking for a narrower window into it.

Using a path segment like `/sensors/type/CO2` implies that "type" is a sub-resource — a child entity of sensors — which is semantically wrong. A sensor's type is a property, not a resource in its own right.

Beyond semantics, query parameters are also far more practical to work with. They compose naturally: you can add `?type=CO2&status=ACTIVE` without touching the URL structure, whereas path-based filters would require a new route for every combination. Query parameters are also inherently optional — when absent, the `@QueryParam` injects `null` and our code gracefully returns the full unfiltered list. Path segments don't work that way; you'd need entirely separate routes for filtered versus unfiltered access. Following RESTful convention here makes the API predictable, composable, and easy for any developer to understand immediately.

---

### Part 4 — Q1: Sub-Resource Locator Pattern Benefits

> *Discuss the architectural benefits of the Sub-Resource Locator pattern.*

Without the sub-resource locator pattern, every nested path like `/sensors/{id}/readings` and `/sensors/{id}/readings/{rid}` would pile up inside a single `SensorResource` class. In a real campus system managing dozens of resource types, that class would become a sprawling monolith — hundreds of methods, hard to read, hard to test, and risky to change.

The sub-resource locator solves this by delegating. Our `SensorResource` handles only what it owns: the `/sensors` collection. When a request arrives at `{sensorId}/readings`, the locator method validates that the parent sensor exists, then returns a fresh `SensorReadingResource` instance:

```java
@Path("{sensorId}/readings")
public SensorReadingResource getReadings(@PathParam("sensorId") String sensorId) {
    // validate sensor exists
    return new SensorReadingResource(sensorId);
}
```

The JAX-RS runtime then hands routing responsibility to `SensorReadingResource`, which knows nothing about sensors beyond the ID passed into its constructor. This gives us proper separation of concerns: each class has a single, clearly scoped responsibility. `SensorReadingResource` is independently unit-testable, independently maintainable, and could theoretically be reused under a different parent path with zero changes to its code.

In deeper hierarchies — think `/buildings/{id}/floors/{id}/rooms/{id}/sensors` — this pattern is the only practical way to keep the codebase manageable. Each level of nesting becomes its own focused class, and the codebase grows linearly rather than exponentially.

---

### Part 5 — Q1: HTTP 422 vs 404

> *Why is HTTP 422 more semantically accurate than 404 when the issue is a missing reference inside a valid JSON payload?*

HTTP 404 has a specific, well-established meaning: the **URL you requested does not exist**. When a client sends `POST /api/v1/sensors`, that URL very much exists — the server found the endpoint and started processing the request. The problem has nothing to do with the URL.

The actual problem is inside the request body: the client included a `roomId` that doesn't correspond to any room in the system. The request is syntactically valid JSON, but it's semantically broken — it references something that isn't there. HTTP 422 Unprocessable Entity is designed precisely for this situation: "I understood your request, the format was fine, but I can't act on the content because of a business logic problem."

From a practical debugging standpoint, this distinction matters a lot. A 404 response would send a developer hunting for typos in their URL. A 422 immediately tells them to look at the request body for invalid data. In our case, the `LinkedResourceNotFoundException` is mapped to 422 with a clear error message naming the missing `roomId`, so the developer knows exactly what to fix and where to look. That's what well-designed error responses are for.

---

### Part 5 — Q2: Security Risks of Stack Traces

> *What are the security risks of exposing internal Java stack traces to external API consumers?*

Exposing a raw Java stack trace to an external client is a serious information disclosure vulnerability — it's listed explicitly in the OWASP Top 10. Here's what an attacker can harvest from a typical stack trace:

**Framework and library versions** are visible in package names (e.g., `org.glassfish.jersey.server.2.41`). An attacker can cross-reference these against public CVE databases to find unpatched vulnerabilities in those exact versions and craft targeted exploits.

**Internal package and class structure** reveals how the application is organised — which classes exist, what they're responsible for, and often what design patterns were used. This is a roadmap for an attacker trying to understand how to manipulate the system.

**File system paths** sometimes appear in stack traces (particularly in I/O errors), exposing the server's directory structure and deployment layout.

**Business logic flow** is readable from the method call sequence — an attacker can trace exactly which code path executed and where it broke, giving them insight into how to craft requests that reach dangerous code paths.

**Database query fragments** occasionally appear in data-access exceptions, potentially exposing table names, column structures, or raw queries that assist SQL injection attempts.

Our `GenericExceptionMapper` addresses all of this. It catches every unhandled `Throwable`, logs the full stack trace server-side where it's useful for our engineers, and returns a clean, generic JSON response to the client — `"An unexpected error occurred"` — with absolutely no internal details. This follows the security principle of minimal information disclosure: give clients only what they need to understand that an error occurred, nothing more.

---

### Part 5 — Q3: Why JAX-RS Filters for Logging

> *Why is it better to use JAX-RS filters for cross-cutting concerns like logging?*

Manually adding `Logger.info()` calls to every resource method is the kind of approach that works fine in a small project and becomes a maintenance nightmare in a real one. Every time a new endpoint gets added, someone has to remember to add the logging calls. They often don't. You end up with inconsistent coverage, gaps in your audit trail, and logging code scattered across dozens of methods that makes the actual business logic harder to read.

Filters solve all of this at once. Our `LoggingFilter` implements both `ContainerRequestFilter` and `ContainerResponseFilter`, which means it runs automatically on every single request and response — no manual wiring required. New resource classes get logging for free just by existing, because the filter operates at the JAX-RS container level, not at the individual method level.

This is the **Single Responsibility Principle** in action. Resource methods should focus entirely on business logic — validating input, manipulating data, returning results. Observability concerns like logging, timing, authentication, and rate limiting are orthogonal to that and belong in separate classes. The `@Provider` annotation on our filter tells the JAX-RS runtime to discover and register it automatically, and changing the logging format across the entire API means editing exactly one file. That's the kind of design that scales.

---

## Technology Stack

| Technology | Purpose |
|------------|---------|
| **Java 11** | Language |
| **JAX-RS 2.1 (Jersey 2.41)** | REST framework |
| **Grizzly 2** | Embedded HTTP server |
| **Jackson** | JSON serialisation/deserialisation |
| **Maven** | Build tool & dependency management |
| **ConcurrentHashMap** | Thread-safe in-memory data store |

---

## License

This project was developed as coursework for the 5COSC022W Client-Server Architectures module at the University of Westminster.