package cc.unitmesh.pick.project.library

object SpringLibrary {
    // Spring
    private val SPRING_MVC_MAVEN = "org.springframework:spring-webmvc"
    private val SPRING_WEBFLUX_MAVEN = "org.springframework:spring-webflux"
    private val SPRING_BOOT_WEB = "org.springframework.boot:spring-boot-starter-web"
    private val SPRING_BOOT = "org.springframework.boot:spring-boot-starter"

    // Spring Data
    private val REACTOR_MAVEN = "io.projectreactor:reactor-core"
    private val MONGO_REACTIVE_STREAMS_MAVEN = "org.mongodb:mongodb-driver-reactivestreams"
    private val SPRING_DATA_COMMONS_MAVEN = "org.springframework.data:spring-data-commons"
    private val JPA_MAVEN = "org.springframework.data:spring-data-jpa"
    private val JPA_BOOT_MAVEN = "org.springframework.boot:spring-boot-starter-data-jpa"

    private val CASSANDRA_MAVEN = "org.springframework.data:spring-data-cassandra"
    private val COUCHBASE_MAVEN = "org.springframework.data:spring-data-couchbase"

    private val JDBC_MAVEN = "org.springframework.data:spring-data-jdbc"
    private val JDBC_BOOT_MAVEN = "org.springframework.data:spring-data-mongodb"
    private val MONGO_MAVEN = "org.springframework.data:spring-data-mongodb"
    private val MONGO_BOOT_MAVEN = "org.springframework.boot:spring-boot-starter-data-mongodb"

    private val NEO4J_MAVEN = "org.springframework.data:spring-data-neo4j"
    private val R2DBC_MAVEN = "org.springframework.data:spring-data-r2dbc"
    private val REDIS_MAVEN = "org.springframework.data:spring-data-redis"

    val SPRING_DATA = listOf(
        LibrarySet("JPA ", listOf(JPA_MAVEN)),
        LibrarySet("JPA SPRING BOOT", listOf(JPA_MAVEN, REACTOR_MAVEN)),
        LibrarySet("CASSANDRA", listOf(CASSANDRA_MAVEN)),
        LibrarySet("REACTIVE CASSANDRA", listOf(CASSANDRA_MAVEN, REACTOR_MAVEN)),
        LibrarySet("COUCHBASE", listOf(COUCHBASE_MAVEN)),
        LibrarySet("REACTIVE COUCHBASE", listOf(COUCHBASE_MAVEN, REACTOR_MAVEN)),
        LibrarySet("JDBC", listOf(JDBC_MAVEN)),
        LibrarySet("JDBC SPRING BOOT", listOf(JDBC_BOOT_MAVEN)),
        LibrarySet("MONGO", listOf(MONGO_MAVEN)),
        LibrarySet("MONGO SPRING BOOT", listOf(MONGO_BOOT_MAVEN)),
        LibrarySet(
            "REACTIVE MONGO",
            listOf(MONGO_MAVEN, REACTOR_MAVEN, MONGO_REACTIVE_STREAMS_MAVEN)
        ),
        LibrarySet("NEO4J", listOf(NEO4J_MAVEN)),
        LibrarySet("R2DBC", listOf(R2DBC_MAVEN)),
        LibrarySet("REDIS", listOf(REDIS_MAVEN))
    )

    val SPRING_MVC = listOf(
        LibraryDescriptor("Spring MVC", SPRING_MVC_MAVEN),
        LibraryDescriptor("Spring WebFlux", SPRING_WEBFLUX_MAVEN),
        LibraryDescriptor("Spring Boot", SPRING_BOOT),
        LibraryDescriptor("Spring Boot Web", SPRING_BOOT)
    )
}