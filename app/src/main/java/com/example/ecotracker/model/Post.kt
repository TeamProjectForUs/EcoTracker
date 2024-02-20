package com.example.ecotracker.model;

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Post(
    @PrimaryKey val name: String,
    )
