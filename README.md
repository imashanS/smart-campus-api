

# Smart Campus REST API

## API Overview
This project implements a Smart Campus REST API using Jakarta RESTful Web Services (JAX-RS) with the Jersey framework. The API allows management of campus rooms, sensors installed within those rooms, and historical sensor readings.

The API demonstrates REST architectural principles including resource-based endpoints, nested resources, exception handling, request/response logging, and in-memory data storage using Java collections.

The system is deployed as a WAR file on Apache Tomcat.

---

## How to Build and Run

1. Ensure Java 17 and Maven are installed

2. Clone the repository
```bash
git clone https://github.com/imashanS/smart-campus-api.git
```

3. Build the project using Maven
```bash
mvn clean package
```

4. Deploy the generated WAR file to Apache Tomcat
```
Copy the WAR file to: tomcat/webapps/
```

5. Start the Tomcat server

6. Access the API at
```
http://localhost:8080/smart-campus-api/api/v1
```

---

## Example API Requests

**Create a room**
```bash
curl -X POST http://localhost:8080/smart_campus_api_war_exploded/api/v1/rooms \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"id":"LIB-301","name":"Library Study Room","capacity":40}'
```

**Get all rooms**
```bash
curl -H "Accept: application/json" \
  http://localhost:8080/smart_campus_api_war_exploded/api/v1/rooms
```

**Create a sensor**
```bash
curl -X POST http://localhost:8080/smart_campus_api_war_exploded/api/v1/sensors \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"id":"TEMP-001","type":"Temperature","status":"ACTIVE","currentValue":0,"roomId":"LIB-301"}'
```

**Filter sensors by type**
```bash
curl -H "Accept: application/json" \
  "http://localhost:8080/smart_campus_api_war_exploded/api/v1/sensors?type=Temperature"
```

**Add a sensor reading**
```bash
curl -X POST http://localhost:8080/smart_campus_api_war_exploded/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"id":"R001","timestamp":1713510000000,"value":24.6}'
```

**Get sensor readings**
```bash
curl -H "Accept: application/json" \
  http://localhost:8080/smart_campus_api_war_exploded/api/v1/sensors/TEMP-001/readings
```

**Test 422 error - invalid roomId**
```bash
curl -X POST http://localhost:8080/smart_campus_api_war_exploded/api/v1/sensors \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"id":"TEMP-999","type":"CO2","status":"ACTIVE","currentValue":0,"roomId":"FAKE-ROOM"}'
```

---

## Conceptual Report

### 1. JAX-RS Resource Lifecycle
JAX-RS resource classes are by default created per request, meaning a new instance of the resource class is created for each incoming HTTP request. This helps avoid shared mutable state inside resource classes and improves thread safety.

JAX-RS also supports singleton resources where a single instance handles all requests. However this requires careful synchronization because multiple threads may access the same instance concurrently.

In this project the resource classes follow the default per-request lifecycle, while shared application data is stored in the DataStore class using static HashMap collections. Because these collections are shared across all requests, concurrent modifications could theoretically cause race conditions in a real multi-threaded environment. In a production system a thread-safe structure such as ConcurrentHashMap would be more appropriate.

---

### 2. HATEOAS
HATEOAS is another REST constraint which states that API responses should contain hyperlinks which allow clients to determine available operations without having to hardcode URLs.

In this case, for instance, the discovery operation, accessible via GET /api/v1, contains links to other significant resources, like /api/v1/rooms and /api/v1/sensors. Hence, it enables a client to determine available resources through dynamic discovery as opposed to hardcoding URLs and using API documentation. In comparison to static documentation, HATEOAS makes the API self-explanatory and resilient to URL changes which might break clients.

---
### 3. Returning Full Objects or Identifiers

Returning full objects provides additional information in one response, but it requires more bandwidth and potentially reveals unnecessary information. Conversely, returning identifiers creates a concise response and decouples resources.

In this API, for example, sensors reference a roomId field instead of returning a full room object. Therefore, it ensures a small response payload and a loose coupling between sensors and rooms.

---

### 4. DELETE Is Idempotent
DELETE operation in HTTP protocol is considered an idempotent request. It means that repeating the same request leads to the same outcome.

In the given API, the first DELETE request deletes the room and responds with 200 OK status code. The second time, when the same request is repeated, the server returns the 404 Not Found because the room is already deleted. Though there is a difference in response codes, the state of the resource remains unchanged, fulfilling the definition of idempotence provided in HTTP protocol.

---

### 5. Wrong Content-Type
If the client sends a wrong Content-Type header value for the endpoint with @Consumes(MediaType.APPLICATION_JSON) annotation, the JAX-RS framework rejects such a request even before processing it through the endpoint handler.

For instance, when the client sends a request in XML format or plain text instead of the JSON document, it would lead to HTTP 415 error. It guarantees that the resource method receives the correct media type as the payload only.

---

### 6. Query Parameter vs Path-Based Filtering
If you have an optional condition or filtering, it is usually done using query parameters. However, in the case of a path parameter, we identify a unique resource.

For instance, GET /sensors?type=Temperature has a query parameter to filter the output. In other words, this technique will keep our base URI clean and give us the flexibility to filter the results without creating additional paths for each value. Instead, GET /sensors/type/Temperature may make you believe that type/Temperature is a different resource. As a result, using the @QueryParam annotation in the SensorResource class is the correct approach.

---
### 7. Sub-Resource Locator Pattern

The Sub-Resource Locator pattern allows nested resources to be handled by separate resource classes instead of a single large resource controller.

Here, in the sensor readings sample, you get the sensor readings by accessing the endpoint at /sensors/{sensorId}/readings. To achieve this purpose, I added a sub-resource locator to the SensorResource class, which returns a SensorReadingResource class. This will ensure that we keep our classes organized, preventing SensorResource from becoming overly bloated.

---

### 8. Using HTTP 422 vs 404 for Missing References
HTTP Status Code 422 (Unprocessable Entity): The request is well-formed but unable to be followed due to semantic errors.

In our API creation of sensors needs a valid roomId. In case the client tries to create a sensor in a non-existing room, the request body will be valid JSON, but semantically wrong. An HTTP status code 404 in this scenario would indicate that the endpoint itself is not found, which is not quite accurate. Using HTTP 422 makes it clear that the endpoint was found and understood but the object inside the request body is not valid.

---

### 9. Security Risks of Exposing Stack Traces
If the API returns a stack trace of a Java exception, the client may be able to obtain information on package names, structure, class name, and other details of the application's inner workings. Such information can be potentially harmful, as an attacker can identify security issues in certain libraries and plan attacks according to how the system works internally.

The GlobalExceptionMapper class intercepts all uncaught exceptions and responds with a generic JSON message, thus ensuring that the internal implementation details are not exposed.

---

### 10. Logging Using Filters
By leveraging JAX-RS filters for logging and other cross-cutting concerns, consistent behavior can be achieved across all resources without having to change the individual methods.

The logging filter utilized in this project is the ApiLoggingFilter, which implements ContainerRequestFilter and ContainerResponseFilter interfaces. With the help of these filters, the HTTP method and request URI are logged for each request received, while the response status code is logged for every response issued.
```
