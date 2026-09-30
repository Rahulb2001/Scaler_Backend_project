# Scaler Backend Project

A small e-commerce-flavoured microservices backend, built as a Spring Boot /
Eureka / Kafka learning project. Six independently deployable services, one
service registry, and a Docker Compose file that wires all of them together
with MySQL, Redis and Kafka.

## Services

| Service | Port | Registers with Eureka? | Purpose |
|---|---|---|---|
| `service-discovery` | 8761 | n/a (it *is* the registry) | Netflix Eureka server |
| `user-authentication-service` | 8081 | yes, as `userservice` | Signup/login, JWT-based sessions, publishes a signup event to Kafka |
| `notification-service` | - (no REST API) | no - pure background worker | Consumes the signup event and emails the user a welcome message |
| `product-catalog-service` | 8082 | yes, as `productservice` | Product/category catalog; can proxy fakestoreapi.com with a Redis cache, or serve real rows from MySQL |
| `payment-service` | 8083 | yes, as `paymentservice` | Generates a Razorpay or Stripe payment link; verifies incoming Stripe webhooks |
| `docker-demo` | 8080 | no | Minimal hello-world app used to demonstrate packaging into a Docker image |

## Architecture

```
                      +----------------------+
                      |  service-discovery   |   (Eureka registry, :8761)
                      +-----------^----------+
                                  | register / lookup
          +------------------------+------------------------+
          |                       |                          |
+---------+----------+  +---------+----------+  +------------+-----------+
| user-authentication |  |  product-catalog   |  |    payment-service     |
|      -service        |<-|     -service       |  |         :8083         |
|       :8081          |  |       :8082        |  +------------------------+
+---------+------------+  +---------+----------+
          | Kafka topic "signup"    |
          v                          +--> MySQL (productcatalog_db)
+----------------------+             +--> Redis (fakestoreapi cache)
| notification-service |             +--> fakestoreapi.com
+----------------------+

user-authentication-service also owns MySQL (userauth_db).
product-catalog-service calls user-authentication-service over HTTP via
Eureka + a @LoadBalanced RestTemplate ("http://userservice/...") - there's
no API gateway or Feign client in this project, that's the one and only
inter-service HTTP call.
```

## What's different from the version this was built from

This project started as a rewrite of an earlier student project with the
same six services. A few things were fixed or filled in along the way:

- **JWT tokens actually last a day** instead of expiring 10 *milliseconds*
  after being issued.
- **Eureka's client config uses the right property name**
  (`eureka.client.service-url.defaultZone`, not `spring.client...`, which
  silently did nothing).
- **The FakeStoreAPI-backed product service is fully implemented** -
  `getAllProducts`, `createProduct` and `deleteProduct` were stubs before.
- **Search sorting is driven by the request**, not hardcoded to
  "category, then description".
- **The payment gateway strategy actually reads the request** - it used to
  always return Stripe no matter what was asked for.
- **The Stripe webhook verifies its signature** before trusting the payload,
  instead of accepting and logging anything posted to it.
- **No secrets are hardcoded.** The Gmail app password notification-service
  needs now comes from an environment variable, with a graceful no-op if it
  isn't set, instead of being committed in source.
- **A working JWT auth filter** protects `/auth/me` and `/auth/logout` in
  user-authentication-service - the previous security config permitted
  every request unconditionally.
- **`docker-compose.yml` at the project root** brings up every service plus
  MySQL, Redis, Kafka/Zookeeper together, with a Dockerfile for every
  service rather than just one demo module.

## Running it

Nothing here has been run locally as part of building it (the machine this
was written on doesn't have Docker installed) - `mvn compile` was used to
confirm every module actually builds. That means when you first run this
for real, budget time to shake out anything environment-specific.

**Everything at once, via Docker Compose:**

```bash
cp .env.example .env   # fill in real secrets if you want mail/payments to work
docker compose up --build
```

This starts MySQL, Redis, Kafka+Zookeeper, and all six services, wired
together with the right hostnames and ports. Give it a minute - MySQL needs
to pass its health check before the two database-backed services start.

**One service at a time, without Docker:**

Each service is a normal Maven project. You need MySQL, Redis and Kafka
running locally (or point the relevant `*_HOST` / `*_BOOTSTRAP_SERVERS` env
vars at wherever they live), then, from inside a service's folder:

```bash
./mvnw spring-boot:run
```

Start `service-discovery` first - the other services will retry registering
with it until it's up, but they'll be noisy about it until then.

## Trying it out

```bash
# create an account
curl -X POST http://localhost:8081/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"name":"Asha","email":"asha@example.com","password":"hunter2"}'

# log in
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"asha@example.com","password":"hunter2"}'

# browse the catalog (proxies fakestoreapi.com, cached in Redis)
curl http://localhost:8082/products

# generate a payment link
curl -X POST http://localhost:8083/payment \
  -H "Content-Type: application/json" \
  -d '{"amount":499,"orderId":"order-1","name":"Asha","email":"asha@example.com","phoneNumber":"9999999999","preferredGateway":"STRIPE"}'
```

Signing up publishes an event to Kafka's `signup` topic; if `MAIL_USERNAME`
and `MAIL_APP_PASSWORD` are set, `notification-service` picks it up and
emails the welcome message for real.
