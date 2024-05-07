package com.service

import com.dto.Comment
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.Statement

@Serializable
class CommentService(private val connection: Connection) {
    companion object {
        private const val INSERT_COMMENT = "INSERT INTO comments (post_Id, content) VALUES (?, ?)"
        private const val UPDATE_COMMENT = "UPDATE comments SET content = ? WHERE id = ?"
        private const val DELETE_COMMENT = "DELETE FROM comments WHERE id = ?"
    }

    suspend fun create(comment: Comment): Comment {
        val statement = connection.prepareStatement(INSERT_COMMENT, Statement.RETURN_GENERATED_KEYS)
        statement.setInt(1, comment.postId)
        statement.setString(2, comment.content)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        return if (generatedKeys.next()) {
            comment.copy(id = generatedKeys.getInt(1))
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted comment")
        }
    }

    fun update(id: Int, comment: Comment) {
        val statement = connection.prepareStatement(UPDATE_COMMENT)
        statement.setString(1, comment.content)
        statement.setInt(2, id)
        statement.executeUpdate()
    }

    fun delete(id: Int) {
        val statement = connection.prepareStatement(DELETE_COMMENT)
        statement.setInt(1, id)
        statement.executeUpdate()
    }
}