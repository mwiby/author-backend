package com.service

import com.dto.Author
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.Statement



@Serializable
class AuthorService(private val connection: Connection) {
    companion object {
        private const val INSERT_AUTHOR = "INSERT INTO authors (name) VALUES (?)"
        private const val SELECT_AUTHOR_BY_ID = "SELECT * FROM authors WHERE id = ?"
        private const val UPDATE_AUTHOR = "UPDATE authors SET name = ? WHERE id = ?"
        private const val DELETE_AUTHOR = "DELETE FROM authors WHERE id = ?"
    }

    fun getAllAuthors(): List<Author> {
        val authors = mutableListOf<Author>()
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM authors")

        while (resultSet.next()) {
            val author = Author(
                id = resultSet.getInt("id"),
                name = resultSet.getString("name")
            )
            authors.add(author)
        }

        return authors
    }

     fun create(author: Author): Author {
        val statement = connection.prepareStatement(INSERT_AUTHOR, Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, author.name)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        return if (generatedKeys.next()) {
            author.copy(id = generatedKeys.getInt(1))
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted author")
        }
    }

    fun read(id: Int): Author? {
        val statement = connection.prepareStatement(SELECT_AUTHOR_BY_ID)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            Author(
                id = resultSet.getInt("id"),
                name = resultSet.getString("name")
            )
        } else {
            null
        }
    }

    fun update(id: Int, author: Author) {
        val statement = connection.prepareStatement(UPDATE_AUTHOR)
        statement.setString(1, author.name)
        statement.setInt(2, id)
        statement.executeUpdate()
    }

    fun delete(id: Int) {
        val statement = connection.prepareStatement(DELETE_AUTHOR)
        statement.setInt(1, id)
        statement.executeUpdate()
    }
}