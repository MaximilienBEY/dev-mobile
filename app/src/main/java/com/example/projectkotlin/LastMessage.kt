package com.example.projectkotlin

import java.util.*

class LastMessage {
    var id: String? = null
    var message: Message? = null

    constructor() {}
    constructor(id: String?, message: Message?) {
        this.id = id
        this.message = message
    }
}