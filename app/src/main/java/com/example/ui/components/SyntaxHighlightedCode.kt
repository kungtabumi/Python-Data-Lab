package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DeepNavyDark
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.PythonBlueLight
import com.example.ui.theme.PythonYellow
import com.example.ui.theme.SleekNavyBorder
import com.example.ui.theme.SyntaxBuiltin
import com.example.ui.theme.SyntaxComment
import com.example.ui.theme.SyntaxFunction
import com.example.ui.theme.SyntaxKeyword
import com.example.ui.theme.SyntaxNumber
import com.example.ui.theme.SyntaxString

@Composable
fun SyntaxHighlightedCode(
    code: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    showLineNumbers: Boolean = true
) {
    val clipboardManager = LocalClipboardManager.current
    val lines = code.lines()

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF0F172A),
        border = androidx.compose.foundation.BorderStroke(1.dp, SleekNavyBorder)
    ) {
        Column {
            // Header bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E293B))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = null,
                    tint = PythonYellow,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title ?: "Python Code",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.9f)
                    ),
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { clipboardManager.setText(AnnotatedString(code)) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy code",
                        tint = Color.LightGray,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Code lines with syntax highlighting
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(12.dp)
            ) {
                if (showLineNumbers) {
                    Column(modifier = Modifier.padding(end = 12.dp)) {
                        lines.indices.forEach { i ->
                            Text(
                                text = "${i + 1}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    color = Color.Gray.copy(alpha = 0.6f)
                                )
                            )
                        }
                    }
                }

                Column {
                    lines.forEach { line ->
                        Text(
                            text = highlightPythonLine(line),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

fun highlightPythonLine(line: String): AnnotatedString {
    return buildAnnotatedString {
        var idx = 0
        val len = line.length

        while (idx < len) {
            val c = line[idx]

            // Comment
            if (c == '#') {
                val comment = line.substring(idx)
                pushStyle(SpanStyle(color = SyntaxComment, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic))
                append(comment)
                pop()
                break
            }

            // String literal
            if (c == '"' || c == '\'') {
                val quote = c
                val start = idx
                idx++
                while (idx < len && line[idx] != quote) {
                    if (line[idx] == '\\' && idx + 1 < len) idx++
                    idx++
                }
                if (idx < len) idx++ // consume closing quote
                val str = line.substring(start, idx)
                pushStyle(SpanStyle(color = SyntaxString, fontWeight = FontWeight.Medium))
                append(str)
                pop()
                continue
            }

            // Number
            if (c.isDigit()) {
                val start = idx
                while (idx < len && (line[idx].isDigit() || line[idx] == '.')) {
                    idx++
                }
                pushStyle(SpanStyle(color = SyntaxNumber))
                append(line.substring(start, idx))
                pop()
                continue
            }

            // Identifier / Keyword
            if (c.isLetter() || c == '_') {
                val start = idx
                while (idx < len && (line[idx].isLetterOrDigit() || line[idx] == '_')) {
                    idx++
                }
                val word = line.substring(start, idx)
                when (word) {
                    "for", "in", "if", "else", "elif", "def", "class", "return", "import", "from", "as", "del", "and", "or", "not", "is", "while", "break", "continue" -> {
                        pushStyle(SpanStyle(color = SyntaxKeyword, fontWeight = FontWeight.Bold))
                        append(word)
                        pop()
                    }
                    "True", "False", "None" -> {
                        pushStyle(SpanStyle(color = PythonYellow, fontWeight = FontWeight.Bold))
                        append(word)
                        pop()
                    }
                    "print", "len", "type", "list", "tuple", "set", "dict", "range", "sum", "min", "max", "sorted" -> {
                        pushStyle(SpanStyle(color = SyntaxBuiltin, fontWeight = FontWeight.SemiBold))
                        append(word)
                        pop()
                    }
                    "append", "insert", "extend", "remove", "pop", "sort", "reverse", "add", "discard", "union", "intersection", "difference", "keys", "values", "items", "get", "update" -> {
                        pushStyle(SpanStyle(color = SyntaxFunction, fontWeight = FontWeight.SemiBold))
                        append(word)
                        pop()
                    }
                    else -> {
                        pushStyle(SpanStyle(color = Color(0xFFE2E8F0)))
                        append(word)
                        pop()
                    }
                }
                continue
            }

            // Operators & Punctuation
            val punctStyle = when (c) {
                '[', ']', '(', ')', '{', '}' -> SpanStyle(color = PythonYellow)
                ':', '=', '+', '-', '*', '/', '|', '&', '^' -> SpanStyle(color = PythonBlueLight, fontWeight = FontWeight.Bold)
                else -> SpanStyle(color = Color.White)
            }
            pushStyle(punctStyle)
            append(c.toString())
            pop()
            idx++
        }
    }
}
