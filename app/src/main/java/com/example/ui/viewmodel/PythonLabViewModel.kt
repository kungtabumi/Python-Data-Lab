package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiTutorService
import com.example.data.CurriculumData
import com.example.data.local.AppDatabase
import com.example.data.local.PythonLabRepository
import com.example.data.local.SavedSnippetEntity
import com.example.data.local.UserProgressEntity
import com.example.engine.ExecutionResult
import com.example.engine.LocalPythonInterpreter
import com.example.model.ChatMessage
import com.example.model.DataStructureType
import com.example.model.QuizQuestion
import com.example.model.RewardCelebrationEvent
import com.example.model.TopicLesson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PythonLabViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PythonLabRepository
    private val pythonInterpreter = LocalPythonInterpreter()
    private val geminiTutor = GeminiTutorService()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = PythonLabRepository(db.progressDao())
    }

    val userProgress: StateFlow<UserProgressEntity> = repository.userProgress.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserProgressEntity()
    )

    val savedSnippets: StateFlow<List<SavedSnippetEntity>> = repository.savedSnippets.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Reward Celebration State
    private val _rewardCelebration = MutableStateFlow<RewardCelebrationEvent?>(null)
    val rewardCelebration: StateFlow<RewardCelebrationEvent?> = _rewardCelebration.asStateFlow()

    fun dismissRewardCelebration() {
        _rewardCelebration.value = null
    }

    fun triggerBadgeCelebration(badge: com.example.model.AchievementBadge) {
        _rewardCelebration.value = RewardCelebrationEvent(
            badge = badge,
            lessonTitle = badge.title,
            structureDisplayName = badge.category,
            structureEmoji = badge.iconEmoji,
            xpGained = badge.requiredXp,
            isModuleCompleted = true,
            celebrationQuote = badge.description
        )
    }

    // Navigation & Screen Context State
    data class NavigationOrigin(
        val sourceScreen: String? = null, // e.g., "lab", "lesson", "playground"
        val sourceTitle: String? = null   // e.g., "Lab Set", "Modul List", "Code Playground"
    )

    private val _activeLabStructure = MutableStateFlow(DataStructureType.LIST)
    val activeLabStructure: StateFlow<DataStructureType> = _activeLabStructure.asStateFlow()

    fun setActiveLabStructure(structure: DataStructureType) {
        _activeLabStructure.value = structure
    }

    private val _selectedModuleFilter = MutableStateFlow<DataStructureType?>(null)
    val selectedModuleFilter: StateFlow<DataStructureType?> = _selectedModuleFilter.asStateFlow()

    fun setSelectedModuleFilter(filter: DataStructureType?) {
        _selectedModuleFilter.value = filter
    }

    private val _playgroundOrigin = MutableStateFlow(NavigationOrigin())
    val playgroundOrigin: StateFlow<NavigationOrigin> = _playgroundOrigin.asStateFlow()

    fun setPlaygroundOrigin(sourceScreen: String?, sourceTitle: String?) {
        _playgroundOrigin.value = NavigationOrigin(sourceScreen, sourceTitle)
    }

    fun clearPlaygroundOrigin() {
        _playgroundOrigin.value = NavigationOrigin(null, null)
    }

    private val _aiTutorOrigin = MutableStateFlow(NavigationOrigin())
    val aiTutorOrigin: StateFlow<NavigationOrigin> = _aiTutorOrigin.asStateFlow()

    fun setAiTutorOrigin(sourceScreen: String?, sourceTitle: String?) {
        _aiTutorOrigin.value = NavigationOrigin(sourceScreen, sourceTitle)
    }

    fun clearAiTutorOrigin() {
        _aiTutorOrigin.value = NavigationOrigin(null, null)
    }

    // Code Playground State
    private val _currentCode = MutableStateFlow(
        """
        # ==========================================
        # PYTHON DATALAB - LABORATORIUM INTERAKTIF
        # SMAN 1 Kepanjen - Guru: Hermawan Hidayat
        # ==========================================

        # 1. Eksperimen List
        buah = ["Apel", "Jeruk", "Mangga"]
        buah.append("Pisang")
        print("List Buah:", buah)

        # 2. Eksperimen Tuple
        koordinat = (10, 20, 30)
        print("Tuple:", koordinat)

        # 3. Eksperimen Set
        angka = {1, 2, 2, 3, 4}
        print("Set Unik:", angka)

        # 4. Eksperimen Dictionary
        siswa = {"nama": "Andi", "kelas": "XI", "nilai": 95}
        print("Nilai Andi:", siswa["nilai"])
        """.trimIndent()
    )
    val currentCode: StateFlow<String> = _currentCode.asStateFlow()

    private val _executionResult = MutableStateFlow<ExecutionResult?>(null)
    val executionResult: StateFlow<ExecutionResult?> = _executionResult.asStateFlow()

    private val _isExecuting = MutableStateFlow(false)
    val isExecuting: StateFlow<Boolean> = _isExecuting.asStateFlow()

    // AI Tutor Chat State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "welcome_ai",
                sender = "AI Tutor",
                text = "Halo! Saya AI Tutor Python DataLab (SMAN 1 Kepanjen). Kamu bisa bertanya apa saja seputar dasar pemrograman Python, struktur data (List, Tuple, Set, Dict), tips belajar koding, atau minta bantuan memeriksa error kodemu!",
                isFromUser = false
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    fun updateCode(newCode: String) {
        _currentCode.value = newCode
    }

    fun runCurrentCode() {
        viewModelScope.launch {
            _isExecuting.value = true
            val result = pythonInterpreter.executeCode(_currentCode.value)
            _executionResult.value = result
            _isExecuting.value = false
        }
    }

    fun loadCodeSnippet(snippet: String) {
        _currentCode.value = snippet
        runCurrentCode()
    }

    fun saveSnippet(title: String, category: String) {
        viewModelScope.launch {
            repository.saveSnippet(title, _currentCode.value, category)
        }
    }

    fun deleteSnippet(id: Int) {
        viewModelScope.launch {
            repository.deleteSnippet(id)
        }
    }

    suspend fun executeSnippet(code: String): ExecutionResult {
        return pythonInterpreter.executeCode(code)
    }

    fun recordSnippetChallengeCompleted(challengeId: String) {
        viewModelScope.launch {
            val prevProgress = userProgress.value
            val prevIds = prevProgress.completedLessonIds.split(",").filter { it.isNotBlank() }.toSet()
            val snippetKey = "snippet_$challengeId"
            if (!prevIds.contains(snippetKey)) {
                repository.markLessonCompleted(snippetKey, prevProgress, xpReward = 15)
                val updatedProgress = repository.userProgressSnapshot()
                val badge = checkAndAwardBadges(updatedProgress)
                _rewardCelebration.value = RewardCelebrationEvent(
                    badge = badge,
                    lessonTitle = "Latihan Snippet: $challengeId",
                    structureDisplayName = "Operasi Python",
                    structureEmoji = "⚡",
                    xpGained = 15,
                    isModuleCompleted = false,
                    totalLessonsInModule = 1,
                    completedInModule = 1,
                    celebrationQuote = "Hebat! Kamu berhasil menyelesaikan dan memverifikasi operasi snippet data structure!"
                )
            }
        }
    }

    fun completeLesson(lesson: TopicLesson) {
        viewModelScope.launch {
            val prevProgress = userProgress.value
            val prevCompleted = prevProgress.completedLessonIds.split(",").filter { it.isNotBlank() }.toSet()
            val isNewCompletion = !prevCompleted.contains(lesson.id)

            repository.markLessonCompleted(lesson.id, prevProgress, xpReward = 30)
            
            // Check newly awarded badges or module completion
            val updatedProgress = repository.userProgressSnapshot()
            val newCompleted = updatedProgress.completedLessonIds.split(",").filter { it.isNotBlank() }.toSet()
            val totalInStructure = CurriculumData.allLessons.count { it.structureType == lesson.structureType }
            val completedInStructure = newCompleted.count { id ->
                CurriculumData.allLessons.find { it.id == id }?.structureType == lesson.structureType
            }
            val isModuleComplete = completedInStructure >= totalInStructure

            val newlyUnlockedBadge = checkAndAwardBadges(updatedProgress)

            if (isNewCompletion) {
                val emoji = when (lesson.structureType) {
                    DataStructureType.LIST -> "📜"
                    DataStructureType.TUPLE -> "🔒"
                    DataStructureType.SET -> "⭕"
                    DataStructureType.DICTIONARY -> "📖"
                    DataStructureType.NESTED -> "🌳"
                    DataStructureType.COMPARISON -> "📊"
                }

                _rewardCelebration.value = RewardCelebrationEvent(
                    badge = newlyUnlockedBadge,
                    lessonTitle = lesson.title,
                    structureDisplayName = lesson.structureType.displayName,
                    structureEmoji = emoji,
                    xpGained = if (newlyUnlockedBadge != null) 30 + newlyUnlockedBadge.requiredXp else 30,
                    isModuleCompleted = isModuleComplete,
                    totalLessonsInModule = totalInStructure,
                    completedInModule = completedInStructure,
                    celebrationQuote = if (isModuleComplete) {
                        "Selamat! Kamu telah menguasai seluruh kurikulum ${lesson.structureType.displayName}!"
                    } else {
                        "Pemahaman materi ${lesson.title} tuntas. Pertahankan prestasimu!"
                    }
                )
            }
        }
    }

    fun completeQuiz(quiz: QuizQuestion) {
        viewModelScope.launch {
            repository.recordQuizSuccess(quiz.id, userProgress.value, xpReward = quiz.xpReward)
            val updatedProgress = repository.userProgressSnapshot()
            val newlyUnlocked = checkAndAwardBadges(updatedProgress)
            if (newlyUnlocked != null) {
                _rewardCelebration.value = RewardCelebrationEvent(
                    badge = newlyUnlocked,
                    lessonTitle = "Kuis Berhasil Dijawab!",
                    structureDisplayName = quiz.structureType.displayName,
                    structureEmoji = "🏆",
                    xpGained = quiz.xpReward + newlyUnlocked.requiredXp,
                    celebrationQuote = "Lencana baru berhasil dibuka lewat latihan kuis!"
                )
            }
        }
    }

    fun sendAiQuestion(
        question: String,
        attachCurrentCode: Boolean = false,
        topicContext: String? = null
    ) {
        if (question.isBlank()) return

        val userMsg = ChatMessage(
            id = System.currentTimeMillis().toString(),
            sender = "Siswa",
            text = question,
            isFromUser = true
        )
        _chatMessages.value = _chatMessages.value + userMsg

        viewModelScope.launch {
            _isAiThinking.value = true
            val contextCode = if (attachCurrentCode) _currentCode.value else null
            val execResult = _executionResult.value
            val history = _chatMessages.value

            val aiMsgId = (System.currentTimeMillis() + 1).toString()
            var hasAddedAiMessage = false
            val responseAccumulator = StringBuilder()

            geminiTutor.askTutorStream(
                userMessage = question,
                contextCode = contextCode,
                conversationHistory = history,
                lastExecutionError = if (attachCurrentCode) execResult?.error else null,
                lastExecutionStdout = if (attachCurrentCode) execResult?.stdout else null,
                activeTopicTitle = topicContext,
                onChunkReceived = { chunk ->
                    responseAccumulator.append(chunk)
                    val currentText = responseAccumulator.toString()
                    _isAiThinking.value = false

                    if (!hasAddedAiMessage) {
                        hasAddedAiMessage = true
                        _chatMessages.value = _chatMessages.value + ChatMessage(
                            id = aiMsgId,
                            sender = "AI Tutor",
                            text = currentText,
                            isFromUser = false
                        )
                    } else {
                        _chatMessages.value = _chatMessages.value.map { msg ->
                            if (msg.id == aiMsgId) msg.copy(text = currentText) else msg
                        }
                    }
                }
            )

            _isAiThinking.value = false
        }
    }

    fun clearChatHistory() {
        _chatMessages.value = listOf(
            ChatMessage(
                id = "welcome_ai",
                sender = "AI Tutor",
                text = "Halo! Saya AI Tutor Python DataLab (SMAN 1 Kepanjen). Kamu bisa bertanya apa saja seputar dasar pemrograman Python, struktur data (List, Tuple, Set, Dict), tips belajar koding, atau minta bantuan memeriksa error kodemu!",
                isFromUser = false
            )
        )
    }

    fun askAboutActiveError() {
        val err = _executionResult.value?.error
        if (!err.isNullOrBlank()) {
            sendAiQuestion(
                question = "Mengapa kode saya menghasilkan error berikut dan bagaimana solusi kode yang tepat: $err",
                attachCurrentCode = true
            )
        }
    }

    fun resetProgress() {
        viewModelScope.launch {
            repository.resetAllProgress()
        }
    }

    private suspend fun checkAndAwardBadges(progress: UserProgressEntity): com.example.model.AchievementBadge? {
        val completedLessons = progress.completedLessonIds.split(",").filter { it.isNotBlank() }
        val completedQuizzes = progress.completedQuizIds.split(",").filter { it.isNotBlank() }
        val currentBadges = progress.unlockedBadgeIds.split(",").filter { it.isNotBlank() }.toSet()
        var lastUnlockedBadgeId: String? = null

        if (completedLessons.isNotEmpty() && !currentBadges.contains("b_first_step")) {
            repository.unlockBadge("b_first_step", progress)
            lastUnlockedBadgeId = "b_first_step"
        }
        if (completedLessons.count { it.startsWith("list") } >= 14 && !currentBadges.contains("b_list_master")) {
            repository.unlockBadge("b_list_master", progress)
            lastUnlockedBadgeId = "b_list_master"
        }
        if (completedLessons.count { it.startsWith("tuple") } >= 9 && !currentBadges.contains("b_tuple_guardian")) {
            repository.unlockBadge("b_tuple_guardian", progress)
            lastUnlockedBadgeId = "b_tuple_guardian"
        }
        if (completedLessons.count { it.startsWith("set") } >= 10 && !currentBadges.contains("b_set_maestro")) {
            repository.unlockBadge("b_set_maestro", progress)
            lastUnlockedBadgeId = "b_set_maestro"
        }
        if (completedLessons.count { it.startsWith("dict") } >= 12 && !currentBadges.contains("b_dict_wizard")) {
            repository.unlockBadge("b_dict_wizard", progress)
            lastUnlockedBadgeId = "b_dict_wizard"
        }
        if (completedQuizzes.size >= 5 && !currentBadges.contains("b_quiz_champion")) {
            repository.unlockBadge("b_quiz_champion", progress)
            lastUnlockedBadgeId = "b_quiz_champion"
        }

        return if (lastUnlockedBadgeId != null) {
            CurriculumData.allAchievements.find { it.id == lastUnlockedBadgeId }
        } else null
    }
}
