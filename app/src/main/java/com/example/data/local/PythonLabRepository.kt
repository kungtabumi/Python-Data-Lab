package com.example.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PythonLabRepository(private val dao: ProgressDao) {

    val userProgress: Flow<UserProgressEntity> = dao.getUserProgress().map { entity ->
        entity ?: UserProgressEntity(
            id = 1,
            totalXp = 0,
            streakDays = 1,
            lastActiveDate = getTodayDateString(),
            completedLessonIds = "",
            completedQuizIds = "",
            unlockedBadgeIds = "",
            studentName = "Siswa XI SMAN 1 Kepanjen"
        )
    }

    val savedSnippets: Flow<List<SavedSnippetEntity>> = dao.getAllSavedSnippets()

    suspend fun userProgressSnapshot(): UserProgressEntity {
        return dao.getUserProgressOnce() ?: UserProgressEntity(
            id = 1,
            totalXp = 0,
            streakDays = 1,
            lastActiveDate = getTodayDateString(),
            completedLessonIds = "",
            completedQuizIds = "",
            unlockedBadgeIds = "",
            studentName = "Siswa XI SMAN 1 Kepanjen"
        )
    }

    suspend fun markLessonCompleted(lessonId: String, current: UserProgressEntity, xpReward: Int = 30) {
        val completedSet = current.completedLessonIds.split(",").filter { it.isNotBlank() }.toMutableSet()
        val isNew = completedSet.add(lessonId)
        val newXp = if (isNew) current.totalXp + xpReward else current.totalXp

        val updated = current.copy(
            completedLessonIds = completedSet.joinToString(","),
            totalXp = newXp,
            lastActiveDate = getTodayDateString()
        )
        dao.insertOrUpdateProgress(updated)
    }

    suspend fun recordQuizSuccess(quizId: String, current: UserProgressEntity, xpReward: Int = 50) {
        val completedQuizzes = current.completedQuizIds.split(",").filter { it.isNotBlank() }.toMutableSet()
        val isNew = completedQuizzes.add(quizId)
        val newXp = if (isNew) current.totalXp + xpReward else current.totalXp

        val updated = current.copy(
            completedQuizIds = completedQuizzes.joinToString(","),
            totalXp = newXp,
            lastActiveDate = getTodayDateString()
        )
        dao.insertOrUpdateProgress(updated)
    }

    suspend fun unlockBadge(badgeId: String, current: UserProgressEntity) {
        val unlockedBadges = current.unlockedBadgeIds.split(",").filter { it.isNotBlank() }.toMutableSet()
        unlockedBadges.add(badgeId)
        dao.insertOrUpdateProgress(current.copy(unlockedBadgeIds = unlockedBadges.joinToString(",")))
    }

    suspend fun saveSnippet(title: String, code: String, category: String) {
        dao.insertSnippet(
            SavedSnippetEntity(
                title = title,
                code = code,
                category = category
            )
        )
    }

    suspend fun deleteSnippet(id: Int) {
        dao.deleteSnippetById(id)
    }

    suspend fun resetAllProgress() {
        dao.insertOrUpdateProgress(
            UserProgressEntity(
                id = 1,
                totalXp = 0,
                streakDays = 1,
                lastActiveDate = getTodayDateString(),
                completedLessonIds = "",
                completedQuizIds = "",
                unlockedBadgeIds = "",
                studentName = "Siswa XI SMAN 1 Kepanjen"
            )
        )
    }

    private fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}
