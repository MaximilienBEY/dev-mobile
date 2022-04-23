package com.example.projectkotlin

import java.util.*

class Message {
    var content: String? = null
    var authorId: String? = null
    var createdAt: Date? = null

    constructor() {}
    constructor(content: String?, authorId: String?, createdAt: Date?) {
        this.content = content
        this.authorId = authorId
        this.createdAt = createdAt
    }
}