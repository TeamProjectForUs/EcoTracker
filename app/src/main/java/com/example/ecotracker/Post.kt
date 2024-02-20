package com.example.ecotracker

class Post(var title: String, var content: String) {

    fun getTitleFromPost(): String {
        return title
    }

    fun setTitleFromString(title: String) {
        this.title = title
    }

    fun getContentFromPost(): String {
        return content
    }

    fun setContentFromString(content: String) {
        this.content = content
    }
}
