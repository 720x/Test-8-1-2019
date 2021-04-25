package com.harshit.imagesearch.models

class UserModel {
    var key: String? = null
        private set
    var email: String? = null
        private set

    var name: String? = null
        private set


    constructor() {}
    constructor(
        key: String,
        email: String,
        name: String,


    ) {
        setKey(key)
        setEmail(email)
        setName(name)
    }

    fun setKey(key: String) {
        require(!key.isEmpty()) { "Provided key is an empty string." }
        this.key = key
    }

 