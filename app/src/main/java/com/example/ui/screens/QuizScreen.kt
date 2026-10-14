package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CurriculumData
import com.example.model.DataStructureType
import com.example.model.QuizQuestion
import com.example.ui.components.RewardCelebrationDialog
import com.example.ui.components.SyntaxHighlightedCode
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentEmeraldLight
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.LimeSoft
import com.example.ui.theme.LimeSoftContainer
import com.example.ui.theme.LimeSoftDark
import com.example.ui.theme.LimeSoftLight
import com.example.ui.theme.LimeSoftOn
import com.example.ui.theme.PythonBlue
import com.example.ui.theme.PythonBlueDark
import com.example.ui.theme.PythonBlueLight
import com.example.ui.theme.PythonYellow
import com.example.ui.theme.PythonYellowDark
import com.example.ui.theme.SleekNavyBg
import com.example.ui.theme.SleekNavyBorder
import com.example.ui.theme.SleekNavyBorderSubtle
import com.example.ui.theme.SleekNavyCard
import com.example.ui.theme.SleekNavySurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.PythonLabViewModel

@Composable
fun QuizScreen(
    viewModel: PythonLabViewModel,
    onBack: (() -> Unit)? = null
) {
    BackHandler(enabled = onBack != null) {
        onBack?.invoke()
    }

    val progress by viewModel.userProgress.collectAsState()
    val completedQuizzes = progress.completedQuizIds.split(",").filter { it.isNotBlank() }.toSet()
    val allQuizzes = CurriculumData.allQuizzes
    val celebrationReward by viewModel.rewardCelebration.collectAsState()

    celebrationReward?.let { reward ->
        RewardCelebrationDialog(
            reward = reward,
            onDismiss = { viewModel.dismissRewardCelebration() }
        )
    }

    // Active Category Filter: null means All 20 Questions
    var selectedCategory by remember { mutableStateOf<DataStructureType?>(null) }

    val filteredQuizzes by remember(selectedCategory) {
        derivedStateOf {
            if (selectedCategory == null) {
                allQuizzes
            } else {
                allQuizzes.filter { it.structureType == selectedCategory }
            }
        }
    }

    var currentQuizIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }
    var showHint by remember { mutableStateOf(false) }
    var showScoreSummary by remember { mutableStateOf(false) }

    // Session interactive performance tracker
    val sessionAnswerResults = remember { mutableStateMapOf<String, Boolean>() } // questionId -> isCorrect
    var currentStreak by remember { mutableIntStateOf(0) }
    var maxStreak by remember { mutableIntStateOf(0) }

    // Boundary safe index
    val safeIndex = currentQuizIndex.coerceIn(0, (filteredQuizzes.size - 1).coerceAtLeast(0))
    val currentQuiz = filteredQuizzes.getOrNull(safeIndex) ?: allQuizzes.first()
    val isAlreadyDone = completedQuizzes.contains(currentQuiz.id)

    // Completion Dialog
    if (showScoreSummary) {
        QuizCompletionDialog(
            totalQuestions = filteredQuizzes.size,
            correctCount = filteredQuizzes.count { sessionAnswerResults[it.id] == true },
            streak = maxStreak,
            onRestart = {
                sessionAnswerResults.clear()
                currentStreak = 0
                maxStreak = 0
                currentQuizIndex = 0
                selectedOptionIndex = null
                isSubmitted = false
                showHint = false
                showScoreSummary = false
            },
            onDismiss = { showScoreSummary = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp)
            .testTag("quiz_screen")
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // Header Title & Stats Pill
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (onBack != null) {
                    IconButton(
                        onClick = { onBack?.invoke() },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SleekNavySurface)
                            .border(1.dp, SleekNavyBorder, CircleShape)
                            .testTag("quiz_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = LimeSoftDark,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Column {
                    Text(
                        text = "KUIS & EVALUASI PEMAHAMAN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PythonBlueDark,
                            letterSpacing = 1.3.sp,
                            fontSize = 10.sp
                        )
                    )
                    Text(
                        text = "Tantangan 20 Soal Python",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }
            }

            // Streak & Progress Pill
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (currentStreak >= 3) AccentOrange.copy(alpha = 0.15f) else SleekNavyCard,
                border = BorderStroke(1.dp, if (currentStreak >= 3) AccentOrange.copy(alpha = 0.5f) else SleekNavyBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStreak > 0) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = AccentOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "$currentStreak",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AccentOrange,
                                fontSize = 11.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = PythonYellowDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${completedQuizzes.size}/${allQuizzes.size} Selesai",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips (List, Tuple, Set, Dict, Semua)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val categories = listOf(
                null to "Semua (20)",
                DataStructureType.LIST to "📜 List (5)",
                DataStructureType.TUPLE to "🔒 Tuple (5)",
                DataStructureType.SET to "⭕ Set (5)",
                DataStructureType.DICTIONARY to "📖 Dict (5)"
            )

            categories.forEach { (catType, label) ->
                val isSelected = selectedCategory == catType
                val activeBg = if (isSelected) PythonBlue else SleekNavySurface
                val activeText = if (isSelected) Color.White else TextPrimary
                val activeBorder = if (isSelected) PythonBlue else SleekNavyBorder

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = activeBg,
                    border = BorderStroke(1.dp, activeBorder),
                    modifier = Modifier.clickable {
                        if (selectedCategory != catType) {
                            selectedCategory = catType
                            currentQuizIndex = 0
                            selectedOptionIndex = null
                            isSubmitted = false
                            showHint = false
                        }
                    }
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = activeText,
                            fontSize = 11.5.sp
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Interactive Quick Question Jump Bar (Pill 1 .. N)
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = SleekNavySurface,
            border = BorderStroke(1.dp, SleekNavyBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 10.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Soal:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        fontSize = 10.5.sp
                    ),
                    modifier = Modifier.padding(end = 4.dp)
                )

                filteredQuizzes.forEachIndexed { idx, q ->
                    val isCurrent = idx == safeIndex
                    val result = sessionAnswerResults[q.id]
                    val isDone = completedQuizzes.contains(q.id)

                    val pillBg = when {
                        isCurrent -> PythonBlue
                        result == true -> AccentEmeraldLight
                        result == false -> AccentCoral.copy(alpha = 0.15f)
                        isDone -> SleekNavyCard
                        else -> SleekNavyBg
                    }

                    val pillText = when {
                        isCurrent -> Color.White
                        result == true -> AccentEmerald
                        result == false -> AccentCoral
                        isDone -> TextPrimary
                        else -> TextMuted
                    }

                    val pillBorder = when {
                        isCurrent -> PythonBlue
                        result == true -> AccentEmerald
                        result == false -> AccentCoral
                        else -> SleekNavyBorder
                    }

                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(pillBg)
                            .border(BorderStroke(if (isCurrent) 1.5.dp else 1.dp, pillBorder), CircleShape)
                            .clickable {
                                currentQuizIndex = idx
                                selectedOptionIndex = null
                                isSubmitted = false
                                showHint = false
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (result == true) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Benar",
                                tint = AccentEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                        } else if (result == false) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Salah",
                                tint = AccentCoral,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Text(
                                text = "${idx + 1}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                                    color = pillText,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Linear Progress Indicator
        val progressRatio = (safeIndex + 1).toFloat() / filteredQuizzes.size.toFloat()
        LinearProgressIndicator(
            progress = { progressRatio },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = PythonBlue,
            trackColor = SleekNavyBorderSubtle
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Quiz Body Scrollable
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Main Question Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quiz_card"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = SleekNavySurface),
                    border = BorderStroke(1.dp, SleekNavyBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Badge Row: Topic tag, difficulty, XP
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = when (currentQuiz.structureType) {
                                        DataStructureType.LIST -> PythonBlue.copy(alpha = 0.12f)
                                        DataStructureType.TUPLE -> PythonYellowDark.copy(alpha = 0.12f)
                                        DataStructureType.SET -> AccentEmerald.copy(alpha = 0.12f)
                                        DataStructureType.DICTIONARY -> AccentOrange.copy(alpha = 0.12f)
                                        else -> SleekNavyCard
                                    }
                                ) {
                                    Text(
                                        text = "${currentQuiz.structureType.title} • Soal ${safeIndex + 1}/${filteredQuizzes.size}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = when (currentQuiz.structureType) {
                                                DataStructureType.LIST -> PythonBlueDark
                                                DataStructureType.TUPLE -> PythonYellowDark
                                                DataStructureType.SET -> AccentEmerald
                                                DataStructureType.DICTIONARY -> AccentOrange
                                                else -> TextPrimary
                                            },
                                            fontSize = 10.5.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = when (currentQuiz.difficulty) {
                                        "Easy" -> AccentEmerald.copy(alpha = 0.1f)
                                        "Medium" -> PythonYellowDark.copy(alpha = 0.1f)
                                        else -> AccentCoral.copy(alpha = 0.1f)
                                    }
                                ) {
                                    Text(
                                        text = when (currentQuiz.difficulty) {
                                            "Easy" -> "Mudah"
                                            "Medium" -> "Sedang"
                                            else -> "Tantangan"
                                        },
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = when (currentQuiz.difficulty) {
                                                "Easy" -> AccentEmerald
                                                "Medium" -> PythonYellowDark
                                                else -> AccentCoral
                                            },
                                            fontSize = 10.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Stars,
                                    contentDescription = null,
                                    tint = PythonYellowDark,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "+${currentQuiz.xpReward} XP",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PythonYellowDark
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Question Title
                        Text(
                            text = currentQuiz.question,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                lineHeight = 22.sp
                            )
                        )

                        // Code snippet if question includes code
                        if (currentQuiz.codeSnippet != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            SyntaxHighlightedCode(code = currentQuiz.codeSnippet, title = "Potongan Kode Python")
                        }

                        // Interactive Hint Toggle Button
                        if (currentQuiz.hint.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { showHint = !showHint }
                                    .padding(vertical = 4.dp, horizontal = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = "Petunjuk",
                                    tint = if (showHint) PythonYellowDark else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (showHint) "Sembunyikan Petunjuk" else "💡 Butuh Petunjuk Berpikir?",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (showHint) PythonYellowDark else PythonBlueDark,
                                        fontSize = 11.sp
                                    )
                                )
                            }

                            AnimatedVisibility(
                                visible = showHint,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = PythonYellowDark.copy(alpha = 0.08f),
                                    border = BorderStroke(1.dp, PythonYellowDark.copy(alpha = 0.3f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            text = "💡 Clue:",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = PythonYellowDark
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = currentQuiz.hint,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = TextPrimary,
                                                lineHeight = 18.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Interactive Options List
            itemsIndexed(currentQuiz.options) { index, optionText ->
                val isSelected = selectedOptionIndex == index
                val isCorrect = index == currentQuiz.correctOptionIndex

                val borderColor = when {
                    isSubmitted && isCorrect -> AccentEmerald
                    isSubmitted && isSelected && !isCorrect -> AccentCoral
                    isSelected -> PythonBlue
                    else -> SleekNavyBorder
                }

                val cardBg = when {
                    isSubmitted && isCorrect -> AccentEmerald.copy(alpha = 0.12f)
                    isSubmitted && isSelected && !isCorrect -> AccentCoral.copy(alpha = 0.12f)
                    isSelected -> PythonBlue.copy(alpha = 0.08f)
                    else -> SleekNavySurface
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(enabled = !isSubmitted) {
                            selectedOptionIndex = index
                        }
                        .testTag("quiz_option_$index"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(if (isSelected || isSubmitted) 1.5.dp else 1.dp, borderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isSubmitted && isCorrect -> AccentEmerald
                                        isSubmitted && isSelected && !isCorrect -> AccentCoral
                                        isSelected -> PythonBlue
                                        else -> SleekNavyBorderSubtle
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = listOf("A", "B", "C", "D").getOrElse(index) { "$index" },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected || isSubmitted) Color.White else TextPrimary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = optionText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextPrimary,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        if (isSubmitted) {
                            if (isCorrect) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Benar",
                                    tint = AccentEmerald,
                                    modifier = Modifier.size(22.dp)
                                )
                            } else if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Salah",
                                    tint = AccentCoral,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Interactive Feedback & Explanation Card
            item {
                if (isSubmitted) {
                    val wasCorrect = selectedOptionIndex == currentQuiz.correctOptionIndex

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (wasCorrect) AccentEmerald.copy(alpha = 0.08f) else SleekNavySurface
                        ),
                        border = BorderStroke(1.dp, if (wasCorrect) AccentEmerald else SleekNavyBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (wasCorrect) Icons.Default.CheckCircle else Icons.Default.Lightbulb,
                                        contentDescription = null,
                                        tint = if (wasCorrect) AccentEmerald else PythonYellowDark,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (wasCorrect) "Hebat! Jawaban Anda Tepat 🎉" else "Pembahasan Konsep 💡",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (wasCorrect) AccentEmerald else TextPrimary
                                        )
                                    )
                                }

                                if (wasCorrect) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = AccentEmerald.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "+${currentQuiz.xpReward} XP Didapat",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = AccentEmerald
                                            ),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = currentQuiz.explanation,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextPrimary,
                                    lineHeight = 21.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Action Buttons
                if (!isSubmitted) {
                    Button(
                        onClick = {
                            if (selectedOptionIndex != null) {
                                isSubmitted = true
                                val isCorrect = selectedOptionIndex == currentQuiz.correctOptionIndex
                                sessionAnswerResults[currentQuiz.id] = isCorrect

                                if (isCorrect) {
                                    currentStreak++
                                    if (currentStreak > maxStreak) maxStreak = currentStreak
                                    viewModel.completeQuiz(currentQuiz)
                                } else {
                                    currentStreak = 0
                                }
                            }
                        },
                        enabled = selectedOptionIndex != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_quiz_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PythonBlue,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Kirim Jawaban",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                selectedOptionIndex = null
                                isSubmitted = false
                                showHint = false
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, SleekNavyBorder)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Coba Lagi", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = {
                                if (safeIndex < filteredQuizzes.size - 1) {
                                    currentQuizIndex = safeIndex + 1
                                    selectedOptionIndex = null
                                    isSubmitted = false
                                    showHint = false
                                } else {
                                    showScoreSummary = true
                                }
                            },
                            modifier = Modifier
                                .weight(1.4f)
                                .height(50.dp)
                                .testTag("next_quiz_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PythonBlue,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = if (safeIndex < filteredQuizzes.size - 1) "Soal Berikutnya →" else "Lihat Rekap Skor 🏆",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Summary Button if at least 1 question attempted
                if (sessionAnswerResults.isNotEmpty()) {
                    TextButton(
                        onClick = { showScoreSummary = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = PythonBlueDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Buka Rekapitulasi Skor Sesi Ini (${sessionAnswerResults.count { it.value }} / ${filteredQuizzes.size} Benar)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = PythonBlueDark
                            )
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}

@Composable
private fun QuizCompletionDialog(
    totalQuestions: Int,
    correctCount: Int,
    streak: Int,
    onRestart: () -> Unit,
    onDismiss: () -> Unit
) {
    val percentage = if (totalQuestions > 0) ((correctCount.toFloat() / totalQuestions.toFloat()) * 100).toInt() else 0

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onRestart,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PythonBlue)
            ) {
                Text("Ulangi Latihan Kuis", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup", color = TextPrimary)
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = PythonYellowDark,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Rekapitulasi Kuis Python",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Score hero card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PythonBlue.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, PythonBlue.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$percentage%",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = PythonBlueDark
                            )
                        )
                        Text(
                            text = "$correctCount dari $totalQuestions Soal Terjawab Benar",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        )
                        if (streak > 1) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "🔥 Rekor Streak: $streak Beruntun",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AccentOrange
                                )
                            )
                        }
                    }
                }

                Text(
                    text = when {
                        percentage >= 80 -> "Luar biasa! Penguasaan konsep List, Tuple, Set, dan Dictionary Anda sangat matang."
                        percentage >= 50 -> "Bagus! Terus berlatih dan eksplorasi laboratorium visualizer untuk memperkuat pemahaman."
                        else -> "Tetap semangat! Pelajari kembali materi modul dan gunakan visualizer untuk memahami cara kerja memori."
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                )
            }
        },
        containerColor = SleekNavySurface,
        shape = RoundedCornerShape(22.dp)
    )
}
