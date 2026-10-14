package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TouchApp
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
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
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.LimeSoftDark
import com.example.ui.theme.LimeSoftLight
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
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun VisualListContainer(
    initialItems: List<String> = listOf("Apel", "Jeruk", "Mangga"),
    visualizationType: VisualizationType = VisualizationType.LIST_INDEX,
    selectedSliceRange: IntRange? = null,
    highlightIndex: Int? = null,
    isInteractiveLab: Boolean = false,
    onCodeGenerated: ((String) -> Unit)? = null
) {
    val items = remember { mutableStateListOf<String>().apply { addAll(initialItems) } }
    var activeHighlightIndex by remember { mutableStateOf(highlightIndex) }
    var sliceStart by remember { mutableIntStateOf(0) }
    var sliceStop by remember { mutableIntStateOf(items.size.coerceAtLeast(1)) }
    var lastActionLog by remember { mutableStateOf("List terinisialisasi dengan ${items.size} elemen. Sentuh elemen untuk memeriksa.") }
    var generatedPythonCode by remember { mutableStateOf("buah = [\"${items.joinToString("\", \"")}\"]") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("visual_list_container"),
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
                            .background(PythonBlue)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "VISUALISASI MEMORI LIST",
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
                        text = "len = ${items.size}",
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
                text = "Setiap sel mewakili slot memori terurut (Index 0 hingga ${items.size - 1.coerceAtLeast(0)}).",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMuted,
                    fontSize = 11.5.sp
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Memory Stage Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SleekNavyCard)
                    .border(1.dp, SleekNavyBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                // Legend Guide
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = PythonBlue.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, PythonBlue.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "Indeks Positif (Maju)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PythonBlueDark,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFF1F5F9),
                            border = BorderStroke(1.dp, SleekNavyBorder)
                        ) {
                            Text(
                                text = "Indeks Negatif (Mundur)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Memory Cells Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = if (items.size <= 3) Arrangement.Center else Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (items.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "[ ] (List Kosong - Belum ada elemen)",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextMuted,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }

                    items.forEachIndexed { index, itemValue ->
                        val isHighlighted = activeHighlightIndex == index ||
                                (selectedSliceRange != null && index in selectedSliceRange) ||
                                (visualizationType == VisualizationType.LIST_SLICING && index in sliceStart until sliceStop)
                        val isSliceActive = visualizationType == VisualizationType.LIST_SLICING && index in sliceStart until sliceStop
                        val negativeIndex = index - items.size

                        val scale by animateFloatAsState(
                            targetValue = if (isHighlighted) 1.05f else 1f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                            label = "scale"
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                                .scale(scale)
                                .clickable {
                                    activeHighlightIndex = index
                                    lastActionLog = "Akses buah[$index] (atau buah[$negativeIndex]) = '$itemValue'"
                                    generatedPythonCode = "elemen = buah[$index]  # Output: '$itemValue'"
                                    onCodeGenerated?.invoke(generatedPythonCode)
                                }
                        ) {
                            // Positive index pill (Top)
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isHighlighted) PythonBlue else PythonBlue.copy(alpha = 0.12f),
                                border = BorderStroke(
                                    1.dp,
                                    if (isHighlighted) PythonBlue else PythonBlue.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier.padding(bottom = 5.dp)
                            ) {
                                Text(
                                    text = "[$index]",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isHighlighted) Color.White else PythonBlueDark,
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }

                            // Main Memory Box Cell (High contrast white card with dark text)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = when {
                                    isSliceActive -> PythonYellowLight
                                    isHighlighted -> PythonBlue.copy(alpha = 0.1f)
                                    else -> Color.White
                                },
                                border = BorderStroke(
                                    width = if (isHighlighted || isSliceActive) 2.5.dp else 1.5.dp,
                                    color = when {
                                        isSliceActive -> PythonYellow
                                        isHighlighted -> PythonBlue
                                        else -> SleekNavyBorder
                                    }
                                ),
                                shadowElevation = if (isHighlighted) 6.dp else 1.5.dp,
                                modifier = Modifier.size(width = 86.dp, height = 72.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(6.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = itemValue,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary,
                                                fontSize = 13.5.sp,
                                                textAlign = TextAlign.Center
                                            ),
                                            maxLines = 2
                                        )

                                        if (isHighlighted) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Terpilih",
                                                tint = PythonBlue,
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Negative index pill (Bottom)
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isHighlighted) Color(0xFF334155) else Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, SleekNavyBorder),
                                modifier = Modifier.padding(top = 5.dp)
                            ) {
                                Text(
                                    text = "[$negativeIndex]",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isHighlighted) Color.White else TextSecondary,
                                        fontSize = 10.5.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Status Bar & Python Code
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SleekNavyCard,
                border = BorderStroke(1.dp, SleekNavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = null,
                            tint = PythonBlueDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = lastActionLog,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, SleekNavyBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = ">>> ",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = PythonBlueDark
                                )
                            )
                            Text(
                                text = generatedPythonCode,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }

            // Slicing Interactive Controls
            if (visualizationType == VisualizationType.LIST_SLICING) {
                Spacer(modifier = Modifier.height(14.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SleekNavyCard)
                        .border(1.dp, SleekNavyBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ContentCut,
                                contentDescription = null,
                                tint = PythonYellowDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "KONTROL SLICING LIST",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PythonYellowDark
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PythonYellowLight,
                            border = BorderStroke(1.dp, PythonYellow.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "buah[$sliceStart : $sliceStop]",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = PythonYellowDark
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Hasil Sub-list: ${items.subList(sliceStart.coerceIn(0, items.size), sliceStop.coerceIn(0, items.size))}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Start Stepper
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Start ($sliceStart)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Button(
                                    onClick = {
                                        if (sliceStart > 0) sliceStart--
                                        generatedPythonCode = "buah[$sliceStart:$sliceStop]  # Hasil: ${items.subList(sliceStart.coerceIn(0, items.size), sliceStop.coerceIn(0, items.size))}"
                                        lastActionLog = "Slicing dipotong mulai dari index $sliceStart"
                                    },
                                    modifier = Modifier.weight(1f).height(42.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PythonBlue),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("-", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                                Button(
                                    onClick = {
                                        if (sliceStart < sliceStop) sliceStart++
                                        generatedPythonCode = "buah[$sliceStart:$sliceStop]  # Hasil: ${items.subList(sliceStart.coerceIn(0, items.size), sliceStop.coerceIn(0, items.size))}"
                                        lastActionLog = "Slicing dipotong mulai dari index $sliceStart"
                                    },
                                    modifier = Modifier.weight(1f).height(42.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PythonBlue),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }

                        // Stop Stepper
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Stop ($sliceStop)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Button(
                                    onClick = {
                                        if (sliceStop > sliceStart) sliceStop--
                                        generatedPythonCode = "buah[$sliceStart:$sliceStop]  # Hasil: ${items.subList(sliceStart.coerceIn(0, items.size), sliceStop.coerceIn(0, items.size))}"
                                        lastActionLog = "Slicing berhenti sebelum index $sliceStop"
                                    },
                                    modifier = Modifier.weight(1f).height(42.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PythonYellowDark),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("-", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                                Button(
                                    onClick = {
                                        if (sliceStop < items.size) sliceStop++
                                        generatedPythonCode = "buah[$sliceStart:$sliceStop]  # Hasil: ${items.subList(sliceStart.coerceIn(0, items.size), sliceStop.coerceIn(0, items.size))}"
                                        lastActionLog = "Slicing berhenti sebelum index $sliceStop"
                                    },
                                    modifier = Modifier.weight(1f).height(42.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PythonYellowDark),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // Interactive Operation Buttons (Append, Pop, Sort, Reverse, Reset)
            if (isInteractiveLab || visualizationType == VisualizationType.LIST_MUTATION) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "🛠️ Eksperimen Mutasi List Langsung:",
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
                            val fruitPool = listOf("Pisang", "Melon", "Anggur", "Pepaya", "Nanas")
                            val newItem = fruitPool[(items.size) % fruitPool.size]
                            items.add(newItem)
                            activeHighlightIndex = items.size - 1
                            sliceStop = items.size
                            lastActionLog = "append('$newItem') sukses menambahkan elemen ke slot paling akhir."
                            generatedPythonCode = "buah.append(\"$newItem\")"
                            onCodeGenerated?.invoke(generatedPythonCode)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PythonBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(Modifier.width(4.dp))
                        Text("append()", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            if (items.isNotEmpty()) {
                                val removed = items.removeAt(items.size - 1)
                                activeHighlightIndex = null
                                sliceStop = items.size
                                lastActionLog = "pop() menghapus dan mengembalikan elemen '$removed' dari akhir list."
                                generatedPythonCode = "dihapus = buah.pop()  # Hasil: '$removed'"
                                onCodeGenerated?.invoke(generatedPythonCode)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentCoral),
                        shape = RoundedCornerShape(10.dp),
                        enabled = items.isNotEmpty()
                    ) {
                        Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(Modifier.width(4.dp))
                        Text("pop()", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            items.sort()
                            activeHighlightIndex = null
                            lastActionLog = "sort() mengurutkan seluruh elemen list menurut alfabetis."
                            generatedPythonCode = "buah.sort()  # Sekarang terurut A-Z"
                            onCodeGenerated?.invoke(generatedPythonCode)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PythonYellowDark),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.SortByAlpha, null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(Modifier.width(4.dp))
                        Text("sort()", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            items.reverse()
                            activeHighlightIndex = null
                            lastActionLog = "reverse() membalik urutan elemen di tempat."
                            generatedPythonCode = "buah.reverse()"
                            onCodeGenerated?.invoke(generatedPythonCode)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.SwapHoriz, null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(Modifier.width(4.dp))
                        Text("reverse()", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = {
                            items.clear()
                            items.addAll(initialItems)
                            activeHighlightIndex = null
                            sliceStart = 0
                            sliceStop = items.size
                            lastActionLog = "List dikembalikan ke data awal."
                            generatedPythonCode = "buah = [\"${initialItems.joinToString("\", \"")}\"]"
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
}
