plugins {
    id("java")
    id("application")
    id("io.freefair.lombok") version "8.10.2"
}

group = "org.example"
version = "unspecified"

application {
    mainClass.set("com.programacion.MainLibraryProyect")
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Core y Spring JDBC
    implementation("org.springframework:spring-context:6.2.8")
    implementation("org.springframework:spring-jdbc:6.2.8")
    implementation("org.springframework:spring-tx:6.2.8")

    // Spring Data JDBC y Relational
    implementation("org.springframework.data:spring-data-jdbc:3.5.1")
    implementation("org.springframework.data:spring-data-relational:3.5.1")

    // Base de datos: PostgreSQL
    implementation("org.postgresql:postgresql:42.7.3")

    // Pool de conexiones HikariCP
    implementation("com.zaxxer:HikariCP:5.0.1")

    // FlatLaf UI
    implementation("com.formdev:flatlaf:3.6")
}

sourceSets {
    main {
        output.setResourcesDir(file("${buildDir}/classes/java/main"))
    }
}
