package com.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.sql.*
import java.io.File
import java.util.*


fun Application.configureDatabases() {
    val dbConnection: Connection = connectToPostgres(embedded = false)
    val cityService = CityService(dbConnection)


    routing {
    
        // Create city
        post("/cities") {
            val city = call.receive<City>()
            val id = cityService.create(city)
            call.respond(HttpStatusCode.Created, id)
        }
    
        // Read city
        get("/cities/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            try {
                val city = cityService.read(id)
                call.respond(HttpStatusCode.OK, city)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    
        // Update city
        put("/cities/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val user = call.receive<City>()
            cityService.update(id, user)
            call.respond(HttpStatusCode.OK)
        }
    
        // Delete city
        delete("/cities/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            cityService.delete(id)
            call.respond(HttpStatusCode.OK)
        }
    }
    environment.monitor.subscribe(ApplicationStopPreparing) {
        dbConnection.close()
    }
}

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
