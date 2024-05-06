package com.dto

import kotlinx.serialization.Serializable

@Serializable
data class Author(
    val id: Int? = null,
    val name: String
)

@Serializable
data class Post(
    val id: Int? = null,
    val authorId: Int,
    val title: String,
    val content: String,
    val comments: List<Comment>? = null
)

@Serializable
data class Comment(
    val id: Int? = null,
    val postId: Int,
    val content: String
)