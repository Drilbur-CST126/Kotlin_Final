package com.jnich.kotlinfinal.model

import java.io.Serializable

data class Post(
    var author: User,
    var content: String,
    var imageUrl: String? = null,
    var likes: Int
): Serializable