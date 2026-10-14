package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.ui.theme.AccentAmber
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

@Composable
fun AiTutorScreen(
    viewModel: PythonLabViewModel,
    onBack: (() -> Unit)? = null,
    initialPrompt: String? = null
) {
    val messages by viewModel.chatMessages.collectAsState()
    val isAiThinking by viewModel.isAiThinking.collectAsState()
    val currentCode by viewModel.currentCode.collectAsState()
    val executionResult by viewModel.executionResult.collectAsState()
    val aiTutorOrigin by viewModel.aiTutorOrigin.collectAsState()

    BackHandler(enabled = onBack != null && aiTutorOrigin.sourceScreen != null) {
        onBack?.invoke()
    }

    var inputText by remember { mutableStateOf(initialPrompt ?: "") }
    var attachCode by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size, messages.lastOrNull()?.text?.length) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val quickQuestions = remember(attachCode, executionResult) {
        if (executionResult?.error != null) {
            listOf(
                "Jelaskan penyebab error kode saya dan perbaiki!",
                "Apa kaidah Python yang dilanggar kode saya?",
                "Kapan harus pakai Tuple dibanding List?",
                "Kenapa Set otomatis eliminasi duplikat?"
            )
        } else if (attachCode) {
            listOf(
                "Analisis efisiensi Big-O kode saya!",
                "Apakah kode saya sudah mengikuti best practice?",
                "Bagaimana cara mengoptimalkan struktur data kode ini?",
                "Kapan pakai Tuple vs List vs Set vs Dict?"
            )
        } else {
            listOf(
                "Apa perbedaan List, Tuple, Set, dan Dictionary?",
                "Kapan harus pakai Tuple dibanding List?",
                "Kenapa Set otomatis eliminasi duplikat & operasi Venn?",
                "Bagaimana cara kerja Dictionary (Key-Value) dan dict.get()?",
                "Apa beda append() vs extend() pada List?",
                "Kenapa tuple (1,) butuh tanda koma di akhir?",
                "Mengapa list tidak bisa jadi key dictionary atau elemen set?",
                "Bagaimana contoh struktur data bersarang (Nested)?",
                "Apa itu List Comprehension dan contohnya?",
                "Bagaimana cara menggabungkan dua dictionary?",
                "Apa itu operator dan operan dalam Python?",
                "Tips belajar koding untuk pemula?"
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp)
            .testTag("ai_tutor_screen")
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Header Title & Grounding Verification Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (onBack != null && aiTutorOrigin.sourceScreen != null) {
                    IconButton(
                        onClick = { onBack?.invoke() },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SleekNavySurface)
                            .border(1.dp, SleekNavyBorder, CircleShape)
                            .testTag("ai_tutor_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali ke ${aiTutorOrigin.sourceTitle ?: "Playground"}",
                            tint = LimeSoftDark,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "AI TUTOR ASISTEN",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = LimeSoftDark,
                                letterSpacing = 1.5.sp,
                                fontSize = 10.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = PythonBlueLight.copy(alpha = 0.2f),
                            border = BorderStroke(0.5.dp, PythonBlue.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = PythonBlue,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Python 3.11 Validated",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PythonBlue
                                    )
                                )
                            }
                        }
                    }
                    Text(
                        text = "Tanya Guru AI SMAN 1 Kepanjen",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.clearChatHistory() },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Bersihkan Chat",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = LimeSoftContainer,
                    border = BorderStroke(1.dp, LimeSoft.copy(alpha = 0.45f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = LimeSoftDark,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Aktif",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AccentEmerald
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Origin Context Banner (If opened from Code Playground or Lab)
        if (aiTutorOrigin.sourceScreen != null) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = LimeSoftContainer,
                border = BorderStroke(1.dp, LimeSoft.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onBack?.invoke() }
                    .testTag("ai_tutor_origin_banner")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = LimeSoftDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Konsultasi dari: ${aiTutorOrigin.sourceTitle ?: "Code Playground"}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = LimeSoftDark,
                                fontSize = 12.sp
                            )
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = LimeSoft.copy(alpha = 0.2f),
                        border = BorderStroke(0.5.dp, LimeSoft.copy(alpha = 0.8f))
                    ) {
                        Text(
                            text = "← Kembali",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = LimeSoftDark,
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }
            }
        }

        // Active Error Context Banner (If student code has an execution error in playground)
        if (executionResult?.error != null) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AccentAmber.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, AccentAmber.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.askAboutActiveError()
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = AccentAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Terdeteksi Error di Playground!",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AccentAmber
                            )
                        )
                        Text(
                            text = executionResult?.error?.lines()?.firstOrNull() ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            maxLines = 1
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Tanya AI ➔",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PythonBlue,
                            fontSize = 11.sp
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Quick Suggestions Horizontal Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickQuestions.forEach { prompt ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, SleekNavyBorder),
                    modifier = Modifier.clickable {
                        viewModel.sendAiQuestion(prompt, attachCurrentCode = attachCode)
                    }
                ) {
                    Text(
                        text = prompt,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PythonBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                val isUser = msg.isFromUser
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    if (!isUser) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(PythonBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFFFDE047),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Card(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser) PythonBlue else SleekNavySurface
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isUser) Color.Transparent else SleekNavyBorder
                        ),
                        modifier = Modifier.fillMaxWidth(0.92f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isUser) "Kamu (Siswa)" else "AI Tutor (Hermawan Hidayat, S.Kom.)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isUser) Color(0xFFFDE047) else PythonBlue,
                                        fontSize = 10.sp
                                    )
                                )
                                if (!isUser) {
                                    Text(
                                        text = "Verifikasi Python 3.11",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = TextMuted,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))

                            // Render message with code formatting if present
                            RenderFormattedChatMessage(text = msg.text, isUser = isUser)
                        }
                    }
                }
            }

            if (isAiThinking) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = PythonBlue,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "AI Tutor sedang merumuskan jawaban faktual sesuai Python 3.11...",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(10.dp)) }
        }

        // Context Attachment Status Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (attachCode) LimeSoftContainer else SleekNavySurface,
                border = BorderStroke(1.dp, if (attachCode) LimeSoft else SleekNavyBorder),
                modifier = Modifier.clickable { attachCode = !attachCode }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        tint = if (attachCode) LimeSoftDark else TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (attachCode) "✓ Kode Playground Terlampir (${currentCode.lines().size} baris)" else "+ Lampirkan Kode Playground",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = if (attachCode) FontWeight.Bold else FontWeight.Normal,
                            color = if (attachCode) LimeSoftDark else TextSecondary
                        )
                    )
                }
            }

            if (executionResult?.error != null && attachCode) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AccentAmber.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Traceback error terlampir",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = AccentAmber,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // Input Field Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SleekNavySurface),
            border = BorderStroke(1.dp, SleekNavyBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = if (attachCode) "Tanyakan terkait kode terlampir atau Python..." else "Tanyakan apa saja seputar Python...",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_chat_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 3
                )

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendAiQuestion(inputText, attachCurrentCode = attachCode)
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(LimeSoft)
                        .testTag("ai_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Kirim",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Cleanly formats chat messages: regular markdown-like text with dark syntax blocks
 * for code segments delimited by triple backticks ```.
 */
@Composable
private fun RenderFormattedChatMessage(text: String, isUser: Boolean) {
    if (isUser || !text.contains("```")) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = if (isUser) Color.White else TextPrimary,
                lineHeight = 20.sp
            )
        )
        return
    }

    val segments = text.split("```")
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        for (i in segments.indices) {
            val segment = segments[i]
            if (segment.isBlank()) continue

            if (i % 2 == 1) {
                // Code block
                val cleanCode = segment.trimStart()
                    .removePrefix("python")
                    .removePrefix("py")
                    .trimStart('\r', '\n')
                    .trimEnd()
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SyntaxBackground,
                    border = BorderStroke(0.5.dp, Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PYTHON 3.11",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF94A3B8),
                                    letterSpacing = 1.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = cleanCode,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFA3E635),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.5.sp,
                                lineHeight = 17.sp
                            )
                        )
                    }
                }
            } else {
                // Normal explanatory text
                Text(
                    text = segment.trim(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextPrimary,
                        lineHeight = 20.sp
                    )
                )
            }
        }
    }
}
