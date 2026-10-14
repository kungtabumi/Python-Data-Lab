package com.example.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.engine.LocalPythonInterpreter
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentEmeraldLight
import com.example.ui.theme.DeepNavyDark
import com.example.ui.theme.PythonBlue
import com.example.ui.theme.PythonBlueDark
import com.example.ui.theme.PythonBlueLight
import com.example.ui.theme.PythonYellow
import com.example.ui.theme.PythonYellowDark
import com.example.ui.theme.PythonYellowLight
import com.example.ui.theme.SleekNavyBorder
import com.example.ui.theme.SleekNavyBorderSubtle
import com.example.ui.theme.SleekNavyCard
import com.example.ui.theme.SleekNavySurface
import com.example.ui.theme.SyntaxBackground
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

fun buildNestedCodeSnippet(accessPath: String): String {
    return """
        # Struktur Data Bersarang (Nested Data Structure)
        siswa = {
            "nama": "Andi",
            "nilai": [80, 90, 85],
            "koordinat": (10, 20),
            "ekskul": {"Basket", "Coding"}
        }

        # Mengakses elemen pohon bertingkat:
        hasil = $accessPath
        print("$accessPath ->", hasil)
    """.trimIndent()
}

@Composable
fun VisualNestedContainer(
    onCodeGenerated: ((String) -> Unit)? = null
) {
    var activePath by remember { mutableStateOf("siswa['nilai'][1]") }
    var highlightedNode by remember { mutableStateOf("nilai_1") }
    var localExecutionOutput by remember { mutableStateOf<String?>(null) }
    var isExecutingLocally by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val localInterpreter = remember { LocalPythonInterpreter() }

    val currentGeneratedCode = remember(activePath) {
        buildNestedCodeSnippet(activePath)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("visual_nested_container"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SleekNavySurface),
        border = BorderStroke(1.dp, SleekNavyBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
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
                            .background(PythonBlueDark)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NESTED DATA STRUCTURE TREE",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PythonBlueDark,
                            letterSpacing = 1.sp
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PythonBlue.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, PythonBlue.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "🌟 POHON BERSARANG",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PythonBlueDark,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Hierarki akses bertingkat: Dict ➔ List/Tuple/Set ➔ Elemen Tunggal.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMuted,
                    fontSize = 11.5.sp
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Tree Visualization Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SleekNavyCard)
                    .border(1.dp, SleekNavyBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Level 0: Root Dictionary
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White,
                        border = BorderStroke(2.dp, PythonBlue),
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .padding(bottom = 6.dp)
                            .clickable {
                                highlightedNode = "root"
                                activePath = "siswa"
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "ROOT DICTIONARY: siswa { ... }",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = PythonBlueDark
                                )
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = PythonBlueDark,
                        modifier = Modifier.size(18.dp)
                    )

                    // Level 1: Dictionary Branches
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Branch 1: "nama" -> "Andi" (String)
                        NestedBranchCard(
                            keyLabel = "\"nama\"",
                            typeLabel = "str",
                            isHighlighted = highlightedNode == "nama",
                            onClick = {
                                highlightedNode = "nama"
                                activePath = "siswa['nama']"
                            }
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, SleekNavyBorder)
                            ) {
                                Text(
                                    text = "\"Andi\"",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Branch 2: "nilai" -> LIST [80, 90, 85]
                        val isNilaiBranchActive = highlightedNode.startsWith("nilai")
                        NestedBranchCard(
                            keyLabel = "\"nilai\"",
                            typeLabel = "list [ ]",
                            isHighlighted = isNilaiBranchActive,
                            onClick = {
                                highlightedNode = "nilai"
                                activePath = "siswa['nilai']"
                            }
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(80 to 0, 90 to 1, 85 to 2).forEach { (score, idx) ->
                                    val isChildActive = highlightedNode == "nilai_$idx"
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isChildActive) PythonBlue else Color.White,
                                        border = BorderStroke(
                                            1.dp,
                                            if (isChildActive) PythonBlue else SleekNavyBorder
                                        ),
                                        shadowElevation = if (isChildActive) 2.dp else 1.dp,
                                        modifier = Modifier.clickable {
                                            highlightedNode = "nilai_$idx"
                                            activePath = "siswa['nilai'][$idx]"
                                        }
                                    ) {
                                        Text(
                                            text = "[$idx]: $score",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isChildActive) Color.White else PythonBlueDark
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Branch 3: "koordinat" -> TUPLE (10, 20)
                        NestedBranchCard(
                            keyLabel = "\"koordinat\"",
                            typeLabel = "tuple ( ) 🔒",
                            isHighlighted = highlightedNode.startsWith("koordinat"),
                            onClick = {
                                highlightedNode = "koordinat_0"
                                activePath = "siswa['koordinat'][0]"
                            }
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(10 to 0, 20 to 1).forEach { (valKoor, idx) ->
                                    val isChildActive = highlightedNode == "koordinat_$idx"
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isChildActive) PythonYellowDark else Color.White,
                                        border = BorderStroke(
                                            1.dp,
                                            if (isChildActive) PythonYellowDark else SleekNavyBorder
                                        ),
                                        shadowElevation = if (isChildActive) 2.dp else 1.dp,
                                        modifier = Modifier.clickable {
                                            highlightedNode = "koordinat_$idx"
                                            activePath = "siswa['koordinat'][$idx]"
                                        }
                                    ) {
                                        Text(
                                            text = "($idx): $valKoor",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isChildActive) Color.White else PythonYellowDark
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Branch 4: "ekskul" -> SET {"Basket", "Coding"}
                        NestedBranchCard(
                            keyLabel = "\"ekskul\"",
                            typeLabel = "set { } ⭕",
                            isHighlighted = highlightedNode == "ekskul",
                            onClick = {
                                highlightedNode = "ekskul"
                                activePath = "siswa['ekskul']"
                            }
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (highlightedNode == "ekskul") AccentEmerald else Color.White,
                                border = BorderStroke(
                                    1.dp,
                                    if (highlightedNode == "ekskul") AccentEmerald else SleekNavyBorder
                                )
                            ) {
                                Text(
                                    text = "{\"Basket\", \"Coding\"}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = if (highlightedNode == "ekskul") Color.White else AccentEmerald
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Navigation Path Display
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = SleekNavyCard,
                border = BorderStroke(1.dp, SleekNavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = AccentEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Akses Jalur: $activePath",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Preset Navigation Buttons
            Text(
                text = "⚡ Coba Akses Jalur Bertingkat:",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        highlightedNode = "nilai_1"
                        activePath = "siswa['nilai'][1]"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PythonBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("siswa['nilai'][1] ➔ 90", color = Color.White, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        highlightedNode = "koordinat_0"
                        activePath = "siswa['koordinat'][0]"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PythonYellowDark),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("siswa['koordinat'][0] ➔ 10", color = Color.White, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        highlightedNode = "nama"
                        activePath = "siswa['nama']"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("siswa['nama'] ➔ 'Andi'", color = Color.White, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        highlightedNode = "ekskul"
                        activePath = "siswa['ekskul']"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("siswa['ekskul'] ➔ Set", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Syntax Code Preview
            SyntaxHighlightedCode(
                code = currentGeneratedCode,
                title = "Nested Python Code"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Execute Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        isExecutingLocally = true
                        val res = localInterpreter.executeCode(currentGeneratedCode)
                        localExecutionOutput = res.stdout.ifBlank { res.error ?: "Selesai dieksekusi tanpa output." }
                        isExecutingLocally = false
                        onCodeGenerated?.invoke(currentGeneratedCode)
                    }
                },
                enabled = !isExecutingLocally,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PythonYellow),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("nested_send_console_btn")
            ) {
                if (isExecutingLocally) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = DeepNavyDark,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Mengeksekusi...",
                        color = DeepNavyDark,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = DeepNavyDark
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Jalankan Jalur Akses Ini (Live Output)",
                        color = DeepNavyDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (localExecutionOutput != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SyntaxBackground,
                    border = BorderStroke(1.dp, SleekNavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "HASIL TERMINAL (LIVE):",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF94A3B8),
                                fontSize = 9.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = localExecutionOutput ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFFA3E635),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NestedBranchCard(
    keyLabel: String,
    typeLabel: String,
    isHighlighted: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val bgCol by animateColorAsState(
        targetValue = if (isHighlighted) PythonYellowLight else Color.White,
        animationSpec = tween(300), label = "branchBg"
    )
    val borderCol by animateColorAsState(
        targetValue = if (isHighlighted) PythonYellowDark else SleekNavyBorder,
        animationSpec = tween(300), label = "branchBorder"
    )

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = bgCol,
        border = BorderStroke(if (isHighlighted) 1.5.dp else 1.dp, borderCol),
        shadowElevation = if (isHighlighted) 2.dp else 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = SleekNavyBorderSubtle
                ) {
                    Text(
                        text = keyLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = PythonYellowDark
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "($typeLabel)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                )
            }

            content()
        }
    }
}
