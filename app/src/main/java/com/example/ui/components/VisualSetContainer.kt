package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BubbleChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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

enum class VennOperation {
    UNION,
    INTERSECTION,
    DIFFERENCE_A_B,
    DIFFERENCE_B_A,
    SYMMETRIC_DIFFERENCE,
    NONE
}

@Composable
fun VisualSetContainer(
    visualizationType: VisualizationType = VisualizationType.SET_VENN_UNION,
    onCodeGenerated: ((String) -> Unit)? = null
) {
    val initialOp = when (visualizationType) {
        VisualizationType.SET_VENN_INTERSECTION -> VennOperation.INTERSECTION
        VisualizationType.SET_VENN_DIFFERENCE -> VennOperation.DIFFERENCE_A_B
        VisualizationType.SET_VENN_SYMMETRIC_DIFF -> VennOperation.SYMMETRIC_DIFFERENCE
        else -> VennOperation.UNION
    }
    var selectedOp by remember(visualizationType) { mutableStateOf(initialOp) }
    var deduplicateStep by remember { mutableIntStateOf(1) }
    val isDeduplicationDemo = visualizationType == VisualizationType.SET_DUPLICATE_ELIMINATION

    val setA = remember { setOf(1, 2, 3, 4) }
    val setB = remember { setOf(3, 4, 5, 6) }

    val resultSet = remember(selectedOp) {
        when (selectedOp) {
            VennOperation.UNION -> setA union setB
            VennOperation.INTERSECTION -> setA intersect setB
            VennOperation.DIFFERENCE_A_B -> setA subtract setB
            VennOperation.DIFFERENCE_B_A -> setB subtract setA
            VennOperation.SYMMETRIC_DIFFERENCE -> (setA subtract setB) union (setB subtract setA)
            VennOperation.NONE -> emptySet()
        }
    }

    val pythonCode = remember(selectedOp, isDeduplicationDemo, deduplicateStep) {
        if (isDeduplicationDemo) {
            when (deduplicateStep) {
                1 -> "# Langkah 1: Data list dengan elemen duplikat\ndata_list = [1, 2, 3, 3, 4]"
                2 -> "# Langkah 2: Konversi ke set() memfilter duplikat otomatis\nhimpunan = set(data_list)"
                else -> "# Langkah 3: Elemen bernilai sama disatukan\nprint(himpunan)  # Hasil: {1, 2, 3, 4}"
            }
        } else {
            when (selectedOp) {
                VennOperation.UNION -> "a = {1, 2, 3, 4}\nb = {3, 4, 5, 6}\nhasil = a | b  # Union (Gabungan): {1, 2, 3, 4, 5, 6}"
                VennOperation.INTERSECTION -> "a = {1, 2, 3, 4}\nb = {3, 4, 5, 6}\nhasil = a & b  # Irisan (Intersection): {3, 4}"
                VennOperation.DIFFERENCE_A_B -> "a = {1, 2, 3, 4}\nb = {3, 4, 5, 6}\nhasil = a - b  # Selisih A - B: {1, 2}"
                VennOperation.DIFFERENCE_B_A -> "a = {1, 2, 3, 4}\nb = {3, 4, 5, 6}\nhasil = b - a  # Selisih B - A: {5, 6}"
                VennOperation.SYMMETRIC_DIFFERENCE -> "a = {1, 2, 3, 4}\nb = {3, 4, 5, 6}\nhasil = a ^ b  # Symmetric Diff: {1, 2, 5, 6}"
                VennOperation.NONE -> "a = {1, 2, 3, 4}\nb = {3, 4, 5, 6}"
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("visual_set_container"),
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
                            .background(AccentEmerald)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isDeduplicationDemo) "PROSES DEDUPLIKASI SET" else "DIAGRAM VENN & OPERASI SET",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AccentEmerald,
                            letterSpacing = 1.sp
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AccentEmeraldLight,
                    border = BorderStroke(1.dp, AccentEmerald.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "✨ UNIK & NO-INDEX",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AccentEmerald,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isDeduplicationDemo)
                    "Python Set secara otomatis membuang duplikasi dengan algoritma hashing."
                else
                    "Operasi himpunan matematika diimplementasikan dalam operator bitwise Python.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMuted,
                    fontSize = 11.5.sp
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Switch between Deduplication Demo and Venn Diagram
            if (isDeduplicationDemo) {
                // Deduplication Visualizer Box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SleekNavyCard)
                        .border(1.dp, SleekNavyBorder, RoundedCornerShape(14.dp))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Step Indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("1. Input Data", "2. Deteksi Duplikat", "3. Hasil Unik").forEachIndexed { index, stepName ->
                            val stepNumber = index + 1
                            val isCurrent = deduplicateStep == stepNumber
                            val isDone = deduplicateStep > stepNumber

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when {
                                    isCurrent -> AccentEmerald
                                    isDone -> AccentEmeraldLight
                                    else -> SleekNavyBorderSubtle
                                }
                            ) {
                                Text(
                                    text = stepName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = when {
                                            isCurrent -> Color.White
                                            isDone -> AccentEmerald
                                            else -> TextMuted
                                        },
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Elements visual circles
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("1", "2", "3", "3", "4").forEachIndexed { idx, value ->
                            val isDuplicate = idx == 3
                            val opacity by animateFloatAsState(
                                targetValue = if (isDuplicate && deduplicateStep >= 2) 0.15f else 1f,
                                animationSpec = tween(500), label = "op"
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .padding(horizontal = 5.dp)
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isDuplicate && deduplicateStep == 2) AccentCoral.copy(alpha = 0.15f)
                                            else Color.White
                                        )
                                        .border(
                                            2.dp,
                                            if (isDuplicate && deduplicateStep == 2) AccentCoral
                                            else if (isDuplicate && deduplicateStep == 3) SleekNavyBorder.copy(alpha = 0.3f)
                                            else PythonBlue,
                                            CircleShape
                                        )
                                ) {
                                    Text(
                                        text = value,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDuplicate && deduplicateStep == 2) AccentCoral
                                            else if (isDuplicate && deduplicateStep == 3) TextMuted.copy(alpha = opacity)
                                            else TextPrimary
                                        )
                                    )
                                }

                                if (isDuplicate) {
                                    Text(
                                        text = if (deduplicateStep == 1) "sama" else if (deduplicateStep == 2) "buang" else "hilang",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (deduplicateStep >= 2) AccentCoral else TextMuted,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(14.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, SleekNavyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = when (deduplicateStep) {
                                1 -> "1. Input List berisi [1, 2, 3, 3, 4] dengan angka 3 muncul dua kali."
                                2 -> "2. set() mengecek hash table: nilai 3 kedua terdeteksi bertabrakan dan diabaikan."
                                else -> "3. Hasil Himpunan Bersih: {1, 2, 3, 4} (Elemen otomatis berstatus unik!)."
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            deduplicateStep = if (deduplicateStep < 3) deduplicateStep + 1 else 1
                            onCodeGenerated?.invoke(pythonCode)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = if (deduplicateStep < 3) Icons.Default.PlayArrow else Icons.Default.Refresh,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            if (deduplicateStep < 3) "Langkah Berikutnya ➔" else "Ulangi Simulasi",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // Interactive Venn Diagram Visualizer
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SleekNavyCard)
                        .border(1.dp, SleekNavyBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    val isOnlyAHighlighted = selectedOp == VennOperation.UNION || selectedOp == VennOperation.DIFFERENCE_A_B || selectedOp == VennOperation.SYMMETRIC_DIFFERENCE
                    val isIntersectionHighlighted = selectedOp == VennOperation.UNION || selectedOp == VennOperation.INTERSECTION
                    val isOnlyBHighlighted = selectedOp == VennOperation.UNION || selectedOp == VennOperation.DIFFERENCE_B_A || selectedOp == VennOperation.SYMMETRIC_DIFFERENCE

                    // Clean, Perfectly-Aligned 3-Column Venn Segment Visualizer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Section A Only
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isOnlyAHighlighted) PythonBlueLight else Color.White,
                            border = BorderStroke(
                                if (isOnlyAHighlighted) 2.dp else 1.dp,
                                if (isOnlyAHighlighted) PythonBlue else SleekNavyBorder
                            ),
                            shadowElevation = if (isOnlyAHighlighted) 3.dp else 1.dp,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Set A Saja",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PythonBlueDark
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isOnlyAHighlighted) PythonBlue else SleekNavyCard
                                ) {
                                    Text(
                                        text = "{ 1, 2 }",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isOnlyAHighlighted) Color.White else TextPrimary
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        // Section Overlap (Irisan A & B)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isIntersectionHighlighted) AccentEmeraldLight else Color.White,
                            border = BorderStroke(
                                if (isIntersectionHighlighted) 2.dp else 1.dp,
                                if (isIntersectionHighlighted) AccentEmerald else SleekNavyBorder
                            ),
                            shadowElevation = if (isIntersectionHighlighted) 4.dp else 1.dp,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Irisan (A & B)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AccentEmerald
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isIntersectionHighlighted) AccentEmerald else SleekNavyCard
                                ) {
                                    Text(
                                        text = "{ 3, 4 }",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isIntersectionHighlighted) Color.White else TextPrimary
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        // Section B Only
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isOnlyBHighlighted) PythonYellowLight else Color.White,
                            border = BorderStroke(
                                if (isOnlyBHighlighted) 2.dp else 1.dp,
                                if (isOnlyBHighlighted) PythonYellowDark else SleekNavyBorder
                            ),
                            shadowElevation = if (isOnlyBHighlighted) 3.dp else 1.dp,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Set B Saja",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PythonYellowDark
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isOnlyBHighlighted) PythonYellowDark else SleekNavyCard
                                ) {
                                    Text(
                                        text = "{ 5, 6 }",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isOnlyBHighlighted) Color.White else TextPrimary
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Result Pill
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White,
                        border = BorderStroke(1.5.dp, AccentEmerald),
                        shadowElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Hasil Operasi:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted
                                )
                            )
                            Text(
                                text = "{ ${resultSet.sorted().joinToString(", ")} }",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentEmerald,
                                    fontSize = 15.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Venn Operation Buttons
                Text(
                    text = "⭕ Pilih Operasi Himpunan:",
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
                    val ops = listOf(
                        Triple(VennOperation.UNION, "a | b", "Gabungan (Union)"),
                        Triple(VennOperation.INTERSECTION, "a & b", "Irisan (Intersection)"),
                        Triple(VennOperation.DIFFERENCE_A_B, "a - b", "Selisih (A - B)"),
                        Triple(VennOperation.DIFFERENCE_B_A, "b - a", "Selisih (B - A)"),
                        Triple(VennOperation.SYMMETRIC_DIFFERENCE, "a ^ b", "Sym Diff (^)")
                    )

                    ops.forEach { (op, label, desc) ->
                        val isSelected = selectedOp == op
                        Button(
                            onClick = {
                                selectedOp = op
                                onCodeGenerated?.invoke(pythonCode)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) AccentEmerald else SleekNavyCard,
                                contentColor = if (isSelected) Color.White else TextPrimary
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) AccentEmerald else SleekNavyBorder
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = label,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isSelected) Color.White else TextPrimary
                                )
                                Text(
                                    text = desc,
                                    fontSize = 9.sp,
                                    color = if (isSelected) Color.White.copy(alpha = 0.85f) else TextMuted
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Code Display
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = SleekNavyCard,
                border = BorderStroke(1.dp, SleekNavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = pythonCode,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        fontSize = 12.sp
                    ),
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}
