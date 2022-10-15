package com.example.chatapp.ModelClasses

class Posts {
    private var profile: String = ""
    private var username: String = ""
    private var date: String = ""
    private var content: String = ""
    private var image: String = ""
    private var id: String = ""
    private var userId: String = ""

    constructor()

    constructor(profile: String, username: String, date: String, content: String, image: String, id: String, userId: String) {
        this.profile = profile
        this.username = username
        this.date = date
        this.content = content
        this.image = image
        this.id = id
        this.userId = userId
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

    fun getDate(): String {
        return date
    }
    fun setDate(date: String) {
        this.date = date
    }

    fun getContent(): String {
        return content
    }
    fun setContent(content: String) {
        this.content = content
    }

    fun getImage(): String {
        return image
    }
    fun setImage(image: String) {
        this.image = image
    }

    fun getId(): String {
        return id
    }
    fun setId(id: String) {
        this.id = id
    }

    fun getUserId(): String {
        return userId
    }
    fun setUserId(userId: String) {
        this.userId = userId
    }
}