package com.service

import com.dto.Post
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.Statement

@Serializable
class PostService(private val connection: Connection) {
    companion object {
        private const val INSERT_POST = "INSERT INTO posts (author_id, title, content) VALUES (?, ?, ?)"
        private const val SELECT_POST_BY_ID = "SELECT * FROM posts WHERE id = ?"
        private const val UPDATE_POST = "UPDATE posts SET title = ?, content = ? WHERE id = ?"
        private const val DELETE_POST = "DELETE FROM posts WHERE id = ?"
    }

    suspend fun create(post: Post): Post {
        val statement = connection.prepareStatement(INSERT_POST, Statement.RETURN_GENERATED_KEYS)
        statement.setInt(1, post.authorId)
        statement.setString(2, post.title)
        statement.setString(3, post.content)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        return if (generatedKeys.next()) {
            post.copy(id = generatedKeys.getInt(1))
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted post")
        }
    }

     fun read(id: Int): Post? {
        val statement = connection.prepareStatement(SELECT_POST_BY_ID)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            Post(
                id = resultSet.getInt("id"),
                authorId = resultSet.getInt("author_id"),
                title = resultSet.getString("title"),
                content = resultSet.getString("content")
            )
        } else {
            null
        }
    }

    fun update(id: Int, post: Post) {
        val statement = connection.prepareStatement(UPDATE_POST)
        statement.setString(1, post.title)
        statement.setString(2, post.content)
        statement.setInt(3, id)
        statement.executeUpdate()
    }

    fun delete(id: Int) {
        val statement = connection.prepareStatement(DELETE_POST)
        statement.setInt(1, id)
        statement.executeUpdate()
    }
}