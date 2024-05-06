package com.service

import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.Statement

@Serializable
class CommentService(private val connection : Connection) {

}