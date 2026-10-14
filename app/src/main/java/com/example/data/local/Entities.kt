package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val id: Int = 1,
    val totalXp: Int = 0,
    val streakDays: Int = 1,
    val lastActiveDate: String = "",
    val completedLessonIds: String = "", // Comma-separated lesson IDs
    val completedQuizIds: String = "",   // Comma-separated quiz IDs
    val unlockedBadgeIds: String = "",   // Comma-separated badge IDs
    val studentName: String = "Siswa XI-Informatika"
)

@Entity(tableName = "saved_snippets")
data class SavedSnippetEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val code: String,
    val category: String,
    val timestamp: Long = System.currentTimeMillis()
)
