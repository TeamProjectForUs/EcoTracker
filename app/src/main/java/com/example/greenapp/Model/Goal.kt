package com.example.greenapp.Model

data class Goal(val tip: Tip, var done: Boolean) {
    constructor() : this(Tip(), false)

    override fun equals(other: Any?): Boolean {
        return other is Goal && other.tip.id == tip.id
    }
}

