package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VisualizationType
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentEmeraldLight
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
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun VisualDictContainer(
    initialEntries: Map<String, String> = mapOf(
        "nama" to "Andi",
        "kelas" to "XI-Informatika",
        "nilai" to "90",
        "sekolah" to "SMAN 1"
    ),
    visualizationType: VisualizationType = VisualizationType.DICT_KEY_VALUE_TABLE,
    onCodeGenerated: ((String) -> Unit)? = null
) {
    val dictMap = remember {
        mutableStateMapOf<String, String>().apply { putAll(initialEntries) }
    }
    var activeKeyLookup by remember { mutableStateOf<String?>(null) }
    var actionMessage by remember { mutableStateOf("Dictionary memuat ${dictMap.size} pasangan Key-Value. Ketuk baris untuk lookup.") }
    var generatedCode by remember {
        mutableStateOf(
            """
            siswa = {
                "nama": "Andi",
                "kelas": "XI-Informatika",
                "nilai": 90,
                "sekolah": "SMAN 1"
            }
            print("Nama:", siswa["nama"])
            """.trimIndent()
        )
    }

    fun buildFullScript(actionLine: String): String {
        val entriesStr = dictMap.entries.joinToString(",\n") { "    \"${it.key}\": ${if (it.value.toIntOrNull() != null) it.value else "\"${it.value}\""}" }
        return """
            siswa = {
            $entriesStr
            }
            $actionLine
            print("Isi Siswa:", siswa)
        """.trimIndent()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("visual_dict_container"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SleekNavySurface),
        border = BorderStroke(1.dp, SleekNavyBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Bar
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
                            .background(PythonYellowDark)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DICTIONARY KEY-VALUE MAP",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PythonYellowDark,
                            letterSpacing = 1.sp
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SleekNavyCard,
                    border = BorderStroke(1.dp, PythonBlue.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "{ len: ${dictMap.size} }",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PythonBlueDark,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Setiap Key bertindak sebagai indeks unik berbasis string untuk mengambil Value.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMuted,
                    fontSize = 11.5.sp
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Two-column Table Visualizer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SleekNavyCard)
                    .border(1.dp, SleekNavyBorder, RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Column {
                    // Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .border(1.dp, SleekNavyBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔑 KEY (Unik & Hashable)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = PythonYellowDark,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = PythonBlueDark,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "📦 VALUE (Isi Data)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AccentEmerald,
                                textAlign = TextAlign.End,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Key-Value Rows
                    dictMap.entries.forEach { (key, value) ->
                        val isHighlighted = activeKeyLookup == key
                        val rowBg by animateColorAsState(
                            targetValue = if (isHighlighted) PythonYellowLight else Color.White,
                            animationSpec = tween(250), label = "dictRowBg"
                        )
                        val rowBorder by animateColorAsState(
                            targetValue = if (isHighlighted) PythonYellowDark else SleekNavyBorder,
                            animationSpec = tween(250), label = "dictRowBorder"
                        )

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = rowBg,
                            border = BorderStroke(if (isHighlighted) 2.dp else 1.dp, rowBorder),
                            shadowElevation = if (isHighlighted) 3.dp else 1.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable {
                                    activeKeyLookup = key
                                    actionMessage = "Lookup siswa[\"$key\"] -> '$value'"
                                    val script = buildFullScript("val = siswa[\"$key\"]  # Hasil: '$value'\nprint(f\"siswa['$key']:\", val)")
                                    generatedCode = script
                                    onCodeGenerated?.invoke(script)
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Key Pill
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isHighlighted) PythonYellowDark else SleekNavyBorderSubtle,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isHighlighted) PythonYellowDark else SleekNavyBorder
                                    )
                                ) {
                                    Text(
                                        text = "\"$key\"",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isHighlighted) Color.White else PythonYellowDark,
                                            fontSize = 12.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Text(
                                    text = "➔",
                                    color = if (isHighlighted) PythonYellowDark else TextMuted,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )

                                // Value Pill
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isHighlighted) AccentEmeraldLight else Color(0xFFF8FAFC),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isHighlighted) AccentEmerald else SleekNavyBorder
                                    )
                                ) {
                                    Text(
                                        text = if (value.toIntOrNull() != null) value else "\"$value\"",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isHighlighted) AccentEmerald else TextPrimary,
                                            fontSize = 12.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Log
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = SleekNavyCard,
                border = BorderStroke(1.dp, SleekNavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "🔍 $actionMessage",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PythonBlueDark,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Buttons
            Text(
                text = "⚡ Eksperimen Mutasi Dictionary:",
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
                        dictMap["nilai"] = "95"
                        activeKeyLookup = "nilai"
                        actionMessage = "Update nilai: 90 -> 95"
                        val script = buildFullScript("siswa[\"nilai\"] = 95")
                        generatedCode = script
                        onCodeGenerated?.invoke(script)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PythonBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(Modifier.width(4.dp))
                    Text("siswa['nilai'] = 95", color = Color.White, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        dictMap["hobi"] = "Coding"
                        activeKeyLookup = "hobi"
                        actionMessage = "Menambah pasangan baru 'hobi': 'Coding'"
                        val script = buildFullScript("siswa[\"hobi\"] = \"Coding\"")
                        generatedCode = script
                        onCodeGenerated?.invoke(script)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(Modifier.width(4.dp))
                    Text("Tambah 'hobi'", color = Color.White, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        if (dictMap.containsKey("sekolah")) {
                            dictMap.remove("sekolah")
                            activeKeyLookup = null
                            actionMessage = "del siswa['sekolah'] berhasil dihapus."
                            val script = buildFullScript("del siswa[\"sekolah\"]")
                            generatedCode = script
                            onCodeGenerated?.invoke(script)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCoral),
                    shape = RoundedCornerShape(10.dp),
                    enabled = dictMap.containsKey("sekolah")
                ) {
                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(Modifier.width(4.dp))
                    Text("del['sekolah']", color = Color.White, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = {
                        dictMap.clear()
                        dictMap.putAll(initialEntries)
                        activeKeyLookup = null
                        actionMessage = "Reset ke dictionary awal."
                        val script = buildFullScript("# Dictionary direset\npass")
                        generatedCode = script
                        onCodeGenerated?.invoke(script)
                    },
                    border = BorderStroke(1.dp, SleekNavyBorder),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                    Spacer(Modifier.width(4.dp))
                    Text("Reset", color = TextSecondary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
