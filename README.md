# Semestral project documentation

Project found in ppj-2024/ subdirectory.

Application profiles are summed up at the end of this document.

## Persistence model (JPA, ORM)

For implementation, see the [orm](https://github.com/navratil-matej/ppj-2024/tree/master/ppj-2024/src/main/java/net/cuddlebat/ppj2024/orm) package.

Numeric ids used over string column combinations for performance reasons

* Country[id, code, name]
  * unique constraint {code, name}
* City[id, name, country, lat, lon]
  * unique constraint {name, country}
  * on delete country cascade
* Measurement[id, date, city, temperature, humidity, pressure, wind, rain]
  * unique constraint {date, city}
  * on delete city cascade

## Persistence realization (JDBC)

In all cases, tables are automatically generated.

* For profile PROD, MySQL connection is configured.
  * URL: jdbc:mysql://localhost:3306/ppj2024db
  * Username: ppj
  * Password: letmein
  * User requires all permissions on ppj2024db database.
* For profile DEBUG, persistent HSQLDB is configured.
  * URL: jdbc:hsqldb:file:debugdb
  * Information is stored in debugdb.script
* For profiles DEVEL and TEST, transient HSQLDB is configured.

## REST API (WebMVC, JPQL, Swagger)

See [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) for API UI provided by Swagger.

For implementation, see the [repositories](https://github.com/navratil-matej/ppj-2024/tree/master/ppj-2024/src/main/java/net/cuddlebat/ppj2024/repositories) package.

CRUD operations for all entities and relations respecting REST conventions generated with CrudRepository.

Additional endpoints generated with JPQL queries.

* Entity CRUD endpoints: `/<entity>` and `/<entity>/{id}`
* Relation CRUD endpoints: `/<entity>/{id}/<related-entity>` 
* Query endpoints: `/<entity>/search/<query>`
* Aggregations endpoint: `/city/{id}/aggs`

## Aggregations (JPQL, Stream API)

For implementation, see the [WeatherAggregationController](https://github.com/navratil-matej/ppj-2024/blob/master/ppj-2024/src/main/java/net/cuddlebat/ppj2024/rest/WeatherAggregationController.java) class.
- [ ] Should logic be separated to a WeatherAggregationService class?

Aggregations are computed as follows:
* Data for city specified by `{id}` path argument pulled with a custom query endpoint.
* Results are filtered and reduced with a Java Stream API pipeline.

## Automated data acquisition

For implementation, see the [WeatherApiService](https://github.com/navratil-matej/ppj-2024/blob/master/ppj-2024/src/main/java/net/cuddlebat/ppj2024/service/WeatherApiService.java) class.

Custom, configurable bean WeatherApiService with a thread started in @PostConstruct phase.

First pass is done on list of cities from configuration, pulling them from Open Weather Map's Geocoding API as needed.

Subsequent passes are done on all cities in the database, including any manually added ones.

Configuration options are as follows:
* `weatherApi.enabled`: Whether bean should start its collection thread. Mainly for tests.
* `weatherApi.apiKey`: The API key to be used for API requests.
* `weatherApi.apiUrl`: The API url to be targetted by requests.
* `weatherApi.apiPeriodMs`: The time between requests, to account for ratelimits.
* `weatherApi.cities`: A comma-separated list of cities in format City@CountryCode to initiate the app with.

## Unit tests (JUnit, JUnit.Vintage)

For implementation, see the [test](https://github.com/navratil-matej/ppj-2024/tree/master/ppj-2024/src/test) source set.

Tests are performed on CRUD repositories, custom queries and aggregation pipeline.

Tests are organized into `entityControllerTest` for basic CRUD operations and `searchControllerTest` for queries.

Tests use an in-memory database cleared and populated before every test.

## Logging (Logback)

Application uses the INFO, DEBUG and TRACE levels to watch over automated data acquisition:
* TRACE reports on every API request made.
* DEBUG reports on every new entry created.
* INFO reports on the initial pass and automatically generated countries.

Logback is configured to log on different level per profile:
* For profile PROD, app logs at INFO level
* For profile DEBUG, app logs at DEBUG level
* For profile DEVEL, app logs at TRACE level

## Application profile summary

* PROD:
  * MySQL connection
  * INFO logging
  * Data acquisition enabled
* DEBUG:
  * Persistent HSQLDB file
  * DEBUG logging
  * Data acquisition enabled
* DEVEL:
  * Transient HSQLDB in memory
  * TRACE logging
  * Data acquisition enabled
* TEST:
  * Transient HSQLDB in memory
  * DB populated with mock data
  * Data acquisition disabled
