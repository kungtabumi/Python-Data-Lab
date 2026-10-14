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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DataArray
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCoral
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
import com.example.ui.components.InteractiveSnippetExecutionArea
import com.example.ui.theme.SyntaxBackground
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.PythonLabViewModel

enum class PlaygroundTabMode {
    SNIPPET_PRACTICE,
    FULL_EDITOR
}

@Composable
fun CodePlaygroundScreen(
    viewModel: PythonLabViewModel,
    onBack: (() -> Unit)? = null,
    onAskAiAboutError: (String) -> Unit
) {
    val currentCode by viewModel.currentCode.collectAsState()
    val executionResult by viewModel.executionResult.collectAsState()
    val isExecuting by viewModel.isExecuting.collectAsState()
    val savedSnippets by viewModel.savedSnippets.collectAsState()
    val playgroundOrigin by viewModel.playgroundOrigin.collectAsState()

    BackHandler(enabled = onBack != null && playgroundOrigin.sourceScreen != null) {
        onBack?.invoke()
    }

    var activeTabMode by remember { mutableStateOf(PlaygroundTabMode.SNIPPET_PRACTICE) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var snippetTitle by remember { mutableStateOf("") }

    val templates = listOf(
        "List Demo" to """
            # Eksperimen List Siswa
            nilai = [85, 90, 78, 92]
            nilai.append(95)
            print("Daftar Nilai:", nilai)
            print("Rata-rata:", sum(nilai) / len(nilai))
            print("Nilai Tertinggi:", max(nilai))
            print("Slicing [1:3]:", nilai[1:3])
        """.trimIndent(),
        "Tuple Demo" to """
            # Eksperimen Geografis Tuple (Immutable)
            titik = ("SMAN 1 Kepanjen", -8.1306, 112.5719)
            nama, lat, lon = titik
            print(f"Sekolah: {nama}")
            print(f"Koordinat: {lat}, {lon}")
            print("Panjang Tuple:", len(titik))
        """.trimIndent(),
        "Set Demo" to """
            # Eksperimen Ekskul Set (Himpunan Matematika)
            robotik = {"Andi", "Budi", "Citra", "Dedi"}
            pramuka = {"Citra", "Dedi", "Eka", "Fani"}

            print("Irisan (robotik & pramuka):", robotik & pramuka)
            print("Gabungan (robotik | pramuka):", robotik | pramuka)
            print("Selisih (robotik - pramuka):", robotik - pramuka)
            print("Sym Diff (robotik ^ pramuka):", robotik ^ pramuka)
        """.trimIndent(),
        "Dict Demo" to """
            # Eksperimen Data Siswa Dictionary (Key-Value)
            siswa = {
                "nis": "20241101",
                "nama": "Aisyah Putri",
                "jurusan": "Informatika",
                "nilai": [95, 90, 88]
            }
            siswa["nilai"].append(100)
            print("Nama Siswa:", siswa["nama"])
            print("Daftar Nilai:", siswa["nilai"])
            print("Rata-rata Nilai:", sum(siswa["nilai"]) / len(siswa["nilai"]))
            print("Semua Key:", list(siswa.keys()))
        """.trimIndent(),
        "Nested Demo" to """
            # Struktur Data Bersarang (Nested)
            kelas_xi = {
                "nama_kelas": "XI-F",
                "daftar_siswa": [
                    {"nama": "Budi", "skor": 88},
                    {"nama": "Siti", "skor": 94}
                ]
            }
            print("Kelas:", kelas_xi["nama_kelas"])
            print("Siswa ke-1:", kelas_xi["daftar_siswa"][0]["nama"], "Skor:", kelas_xi["daftar_siswa"][0]["skor"])
            print("Siswa ke-2:", kelas_xi["daftar_siswa"][1]["nama"], "Skor:", kelas_xi["daftar_siswa"][1]["skor"])
        """.trimIndent()
    )

    val quickHelpers = listOf(
        "append()" to ".append(\"\")",
        "pop()" to ".pop()",
        "insert()" to ".insert(0, \"\")",
        "len()" to "len()",
        "sum()" to "sum()",
        "max()" to "max()",
        "min()" to "min()",
        "sorted()" to "sorted()",
        "keys()" to ".keys()",
        "values()" to ".values()",
        "items()" to ".items()",
        "print()" to "print()",
        "f\"\"" to "f\"{}\"",
        "[0]" to "[0]",
        "[:]" to "[0:2]",
        "&" to " & ",
        "|" to " | ",
        "-" to " - ",
        "^" to " ^ "
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .imePadding()
            .padding(horizontal = 16.dp)
            .testTag("code_playground_screen")
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Header Title & Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (onBack != null && playgroundOrigin.sourceScreen != null) {
                    IconButton(
                        onClick = { onBack?.invoke() },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SleekNavySurface)
                            .border(1.dp, SleekNavyBorder, CircleShape)
                            .testTag("playground_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali ke ${playgroundOrigin.sourceTitle ?: "Lab"}",
                            tint = LimeSoftDark,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Column {
                    Text(
                        text = "CODE PLAYGROUND",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = LimeSoftDark,
                            letterSpacing = 1.5.sp,
                            fontSize = 10.sp
                        )
                    )
                    Text(
                        text = "Python Execution Engine",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Save Snippet Button
                IconButton(
                    onClick = { showSaveDialog = true },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(SleekNavySurface)
                        .border(1.dp, SleekNavyBorder, CircleShape)
                        .testTag("save_snippet_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "Simpan Kode",
                        tint = LimeSoftDark,
                        modifier = Modifier.size(19.dp)
                    )
                }

                // Reset Template Button
                IconButton(
                    onClick = {
                        viewModel.updateCode(
                            """
                            # Python DataLab Code Editor
                            buah = ["Apel", "Jeruk", "Mangga"]
                            buah.append("Pisang")
                            print("List Buah:", buah)
                            print("Total Item:", len(buah))
                            """.trimIndent()
                        )
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(SleekNavySurface)
                        .border(1.dp, SleekNavyBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = TextSecondary,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Context Banner if navigated from Lab or Lesson
        if (playgroundOrigin.sourceScreen != null) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = LimeSoftContainer,
                border = BorderStroke(1.dp, LimeSoft.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onBack?.invoke() }
                    .testTag("playground_origin_banner")
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
                            imageVector = if (playgroundOrigin.sourceScreen == "lab") Icons.Default.Science else Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = LimeSoftDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kode dari: ${playgroundOrigin.sourceTitle ?: "Lab Interaktif"}",
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
                            text = "← Kembali ke ${if (playgroundOrigin.sourceScreen == "lab") "Lab" else "Materi"}",
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
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Mode Switcher: Latihan Snippet Interaktif vs Editor Bebas (Full IDE)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SleekNavySurface)
                .border(1.dp, SleekNavyBorder, RoundedCornerShape(14.dp))
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val isSnippetMode = activeTabMode == PlaygroundTabMode.SNIPPET_PRACTICE
            val isFullEditorMode = activeTabMode == PlaygroundTabMode.FULL_EDITOR

            Surface(
                shape = RoundedCornerShape(11.dp),
                color = if (isSnippetMode) LimeSoft else Color.Transparent,
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = 44.dp)
                    .clickable { activeTabMode = PlaygroundTabMode.SNIPPET_PRACTICE }
                    .testTag("tab_snippet_practice")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = if (isSnippetMode) Color.White else TextSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Latihan Snippet (13)",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSnippetMode) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSnippetMode) Color.White else TextSecondary,
                            fontSize = 11.5.sp
                        )
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(11.dp),
                color = if (isFullEditorMode) PythonBlue else Color.Transparent,
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = 44.dp)
                    .clickable { activeTabMode = PlaygroundTabMode.FULL_EDITOR }
                    .testTag("tab_full_editor")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        tint = if (isFullEditorMode) Color.White else TextSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Editor Bebas (IDE)",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isFullEditorMode) FontWeight.Bold else FontWeight.Medium,
                            color = if (isFullEditorMode) Color.White else TextSecondary,
                            fontSize = 11.5.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (activeTabMode == PlaygroundTabMode.SNIPPET_PRACTICE) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    InteractiveSnippetExecutionArea(
                        viewModel = viewModel,
                        onOpenInFullEditor = { code ->
                            viewModel.updateCode(code)
                            activeTabMode = PlaygroundTabMode.FULL_EDITOR
                        },
                        onAskAiAboutCode = { code, error ->
                            val prompt = if (!error.isNullOrBlank()) {
                                "Mengapa kode snippet Python ini menghasilkan error berikut dan bagaimana solusinya:\n```python\n$code\n```\nError: $error"
                            } else {
                                "Tolong jelaskan kaidah operasi dan cara kerja kode snippet ini:\n```python\n$code\n```"
                            }
                            onAskAiAboutError(prompt)
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        } else {
            // Template Quick Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                templates.forEach { (label, code) ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, SleekNavyBorder),
                        modifier = Modifier.clickable {
                            viewModel.loadCodeSnippet(code)
                        }
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = PythonBlue,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Editor Area & Console
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
            // Python Code Editor Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("code_editor_card"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = SyntaxBackground),
                    border = BorderStroke(1.dp, SleekNavyBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(AccentCoral)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(AccentAmber)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(AccentEmerald)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "main.py",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 11.sp
                                    )
                                )
                            }

                            Text(
                                text = "Python 3.11 Offline Engine",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFFA3E635),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Syntax Inserter Bar for mobile friendliness
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            quickHelpers.forEach { (label, insertText) ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF1E293B),
                                    border = BorderStroke(1.dp, Color(0xFF334155)),
                                    modifier = Modifier.clickable {
                                        viewModel.updateCode(currentCode + "\n" + insertText)
                                    }
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            color = PythonYellowLight,
                                            fontSize = 10.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Text Field for Coding
                        TextField(
                            value = currentCode,
                            onValueChange = { viewModel.updateCode(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .testTag("python_code_input"),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = Color(0xFFF1F5F9),
                                unfocusedTextColor = Color(0xFFF1F5F9),
                                cursorColor = PythonYellow,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            textStyle = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                lineHeight = 19.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Run Button
                        Button(
                            onClick = { viewModel.runCurrentCode() },
                            enabled = !isExecuting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("run_code_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LimeSoft,
                                contentColor = Color.White
                            )
                        ) {
                            if (isExecuting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Mengeksekusi Kode...", fontWeight = FontWeight.Bold, color = Color.White)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Run",
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Jalankan Kode (Run Python)",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Output Terminal Console Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("output_console_card"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = SleekNavySurface),
                    border = BorderStroke(1.dp, SleekNavyBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = null,
                                    tint = AccentEmerald,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Terminal Output",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                            }

                            if (executionResult != null) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (executionResult?.isSuccess == true) AccentEmerald.copy(alpha = 0.2f) else AccentCoral.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = if (executionResult?.isSuccess == true) "✓ Sukses (${executionResult?.executionTimeMs}ms)" else "✕ Error",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (executionResult?.isSuccess == true) AccentEmerald else AccentCoral,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = SyntaxBackground,
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                        ) {
                            val outputText = when {
                                executionResult == null -> ">>> Klik 'Jalankan Kode' untuk melihat output print() dan state variabel."
                                executionResult?.isSuccess == true -> {
                                    val out = executionResult?.output.orEmpty()
                                    if (out.isBlank()) "(Eksekusi selesai tanpa output print)" else out
                                }
                                else -> executionResult?.errorMessage ?: "SyntaxError"
                            }

                            Text(
                                text = outputText,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = if (executionResult?.isSuccess == false) AccentCoral else AccentEmerald,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                ),
                                modifier = Modifier
                                    .padding(14.dp)
                                    .testTag("console_output_text")
                            )
                        }

                        // If error occurred, show Ask AI button
                        if (executionResult?.isSuccess == false) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    val err = executionResult?.errorMessage ?: "Error"
                                    onAskAiAboutError("Saya mendapatkan error ini saat menjalankan kode Python: $err. Kode saya:\n```python\n$currentCode\n```\nBisakah tolong jelaskan apa penyebabnya dan berikan solusi yang benar?")
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentCoral.copy(alpha = 0.85f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Tanya AI Tutor untuk Perbaiki Error Ini", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Variable Memory Inspector
            if (executionResult?.variables?.isNotEmpty() == true) {
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
                                    imageVector = Icons.Default.DataArray,
                                    contentDescription = null,
                                    tint = PythonBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "🔍 Variabel di Memori (Inspector)",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            executionResult?.variables?.forEach { (varName, varVal) ->
                                val typeName = when (varVal) {
                                    is List<*> -> "list (len=${varVal.size})"
                                    is Set<*> -> "set (len=${varVal.size})"
                                    is Map<*, *> -> "dict (len=${varVal.size})"
                                    is Number -> "number"
                                    is String -> "str (len=${varVal.length})"
                                    is Boolean -> "bool"
                                    else -> varVal?.javaClass?.simpleName ?: "None"
                                }

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    color = SleekNavyBorderSubtle,
                                    border = BorderStroke(1.dp, SleekNavyBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = varName,
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontFamily = FontFamily.Monospace,
                                                    color = PythonBlue,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "($typeName)",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = TextSecondary,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }

                                        Text(
                                            text = varVal.toString().take(35),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                color = TextPrimary
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Saved Snippets List (if any)
            if (savedSnippets.isNotEmpty()) {
                item {
                    Text(
                        text = "🔖 Snippet Kode Tersimpan",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        savedSnippets.forEach { snippet ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = SleekNavySurface),
                                border = BorderStroke(1.dp, SleekNavyBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = snippet.title,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                        )
                                        Text(
                                            text = "${snippet.category} • ID #${snippet.id}",
                                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        IconButton(onClick = { viewModel.loadCodeSnippet(snippet.code) }) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = "Muat", tint = PythonBlue)
                                        }
                                        IconButton(onClick = { viewModel.deleteSnippet(snippet.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = AccentCoral)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(28.dp)) }
        }
        }

        // Save Dialog
        if (showSaveDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showSaveDialog = false },
                title = { Text("Simpan Snippet Python", color = TextPrimary) },
                text = {
                    Column {
                        Text("Beri nama untuk snippet kode ini:", color = TextSecondary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = snippetTitle,
                            onValueChange = { snippetTitle = it },
                            placeholder = { Text("misal: Eksperimen Dict SMAN 1") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (snippetTitle.isNotBlank()) {
                                viewModel.saveSnippet(snippetTitle, "Eksperimen")
                                snippetTitle = ""
                                showSaveDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LimeSoft)
                    ) {
                        Text("Simpan", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showSaveDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = SleekNavyBorderSubtle)
                    ) {
                        Text("Batal", color = TextPrimary)
                    }
                },
                containerColor = SleekNavySurface
            )
        }
    }
}
