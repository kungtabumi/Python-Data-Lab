package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VisualizationType
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.PythonBlue
import com.example.ui.theme.PythonBlueDark
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
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun VisualTupleContainer(
    initialItems: List<String> = listOf("10", "20", "30"),
    visualizationType: VisualizationType = VisualizationType.TUPLE_LOCKED,
    onCodeGenerated: ((String) -> Unit)? = null
) {
    val items = remember { initialItems }
    var showTypeError by remember { mutableStateOf(false) }
    var showUnpacked by remember { mutableStateOf(visualizationType == VisualizationType.TUPLE_UNPACKING) }
    val shakeOffset = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("visual_tuple_container"),
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
                        text = "VISUALISASI MEMORI TUPLE",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PythonYellowDark,
                            letterSpacing = 1.sp
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PythonYellowLight,
                    border = BorderStroke(1.dp, PythonYellow.copy(alpha = 0.6f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = PythonYellowDark,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "IMMUTABLE (KONSTAN)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = PythonYellowDark,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Nilai tuple dikunci di memori saat deklarasi dan tidak dapat dimutasi.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMuted,
                    fontSize = 11.5.sp
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Visual Tuple Memory Box Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
                    .clip(RoundedCornerShape(14.dp))
                    .background(SleekNavyCard)
                    .border(
                        1.5.dp,
                        if (showTypeError) AccentCoral else SleekNavyBorder,
                        RoundedCornerShape(14.dp)
                    )
                    .padding(vertical = 16.dp, horizontal = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left parenthesis
                    Text(
                        text = "(",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            color = PythonYellowDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 36.sp
                        ),
                        modifier = Modifier.padding(end = 6.dp)
                    )

                    items.forEachIndexed { index, itemValue ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        ) {
                            // Index Pill
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SleekNavyBorderSubtle,
                                border = BorderStroke(1.dp, SleekNavyBorder),
                                modifier = Modifier.padding(bottom = 5.dp)
                            ) {
                                Text(
                                    text = "[$index]",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }

                            // Memory Cell Box (High Contrast White Card with Strong Dark Text)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (showTypeError && index == 0) AccentCoral.copy(alpha = 0.12f) else Color.White,
                                border = BorderStroke(
                                    2.dp,
                                    if (showTypeError && index == 0) AccentCoral else PythonYellow
                                ),
                                shadowElevation = 2.dp,
                                modifier = Modifier.size(width = 86.dp, height = 74.dp)
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
                                            text = if (showTypeError && index == 0) "10 ➔ 99" else itemValue,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = if (showTypeError && index == 0) AccentCoral else TextPrimary,
                                                fontSize = 14.sp
                                            ),
                                            maxLines = 1
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = "Terkunci",
                                                tint = if (showTypeError && index == 0) AccentCoral else PythonYellowDark,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "Locked",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = if (showTypeError && index == 0) AccentCoral else TextMuted,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Right parenthesis
                    Text(
                        text = ")",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            color = PythonYellowDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 36.sp
                        ),
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }

            // TypeError Alert Box
            AnimatedVisibility(
                visible = showTypeError,
                enter = fadeIn() + androidx.compose.animation.expandVertically(),
                exit = fadeOut()
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AccentCoral.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, AccentCoral),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = null,
                            tint = AccentCoral,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "❌ TypeError: 'tuple' object does not support item assignment",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentCoral
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Tuple bersifat IMMUTABLE. Nilai elemennya terkunci permanen di memori dan tidak dapat diubah!",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }

            // Unpacking Visualization Box
            AnimatedVisibility(
                visible = showUnpacked,
                enter = fadeIn() + androidx.compose.animation.expandVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SleekNavyCard)
                        .border(1.dp, PythonBlue.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "📦 Hasil Tuple Unpacking (a, b, c = data):",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("a = 10", "b = 20", "c = 30").forEach { varAssign ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White,
                                border = BorderStroke(1.5.dp, AccentEmerald),
                                shadowElevation = 1.dp
                            ) {
                                Text(
                                    text = varAssign,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentEmerald
                                    ),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Action Buttons
            Text(
                text = "🧪 Eksperimen Interaktif Tuple:",
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
                        showTypeError = true
                        showUnpacked = false
                        coroutineScope.launch {
                            repeat(3) {
                                shakeOffset.animateTo(10f, tween(50))
                                shakeOffset.animateTo(-10f, tween(50))
                            }
                            shakeOffset.animateTo(0f, tween(50))
                        }
                        onCodeGenerated?.invoke("data = (10, 20, 30)\ndata[0] = 99  # ❌ Memicu TypeError: 'tuple' object does not support item assignment!")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentCoral,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Warning, null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(Modifier.width(4.dp))
                    Text("Coba Ubah: data[0] = 99", fontWeight = FontWeight.SemiBold, color = Color.White)
                }

                Button(
                    onClick = {
                        showUnpacked = true
                        showTypeError = false
                        onCodeGenerated?.invoke("a, b, c = (10, 20, 30)\nprint('Hasil unpack:', a, b, c)")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PythonBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.CallSplit, null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(Modifier.width(4.dp))
                    Text("Unpacking (a,b,c)", fontWeight = FontWeight.SemiBold, color = Color.White)
                }

                OutlinedButton(
                    onClick = {
                        showTypeError = false
                        showUnpacked = false
                    },
                    border = BorderStroke(1.dp, SleekNavyBorder),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                    Spacer(Modifier.width(4.dp))
                    Text("Reset", fontWeight = FontWeight.SemiBold, color = TextSecondary)
                }
            }
        }
    }
}
