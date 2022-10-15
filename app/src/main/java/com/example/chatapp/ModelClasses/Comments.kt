package com.example.chatapp.ModelClasses

class Comments {
    private var profile: String = ""
    private var username: String = ""
    private var time: String = ""
    private var comment: String = ""
    private var image: String = ""

    constructor()
    constructor(profile: String, username: String, time: String, comment: String, image: String) {
        this.profile = profile
        this.username = username
        this.time = time
        this.comment = comment
        this.image = image
    }

    fun getProfile(): String {
        return profile
    }
    fun setProfile(profile: String) {
        this.profile = profile
    }

    fun getUsername(): String {
        return username
    }
    fun setUsername(username: String) {
        this.username = username
    }

    fun getTime(): String {
        return time
    }
    fun setTime(time: String) {
        this.time = time
    }

    fun getComment(): String {
        return comment
    }
    fun setComment(comment: String) {
        this.comment = comment
    }

    fun getImage(): String {
        return image
    }
    fun setImage(image: String) {
        this.image = image
    }
}