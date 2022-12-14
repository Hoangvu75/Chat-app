package com.example.chatapp.ModelClasses

class Messages {
    private var message: String = ""
    private var sender: String = ""
    private var receiver: String = ""
    private var time: String = ""
    private var image: String = ""
    private var removed: String = ""
    private var id: String = ""

    constructor()

    constructor(message: String, sender: String, receiver: String, time: String, image: String, removed: String, id: String) {
        this.message = message
        this.sender = sender
        this.receiver = receiver
        this.time = time
        this.image = image
        this.removed = removed
        this.id = id
    }

    fun getMessage(): String? {
        return message
    }
    fun setMessage(message: String) {
        this.message = message
    }

    fun getSender(): String? {
        return sender
    }
    fun setSender(sender: String) {
        this.sender = sender
    }

    fun getReceiver(): String? {
        return receiver
    }
    fun setReceiver(receiver: String) {
        this.receiver = receiver
    }

    fun getTime(): String? {
        return time
    }
    fun setTime(time: String) {
        this.time = time
    }

    fun getImage(): String? {
        return image
    }
    fun setImage(image: String) {
        this.image = image
    }

    fun getRemoved(): String? {
        return removed
    }
    fun setRemoved(removed: String) {
        this.removed = removed
    }

    fun getId(): String? {
        return id
    }
    fun setId(id: String) {
        this.id = id
    }
}