package com.example.model

data class RewardCelebrationEvent(
    val badge: AchievementBadge? = null,
    val lessonTitle: String,
    val structureDisplayName: String,
    val structureEmoji: String,
    val xpGained: Int = 30,
    val isModuleCompleted: Boolean = false,
    val totalLessonsInModule: Int = 0,
    val completedInModule: Int = 0,
    val celebrationQuote: String = "Hebat! Pemahaman struktur datamu semakin kuat."
)
