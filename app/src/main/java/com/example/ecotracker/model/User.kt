package com.example.ecotracker.model;

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val name: String,
    val password: String,
    val email: String,
    var isChecked: Boolean)
