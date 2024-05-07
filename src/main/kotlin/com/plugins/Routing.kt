package com.plugins

import com.dto.Author
import com.dto.Comment
import com.dto.Post
import com.dto.PostWithComments
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
            get {
                val authors = authorService.getAllAuthors()
                call.respond(HttpStatusCode.OK, authors)
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

        // Posts
        route("/posts") {

            post {
                val post = call.receive<Post>()
                val createdPost = postService.create(post)
                call.respond(HttpStatusCode.Created, createdPost)
            }
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val (post, comments) = postService.read(id) // Read both post and comments
                if (post != null) {
                    val postWithComments: Any = if (comments.isNotEmpty()) {
                        PostWithComments(post, comments)
                    } else {
                        post // Return post without comments
                    }
                    call.respond(HttpStatusCode.OK, postWithComments)
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
                val post = call.receive<Post>()
                postService.update(id, post)
                call.respond(HttpStatusCode.OK)
            }
            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }
                postService.delete(id)
                call.respond(HttpStatusCode.OK)
            }

        }

        //Comment
        route("/comments") {

            post {
                val comment = call.receive<Comment>()
                val createdComment = commentService.create(comment)
                call.respond(HttpStatusCode.Created, createdComment)
            }

            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@put
                }
                val comment = call.receive<Comment>()
                commentService.update(id, comment)
                call.respond(HttpStatusCode.OK)
            }

            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }
                commentService.delete(id)
                call.respond(HttpStatusCode.OK)
            }

        }

    }
    environment.monitor.subscribe(ApplicationStopPreparing) {
        dbConnection.close()
    }
}
