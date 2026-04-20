# Smart Campus — Sensor & Room Management API

## API Design Overview

This project implements a robust, scalable RESTful API designed to manage physical and sensor infrastructure across a university's "Smart Campus". Built entirely on Java using JAX-RS (Jersey) and an embedded Grizzly HTTP server, it intentionally avoids external heavy frameworks like Spring Boot, opting for core Java Enterprise principles.

The API exposes three core resource models:
- **Rooms**: Represents physical spaces on campus, tracking details like maximum capacity for safety regulations.
- **Sensors**: Abstract definitions of physical hardware (e.g., CO2 monitors, temperature sensors) assigned to specific Rooms.
- **Sensor Readings**: Historical event logs recording individual metric captures over time.

Key design decisions include:
- **Resource Hierarchy**: Uses deep nesting for natural relationships (e.g., `/sensors/{id}/readings`).
- **Idempotency & Safety**: Strict guarantees on HTTP verbs. `DELETE` operations block room removal if active sensors are still attached (returning a 409 Conflict).
- **HATEOAS Support**: The root `/api/v1` endpoint serves as a discovery beacon with hypermedia links mapping the available resource collections.
- **Thread-safe Design**: Data is kept entirely in-memory as per assignment requirements, managed by a singleton data store using `ConcurrentHashMap` to handle asynchronous operations gracefully.
- **Standardized Error Handling**: Unified exception mappers prevent system leakage, translating runtime exceptions intelligently (e.g., mapping bad database references to 422 Unprocessable Entity).

---

## How to Build & Run

### Option 1: Terminal (Maven CLI)

1. **Clone the project:**
   ```bash
   git clone https://github.com/thiviru7715/CSA-CW.git
   cd CSA-CW
   ```
2. **Compile the project:**
   ```bash
   mvn clean compile
   ```
   *(Or use `./mvnw compile` if Maven is not installed globally)*
3. **Launch the server:**
   ```bash
   mvn exec:java
   ```
4. **Verify:** You should see `Smart Campus API started at: http://localhost:8080/api/v1/` in your terminal. Press **Enter** to stop the server gracefully.

### Option 2: NetBeans IDE

1. Open NetBeans.
2. Select **File > Open Project**.
3. Navigate to the downloaded `CSA-CW` folder and select it (NetBeans will detect it as a Maven project automatically).
4. In the Projects explorer pane, right-click on the `CSA-CW` project name and select **Run**.
   *(Alternatively, expand `Source Packages` > `com.smartcampus` > right-click `Main.java` and select **Run File**)*
5. The NetBeans Output window will display `Smart Campus API started at: http://localhost:8080/api/v1/`.

---

## Sample API Interactions

Here are five key `curl` commands demonstrating standard interactions with the system. To view the results, simply copy and paste these into your terminal while the server is running.

### 1. Root Discovery (HATEOAS Navigation)
```bash
curl -X GET http://localhost:8080/api/v1/
```

### 2. Create a specific Room
```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"LIB-301", "name":"Library Study", "capacity":30}'
```

### 3. Create a Sensor (Validating the Room)
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"CO2-01", "type":"CO2", "status":"ACTIVE", "currentValue":400, "roomId":"LIB-301"}'
```

### 4. Record a Sensor Reading (With Side Effect)
```bash
curl -X POST http://localhost:8080/api/v1/sensors/CO2-01/readings \
  -H "Content-Type: application/json" \
  -d '{"value":420.5}'
```
*Note: This will automatically trace back and update the parent `CO2-01` sensor's `currentValue` property to `420.5`.*

### 5. Attempt Deletion of Occupied Room (Triggers 409 Conflict Error Handler)
```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301
```

---

## Report — Answers to Coursework Questions

### Part 1: Service Architecture & Setup

**Q1: Explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on your in-memory data structures.**

By default, JAX-RS operates on a **per-request lifecycle**. This means the framework instantiates a brand new resource object every time an HTTP request comes in, and deletes it right after. If you try to store data directly inside the controller’s instance variables, it will vanish immediately. 
To prevent this data loss, I decoupled the storage mechanism into a separate `DataStore` class applying the Singleton pattern. This singleton uses `ConcurrentHashMap` structures. Because the embedded server processes multiple web requests concurrently across different threads, using standard Lists or HashMaps would cause data corruption (race conditions). The concurrent maps safely handle simultaneous reads and writes.

**Q2: Why is the provision of "Hypermedia" (HATEOAS) considered a hallmark of advanced RESTful design?**

Hypermedia turns an API from a static list of URLs into a dynamic web of navigation. Just like browsing a website where you click buttons to navigate pages, HATEOAS embeds the next available action links directly inside the API response. For client developers, this means they don’t need to hardcode specific `/api/v1/rooms` paths or constantly check documentation to figure out what they can do next. The API acts as its own guide, making the frontend much easier to build and adapt if URL routing structures change in the future.

### Part 2: Room Management

**Q1: When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects?**

Returning just a list of IDs keeps the network payload incredibly small, which is great for mobile bandwidth. However, if the client app actually needs to draw a dashboard showing room names and capacities, it's forced to make a separate `GET /rooms/{id}` call for every single ID. If there are 100 rooms, that’s 100 extra network trips (the "N+1" problem), leading to slow load times.
Returning the full object upfront fixes this latency by delivering all necessary data in one go but makes the initial file much larger. For this campus assignment, returning the full objects is the optimal choice since we want to populate the UI efficiently without overwhelming the network with excessive micro-requests.

**Q2: Is the DELETE operation idempotent in your implementation? Provide a detailed justification.**

Yes, the operation is completely idempotent. If a client sends `DELETE /rooms/LIB-301`, the server deletes it and sends back a `204 No Content` code. If the network hiccups and the exact same `DELETE` request is sent by mistake three seconds later, the server notices the room is already gone and returns a `404 Not Found`. 
Idempotency doesn't mean the server returns the identical *status code* every time; it means the end-result state of the server doesn't break or behave weirdly if the request is duplicated. Whether you request deletion once or a hundred times, the final state is identical: the room does not exist.

### Part 3: Sensor Operations & Linking

**Q1: We explicitly use @Consumes(MediaType.APPLICATION_JSON). Explain the technical consequences if a client attempts to send text/plain. How does JAX-RS handle it?**

If a client sends data using the wrong content type (like simple text instead of JSON), JAX-RS intercepts it at the framework layer before our Java method is even invoked. It automatically rejects the request and sends the client an **HTTP 415 Unsupported Media Type** error. The technical advantage here is security and cleanliness: we don't have to write messy `if(contentType != "json")` blocks inside our application code. The tool perfectly strictly enforces the architectural boundaries by itself based solely on the annotation.

**Q2: Why is the query parameter approach generally considered superior for filtering?**

In REST rules, the URL path (`/sensors`) defines the "noun" — the specific bucket of things you are looking at. A query parameter (`?type=CO2`) represents the "adverb" — how you want to filter that view. 
If we used paths for filtering (like `/sensors/type/CO2`), it wrongly implies that a "type" is a physical sub-resource owned by a sensor, which it isn't. Furthermore, query parameters are designed to be optional and combined easily (like `?type=CO2&status=ACTIVE`). Path segments are rigid, so you'd have to build many complicated custom routes to achieve the same result.

### Part 4: Deep Nesting with Sub-Resources

**Q1: Discuss the architectural benefits of the Sub-Resource Locator pattern.**

Without this pattern, your main `SensorResource.java` controller would quickly become bloated, trying to handle general sensor routes alongside deep reading routes like `sensors/{id}/readings/update` all in one file. 
The Sub-Resource Locator solves this by acting as a traffic cop. When a request hits `/sensors/{id}/readings`, the main sensor controller simply points the framework to an entirely separate `SensorReadingResource.java` class to handle the rest of the work. This keeps the code organized, prevents classes from becoming 1,000-line monsters, and makes testing specific chunks of business logic much easier. 

### Part 5: Advanced Error Handling & Logging

**Q1: Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?**

A 404 essentially screams "This URL doesn't exist!". But when we send a `POST /sensors` with a bad `roomId`, the URL `/sensors` *does* exist perfectly fine. The issue is purely semantic: the JSON structural syntax is mathematically correct, but the data context inside it refers to a room logic that isn't verifiable. The 422 standard explicitly translates to "Unprocessable Entity", effectively telling the client: "Your web address is correct, and I understand your formatting, but the data business logic you supplied is flawed." This saves hours of debugging since developers know not to mess with their URLs.

**Q2: Explain the risks associated with exposing internal Java stack traces to external API consumers.**

Java stack traces are a gold mine for hackers. A raw error printout exposes exact internal directory structures, the names of Java classes, and the specific versions of background libraries (like Jersey 2.x) currently running. Attackers can cross-reference those specific library builds with CVE databases to find known, unpatched vulnerabilities to exploit. Furthermore, seeing the execution flow gives away how your authentication mechanisms or logic fail, showing exactly where they need to hit to crack the system. Custom error mappers ensure clients only receive safe, generalized "Internal Server Error" messages instead.

**Q3: Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging?**

If we manually wrote `logger.info()` at the top of every single Java controller method, we would inevitably forget to add it somewhere, resulting in blind spots. Even worse, it clutters up clean business logic with repetitive monitoring code. 
By creating a global JAX-RS Filter, the framework automatically forces *every* single incoming and outgoing HTTP signal through our logging checkpoint without us ever asking. This achieves the "Single Responsibility Principle" — the API methods only care about processing data, while the filter handles the global auditing seamlessly, keeping the whole architecture remarkably cleaner.