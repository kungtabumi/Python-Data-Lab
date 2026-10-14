package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.ExecutionResult
import com.example.model.DataStructureType
import com.example.model.SnippetChallenge
import com.example.model.SnippetPracticeRepository
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.LimeSoft
import com.example.ui.theme.LimeSoftContainer
import com.example.ui.theme.LimeSoftDark
import com.example.ui.theme.LimeSoftLight
import com.example.ui.theme.PythonBlue
import com.example.ui.theme.PythonBlueLight
import com.example.ui.theme.SleekNavyBg
import com.example.ui.theme.SleekNavyBorder
import com.example.ui.theme.SleekNavyCard
import com.example.ui.theme.SleekNavySurface
import com.example.ui.theme.SyntaxBackground
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.PythonLabViewModel
import kotlinx.coroutines.launch

/**
 * Interactive Code Snippet Execution Area for practicing and testing
 * small Python Data Structure operations with live evaluation.
 */
@Composable
fun InteractiveSnippetExecutionArea(
    viewModel: PythonLabViewModel,
    onOpenInFullEditor: (String) -> Unit,
    onAskAiAboutCode: (code: String, error: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val allChallenges = remember { SnippetPracticeRepository.allChallenges }

    var selectedCategory by remember { mutableStateOf<DataStructureType?>(null) }
    var selectedChallengeIndex by remember { mutableStateOf(0) }

    val filteredChallenges = remember(selectedCategory) {
        if (selectedCategory == null) allChallenges
        else allChallenges.filter { it.category == selectedCategory }
    }

    val currentChallenge = filteredChallenges.getOrElse(selectedChallengeIndex) { filteredChallenges.first() }

    var snippetCode by remember(currentChallenge.id) { mutableStateOf(currentChallenge.initialCode) }
    var executionResult by remember(currentChallenge.id) { mutableStateOf<ExecutionResult?>(null) }
    var isRunning by remember { mutableStateOf(false) }
    var verificationStatus by remember(currentChallenge.id) { mutableStateOf<Boolean?>(null) }
    var showSolution by remember(currentChallenge.id) { mutableStateOf(false) }
    var showHint by remember(currentChallenge.id) { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Category Filter Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // "Semua" Pill
            val isAllSelected = selectedCategory == null
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isAllSelected) PythonBlue else SleekNavySurface,
                border = BorderStroke(1.dp, if (isAllSelected) PythonBlue else SleekNavyBorder),
                modifier = Modifier.clickable {
                    selectedCategory = null
                    selectedChallengeIndex = 0
                }
            ) {
                Text(
                    text = "Semua (${allChallenges.size})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isAllSelected) Color.White else TextPrimary,
                        fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                )
            }

            // Category-specific Pills
            DataStructureType.values().forEach { type ->
                val isSelected = selectedCategory == type
                val count = allChallenges.count { it.category == type }
                val label = when (type) {
                    DataStructureType.LIST -> "List [ ]"
                    DataStructureType.TUPLE -> "Tuple ( )"
                    DataStructureType.SET -> "Set { }"
                    DataStructureType.DICTIONARY -> "Dict {k:v}"
                    DataStructureType.NESTED -> "Nested [[ ]]"
                    DataStructureType.COMPARISON -> "Komparasi"
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) LimeSoftContainer else SleekNavySurface,
                    border = BorderStroke(1.dp, if (isSelected) LimeSoft else SleekNavyBorder),
                    modifier = Modifier.clickable {
                        selectedCategory = type
                        selectedChallengeIndex = 0
                    }
                ) {
                    Text(
                        text = "$label ($count)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isSelected) LimeSoftDark else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
        }

        // 2. Challenge Selector Horizontal Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filteredChallenges.forEachIndexed { idx, ch ->
                val isCurrent = ch.id == currentChallenge.id
                val diffColor = when (ch.difficulty) {
                    "Mudah" -> AccentEmerald
                    "Sedang" -> AccentAmber
                    else -> AccentCoral
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isCurrent) PythonBlue.copy(alpha = 0.18f) else SleekNavySurface,
                    border = BorderStroke(
                        if (isCurrent) 2.dp else 1.dp,
                        if (isCurrent) PythonBlue else SleekNavyBorder
                    ),
                    modifier = Modifier
                        .clickable { selectedChallengeIndex = idx }
                        .testTag("snippet_challenge_${ch.id}")
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(diffColor)
                            )
                            Text(
                                text = ch.difficulty,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = diffColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.5.sp
                                )
                            )
                        }

                        Text(
                            text = ch.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                color = if (isCurrent) PythonBlue else TextPrimary,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.widthIn(max = 180.dp)
                        )
                    }
                }
            }
        }

        // 3. Challenge Mission Card (Objective & Target)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SleekNavySurface),
            border = BorderStroke(1.dp, SleekNavyBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PythonBlueLight.copy(alpha = 0.2f),
                            border = BorderStroke(0.5.dp, PythonBlue.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = currentChallenge.category.name,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = PythonBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = currentChallenge.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // XP Badge
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = LimeSoftContainer,
                        border = BorderStroke(0.5.dp, LimeSoft)
                    ) {
                        Text(
                            text = "+15 XP",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = LimeSoftDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp
                            ),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "🎯 Target Operasi:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        fontSize = 10.5.sp
                    )
                )
                Text(
                    text = currentChallenge.objective,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextPrimary,
                        lineHeight = 19.sp,
                        fontSize = 13.sp
                    )
                )

                // Hint & Solution responsive toggle buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (showHint) LimeSoftContainer else SleekNavySurface,
                        border = BorderStroke(0.5.dp, if (showHint) LimeSoft else SleekNavyBorder),
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 36.dp)
                            .clickable { showHint = !showHint }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = null,
                                tint = LimeSoftDark,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (showHint) "Tutup Petunjuk" else "Petunjuk Sintaks",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = LimeSoftDark,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (showSolution) AccentAmber.copy(alpha = 0.15f) else SleekNavySurface,
                        border = BorderStroke(0.5.dp, if (showSolution) AccentAmber else SleekNavyBorder),
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 36.dp)
                            .clickable { showSolution = !showSolution }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = AccentAmber,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (showSolution) "Tutup Solusi" else "Kunci Jawaban",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = AccentAmber,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                // Expandable Hint
                AnimatedVisibility(visible = showHint) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = LimeSoftContainer.copy(alpha = 0.6f),
                        border = BorderStroke(0.5.dp, LimeSoft),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text(
                            text = "💡 Petunjuk: ${currentChallenge.hint}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextPrimary,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            ),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                // Expandable Official Solution & CPython Grounding
                AnimatedVisibility(visible = showSolution) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SyntaxBackground,
                        border = BorderStroke(0.5.dp, Color(0xFF334155)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "SOLUSI RESMI PYTHON 3.11",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF94A3B8),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                )
                                Text(
                                    text = "Gunakan Kode Ini ➔",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFFA3E635),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.clickable {
                                        snippetCode = currentChallenge.solutionCode
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentChallenge.solutionCode,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFFA3E635),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "🔬 Kaidah: ${currentChallenge.expectedExplanation}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 10.5.sp,
                                    lineHeight = 15.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // 4. Interactive Monospace Code Editor
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("snippet_editor_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SyntaxBackground),
            border = BorderStroke(1.dp, SleekNavyBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Editor Terminal Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AccentCoral))
                        Spacer(modifier = Modifier.width(5.dp))
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AccentAmber))
                        Spacer(modifier = Modifier.width(5.dp))
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AccentEmerald))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "snippet_${currentChallenge.id}.py",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFFCBD5E1),
                                fontSize = 10.5.sp
                            )
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                snippetCode = currentChallenge.initialCode
                                executionResult = null
                                verificationStatus = null
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(
                            onClick = { onOpenInFullEditor(snippetCode) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInFull,
                                contentDescription = "Buka di Editor Penuh",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Editable Snippet Field
                OutlinedTextField(
                    value = snippetCode,
                    onValueChange = {
                        snippetCode = it
                        verificationStatus = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 140.dp, max = 220.dp)
                        .testTag("snippet_code_input"),
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.5.sp,
                        color = Color(0xFFF1F5F9),
                        lineHeight = 18.sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF334155),
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color(0xFF0B1120),
                        unfocusedContainerColor = Color(0xFF0B1120),
                        focusedTextColor = Color(0xFFF1F5F9),
                        unfocusedTextColor = Color(0xFFF1F5F9)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Quick Syntax Insertion Toolbar
                if (currentChallenge.quickOps.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        currentChallenge.quickOps.forEach { (label, insertSnippet) ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1E293B),
                                border = BorderStroke(0.5.dp, Color(0xFF334155)),
                                modifier = Modifier.clickable {
                                    snippetCode = if (snippetCode.trim().endsWith(":")) {
                                        "$snippetCode\n    $insertSnippet"
                                    } else {
                                        "$snippetCode\n$insertSnippet"
                                    }
                                }
                            ) {
                                Text(
                                    text = "+ $label",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFFA3E635),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Action Control Bar (Run & Verify Buttons)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "Jalankan Snippet" Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        isRunning = true
                        val res = viewModel.executeSnippet(snippetCode)
                        executionResult = res
                        isRunning = false
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = 48.dp)
                    .testTag("run_snippet_btn"),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LimeSoft,
                    contentColor = Color.White
                )
            ) {
                if (isRunning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Mengeksekusi...", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Jalankan",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Jalankan", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, maxLines = 1)
                }
            }

            // "Uji Solusi" Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        isRunning = true
                        val res = viewModel.executeSnippet(snippetCode)
                        executionResult = res
                        val isCorrect = res.error == null && currentChallenge.validator(res)
                        verificationStatus = isCorrect
                        if (isCorrect) {
                            // Award XP to student for successfully mastering this small operation
                            viewModel.recordSnippetChallengeCompleted(currentChallenge.id)
                        }
                        isRunning = false
                    }
                },
                modifier = Modifier
                    .weight(1.1f)
                    .defaultMinSize(minHeight = 48.dp)
                    .testTag("verify_snippet_btn"),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PythonBlue,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Uji",
                    tint = Color(0xFFFDE047),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Uji Solusi", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, maxLines = 1)
            }
        }

        // 6. Verification Feedback Result Banner
        if (verificationStatus != null) {
            val isSuccess = verificationStatus == true
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isSuccess) AccentEmerald.copy(alpha = 0.12f) else AccentCoral.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, if (isSuccess) AccentEmerald else AccentCoral),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isSuccess) Icons.Default.Verified else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (isSuccess) AccentEmerald else AccentCoral,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSuccess) "🎉 Berhasil! Target Tercapai!" else "Belum Sesuai Target Tantangan",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSuccess) AccentEmerald else AccentCoral
                            )
                        )
                    }

                    Text(
                        text = if (isSuccess)
                            "+15 XP telah ditambahkan! ${currentChallenge.expectedExplanation}"
                        else
                            "Periksa kembali instruksi atau klik 'Petunjuk Sintaks'. Pastikan operasi variabel atau output sudah dicetak.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextPrimary,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        )
                    )

                    if (!isSuccess) {
                        OutlinedButton(
                            onClick = {
                                onAskAiAboutCode(snippetCode, executionResult?.error)
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, PythonBlue),
                            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 40.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = PythonBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tanya AI Tutor untuk Solusi", color = PythonBlue, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 7. Live Execution Terminal Output & Memory Variables
        if (executionResult != null) {
            val res = executionResult!!
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SleekNavySurface),
                border = BorderStroke(1.dp, SleekNavyBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = null,
                                tint = PythonBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Hasil Eksekusi Python",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }

                        // Latency Chip
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SleekNavySurface,
                            border = BorderStroke(0.5.dp, SleekNavyBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${res.executionTimeMs} ms",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        color = TextSecondary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Stdout Console Box
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SyntaxBackground,
                        border = BorderStroke(0.5.dp, Color(0xFF334155)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "TERMINAL STDOUT:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 9.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (res.stdout.isBlank() && res.error == null) {
                                Text(
                                    text = "[Kode berhasil dieksekusi tanpa print output. Gunakan print() untuk melihat nilai.]",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF94A3B8),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp
                                    )
                                )
                            } else if (res.stdout.isNotBlank()) {
                                Text(
                                    text = res.stdout,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFFA3E635),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.5.sp,
                                        lineHeight = 17.sp
                                    ),
                                    modifier = Modifier.horizontalScroll(rememberScrollState())
                                )
                            }

                            // Error display inside console if any
                            if (res.error != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "❌ ${res.error}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFFF87171),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.5.sp
                                    ),
                                    modifier = Modifier.horizontalScroll(rememberScrollState())
                                )
                            }
                        }
                    }

                    // Inspected Variables in Memory
                    if (res.variables.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Inspeksi Variabel di Memori (${res.variables.size} item):",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                fontSize = 10.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            res.variables.forEach { (varName, varVal) ->
                                val typeTag = when (varVal) {
                                    is List<*> -> "list[${varVal.size}]"
                                    is Set<*> -> "set[${varVal.size}]"
                                    is Map<*, *> -> "dict[${varVal.size}]"
                                    is Array<*> -> "tuple[${varVal.size}]"
                                    else -> varVal?.javaClass?.simpleName?.lowercase() ?: "none"
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = SleekNavySurface,
                                    border = BorderStroke(1.dp, SleekNavyBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "$varName: ",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = PythonBlue,
                                                fontSize = 11.sp
                                            )
                                        )
                                        Text(
                                            text = "$varVal ",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                color = TextPrimary,
                                                fontSize = 11.sp
                                            )
                                        )
                                        Text(
                                            text = "($typeTag)",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.5.sp,
                                                color = TextSecondary
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
