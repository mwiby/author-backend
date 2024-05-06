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
)

@Serializable
data class Comment(
    val id: Int? = null,
    val postId: Int,
    val content: String
)

@Serializable
data class PostWithComments(
    val post: Post,
    val comments: List<Comment>
)