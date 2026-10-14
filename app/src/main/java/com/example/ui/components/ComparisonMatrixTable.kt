package com.example.ui.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.PythonBlue
import com.example.ui.theme.PythonBlueDark
import com.example.ui.theme.PythonYellowDark
import com.example.ui.theme.SleekNavyBg
import com.example.ui.theme.SleekNavyBorder
import com.example.ui.theme.SleekNavyBorderSubtle
import com.example.ui.theme.SleekNavyCard
import com.example.ui.theme.SleekNavySurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class ComparisonRow(
    val aspect: String,
    val listVal: String,
    val tupleVal: String,
    val setVal: String,
    val dictVal: String
)

@Composable
fun ComparisonMatrixTable(modifier: Modifier = Modifier) {
    val rows = listOf(
        ComparisonRow("Sintaks Kurung", "[ ] Siku", "( ) Bulat", "{ } Kurawal", "{k: v} Pasangan"),
        ComparisonRow("Mutabilitas", "Mutable (Bisa diubah)", "Immutable 🔒 (Konstan)", "Mutable (Bisa diubah)", "Mutable (Bisa diubah)"),
        ComparisonRow("Urutan (Ordered)", "Ya (Terurut)", "Ya (Terurut)", "Tidak (Unordered)", "Ya (Sejak Python 3.7)"),
        ComparisonRow("Pengindeksan", "Ya: [0], [-1]", "Ya: [0], [-1]", "Tidak Ada (No Index)", "Berdasarkan Key: ['k']"),
        ComparisonRow("Duplikasi Nilai", "Boleh Duplikat", "Boleh Duplikat", "Dilarang ❌ (Unik)", "Key Unik, Val Boleh"),
        ComparisonRow("Kecepatan Akses", "O(1) by index", "O(1) by index (Fast)", "O(1) by hash lookup", "O(1) by key lookup"),
        ComparisonRow("Kasus Ideal", "Daftar belanja, skor, todo", "Koordinat, config, hari", "Himpunan, buang duplikat", "Database data, profil JSON")
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SleekNavySurface),
        border = BorderStroke(1.dp, SleekNavyBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.TableChart,
                    contentDescription = null,
                    tint = PythonBlueDark,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "MATRIKS KOMPARASI 4 STRUKTUR DATA",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PythonBlueDark,
                        letterSpacing = 1.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Scrollable Matrix Table
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(1.dp, SleekNavyBorder, RoundedCornerShape(12.dp))
                    .horizontalScroll(rememberScrollState())
            ) {
                Column {
                    // Header Row
                    Row(
                        modifier = Modifier
                            .background(SleekNavyCard)
                            .padding(12.dp)
                    ) {
                        TableCell(text = "Karakteristik", isHeader = true, headerColor = TextPrimary, width = 130)
                        TableCell(text = "📜 List", isHeader = true, headerColor = PythonBlueDark, width = 145)
                        TableCell(text = "🔒 Tuple", isHeader = true, headerColor = PythonYellowDark, width = 145)
                        TableCell(text = "⭕ Set", isHeader = true, headerColor = AccentEmerald, width = 145)
                        TableCell(text = "📖 Dictionary", isHeader = true, headerColor = AccentOrange, width = 155)
                    }

                    // Body Rows
                    rows.forEachIndexed { idx, row ->
                        val rowBg = if (idx % 2 == 0) Color.White else SleekNavyBg.copy(alpha = 0.5f)
                        Row(
                            modifier = Modifier
                                .background(rowBg)
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TableCell(text = row.aspect, isHeader = false, isAspect = true, width = 130)
                            TableCell(text = row.listVal, isHeader = false, width = 145)
                            TableCell(text = row.tupleVal, isHeader = false, width = 145)
                            TableCell(text = row.setVal, isHeader = false, width = 145)
                            TableCell(text = row.dictVal, isHeader = false, width = 155)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TableCell(
    text: String,
    isHeader: Boolean,
    isAspect: Boolean = false,
    headerColor: Color = TextPrimary,
    width: Int
) {
    Box(
        modifier = Modifier
            .width(width.dp)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            style = if (isHeader) {
                MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = headerColor
                )
            } else if (isAspect) {
                MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            } else {
                MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.5.sp
                )
            }
        )
    }
}
