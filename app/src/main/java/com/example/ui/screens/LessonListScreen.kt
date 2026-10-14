package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CurriculumData
import com.example.model.DataStructureType
import com.example.model.TopicLesson
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.LimeSoft
import com.example.ui.theme.LimeSoftContainer
import com.example.ui.theme.LimeSoftDark
import com.example.ui.theme.LimeSoftLight
import com.example.ui.theme.LimeSoftOn
import com.example.ui.theme.PythonBlue
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
fun LessonListScreen(
    viewModel: PythonLabViewModel,
    initialFilter: DataStructureType? = null,
    onBack: (() -> Unit)? = null,
    onSelectLesson: (TopicLesson) -> Unit
) {
    val filterFromVm by viewModel.selectedModuleFilter.collectAsState()
    var selectedFilter by remember { mutableStateOf<DataStructureType?>(initialFilter ?: filterFromVm) }

    androidx.compose.runtime.LaunchedEffect(initialFilter) {
        if (initialFilter != null) {
            selectedFilter = initialFilter
            viewModel.setSelectedModuleFilter(initialFilter)
        } else if (filterFromVm != null) {
            selectedFilter = filterFromVm
        }
    }

    BackHandler(enabled = onBack != null && selectedFilter != null) {
        if (selectedFilter != null) {
            selectedFilter = null
            viewModel.setSelectedModuleFilter(null)
        } else {
            onBack?.invoke()
        }
    }

    val progress by viewModel.userProgress.collectAsState()
    val completedSet = progress.completedLessonIds.split(",").filter { it.isNotBlank() }.toSet()

    val filteredLessons = remember(selectedFilter) {
        if (selectedFilter == null) {
            CurriculumData.allLessons
        } else {
            CurriculumData.allLessons.filter { it.structureType == selectedFilter }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp)
            .testTag("lesson_list_screen")
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Title Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null && selectedFilter != null) {
                IconButton(
                    onClick = {
                        selectedFilter = null
                        viewModel.setSelectedModuleFilter(null)
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SleekNavySurface)
                        .border(1.dp, SleekNavyBorder, CircleShape)
                        .testTag("modules_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Tampilkan Semua Materi",
                        tint = LimeSoftDark,
                        modifier = Modifier.size(19.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
            }

            Column {
                Text(
                    text = "KURIKULUM MATERI",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = LimeSoftDark,
                        letterSpacing = 1.5.sp,
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = if (selectedFilter != null) "Modul ${selectedFilter?.displayName}" else "Modul Pembelajaran Python",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "4 Struktur Data Utama Kelas XI SMAN 1 Kepanjen",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedFilter == null,
                onClick = {
                    selectedFilter = null
                    viewModel.setSelectedModuleFilter(null)
                },
                label = { Text("Semua (${CurriculumData.allLessons.size})") },
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = LimeSoft,
                    selectedLabelColor = Color.White,
                    containerColor = SleekNavyCard,
                    labelColor = TextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedFilter == null,
                    borderColor = SleekNavyBorder,
                    selectedBorderColor = LimeSoft
                )
            )

            DataStructureType.values().forEach { type ->
                val count = CurriculumData.allLessons.count { it.structureType == type }
                val isSelected = selectedFilter == type
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedFilter = type
                        viewModel.setSelectedModuleFilter(type)
                    },
                    label = { Text("${type.displayName} ($count)") },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LimeSoft,
                        selectedLabelColor = Color.White,
                        containerColor = SleekNavyCard,
                        labelColor = TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = SleekNavyBorder,
                        selectedBorderColor = LimeSoft
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // List of Lessons
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredLessons, key = { it.id }) { lesson ->
                val isCompleted = completedSet.contains(lesson.id)

                val accentColor = when (lesson.structureType) {
                    DataStructureType.LIST -> PythonBlue
                    DataStructureType.TUPLE -> LimeSoftDark
                    DataStructureType.SET -> AccentEmerald
                    DataStructureType.DICTIONARY -> AccentOrange
                    DataStructureType.NESTED -> PythonYellowLight
                    DataStructureType.COMPARISON -> PythonBlue
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectLesson(lesson) }
                        .testTag("lesson_item_${lesson.id}"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCompleted) LimeSoftLight else SleekNavySurface
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isCompleted) LimeSoft.copy(alpha = 0.5f) else SleekNavyBorder
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Completion Status Icon
                        Icon(
                            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = if (isCompleted) "Selesai" else "Belum",
                            tint = if (isCompleted) LimeSoft else TextMuted,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = accentColor.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = lesson.structureType.displayName,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = accentColor,
                                            fontSize = 10.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = lesson.title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = lesson.summary,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                ),
                                maxLines = 2
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}
