# jwt-auth-example

A micronaut application demonstrating JWT validation on incoming HTTP requests

Implements a simple Hello World GET endpoint that requires a valid JWT to access

Expects the JWT to be signed with the HS256 algorithm 

Demonstrates:
* Use of JWTs to restrict access to an endpoint
* Use of `roles` claim in JWT to restrict access to specific user types
* Accessing claims within a JWT when processing a request

# Usage
## Prerequisites:
 * Java 17 or higher

## Commands
 * Run tests `./gradlew test`
 * Build docker image to local docker installation: `./gradlew jibDockerBuild`

---

## Micronaut 3.8.2 Documentation

- [User Guide](https://docs.micronaut.io/3.8.2/guide/index.html)
- [API Reference](https://docs.micronaut.io/3.8.2/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/3.8.2/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)
---

- [Jib Gradle Plugin](https://plugins.gradle.org/plugin/com.google.cloud.tools.jib)
- [Shadow Gradle Plugin](https://plugins.gradle.org/plugin/com.github.johnrengelman.shadow)
## Feature lombok documentation

- [Micronaut Project Lombok documentation](https://docs.micronaut.io/latest/guide/index.html#lombok)

- [https://projectlombok.org/features/all](https://projectlombok.org/features/all)


## Feature security-jwt documentation

- [Micronaut Security JWT documentation](https://micronaut-projects.github.io/micronaut-security/latest/guide/index.html)


## Feature http-client documentation

- [Micronaut HTTP Client documentation](https://docs.micronaut.io/latest/guide/index.html#httpClient)


