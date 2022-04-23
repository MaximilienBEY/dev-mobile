package com.example.projectkotlin

import java.util.*

class User {
    var uid: String? = null
    var username: String? = null
    var email: String? = null
    var last_message: String? = null
    var last_date: Date? = null

    constructor() {}
    constructor(uid: String?, username: String?, email: String?, last_message: String?, last_date: Date?) {
        this.uid = uid
        this.username = username
        this.email = email
        this.last_message = last_message
        this.last_date = last_date
    }
}