package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BubbleChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CurriculumData
import com.example.model.DataStructureType
import com.example.ui.components.AchievementBadgesSection
import com.example.ui.components.RewardCelebrationDialog
import com.example.ui.theme.AccentAmber
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
import com.example.ui.theme.PythonYellowLight
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
fun HomeScreen(
    viewModel: PythonLabViewModel,
    onNavigateToStructure: (DataStructureType) -> Unit,
    onNavigateToPlayground: () -> Unit,
    onNavigateToLab: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToAi: () -> Unit
) {
    val progress by viewModel.userProgress.collectAsState()
    val completedSet = progress.completedLessonIds.split(",").filter { it.isNotBlank() }.toSet()
    val totalLessons = CurriculumData.allLessons.size
    val progressPercentage = if (totalLessons > 0) completedSet.size.toFloat() / totalLessons.toFloat() else 0f
    val progressPercentInt = (progressPercentage * 100).toInt()
    val userLevel = (progress.totalXp / 150) + 1
    val unlockedBadgeIds = progress.unlockedBadgeIds.split(",").filter { it.isNotBlank() }.toSet()
    val celebrationReward by viewModel.rewardCelebration.collectAsState()

    val badgeListWithStatus = CurriculumData.allAchievements.map { badge ->
        badge.copy(isUnlocked = unlockedBadgeIds.contains(badge.id))
    }

    celebrationReward?.let { reward ->
        RewardCelebrationDialog(
            reward = reward,
            onDismiss = { viewModel.dismissRewardCelebration() }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp)
            .testTag("home_screen_scroll"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Sleek Header Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PYTHON DATALAB",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = LimeSoftDark,
                            letterSpacing = 2.sp,
                            fontSize = 10.sp
                        )
                    )
                    Text(
                        text = "Dashboard Belajar",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Streak Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SleekNavySurface,
                        shadowElevation = 2.dp,
                        border = BorderStroke(1.dp, LimeSoft.copy(alpha = 0.35f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Streak",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextSecondary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 9.sp
                                    )
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${progress.streakDays} Hari",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = LimeSoftDark,
                                            fontSize = 12.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(text = "🔥", fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // Avatar Circle with gradient
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .border(2.dp, LimeSoft, CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(PythonBlueLight, PythonBlueDark)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "XI",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Section: Sleek Progress Card (Matching Design)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sleek_progress_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SleekNavySurface),
                border = BorderStroke(1.dp, SleekNavyBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Progres Belajar",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "$progressPercentInt%",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Selesai (${completedSet.size}/$totalLessons)",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondary
                                    ),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }

                        // Circular XP badge
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .border(2.5.dp, LimeSoft, CircleShape)
                                .background(LimeSoftLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${progress.totalXp}",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = LimeSoftDark,
                                        fontSize = 11.sp
                                    )
                                )
                                Text(
                                    text = "XP",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 8.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Bar
                    LinearProgressIndicator(
                        progress = { progressPercentage },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = LimeSoft,
                        trackColor = LimeSoftContainer
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Structure status pill tags
                    val listDone = completedSet.count { it.startsWith("list") } >= 10
                    val tupleDone = completedSet.count { it.startsWith("tuple") } >= 6
                    val setDone = completedSet.count { it.startsWith("set") } >= 6
                    val dictDone = completedSet.count { it.startsWith("dict") } >= 8

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusPill(label = "List", isCompleted = listDone)
                        StatusPill(label = "Tuple", isCompleted = tupleDone)
                        StatusPill(label = "Set", isCompleted = setDone)
                        StatusPill(label = "Dict", isCompleted = dictDone)
                    }
                }
            }
        }

        // Section: Sleek Hero Lab Visualizer Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sleek_lab_hero_card"),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = SleekNavySurface),
                border = BorderStroke(1.dp, SleekNavyBorder)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🧪 LAB VISUALIZER",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = LimeSoftDark,
                                letterSpacing = 1.sp
                            )
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = LimeSoftLight,
                            border = BorderStroke(1.dp, LimeSoft.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "buah.append('Pisang')",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = LimeSoftDark,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Memory visual elements row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        VisualItemBox(value = "Apel", index = 0, isHighlighted = false)
                        VisualItemBox(value = "Jeruk", index = 1, isHighlighted = true)
                        VisualItemBox(value = "Mangga", index = 2, isHighlighted = false)
                        VisualItemGhostBox(value = "+", index = 3)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Live In/Out Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF0F172A),
                        border = BorderStroke(1.dp, Color(0xFF1E293B))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "In: ",
                                    color = Color(0xFFA3E635),
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "buah[1]",
                                    color = Color.White,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Out: ",
                                    color = Color(0xFF34D399),
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "'Jeruk'",
                                    color = Color.White,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onNavigateToLab,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("launch_experiment_btn"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LimeSoft,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Jalankan Eksperimen Interaktif",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Section: Recommendation / Daily Mission Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNavigateToQuiz)
                    .testTag("recommendation_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SleekNavySurface),
                border = BorderStroke(1.dp, SleekNavyBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(LimeSoftContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "💡", fontSize = 18.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "REKOMENDASI BELAJAR",
                                style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = LimeSoftDark,
                                letterSpacing = 1.sp,
                                fontSize = 9.sp
                            )
                        )
                            Text(
                                text = "Uji pemahamanmu di Kuis Tantangan Data Structure!",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = LimeSoft,
                        modifier = Modifier.clickable(onClick = onNavigateToQuiz)
                    ) {
                        Text(
                            text = "Mulai",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Featured Interactive Snippet Drill Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNavigateToPlayground)
                    .testTag("featured_snippet_drill_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SleekNavySurface),
                border = BorderStroke(1.dp, SleekNavyBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PythonBlueLight.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "⚡", fontSize = 18.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "AREA LATIHAN INTERAKTIF",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PythonBlue,
                                        letterSpacing = 1.sp,
                                        fontSize = 9.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = LimeSoftContainer
                                ) {
                                    Text(
                                        text = "13 Drill",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = LimeSoftDark,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 8.5.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Latih operasi List, Tuple, Set & Dict langsung di aplikasi!",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = PythonBlue,
                        modifier = Modifier.clickable(onClick = onNavigateToPlayground)
                    ) {
                        Text(
                            text = "Latih",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Section: 4 Struktur Data Python
        item {
            Text(
                text = "📚 4 Pilar Struktur Data Python",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                ),
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        // 1. LIST CARD
        item {
            val listCompletedCount = completedSet.count { it.startsWith("list") }
            SleekStructureCard(
                title = "1. List [ ... ]",
                type = DataStructureType.LIST,
                totalTopics = 14,
                completedTopics = listCompletedCount,
                tagline = "Mutable • Ordered • Indexable • Duplikat OK",
                icon = Icons.Default.FormatListNumbered,
                accentColor = PythonBlueLight,
                onClick = { onNavigateToStructure(DataStructureType.LIST) }
            )
        }

        // 2. TUPLE CARD
        item {
            val tupleCompletedCount = completedSet.count { it.startsWith("tuple") }
            SleekStructureCard(
                title = "2. Tuple ( ... ) 🔒",
                type = DataStructureType.TUPLE,
                totalTopics = 9,
                completedTopics = tupleCompletedCount,
                tagline = "Immutable • Ordered • Read-Only • Hemat Memori",
                icon = Icons.Default.Lock,
                accentColor = LimeSoft,
                onClick = { onNavigateToStructure(DataStructureType.TUPLE) }
            )
        }

        // 3. SET CARD
        item {
            val setCompletedCount = completedSet.count { it.startsWith("set") }
            SleekStructureCard(
                title = "3. Set { ... } ⭕",
                type = DataStructureType.SET,
                totalTopics = 10,
                completedTopics = setCompletedCount,
                tagline = "Unik (No Duplicates) • Unordered • Operasi Venn",
                icon = Icons.Default.BubbleChart,
                accentColor = AccentEmerald,
                onClick = { onNavigateToStructure(DataStructureType.SET) }
            )
        }

        // 4. DICTIONARY CARD
        item {
            val dictCompletedCount = completedSet.count { it.startsWith("dict") }
            SleekStructureCard(
                title = "4. Dictionary { key: val } 📖",
                type = DataStructureType.DICTIONARY,
                totalTopics = 12,
                completedTopics = dictCompletedCount,
                tagline = "Key-Value Pairs • Hash Map • Akses O(1)",
                icon = Icons.AutoMirrored.Filled.MenuBook,
                accentColor = AccentOrange,
                onClick = { onNavigateToStructure(DataStructureType.DICTIONARY) }
            )
        }

        // Nested & Comparison Matrix Links
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToStructure(DataStructureType.NESTED) },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SleekNavySurface),
                    border = BorderStroke(1.dp, SleekNavyBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(LimeSoftContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AccountTree, contentDescription = null, tint = LimeSoftDark, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Nested Tree",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                        )
                        Text(
                            text = "Dict dalam List / Set",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToStructure(DataStructureType.COMPARISON) },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SleekNavySurface),
                    border = BorderStroke(1.dp, SleekNavyBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PythonBlue.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.TableChart, contentDescription = null, tint = PythonBlue, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Matriks Komparasi",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                        )
                        Text(
                            text = "List vs Tuple vs Set vs Dict",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                        )
                    }
                }
            }
        }

        // AI Tutor Card Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNavigateToAi)
                    .testTag("ai_tutor_banner_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = SleekNavySurface),
                border = BorderStroke(1.dp, SleekNavyBorder)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(LimeSoftContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = LimeSoftDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tanya AI Tutor Python",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Tanyakan konsep struktur data atau error Python kapan saja",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = LimeSoftDark
                    )
                }
            }
        }

        // Achievements & Badges Showcase
        item {
            AchievementBadgesSection(
                badges = badgeListWithStatus,
                onBadgeClick = { badge ->
                    if (badge.isUnlocked) {
                        viewModel.triggerBadgeCelebration(badge)
                    }
                }
            )
        }

        item { Spacer(modifier = Modifier.height(28.dp)) }
    }
}

@Composable
fun StatusPill(label: String, isCompleted: Boolean) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (isCompleted) AccentEmerald.copy(alpha = 0.12f) else SleekNavyCard,
        border = BorderStroke(1.dp, if (isCompleted) AccentEmerald.copy(alpha = 0.3f) else SleekNavyBorder)
    ) {
        Text(
            text = if (isCompleted) "✓ $label" else "○ $label",
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (isCompleted) AccentEmerald else TextSecondary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun VisualItemBox(value: String, index: Int, isHighlighted: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (isHighlighted) LimeSoftContainer else SleekNavyCard)
                .border(
                    width = if (isHighlighted) 2.dp else 1.dp,
                    color = if (isHighlighted) LimeSoft else SleekNavyBorder,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isHighlighted) LimeSoftDark else TextPrimary,
                    fontSize = 12.sp
                )
            )
        }
        Text(
            text = "[$index]",
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                color = if (isHighlighted) LimeSoftDark else TextSecondary,
                fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
                fontSize = 10.sp
            )
        )
    }
}

@Composable
fun VisualItemGhostBox(value: String, index: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SleekNavyCard)
                .border(
                    width = 1.dp,
                    color = SleekNavyBorder,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
        }
        Text(
            text = "[$index]",
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                color = TextSecondary,
                fontSize = 10.sp
            )
        )
    }
}

@Composable
fun SleekStructureCard(
    title: String,
    type: DataStructureType,
    totalTopics: Int,
    completedTopics: Int,
    tagline: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("structure_card_${type.name.lowercase()}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SleekNavySurface),
        border = BorderStroke(1.dp, SleekNavyBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(accentColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "$completedTopics / $totalTopics Selesai",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (completedTopics == totalTopics) LimeSoftDark else TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = accentColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = tagline,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.5.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            val progressRatio = if (totalTopics > 0) completedTopics.toFloat() / totalTopics.toFloat() else 0f
            LinearProgressIndicator(
                progress = { progressRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(CircleShape),
                color = accentColor,
                trackColor = SleekNavyBorderSubtle
            )
        }
    }
}
