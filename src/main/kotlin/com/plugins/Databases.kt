package com.plugins


import io.ktor.server.application.*
import java.sql.*
import java.io.File
import java.util.*


fun Application.connectToPostgres(embedded: Boolean): Connection {

    // Load configuration from application.properties
    val properties = loadPropertiesFromFile("src/main/resources/application.properties")

    // Extract database configuration
    val url = properties.getProperty("database.url")
    val user = properties.getProperty("database.user")
    val password = properties.getProperty("database.password")

    return if (embedded) {
        // If embedded is true, return an embedded H2 database connection
        Class.forName("org.h2.Driver")
        DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "root", "")
    } else {
        // Otherwise, return a Postgres database connection
        Class.forName("org.postgresql.Driver")
        DriverManager.getConnection(url, user, password)
    }
}

fun loadPropertiesFromFile(fileName: String): Properties {
    val properties = Properties()
    val configFile = File(fileName)
    configFile.inputStream().use { input ->
        properties.load(input)
    }
    return properties
}
