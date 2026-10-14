package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.ExecutionResult
import com.example.model.TopicLesson
import com.example.model.VisualizationType
import com.example.ui.components.ComparisonMatrixTable
import com.example.ui.components.SyntaxHighlightedCode
import com.example.ui.components.VisualDictContainer
import com.example.ui.components.VisualListContainer
import com.example.ui.components.VisualNestedContainer
import com.example.ui.components.VisualSetContainer
import com.example.ui.components.VisualTupleContainer
import com.example.ui.components.RewardCelebrationDialog
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.LimeSoft
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
import com.example.ui.theme.SyntaxBackground
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.PythonLabViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonDetailScreen(
    lesson: TopicLesson,
    viewModel: PythonLabViewModel,
    onBack: () -> Unit,
    onOpenInPlayground: (String) -> Unit
) {
    val progress by viewModel.userProgress.collectAsState()
    val isCompleted = progress.completedLessonIds.split(",").contains(lesson.id)
    val celebrationReward by viewModel.rewardCelebration.collectAsState()

    var isExecuting by remember { mutableStateOf(false) }
    var executionResult by remember(lesson.id) { mutableStateOf<ExecutionResult?>(null) }
    val coroutineScope = rememberCoroutineScope()

    celebrationReward?.let { reward ->
        RewardCelebrationDialog(
            reward = reward,
            onDismiss = { viewModel.dismissRewardCelebration() }
        )
    }

    BackHandler {
        onBack()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = lesson.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            ),
                            maxLines = 1
                        )
                        Text(
                            text = lesson.structureType.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(color = LimeSoftDark)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = TextPrimary
                        )
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .testTag("lesson_detail_scroll"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // 1. VISUALIZER COMPONENT
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🔬 Visualisasi Memori Interaktif",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Sentuh elemen kotak atau tombol aksi di bawah untuk mengeksplorasi memori.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMuted,
                        fontSize = 11.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))

                when (lesson.visualizationType) {
                    VisualizationType.LIST_INDEX,
                    VisualizationType.LIST_NEGATIVE_INDEX,
                    VisualizationType.LIST_SLICING,
                    VisualizationType.LIST_MUTATION -> {
                        VisualListContainer(
                            initialItems = lesson.initialDataState,
                            visualizationType = lesson.visualizationType,
                            isInteractiveLab = true,
                            onCodeGenerated = { code -> }
                        )
                    }
                    VisualizationType.TUPLE_LOCKED,
                    VisualizationType.TUPLE_UNPACKING,
                    VisualizationType.TUPLE_IMMUTABLE_ERROR -> {
                        VisualTupleContainer(
                            initialItems = lesson.initialDataState,
                            visualizationType = lesson.visualizationType
                        )
                    }
                    VisualizationType.SET_DUPLICATE_ELIMINATION,
                    VisualizationType.SET_VENN_UNION,
                    VisualizationType.SET_VENN_INTERSECTION,
                    VisualizationType.SET_VENN_DIFFERENCE,
                    VisualizationType.SET_VENN_SYMMETRIC_DIFF -> {
                        VisualSetContainer(visualizationType = lesson.visualizationType)
                    }
                    VisualizationType.DICT_KEY_VALUE_TABLE,
                    VisualizationType.DICT_KEY_LOOKUP,
                    VisualizationType.DICT_MUTATION -> {
                        VisualDictContainer(visualizationType = lesson.visualizationType)
                    }
                    VisualizationType.NESTED_TREE_PATH -> {
                        VisualNestedContainer()
                    }
                    VisualizationType.COMPARISON_TABLE -> {
                        ComparisonMatrixTable()
                    }
                }
            }

            // 2. EXPLANATION & THEORY
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = SleekNavySurface),
                    border = BorderStroke(1.dp, SleekNavyBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = PythonBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Penjelasan Materi",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = lesson.explanation,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextPrimary,
                                lineHeight = 22.sp
                            )
                        )
                    }
                }
            }

            // 3. CODE SNIPPET & IN-LESSON LIVE CONSOLE
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "💻 Contoh Kode Python",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Jalankan langsung di materi tanpa berpindah ke tab Lab.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMuted,
                                fontSize = 11.5.sp
                            )
                        )
                    }

                    if (executionResult != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (executionResult!!.isSuccess) AccentEmerald.copy(alpha = 0.15f) else AccentCoral.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, if (executionResult!!.isSuccess) AccentEmerald else AccentCoral)
                        ) {
                            Text(
                                text = if (executionResult!!.isSuccess) "✓ Selesai" else "⚠ Error",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (executionResult!!.isSuccess) AccentEmerald else AccentCoral,
                                    fontSize = 10.5.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                SyntaxHighlightedCode(code = lesson.codeExample, title = lesson.title)

                Spacer(modifier = Modifier.height(10.dp))

                // Control Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isExecuting = true
                                val result = viewModel.executeSnippet(lesson.codeExample)
                                executionResult = result
                                isExecuting = false
                            }
                        },
                        enabled = !isExecuting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PythonBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("run_in_lesson_btn")
                    ) {
                        if (isExecuting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Mengeksekusi...",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        } else {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (executionResult == null) "Jalankan di Materi Ini" else "Jalankan Ulang",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Optional button to open in full code lab if student wants to freely code
                    OutlinedButton(
                        onClick = {
                            viewModel.loadCodeSnippet(lesson.codeExample)
                            onOpenInPlayground(lesson.codeExample)
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PythonBlue),
                        border = BorderStroke(1.dp, SleekNavyBorder),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("open_lab_from_lesson_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            tint = PythonBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Buka di Lab",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PythonBlue
                        )
                    }
                }

                // Live Terminal Output Box right inside the lesson!
                if (executionResult != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    val res = executionResult!!
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("lesson_terminal_output_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SyntaxBackground),
                        border = BorderStroke(
                            1.dp,
                            if (res.isSuccess) AccentEmerald.copy(alpha = 0.5f) else AccentCoral.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Terminal Top Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(Color(0xFFFF5F56)))
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(Color(0xFFFFBD2E)))
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(Color(0xFF27C93F)))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "output terminal python (live)",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            color = Color(0xFF94A3B8),
                                            fontSize = 10.5.sp
                                        )
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF1E293B),
                                        border = BorderStroke(0.5.dp, Color(0xFF334155))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Speed,
                                                contentDescription = null,
                                                tint = Color(0xFF94A3B8),
                                                modifier = Modifier.size(11.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "${res.executionTimeMs} ms",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 10.sp,
                                                    color = Color(0xFF94A3B8),
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    IconButton(
                                        onClick = { executionResult = null },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Tutup Output",
                                            tint = Color(0xFF94A3B8),
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Stdout Output Text
                            if (res.stdout.isNotBlank()) {
                                Text(
                                    text = res.stdout,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFA3E635),
                                        fontSize = 12.5.sp,
                                        lineHeight = 18.sp
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState())
                                )
                            } else if (res.error == null) {
                                Text(
                                    text = "[Program berhasil dijalankan tanpa pesan output print]",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.5.sp
                                    )
                                )
                            }

                            // Error Output if any
                            if (res.error != null) {
                                if (res.stdout.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                Text(
                                    text = "Traceback (most recent call last):\n  ${res.error}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFF87171),
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState())
                                )

                                // Educational note for Tuple immutability lesson
                                if (lesson.visualizationType == VisualizationType.TUPLE_IMMUTABLE_ERROR ||
                                    lesson.codeExample.contains("data[0] =") ||
                                    res.error?.contains("tuple") == true) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF1E293B),
                                        border = BorderStroke(0.5.dp, AccentAmber)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "💡 Perhatikan: TypeError ini membuktikan bahwa Tuple bersifat Immutable (nilainya terkunci & tidak dapat diganti).",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = Color(0xFFFCD34D),
                                                    fontSize = 11.sp,
                                                    lineHeight = 15.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            // Variable Inspector in terminal
                            if (res.variables.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = Color(0xFF334155), thickness = 0.5.dp)
                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "MEMORI VARIABEL:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF94A3B8),
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    res.variables.forEach { (varName, value) ->
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFF1E293B),
                                            border = BorderStroke(0.5.dp, Color(0xFF475569))
                                        ) {
                                            Text(
                                                text = "$varName = $value",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontFamily = FontFamily.Monospace,
                                                    color = Color(0xFF7DD3FC),
                                                    fontSize = 10.5.sp
                                                ),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. PRO-TIPS & KEY TAKEAWAYS
            if (lesson.proTips.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = SleekNavySurface),
                        border = BorderStroke(1.dp, AccentAmber.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = AccentAmber,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Tips Guru & Best Practice",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AccentAmber
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            lesson.proTips.forEach { tip ->
                                Row(
                                    modifier = Modifier.padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text("• ", color = AccentAmber, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = tip,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextPrimary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. COMPLETE LESSON & CLAIM XP BUTTON
            item {
                Button(
                    onClick = { viewModel.completeLesson(lesson) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCompleted) AccentEmerald else LimeSoft,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 52.dp)
                        .testTag("complete_lesson_btn")
                ) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isCompleted) "Materi Selesai (XP Sudah Diklaim)" else "Tandai Selesai & Klaim +30 XP",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(28.dp)) }
        }
    }
}
