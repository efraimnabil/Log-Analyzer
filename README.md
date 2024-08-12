# Log-Analyzer

# Environment variables config in `application.properties`

```properties
spring.application.name=Log_Analyzer
influxdb.url=
influxdb.token=
influxdb.bucket=Services
influxdb.org=org
log.file.path=
spring.datasource.url=jdbc:mysql://localhost:3306/service_health
spring.datasource.username=
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
security.jwt.secret-key=<SECRET_KEY_MUST_BE_AT_LEAST_256_BITS>
security.jwt.expiration-time=<IN_MILLISECONDS>
```

# Endpoints

## Authentication

### POST `<BaseURL>/auth/login`

**Request**

```json
{
  "email": "email",
  "password": "password"
}
```

**Response**

```json
{
  "token": "token",
  "expiresIn": 123
}
```

### POST `<BaseURL>/auth/signup`

**Request**

```json
{
  "name": "name",
  "email": "email",
  "password": "password"
}
```

**Response**

```json
{
  "id": 1,
  "name": "name",
  "email": "email@email.com",
  "password": "$2a$10$7othL4sSOdqZn3rXg1qXBeBQmPIiybpXKBTEiDL8/t7Ohv64KAh4e",
  "createdAt": "2024-08-12T15:17:20.112+00:00",
  "services": null,
  "enabled": true,
  "authorities": [],
  "username": "email@email.com",
  "accountNonExpired": true,
  "accountNonLocked": true,
  "credentialsNonExpired": true
}
```
