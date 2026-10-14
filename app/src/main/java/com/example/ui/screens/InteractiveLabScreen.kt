package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.ExecutionResult
import com.example.engine.LocalPythonInterpreter
import com.example.model.DataStructureType
import com.example.model.VisualizationType
import com.example.ui.components.ComparisonMatrixTable
import com.example.ui.components.SyntaxHighlightedCode
import com.example.ui.components.VisualDictContainer
import com.example.ui.components.VisualNestedContainer
import com.example.ui.components.VisualSetContainer
import com.example.ui.components.VisualTupleContainer
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.LimeSoft
import com.example.ui.theme.LimeSoftDark
import com.example.ui.theme.LimeSoftLight
import com.example.ui.theme.LimeSoftOn
import com.example.ui.theme.PythonBlue
import com.example.ui.theme.PythonBlueDark
import com.example.ui.theme.PythonBlueLight
import com.example.ui.theme.PythonYellow
import com.example.ui.theme.PythonYellowDark
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
import kotlin.math.roundToInt

@Composable
fun InteractiveLabScreen(
    viewModel: PythonLabViewModel,
    initialTab: DataStructureType = DataStructureType.LIST,
    onBack: (() -> Unit)? = null,
    onNavigateToPlaygroundWithCode: ((String) -> Unit)? = null
) {
    val selectedStructure by viewModel.activeLabStructure.collectAsState()

    androidx.compose.runtime.LaunchedEffect(initialTab) {
        if (initialTab != DataStructureType.LIST || viewModel.activeLabStructure.value == DataStructureType.LIST) {
            viewModel.setActiveLabStructure(initialTab)
        }
    }

    BackHandler(enabled = onBack != null) {
        onBack?.invoke()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp)
            .testTag("interactive_lab_screen")
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Header Title
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
                            .testTag("interactive_lab_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali ke Beranda",
                            tint = LimeSoftDark,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Column {
                    Text(
                        text = "LABORATORIUM INTERAKTIF",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = LimeSoftDark,
                            letterSpacing = 1.5.sp,
                            fontSize = 10.sp
                        )
                    )
                    Text(
                        text = "Visualizer & Eksperimen Memori",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Structure Tab Selectors
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val tabs = listOf(
                DataStructureType.LIST,
                DataStructureType.TUPLE,
                DataStructureType.SET,
                DataStructureType.DICTIONARY,
                DataStructureType.NESTED,
                DataStructureType.COMPARISON
            )

            tabs.forEach { type ->
                val isSelected = selectedStructure == type
                val color = when (type) {
                    DataStructureType.LIST -> PythonBlue
                    DataStructureType.TUPLE -> PythonYellow
                    DataStructureType.SET -> AccentEmerald
                    DataStructureType.DICTIONARY -> AccentOrange
                    DataStructureType.NESTED -> AccentPurple
                    DataStructureType.COMPARISON -> PythonBlueDark
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) color.copy(alpha = 0.15f) else SleekNavySurface,
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) color else SleekNavyBorder
                    ),
                    modifier = Modifier
                        .clickable { viewModel.setActiveLabStructure(type) }
                        .testTag("lab_tab_${type.name.lowercase()}")
                ) {
                    Text(
                        text = type.displayName,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) color else TextSecondary
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Main Visualizer Body according to selected structure - runs and stays inside Lab
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (selectedStructure) {
                DataStructureType.LIST -> {
                    item {
                        InteractiveListLab()
                    }
                }
                DataStructureType.TUPLE -> {
                    item {
                        InteractiveTupleLab()
                    }
                }
                DataStructureType.SET -> {
                    item {
                        InteractiveSetLab()
                    }
                }
                DataStructureType.DICTIONARY -> {
                    item {
                        InteractiveDictLab()
                    }
                }
                DataStructureType.NESTED -> {
                    item {
                        VisualNestedContainer(
                            onCodeGenerated = null
                        )
                    }
                }
                DataStructureType.COMPARISON -> {
                    item {
                        ComparisonMatrixTable()
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}

// =========================================================================
// IN-LAB CODE EXECUTION & LIVE TERMINAL CONSOLE
// Memungkinkan siswa menjalankan kode Python langsung di Lab tanpa diarahkan ke Playground
// =========================================================================
@Composable
fun LabCodeExecutionConsole(
    code: String,
    title: String,
    themeColor: Color,
    onCodeExecuted: ((ExecutionResult) -> Unit)? = null
) {
    val coroutineScope = rememberCoroutineScope()
    val localInterpreter = remember { LocalPythonInterpreter() }
    val clipboardManager = LocalClipboardManager.current

    var userCustomCode by remember(code) { mutableStateOf(code) }
    var isEditingMode by remember { mutableStateOf(false) }
    var isExecuting by remember { mutableStateOf(false) }
    var executionResult by remember { mutableStateOf<ExecutionResult?>(null) }
    var isCopied by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("lab_code_execution_console"),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0D1527),
        border = BorderStroke(1.dp, SleekNavyBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Title, Mode toggle, Copy button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        tint = themeColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Edit Mode Toggle
                    IconButton(
                        onClick = {
                            isEditingMode = !isEditingMode
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isEditingMode) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = if (isEditingMode) "Selesai Edit" else "Edit Kode Bebas",
                            tint = if (isEditingMode) themeColor else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Reset Code Button
                    IconButton(
                        onClick = {
                            userCustomCode = code
                            isEditingMode = false
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Kode",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Copy Code Button
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(userCustomCode))
                            isCopied = true
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Salin Kode",
                            tint = if (isCopied) AccentEmerald else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Code View / Editor Box
            if (isEditingMode) {
                Column {
                    Text(
                        text = "✏️ Mode Edit: Kamu dapat mengetik dan mencoba kode Python sendiri di Lab!",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = themeColor,
                            fontSize = 11.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = userCustomCode,
                        onValueChange = { userCustomCode = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("lab_code_editor_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SyntaxBackground,
                            unfocusedContainerColor = SyntaxBackground,
                            focusedBorderColor = themeColor,
                            unfocusedBorderColor = SleekNavyBorder,
                            focusedTextColor = Color(0xFFF8FAFC),
                            unfocusedTextColor = Color(0xFFF8FAFC)
                        ),
                        textStyle = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        ),
                        minLines = 4,
                        maxLines = 10
                    )
                }
            } else {
                SyntaxHighlightedCode(
                    code = userCustomCode,
                    title = "Python Lab Code",
                    showLineNumbers = true
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action: Run Code in Lab Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        isExecuting = true
                        val res = localInterpreter.execute(userCustomCode)
                        executionResult = res
                        isExecuting = false
                        onCodeExecuted?.invoke(res)
                    }
                },
                enabled = !isExecuting,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = themeColor,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("run_code_in_lab_btn")
            ) {
                if (isExecuting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Menjalankan di Lab...",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "▶ Jalankan Kode di Lab (Live Output)",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Live Terminal Console Window
            executionResult?.let { result ->
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SyntaxBackground,
                    border = BorderStroke(
                        1.dp,
                        if (result.error != null) AccentCoral else SleekNavyBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("lab_terminal_output_box")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Terminal Top Header Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFEF4444)))
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFF59E0B)))
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF10B981)))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "TERMINAL LAB (LIVE)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (result.error != null) AccentCoral.copy(alpha = 0.2f) else AccentEmerald.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (result.error != null) "● ERROR (${result.executionTimeMs}ms)" else "✓ SUKSES (${result.executionTimeMs}ms)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = if (result.error != null) AccentCoral else AccentEmerald,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Terminal prompt
                        Text(
                            text = "$ python3 lab_visualizer.py",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF64748B),
                                fontSize = 11.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Stdout or Error
                        if (result.stdout.isNotBlank()) {
                            Text(
                                text = result.stdout,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFFA3E635),
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                            )
                        } else if (result.error == null) {
                            Text(
                                text = "(Kode selesai dieksekusi tanpa error. Visualizer sinkron!)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            )
                        }

                        if (result.error != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = result.error ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = AccentCoral,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveListLab(onSendToConsole: ((String) -> Unit)? = null) {
    val items = remember { mutableStateListOf("Apel", "Jeruk", "Mangga") }
    var inputItem by remember { mutableStateOf("") }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var isNegativeIndexMode by remember { mutableStateOf(false) }
    var sliceStart by remember { mutableStateOf(0) }
    var sliceEnd by remember { mutableStateOf(2) }
    var showSlicingHighlight by remember { mutableStateOf(false) }

    var generatedCode by remember {
        mutableStateOf("buah = ['Apel', 'Jeruk', 'Mangga']\nprint('List awal:', buah)")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SleekNavySurface),
        border = BorderStroke(1.dp, SleekNavyBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📦 State Memori List (Mutable)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PythonBlueLight
                        )
                    )
                    Text(
                        text = "Ukuran: ${items.size} elemen • Dapat diubah bebas",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = { isNegativeIndexMode = !isNegativeIndexMode }) {
                        Icon(
                            Icons.Default.SwapHoriz,
                            contentDescription = "Toggle Index Mode",
                            tint = if (isNegativeIndexMode) PythonYellow else TextMuted
                        )
                    }
                    IconButton(onClick = {
                        items.clear()
                        items.addAll(listOf("Apel", "Jeruk", "Mangga"))
                        selectedIndex = null
                        showSlicingHighlight = false
                        generatedCode = "buah = ['Apel', 'Jeruk', 'Mangga']\nprint('Reset list:', buah)"
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = TextMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Visual List Elements Grid / Horizontal Scroll
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    val isSelected = selectedIndex == index
                    val isSliced = showSlicingHighlight && index >= sliceStart && index < sliceEnd

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            selectedIndex = index
                            val negIdx = index - items.size
                            generatedCode = "buah = ${items.joinToString(prefix = "['", postfix = "']", separator = "', '")}\n# Mengakses indeks $index (${negIdx}):\nelemen = buah[$index]\nprint(f'buah[{index}] = {elemen}')"
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    when {
                                        isSelected -> PythonYellow.copy(alpha = 0.25f)
                                        isSliced -> PythonBlueLight.copy(alpha = 0.2f)
                                        else -> SleekNavyCard
                                    }
                                )
                                .border(
                                    1.5.dp,
                                    when {
                                        isSelected -> PythonYellow
                                        isSliced -> PythonBlue
                                        else -> SleekNavyBorder
                                    },
                                    RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color(0xFFB45309) else TextPrimary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isNegativeIndexMode) "[${index - items.size}]" else "[$index]",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = if (isNegativeIndexMode) AccentAmber else PythonYellow,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Operation 1: Append and Insert
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputItem,
                    onValueChange = { inputItem = it },
                    placeholder = { Text("Nama item baru...", color = TextMuted, fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PythonBlue,
                        unfocusedBorderColor = SleekNavyBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Button(
                    onClick = {
                        val name = if (inputItem.isBlank()) "Pisang" else inputItem
                        items.add(name)
                        generatedCode = "buah = ${items.joinToString(prefix = "['", postfix = "']", separator = "', '")}\nbuah.append('$name')\nprint('Setelah append:', buah)"
                        inputItem = ""
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PythonBlue)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("append()")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Operation Buttons: pop, sort, reverse, remove
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = {
                        if (items.isNotEmpty()) {
                            val removed = items.removeAt(items.size - 1)
                            generatedCode = "buah = ${items.joinToString(prefix = "['", postfix = "']", separator = "', '")}\npopped = buah.pop() # '$removed'\nprint('Sisa list:', buah)"
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SleekNavyCard),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("pop()", fontSize = 11.sp, color = AccentCoral)
                }

                Button(
                    onClick = {
                        if (items.isNotEmpty()) {
                            items.sort()
                            generatedCode = "buah = ${items.joinToString(prefix = "['", postfix = "']", separator = "', '")}\nbuah.sort()\nprint('Urutan A-Z:', buah)"
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SleekNavyCard),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("sort()", fontSize = 11.sp, color = AccentEmerald)
                }

                Button(
                    onClick = {
                        if (items.isNotEmpty()) {
                            items.reverse()
                            generatedCode = "buah = ${items.joinToString(prefix = "['", postfix = "']", separator = "', '")}\nbuah.reverse()\nprint('Dibalik:', buah)"
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SleekNavyCard),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("reverse()", fontSize = 11.sp, color = PythonYellow)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Slicing Demo Bar
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SleekNavyCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "✂️ Slicing Range: buah[$sliceStart:$sliceEnd]",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PythonBlueDark
                            )
                        )
                        Text(
                            text = "Mengambil elemen dari indeks $sliceStart hingga ${sliceEnd - 1}",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp)
                        )
                    }

                    Button(
                        onClick = {
                            showSlicingHighlight = true
                            val sliceRes = items.subList(sliceStart.coerceIn(0, items.size), sliceEnd.coerceIn(0, items.size))
                            generatedCode = "buah = ${items.joinToString(prefix = "['", postfix = "']", separator = "', '")}\nhasil_slice = buah[$sliceStart:$sliceEnd]\nprint('Hasil Slicing:', hasil_slice)"
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PythonBlue,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Coba Slice", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Spacer(modifier = Modifier.height(14.dp))

            // Lab Code Execution Console (Integrated in Lab - Zero Redirects)
            LabCodeExecutionConsole(
                code = generatedCode,
                title = "List Operations & Console",
                themeColor = PythonBlue,
                onCodeExecuted = { res ->
                    val varBuah = res.variables["buah"]
                    if (varBuah is List<*>) {
                        val mapped = varBuah.mapNotNull { it?.toString() }
                        if (mapped.isNotEmpty()) {
                            items.clear()
                            items.addAll(mapped)
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun InteractiveTupleLab(onSendToConsole: ((String) -> Unit)? = null) {
    val items = remember { listOf("SMAN 1 Kepanjen", "-8.1306", "112.5719") }
    var showTypeErrorAlert by remember { mutableStateOf(false) }
    val shakeOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    var generatedCode by remember {
        mutableStateOf(
            """
            # Eksperimen Tuple (Immutable)
            titik = ("SMAN 1 Kepanjen", -8.1306, 112.5719)
            nama, lat, lon = titik
            print(f"Sekolah: {nama}")
            print(f"Koordinat: {lat}, {lon}")
            """.trimIndent()
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SleekNavySurface),
        border = BorderStroke(1.dp, SleekNavyBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🔒 State Memori Tuple (Immutable)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Konstan & Terkunci • Read-Only di Memori",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PythonYellowLight,
                    border = BorderStroke(1.dp, PythonYellow.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = PythonYellowDark, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("LOCKED", color = PythonYellowDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Visual Tuple Locked Memory Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
            ) {
                VisualTupleContainer(
                    initialItems = items,
                    visualizationType = VisualizationType.TUPLE_LOCKED
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stress Test: Try Modifying Tuple
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (showTypeErrorAlert) AccentCoral.copy(alpha = 0.1f) else SleekNavyCard,
                border = BorderStroke(1.dp, if (showTypeErrorAlert) AccentCoral else SleekNavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "⚠️ Uji Coba Modifikasi Tuple (Tes Immutability):",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (showTypeErrorAlert) AccentCoral else TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Jika kita mencoba menjalankan `titik[0] = 'SMAN 2'`, Python akan menolak dengan TypeError!",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 11.sp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                showTypeErrorAlert = true
                                scope.launch {
                                    shakeOffset.animateTo(12f, tween(50))
                                    shakeOffset.animateTo(-12f, tween(50))
                                    shakeOffset.animateTo(8f, tween(50))
                                    shakeOffset.animateTo(0f, tween(50))
                                }
                                generatedCode = "titik = ('SMAN 1 Kepanjen', -8.1306, 112.5719)\n# Mencoba mengubah data tuple:\ntitik[0] = 'SMAN Lain' # 💥 TypeError: 'tuple' object does not support item assignment"
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentCoral,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Coba Modifikasi titik[0]", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                showTypeErrorAlert = false
                                generatedCode = "titik = ('SMAN 1 Kepanjen', -8.1306, 112.5719)\n# Tuple Unpacking:\nnama, lat, lon = titik\nprint(f'Sekolah: {nama}')\nprint(f'Lat: {lat}, Lon: {lon}')"
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PythonBlue,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Unpacking tuple", fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Lab Code Execution Console (Integrated in Lab - Zero Redirects)
            LabCodeExecutionConsole(
                code = generatedCode,
                title = "Tuple Immutability & Unpacking",
                themeColor = PythonYellowDark
            )
        }
    }
}

@Composable
fun InteractiveSetLab(onSendToConsole: ((String) -> Unit)? = null) {
    var selectedOp by remember { mutableStateOf("union") }
    var inputNewMember by remember { mutableStateOf("") }
    var duplicateAlertMsg by remember { mutableStateOf<String?>(null) }

    val setA = remember { mutableStateListOf("Andi", "Budi", "Citra", "Dedi") }
    val setB = remember { mutableStateListOf("Citra", "Dedi", "Eka", "Fani") }

    val currentCode = when (selectedOp) {
        "union" -> "A = {${setA.joinToString { "'$it'" }}}\nB = {${setB.joinToString { "'$it'" }}}\n\n# Union (Gabungan A | B):\nhasil = A | B\nprint('Semua Anggota (A | B):', hasil)"
        "intersection" -> "A = {${setA.joinToString { "'$it'" }}}\nB = {${setB.joinToString { "'$it'" }}}\n\n# Intersection (Irisan A & B):\nhasil = A & B\nprint('Anggota Ganda (A & B):', hasil)"
        "diff" -> "A = {${setA.joinToString { "'$it'" }}}\nB = {${setB.joinToString { "'$it'" }}}\n\n# Difference (Selisih A - B):\nhasil = A - B\nprint('Hanya di Robotik (A - B):', hasil)"
        else -> "A = {${setA.joinToString { "'$it'" }}}\nB = {${setB.joinToString { "'$it'" }}}\n\n# Symmetric Difference (A ^ B):\nhasil = A ^ B\nprint('Anggota Eksklusif (A ^ B):', hasil)"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SleekNavySurface),
        border = BorderStroke(1.dp, SleekNavyBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "⭕ Set Interaktif (Himpunan Matematika)",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = AccentEmerald
                )
            )
            Text(
                text = "Unik (No Duplicates) • Tak Berurutan (Unordered)",
                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Operation Selector Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val ops = listOf(
                    "union" to "Gabungan (|)",
                    "intersection" to "Irisan (&)",
                    "diff" to "Selisih (-)",
                    "sym_diff" to "Sym Diff (^)"
                )
                ops.forEach { (key, label) ->
                    val isSel = selectedOp == key
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSel) AccentEmerald.copy(alpha = 0.15f) else SleekNavyCard,
                        border = BorderStroke(1.dp, if (isSel) AccentEmerald else SleekNavyBorder),
                        modifier = Modifier.clickable { selectedOp = key }
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSel) AccentEmerald else TextSecondary,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Visual Venn Component
            VisualSetContainer(
                visualizationType = when (selectedOp) {
                    "union" -> VisualizationType.SET_VENN_UNION
                    "intersection" -> VisualizationType.SET_VENN_INTERSECTION
                    "diff" -> VisualizationType.SET_VENN_DIFFERENCE
                    else -> VisualizationType.SET_VENN_SYMMETRIC_DIFF
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Duplicate Elimination Demonstration
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SleekNavyCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "🧪 Uji Coba Pencegahan Duplikasi (Auto-Deduplicate):",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputNewMember,
                            onValueChange = { inputNewMember = it },
                            placeholder = { Text("misal: Andi atau Gina", fontSize = 11.sp, color = TextMuted) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Button(
                            onClick = {
                                val name = if (inputNewMember.isBlank()) "Andi" else inputNewMember
                                if (setA.contains(name)) {
                                    duplicateAlertMsg = "⚠️ '$name' sudah ada di Set A! Set otomatis mengabaikan duplikasi."
                                } else {
                                    setA.add(name)
                                    duplicateAlertMsg = "✓ '$name' berhasil ditambahkan ke Set A."
                                }
                                inputNewMember = ""
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentEmerald,
                                contentColor = Color.White
                            )
                        ) {
                            Text("A.add()", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (duplicateAlertMsg != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = duplicateAlertMsg ?: "",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (duplicateAlertMsg?.startsWith("⚠️") == true) AccentAmber else AccentEmerald,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Lab Code Execution Console (Integrated in Lab - Zero Redirects)
            LabCodeExecutionConsole(
                code = currentCode,
                title = "Set Operations & Logic",
                themeColor = AccentEmerald,
                onCodeExecuted = { res ->
                    val varA = res.variables["A"]
                    if (varA is Set<*>) {
                        val mapped = varA.mapNotNull { it?.toString() }
                        if (mapped.isNotEmpty()) {
                            setA.clear()
                            setA.addAll(mapped)
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun InteractiveDictLab(onSendToConsole: ((String) -> Unit)? = null) {
    val dictData = remember {
        mutableStateListOf(
            "nis" to "20241101",
            "nama" to "Aisyah Putri",
            "kelas" to "XI IPA 1",
            "jurusan" to "Informatika"
        )
    }

    var newKey by remember { mutableStateOf("") }
    var newVal by remember { mutableStateOf("") }

    val dictCode = "siswa = {\n" +
            dictData.joinToString(",\n") { "    '${it.first}': '${it.second}'" } +
            "\n}\n\n# Mengakses Key:\nprint('Nama:', siswa['nama'])\nprint('Semua Keys:', list(siswa.keys()))\nprint('Semua Values:', list(siswa.values()))"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SleekNavySurface),
        border = BorderStroke(1.dp, SleekNavyBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "📖 Dictionary Interaktif (Key-Value)",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = AccentOrange
                )
            )
            Text(
                text = "Pemetaan Kunci-Nilai • Akses Instan O(1)",
                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Key-Value Interactive Table
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SleekNavyCard,
                border = BorderStroke(1.dp, SleekNavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("KEY (Kunci Unik)", color = PythonYellowDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("VALUE (Nilai)", color = AccentEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    dictData.forEachIndexed { idx, pair ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, SleekNavyBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "'${pair.first}'",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                        color = PythonYellowDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "'${pair.second}'",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontFamily = FontFamily.Monospace,
                                            color = TextPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = { dictData.removeAt(idx) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = AccentCoral, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Add Key-Value Form
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newKey,
                    onValueChange = { newKey = it },
                    placeholder = { Text("Key", fontSize = 11.sp, color = TextMuted) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                OutlinedTextField(
                    value = newVal,
                    onValueChange = { newVal = it },
                    placeholder = { Text("Value", fontSize = 11.sp, color = TextMuted) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Button(
                    onClick = {
                        if (newKey.isNotBlank() && newVal.isNotBlank()) {
                            val existingIdx = dictData.indexOfFirst { it.first == newKey }
                            if (existingIdx != -1) {
                                dictData[existingIdx] = newKey to newVal
                            } else {
                                dictData.add(newKey to newVal)
                            }
                            newKey = ""
                            newVal = ""
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
                ) {
                    Text("dict[k]=v", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Lab Code Execution Console (Integrated in Lab - Zero Redirects)
            LabCodeExecutionConsole(
                code = dictCode,
                title = "Dictionary Mapping & Console",
                themeColor = AccentOrange,
                onCodeExecuted = { res ->
                    val varSiswa = res.variables["siswa"]
                    if (varSiswa is Map<*, *>) {
                        val mapped = varSiswa.entries.mapNotNull { (k, v) ->
                            if (k != null && v != null) k.toString() to v.toString() else null
                        }
                        if (mapped.isNotEmpty()) {
                            dictData.clear()
                            dictData.addAll(mapped)
                        }
                    }
                }
            )
        }
    }
}
