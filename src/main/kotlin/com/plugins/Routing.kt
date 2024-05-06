package com.plugins

import com.dto.Author
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.sql.*
import com.service.AuthorService
import com.service.PostService
import com.service.CommentService
import io.ktor.server.application.*



fun Application.configureRouting(){
    val dbConnection: Connection = connectToPostgres(embedded = false)
    val authorService = AuthorService(dbConnection)
    val postService = PostService(dbConnection)
    val commentService = CommentService(dbConnection)

    routing {
        // Author
        route("/authors") {

            post {
                val author = call.receive<Author>()
                val createdAuthor = authorService.create(author)
                call.respond(HttpStatusCode.Created, createdAuthor)
            }
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val author = authorService.read(id)
                if (author != null) {
                    call.respond(HttpStatusCode.OK, author)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@put
                }
                val author = call.receive<Author>()
                authorService.update(id, author)
                call.respond(HttpStatusCode.OK)
            }
            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }
                authorService.delete(id)
                call.respond(HttpStatusCode.OK)
            }

        }
    }
    environment.monitor.subscribe(ApplicationStopPreparing) {
        dbConnection.close()
    }
}
