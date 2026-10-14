package com.example.ai

import com.example.BuildConfig
import com.example.model.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Intelligent, Context-Aware, Anti-Hallucination AI Tutor Service.
 * Grounded in official Python 3.11 documentation and SMAN 1 Kepanjen Informatics Curriculum.
 * Mentored by: Hermawan Hidayat, S.Kom.
 */
class GeminiTutorService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val baseSystemPrompt = """
        Anda adalah Guru AI Pembimbing Praktikum Informatika SMA Kelas XI (SMAN 1 Kepanjen - Pembimbing: Bapak Hermawan Hidayat, S.Kom.).
        Karakter Anda ramah, suportif, komunikatif, sabar, dan menginspirasi siswa untuk gemar belajar koding dan berpikir komputasional.

        === RUANG LINGKUP KEMAMPUAN KONTEKSTUAL ===
        1. SAPAAN & PERCAKAPAN UMUM:
           - Jika siswa menyapa ("halo", "hai", "selamat pagi/siang/malam", "assalamualaikum", "apa kabar"), sambutlah dengan ramah, hangat, dan antusias. Tanyakan kabar mereka atau apa yang ingin mereka eksplorasi hari ini. JANGAN langsung mendikte daftar struktur data secara kaku jika siswa hanya menyapa.
           - Jika siswa berterima kasih ("makasih", "terima kasih", "paham"), balaslah dengan motivasi hangat ("Sama-sama! Senang bisa membantumu!").
           - Jika siswa bertanya identitas ("siapa kamu", "kamu siapa"), perkenalkan diri Anda dengan bangga sebagai AI Tutor Python DataLab SMAN 1 Kepanjen.
           - Jika siswa meminta lelucon atau fakta seru seputar koding, berikan joke koding/Python yang cerdas dan menghibur.

        2. PANDUAN PENGGUNAAN APLIKASI:
           - Mampu memandu siswa menggunakan fitur aplikasi:
             * Tab Materi: Modul pembelajaran interaktif dengan visualisasi memori & live code runner langsung di tempat.
             * Tab Lab Interaktif: Code Playground dengan lokal Python Engine & tantangan pemrograman bertahap.
             * Tab AI Tutor: Konsultasi kode, bedah error, dan tanya jawab materi.
             * Gamifikasi: XP & Lencana penghargaan.

        3. DASAR-DASAR PEMROGRAMAN & PYTHON 3.11:
           - Variabel & Aturan Identifier: Dynamic Typing, penamaan (diawali huruf/_, no spasi, no angka di awal), case-sensitive, PEP 8 snake_case, re-assignment & memory pointer id().
           - Tipe Data Primitif & Type Casting: int (arbitrary precision), float, str (immutable sequence), bool (True/False, Truthy & Falsy rules), konversi tipe eksplisit int(), float(), str(), bool(), type().
           - Operator & Operan: Definisi Operan vs Operator; Aritmatika (+, -, *, / float, // floor, % modulo, ** pangkat); Perbandingan (==, !=, >, <, >=, <=); Logika (and, or, not, short-circuit); Penugasan (=, +=, -=, dsb.); Keanggotaan (in, not in); Identitas (is vs ==, id objek); Presedensi operator.
           - Input & Output: print(*objects, sep=' ', end='\n'), modern f-string {var} & formatting {num:.2f}, input(prompt) yang SELALU bertipe str dan kebutuhan type casting numerik.
           - Percabangan / Seleksi Kondisi: if, elif, else, pentingnya tanda titik dua (:) dan indentasi 4 spasi (PEP 8), percabangan bersarang (nested if), ternary operator (one-line conditional).
           - Perulangan / Iterasi: for loop dengan range(start, stop, step) [stop eksklusif], iterasi koleksi, enumerate(), while loop dan pencegahan infinite loop, pernyataan kontrol (break, continue, pass, else pada loop).
           - Fungsi & Modularitas: def nama_fungsi(parameter): return nilai, perbedaan parameter vs argumen, default argument, return vs print, scope variabel lokal vs global.
           - Pemecahan Masalah & Diagnosa Error: Membaca traceback, memperbaiki SyntaxError, IndentationError, NameError, ZeroDivisionError, TypeError.

        4. SPESIALISASI UTAMA: 4 STRUKTUR DATA PYTHON & STRUKTUR BERSARANG
           - List (Mutable, Ordered, Index, Duplicates, Amortized O(1) append)
           - Tuple (Immutable, Ordered, Index, Duplicates, Hashable jika isinya hashable, hemat memori)
           - Set (Mutable, Unique, Unordered, Hash Table O(1) membership, operasi Venn)
           - Dictionary (Key-Value, Unique Hashable Keys, Insertion-Order dijamin, O(1) lookup)
           - Nested Data Structures (Struktur Bersarang multi-dimensi & chained indexing)

        === PROTOKOL ANTI-HALUSINASI, KECEPATAN & KUALITAS JAWABAN ===
        1. GROUND TRUTH 100% CPYTHON 3.11: Penjelasan teknis dan kode wajib 100% valid sesuai spesifikasi resmi Python 3.11.
        2. EFISIENSI & RESPON CEPAT (ZERO LATENCY): Jawab langsung pada inti pokok secara padat, jelas, dan lugas tanpa pengantar basa-basi berbelit-belit.
        3. NETRALITAS TANPA BIAS (OBJECTIVE COMPUTATIONAL LOGIC): Berikan analisa berbasis kaidah ilmu komputer yang seimbang, adil, dan obyektif.
        4. DILARANG MENGARANG METODE (NO FAKE METHODS):
           - DILARANG mengklaim tuple.append(), tuple.insert(), atau tuple[0] = x.
           - DILARANG mengklaim set[0] atau set.append(). Set menggunakan add() dan tidak berindeks.
           - DILARANG mengklaim dict.add(). Dictionary menggunakan dict[key] = value atau dict.update().
           - Kunci Dictionary dan elemen Set WAJIB berupa tipe data immutable/hashable (TIDAK BOLEH List).
        5. PRESISI DIAGNOSIS ERROR: Jika menganalisis kode yang menghasilkan error, sebutkan nama persis Exception Python (misal: TypeError, IndexError, KeyError, ValueError, AttributeError) dan jelaskan penyebabnya berdasarkan model memori.
        6. BAHASA & PEDAGOGI: Gunakan Bahasa Indonesia yang ramah, santun, jelas, dan komunikatif.
        7. SESUAIKAN FORMAT DENGAN JENIS PERTANYAAN:
           - Untuk sapaan/obrolan santai: Jawab dengan hangat dan luwes (tidak perlu tabel atau format kaku).
           - Untuk pertanyaan teknis/materi/error:
             * 🎯 **Diagnosa & Jawaban Inti**
             * 🔬 **Penjelasan / Kaidah Python**
             * 💻 **Contoh Kode Terverifikasi**
             * 💡 **Tips Guru Hermawan (Refleksi Siswa)**
        8. KELENGKAPAN INSTRUKSI & PENJELASAN (ANTI-TERPOTONG / COMPLETE INSTRUCTIONS):
           - Selalu berikan instruksi langkah demi langkah secara lengkap dan tuntas dari awal hingga akhir.
           - Dilarang memotong kalimat atau membiarkan blok kode menggantung tanpa penutup triple backtick.
           - Pastikan setiap contoh kode memiliki struktur yang utuh, variabel didefinisikan dengan jelas, dan kurung/sintaks tertutup sempurna.
    """.trimIndent()

    /**
     * Determines if a query is a purely social interaction, greeting, profile check, or app guidance.
     */
    fun isPureSocialChitChat(q: String): Boolean {
        val lower = q.lowercase().trim()
        return isGreeting(lower) || isIdentity(lower) || isAppGuide(lower) ||
               isMotivationOrStudyTips(lower) || isGratitude(lower) || isJoke(lower)
    }

    /**
     * Identifies if a query can be answered immediately with 100% verified, hallucination-free,
     * and bias-free local ground truth without unnecessary network round-trips.
     */
    fun isDirectGroundTruthMatch(question: String, code: String?, executionError: String?): Boolean {
        if (!executionError.isNullOrBlank()) return true
        if (!code.isNullOrBlank() && PythonDataLabKnowledgeBase.analyzeCode(code).isNotEmpty()) return true

        val q = question.lowercase().trim()

        val isNovelProgramRequest = listOf(
            "buatkan program", "tuliskan program", "bikin program", "codingkan",
            "script untuk", "buat program", "tulis program", "bikin fungsi untuk"
        ).any { q.contains(it) }
        if (isNovelProgramRequest) return false

        return isPureSocialChitChat(q)
    }

    /**
     * Ask the AI tutor with streaming support for instant time-to-first-token,
     * low-latency token delivery, and zero-hallucination ground-truth acceleration.
     * Preserves complete contextual grounding and deep understanding of Python data structures
     * (List, Tuple, Set, Dictionary, and Nested structures).
     */
    suspend fun askTutorStream(
        userMessage: String,
        contextCode: String? = null,
        conversationHistory: List<ChatMessage> = emptyList(),
        lastExecutionError: String? = null,
        lastExecutionStdout: String? = null,
        activeTopicTitle: String? = null,
        onChunkReceived: suspend (String) -> Unit
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
        val isOnlineGeminiAvailable = apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY"

        // 1. FAST PATH ACCELERATION FOR PURE SOCIAL CHIT-CHAT (Greeting, Gratitude, Identity, App Guide, Joke)
        if (isPureSocialChitChat(userMessage)) {
            val instantResponse = getOfflineEducationalResponse(
                question = userMessage,
                code = contextCode,
                executionError = lastExecutionError,
                executionStdout = lastExecutionStdout,
                topicTitle = activeTopicTitle
            )
            onChunkReceived(instantResponse)
            return@withContext instantResponse
        }

        // 2. OFFLINE GROUND-TRUTH FALLBACK (If API key is missing or default)
        if (!isOnlineGeminiAvailable) {
            val instantResponse = getOfflineEducationalResponse(
                question = userMessage,
                code = contextCode,
                executionError = lastExecutionError,
                executionStdout = lastExecutionStdout,
                topicTitle = activeTopicTitle
            )
            onChunkReceived(instantResponse)
            return@withContext instantResponse
        }

        // 3. ONLINE CONTEXTUAL AI (Streaming via Gemini API with complete Python 3.11 Ground Truth)
        val groundTruthData = PythonDataLabKnowledgeBase.retrieveRelevantGroundTruth(userMessage, contextCode, activeTopicTitle)
        val fullAccumulator = StringBuilder()

        try {
            val contextualPrompt = buildUserTurnPrompt(
                message = userMessage,
                code = contextCode,
                error = lastExecutionError,
                stdout = lastExecutionStdout,
                topic = activeTopicTitle,
                groundTruth = groundTruthData
            )

            // Construct sanitized, alternating conversational history strictly following Gemini API specification:
            // 1. The first Content in contents must be from the "user" role.
            // 2. Roles must strictly alternate: "user" -> "model" -> "user" -> "model" ...
            val contentsArray = JSONArray()
            val sanitizedTurns = mutableListOf<Pair<String, String>>()
            val filteredHistory = conversationHistory.filter { msg ->
                msg.text.isNotBlank() && msg.id != "welcome_ai"
            }

            for (msg in filteredHistory) {
                if (msg.isFromUser && msg.text == userMessage) continue
                val role = if (msg.isFromUser) "user" else "model"

                if (sanitizedTurns.isEmpty()) {
                    if (role == "user") {
                        sanitizedTurns.add(role to msg.text)
                    }
                } else {
                    val lastRole = sanitizedTurns.last().first
                    if (lastRole == role) {
                        val merged = sanitizedTurns.last().second + "\n\n" + msg.text
                        sanitizedTurns[sanitizedTurns.size - 1] = role to merged
                    } else {
                        sanitizedTurns.add(role to msg.text)
                    }
                }
            }

            if (sanitizedTurns.isNotEmpty() && sanitizedTurns.last().first == "user") {
                val lastUserTurn = sanitizedTurns.removeAt(sanitizedTurns.size - 1)
                if (lastUserTurn.second != userMessage) {
                    sanitizedTurns.add("user" to lastUserTurn.second)
                    sanitizedTurns.add("model" to "Baik, mari kita lanjutkan eksplorasi konsep kodingmu.")
                }
            }

            for ((role, text) in sanitizedTurns.takeLast(6)) {
                contentsArray.put(JSONObject().apply {
                    put("role", role)
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", text))
                    })
                })
            }

            contentsArray.put(JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().apply {
                    put(JSONObject().put("text", contextualPrompt))
                })
            })

            val fullSystemPrompt = "$baseSystemPrompt\n\n$groundTruthData"

            val requestJson = JSONObject().apply {
                put("contents", contentsArray)
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", fullSystemPrompt))
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("thinkingConfig", JSONObject().apply {
                        put("thinkingLevel", "low")
                    })
                    put("temperature", 0.2)
                    put("topP", 0.9)
                    put("maxOutputTokens", 8192)
                })
            }

            val body = requestJson.toString().toRequestBody("application/json".toMediaType())
            val candidateModels = listOf("gemini-3.5-flash", "gemini-3.1-pro-preview", "gemini-3.1-flash-lite-preview")
            var streamCompleted = false

            for (modelName in candidateModels) {
                if (streamCompleted) break
                val targetUrl = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:streamGenerateContent?alt=sse&key=$apiKey"
                val request = Request.Builder()
                    .url(targetUrl)
                    .post(body)
                    .build()

                try {
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        val reader = response.body?.byteStream()?.bufferedReader()
                        var line: String?
                        while (reader?.readLine().also { line = it } != null) {
                            val rawLine = line?.trim() ?: continue
                            if (!rawLine.startsWith("data:")) continue
                            val jsonPayload = rawLine.removePrefix("data:").trim()
                            if (jsonPayload.isEmpty() || jsonPayload == "[DONE]") continue

                            try {
                                val chunkJson = JSONObject(jsonPayload)
                                val candidates = chunkJson.optJSONArray("candidates")
                                val firstCandidate = candidates?.optJSONObject(0)
                                val content = firstCandidate?.optJSONObject("content")
                                val parts = content?.optJSONArray("parts")

                                if (parts != null) {
                                    for (p in 0 until parts.length()) {
                                        val partObj = parts.optJSONObject(p)
                                        val text = partObj?.optString("text")
                                        if (!text.isNullOrEmpty()) {
                                            fullAccumulator.append(text)
                                            onChunkReceived(text)
                                        }
                                    }
                                }
                            } catch (_: Exception) {
                                // Ignore packet boundary parse errors
                            }
                        }
                        if (fullAccumulator.isNotBlank()) {
                            streamCompleted = true
                        }
                    }
                } catch (_: Exception) {
                    // Try next model if connection failed
                }
            }

            if (fullAccumulator.isNotBlank()) {
                val text = fullAccumulator.toString()
                val backtickCount = text.split("```").size - 1
                if (backtickCount % 2 != 0) {
                    val closure = "\n```"
                    fullAccumulator.append(closure)
                    onChunkReceived(closure)
                }
                return@withContext fullAccumulator.toString()
            }

            val fallback = getOfflineEducationalResponse(
                question = userMessage,
                code = contextCode,
                executionError = lastExecutionError,
                executionStdout = lastExecutionStdout,
                topicTitle = activeTopicTitle
            )
            onChunkReceived(fallback)
            fallback
        } catch (e: Exception) {
            if (fullAccumulator.isNotBlank()) {
                val text = fullAccumulator.toString()
                val backtickCount = text.split("```").size - 1
                if (backtickCount % 2 != 0) {
                    val closure = "\n```"
                    fullAccumulator.append(closure)
                    onChunkReceived(closure)
                }
                return@withContext fullAccumulator.toString()
            }
            val fallback = getOfflineEducationalResponse(
                question = userMessage,
                code = contextCode,
                executionError = lastExecutionError,
                executionStdout = lastExecutionStdout,
                topicTitle = activeTopicTitle
            )
            onChunkReceived(fallback)
            fallback
        }
    }

    /**
     * Backward-compatible synchronous query method.
     */
    suspend fun askTutor(
        userMessage: String,
        contextCode: String? = null,
        conversationHistory: List<ChatMessage> = emptyList(),
        lastExecutionError: String? = null,
        lastExecutionStdout: String? = null,
        activeTopicTitle: String? = null
    ): String {
        val resultBuilder = StringBuilder()
        askTutorStream(
            userMessage = userMessage,
            contextCode = contextCode,
            conversationHistory = conversationHistory,
            lastExecutionError = lastExecutionError,
            lastExecutionStdout = lastExecutionStdout,
            activeTopicTitle = activeTopicTitle,
            onChunkReceived = { chunk ->
                resultBuilder.append(chunk)
            }
        )
        return resultBuilder.toString()
    }

    private fun buildUserTurnPrompt(
        message: String,
        code: String?,
        error: String?,
        stdout: String?,
        topic: String?,
        groundTruth: String
    ): String {
        val sb = StringBuilder()
        if (!topic.isNullOrBlank()) {
            sb.appendLine("📍 Topik Pembelajaran Aktif: $topic")
        }

        if (!code.isNullOrBlank()) {
            sb.appendLine("📄 Kode Siswa di Playground:\n```python\n$code\n```")
        }

        if (!error.isNullOrBlank()) {
            sb.appendLine("❌ Pesan Error Hasil Eksekusi Interpreter Siswa:\n```\n$error\n```")
        } else if (!stdout.isNullOrBlank()) {
            sb.appendLine("✅ Output Eksekusi Kode Siswa:\n```\n$stdout\n```")
        }

        sb.appendLine("\n💬 Pertanyaan Siswa: $message")
        sb.appendLine("\nPedoman Guru & Pedagogi:")
        sb.appendLine("1. Jawablah secara spesifik, mendalam, dan kontekstual terhadap inti pertanyaan siswa terkait struktur data (List, Tuple, Set, Dict), operasi, atau konsep pemrograman Python yang ditanyakan.")
        sb.appendLine("2. Hubungkan dengan konteks kode playground dan riwayat percakapan sebelumnya jika ada.")
        sb.appendLine("3. Berpijak kuat pada data valid dan spesifikasi resmi Python 3.11 tanpa halusinasi.")
        sb.appendLine("4. Berikan penjelasan dan instruksi secara lengkap dan tuntas dari awal hingga akhir. Jangan memotong kalimat atau membiarkan kode terputus di tengah jalan.")
        return sb.toString()
    }

    /**
     * Highly factual offline response engine leveraging PythonDataLabKnowledgeBase
     * to eliminate hallucinations and speculation when offline or API key is absent.
     */
    fun getOfflineEducationalResponse(
        question: String,
        code: String?,
        executionError: String? = null,
        executionStdout: String? = null,
        topicTitle: String? = null
    ): String {
        val q = question.lowercase()

        // 1. If there's an active execution error from interpreter or static analyzer, diagnose immediately!
        if (!executionError.isNullOrBlank()) {
            return diagnoseExecutionError(executionError, code)
        }

        // 2. Check if code has detected static issues
        if (!code.isNullOrBlank()) {
            val diagnostics = PythonDataLabKnowledgeBase.analyzeCode(code)
            if (diagnostics.isNotEmpty() && (q.contains("error") || q.contains("salah") || q.contains("cek") || q.contains("kenapa") || q.contains("perbaiki"))) {
                val diag = diagnostics.first()
                return """
                    🎯 **Diagnosa Kode Siswa (SMAN 1 Kepanjen):**
                    Terdeteksi potensi masalah pada ${diag.lineSnippet}:
                    **${diag.probableError}**
                    
                    🔬 **Spesifikasi Teknis Python 3.11 (Data Valid):**
                    ${diag.explanation}
                    *Kaidah:* ${diag.verifiedPythonRule}
                    
                    💻 **Perbaikan Kode Terverifikasi:**
                    ```python
                    ${diag.correctionCode}
                    ```
                    
                    💡 **Tips Guru Hermawan:**
                    Selalu perhatikan mutabilitas data: List bisa diubah, sedangkan Tuple bersifat read-only untuk melindungi integritas nilai.
                """.trimIndent()
            }
        }

        // 3. CONTEXTUAL: Greetings & Casual Salutations ("halo", "hai", "selamat pagi", "apa kabar", dsb.)
        if (isGreeting(q)) {
            val topicNotice = if (!topicTitle.isNullOrBlank()) {
                "\n\n📍 *Catatan Belajar:* Saat ini kamu sedang membuka materi **$topicTitle**."
            } else ""

            return """
                👋 **Halo! Selamat datang di AI Tutor Python DataLab!**
                Senang sekali bisa menyapamu! Saya adalah asisten belajar Informatika SMAN 1 Kepanjen (dibimbing oleh Bapak Hermawan Hidayat, S.Kom.).
                
                Ada yang ingin kita pelajari atau eksplorasi bersama hari ini? 😊$topicNotice
                
                💡 **Beberapa hal seru yang bisa kita diskusikan:**
                - 📖 **Konsep Pemrograman:** Dasar Python, variabel, logika perulangan (loop), atau fungsi (def).
                - 🧬 **Struktur Data:** List, Tuple, Set, Dictionary, atau struktur bersarang (nested).
                - 🛠️ **Diagnosa Error:** Ada pesan error merah di konsol? Tanyakan langsung di sini!
                - 🎯 **Tips Belajar:** Kiat mengasah logika komputasional dan praktik koding yang efisien.
                
                Ketikkan pertanyaanmu atau pilih salah satu tombol topik di atas ya!
            """.trimIndent()
        }

        // 4. CONTEXTUAL: Identity & Profiling ("siapa kamu", "siapa guru", "sman 1 kepanjen")
        if (isIdentity(q)) {
            return """
                🤖 **Profil AI Tutor Python DataLab (SMAN 1 Kepanjen):**
                
                Saya adalah **AI Tutor Cerdas Praktikum Informatika**, dirancang khusus untuk mendampingi siswa kelas XI SMA Negeri 1 Kepanjen dalam menguasai pemrograman Python dan struktur data komputasional.
                
                👨‍🏫 **Pembimbing Kurikulum:**
                Bapak **Hermawan Hidayat, S.Kom.** (Guru Informatika SMAN 1 Kepanjen).
                
                🎯 **Prinsip & Nilai Pembelajaran:**
                1. **Anti-Halusinasi (Ground Truth):** Jawaban teknis selalu berbasis dokumentasi resmi CPython 3.11 dan kaidah memori yang terverifikasi.
                2. **Pedagogis & Suportif:** Membimbing alur berpikir secara mandiri (*computational thinking*), bukan sekadar memberi jalan pintas.
                3. **Interaktif & Kontekstual:** Terhubung langsung dengan interpreter Python lokal, materi visual, dan tantangan lab.
                
                Ada materi atau tantangan koding yang ingin kamu tanyakan hari ini? 🚀
            """.trimIndent()
        }

        // 5. CONTEXTUAL: App Guidance & How-to ("cara pakai", "fitur aplikasi", "panduan")
        if (isAppGuide(q)) {
            return """
                📱 **Panduan Lengkap Fitur Aplikasi Python DataLab:**
                
                Aplikasi ini dirancang sebagai laboratorium digital Informatika interaktif. Berikut 4 fitur utama yang bisa kamu manfaatkan:
                
                1. 📚 **Tab Materi (Pusat Teori & Visualisasi Memori):**
                   - Penjelasan mendalam tentang List, Tuple, Set, dan Dictionary.
                   - **Visualisasi Memori Interaktif:** Melihat representasi RAM, pointer array, dan hash table secara visual.
                   - **Live Code Runner:** Menjalankan contoh kode Python langsung di materi tanpa berpindah layar!
                
                2. 🧪 **Tab Lab Interaktif (Playground & Tantangan):**
                   - Editor kode Python dengan compiler/interpreter lokal berkecepatan tinggi.
                   - Tantangan koding berjenjang untuk menguji pemahaman logikamu.
                
                3. 🤖 **Tab AI Tutor (Konsultasi Belajar 24/7):**
                   - Tanya jawab materi pemrograman, tips koding, serta bedah pesan error secara kontekstual.
                   - Fitur *Lampirkan Kode Aktif* untuk menganalisis kode yang sedang kamu buat di Playground.
                
                4. 🏆 **Gamifikasi & Prestasi Belajar:**
                   - Dapatkan poin XP dan lencana prestasi (*badges*) setiap kali menyelesaikan materi atau tantangan koding.
                
                Yuk, coba eksplorasi salah satu fiturnya sekarang! Ada yang ingin kamu coba lebih dulu?
            """.trimIndent()
        }

        // 6. CONTEXTUAL: Motivation & Study Tips ("susah", "sulit", "bingung", "tips belajar", "malas")
        if (isMotivationOrStudyTips(q)) {
            return """
                💪 **Jangan Menyerah! Koding Itu Soal Melatih Pola Pikir (Refleksi Siswa):**
                
                Merasa bingung, pusing, atau menjumpai error saat belajar koding adalah hal yang **sangat wajar dan dialami oleh semua programmer di dunia**. Bahkan programmer berpengalaman pun menghabiskan sebagian besar waktunya untuk mencari tahu mengapa kodenya belum berjalan!
                
                💡 **4 Kiat Praktis Belajar Koding dari Guru Hermawan:**
                1. 🧩 **Pecah Masalah Jadi Kecil (*Decomposition*):**
                   Jangan langsung menulis 20 baris kode. Tulis 2-3 baris terlebih dahulu, jalankan (*run*), lalu pastikan outputnya sesuai sebelum lanjut.
                2. 🔍 **Sahabati Pesan Error:**
                   Error di Python bukan tanda kamu gagal, melainkan petunjuk ramah dari interpreter:
                   - `IndexError` = Kamu meminta nomor kursi yang tidak ada di daftar.
                   - `TypeError: tuple` = Kamu mencoba mengubah sesuatu yang disegel (immutable).
                   - `KeyError` = Kunci kamar di kamus belum terdaftar.
                3. 🎨 **Gunakan Visualisasi Memori:**
                   Buka tab Materi dan perhatikan diagram memorinya. Membayangkan bagaimana RAM bekerja membuat logika koding jauh lebih mudah dipahami.
                4. 🧪 **Eksperimen di Playground:**
                   Ubah angka-angka di kode contoh dan lihat apa yang terjadi. Dari eksperimen kecil itulah intuisi kodingmu tumbuh.
                
                Tarik napas, santai sejenak, dan katakan pada diri sendiri: *"Saya pasti bisa memahami logikanya!"* Ada baris kode tertentu yang sedang membuatmu bingung? Tuliskan di sini, mari kita pecahkan bersama! 🌟
            """.trimIndent()
        }

        // 7. CONTEXTUAL: Gratitude & Politeness ("terima kasih", "makasih", "paham", "oke siap")
        if (isGratitude(q)) {
            return """
                ✨ **Sama-sama! Senang sekali bisa membantu belajarmu!**
                
                Tetap semangat mengasah logika dan keterampilan kodingmu ya. Ingat, semakin sering kamu berlatih dan bereksperimen di tab Lab, semakin tajam cara berpikir komputasionalmu.
                
                Jika nanti ada kode yang bermasalah, ada konsep yang bikin penasaran, atau ingin mengecek efisiensi kodemu, langsung tanyakan pada saya lagi kapan saja. Selamat belajar dan sukses selalu! 🚀
            """.trimIndent()
        }

        // 8. CONTEXTUAL: Jokes & Fun Facts ("lelucon", "joke", "lucu", "tebak-tebakan", "fakta")
        if (isJoke(q)) {
            return """
                😄 **Pojok Humor Programmer Python:**
                
                *Kenapa programmer lebih suka minum teh hangat sambil koding di malam hari?*
                👉 *Karena mereka tidak suka Java (kopi) dan lebih mencintai kesederhanaan Python!* ☕🐍
                
                ---
                
                *Fakta Seru Python:*
                Nama **Python** sebenarnya bukan diambil dari nama ular sanca lho! Sang penciptanya, **Guido van Rossum**, menamakannya dari grup komedi legendaris asal Inggris bernama **"Monty Python's Flying Circus"** karena beliau ingin bahasa ini menyenangkan dan tidak membosankan untuk dipelajari.
                
                Mau lanjut belajar atau ada pertanyaan seru lainnya seputar koding? 💻
            """.trimIndent()
        }

        // Specific high-precision structure inquiries (prevent misrouting)
        if (q.contains("set{}") || q.contains("set()") || (q.contains("set") && (q.contains("kosong") || q.contains("empty"))) || (q.contains("himpunan") && q.contains("kosong"))) {
            return handleSetQuestions(q, code, topicTitle)
        }
        if (q.contains("discard") || (q.contains("remove") && (q.contains("set") || topicTitle?.lowercase()?.contains("set") == true))) {
            return handleSetQuestions(q, code, topicTitle)
        }
        if ((q.contains("kunci") || q.contains("key")) && (q.contains("dict") || q.contains("dictionary") || q.contains("kamus") || q.contains("hashable") || q.contains("unhashable"))) {
            return handleDictionaryQuestions(q, code, topicTitle)
        }
        if (q.contains("unpack") || q.contains("swap") || q.contains("tukar") || q.contains("(1,)") || (q.contains("koma") && (q.contains("tuple") || topicTitle?.lowercase()?.contains("tuple") == true))) {
            return handleTupleQuestions(q, code, topicTitle)
        }
        if (q.contains("append") || q.contains("extend") || (q.contains("pop") && (q.contains("list") || topicTitle?.lowercase()?.contains("list") == true))) {
            return handleListQuestions(q, code, topicTitle)
        }

        // 9. CONTEXTUAL: Cross-Structure Comparisons & Choices (List vs Tuple vs Set vs Dict)
        if (isCrossStructureComparison(q, topicTitle)) {
            return handleCrossStructureComparison(q, topicTitle)
        }

        // 10. CONTEXTUAL: Nested Structures (List of Dict, Dict of List, Matrix, JSON)
        if (isNestedStructuresQuery(q, topicTitle)) {
            return handleNestedStructures(q, code)
        }

        // 11. CONTEXTUAL: Dictionary Specific Queries (Key-Value, get, items, keys, unhashable keys)
        if (isDictionaryQuery(q, topicTitle, code)) {
            return handleDictionaryQuestions(q, code, topicTitle)
        }

        // 12. CONTEXTUAL: Set Specific Queries (Unique, Venn, add, discard, set() vs {}, unhashable)
        if (isSetQuery(q, topicTitle, code)) {
            return handleSetQuestions(q, code, topicTitle)
        }

        // 13. CONTEXTUAL: Tuple Specific Queries (Immutable, (1,), unpacking, swap, keys)
        if (isTupleQuery(q, topicTitle, code)) {
            return handleTupleQuestions(q, code, topicTitle)
        }

        // 14. CONTEXTUAL: List Specific Queries (append vs extend, pop, remove, comprehension, 2D)
        if (isListQuery(q, topicTitle, code)) {
            return handleListQuestions(q, code, topicTitle)
        }

        // 15. CONTEXTUAL: Slicing & Indexing [start:stop:step]
        if (isSlicingQuery(q)) {
            return handleSlicingAndIndexing(q)
        }

        // 16. CONTEXTUAL: Big-O Complexity / Time & Space Efficiency
        if (isBigOQuery(q)) {
            return handleBigOComplexity(q)
        }

        // 17. CONTEXTUAL: Operators & Operands
        if (isOperatorQuery(q)) {
            return handleOperatorsAndOperands(q)
        }

        // 18. CONTEXTUAL: Input & Output (print, f-string, sep, end, input)
        if (isInputOutputQuery(q)) {
            return handleInputOutput(q)
        }

        // 19. CONTEXTUAL: Branching & Conditionals (if, elif, else, nested if, ternary)
        if (isBranchingQuery(q)) {
            return handleBranchingConditionals(q)
        }

        // 20. CONTEXTUAL: Looping & Iteration (for, while, range, break, continue)
        if (isLoopingQuery(q)) {
            return handleLoopsIteration(q)
        }

        // 21. CONTEXTUAL: Functions & Modular Programming (def, return, scope)
        if (isFunctionsQuery(q)) {
            return handleFunctionsAndScope(q)
        }

        // 22. CONTEXTUAL: Variables & Identifier Naming (Dynamic Typing, snake_case)
        if (isVariablesQuery(q)) {
            return handleVariablesAndNaming(q)
        }

        // 23. CONTEXTUAL: Data Types & Type Casting (int, float, str, bool, casting)
        if (isDataTypesQuery(q)) {
            return handleDataTypesAndCasting(q)
        }

        // 24. CONTEXTUAL: General Python & Computational Thinking
        if (isGeneralPython(q)) {
            return handleGeneralPythonQuery(q)
        }

        // 25. Smart Contextual Default Fallback (with active topic awareness)
        return handleContextualFallback(question, topicTitle)
    }

    private fun isGreeting(q: String): Boolean {
        val trimmed = q.trim().trim('!', '?', '.', ',', ' ', '~')
        val words = trimmed.split("\\s+".toRegex())
        val greetingWords = setOf(
            "halo", "hai", "hello", "hei", "hi", "hey", "hallo", "helo",
            "pagi", "siang", "sore", "malam",
            "assalamualaikum", "assalamu'alaikum", "assalam",
            "kabar", "tes", "ping", "p"
        )
        if (words.size <= 4 && words.any { it in greetingWords }) return true
        if (trimmed.startsWith("selamat pagi") || trimmed.startsWith("selamat siang") ||
            trimmed.startsWith("selamat sore") || trimmed.startsWith("selamat malam") ||
            trimmed.startsWith("apa kabar") || trimmed.startsWith("halo") || trimmed.startsWith("hai") ||
            trimmed.startsWith("hello") || trimmed.startsWith("hei")
        ) return true
        return false
    }

    private fun isIdentity(q: String): Boolean {
        val identityTriggers = listOf(
            "siapa kamu", "kamu siapa", "siapa anda", "anda siapa",
            "tentang kamu", "profilmu", "profil kamu", "siapa namamu",
            "nama kamu", "siapa pembuat", "guru hermawan", "hermawan hidayat",
            "siapa gurumu", "sman 1 kepanjen", "bot apa ini", "ai apa ini",
            "pembuat aplikasi", "tutor siapa"
        )
        return identityTriggers.any { q.contains(it) }
    }

    private fun isAppGuide(q: String): Boolean {
        val guideTriggers = listOf(
            "cara pakai", "cara menggunakan", "fitur aplikasi", "panduan aplikasi",
            "bisa apa saja", "menu apa saja", "bantuan aplikasi", "cara kerja aplikasi",
            "tutorial aplikasi", "cara main", "apa fungsi aplikasi", "panduan penggunaan"
        )
        return guideTriggers.any { q.contains(it) }
    }

    private fun isMotivationOrStudyTips(q: String): Boolean {
        val triggers = listOf(
            "susah", "sulit", "bingung", "pusing", "tips belajar", "cara cepat paham",
            "malas", "motivasi", "menyerah", "capek", "frustasi", "gimana belajar koding",
            "takut koding", "koding sulit", "kenapa koding susah", "tips koding", "tips pemula"
        )
        return triggers.any { q.contains(it) }
    }

    private fun isGratitude(q: String): Boolean {
        val gratitudeTriggers = listOf(
            "terima kasih", "makasih", "thanks", "thank you", "tengkyu",
            "paham sekarang", "sudah paham", "mengerti", "oke siap",
            "mantap", "keren", "bagus", "sip tutor", "oke makasih",
            "makasih ya", "makasi", "terimakasih"
        )
        val trimmed = q.trim().trim('!', '?', '.', ',', ' ')
        return gratitudeTriggers.any { q.contains(it) } || trimmed in setOf("oke", "siap", "sip", "ok", "makasih", "thanks", "mantap")
    }

    private fun isJoke(q: String): Boolean {
        val jokeTriggers = listOf("lelucon", "joke", "lucu", "tebak-tebakan", "cerita lucu", "humor")
        return jokeTriggers.any { q.contains(it) }
    }

    // =========================================================================
    // CONTEXTUAL PYTHON FUNDAMENTALS (OPERATORS, I/O, BRANCHING, LOOPS, VARIABLES, TYPES, FUNCTIONS)
    // =========================================================================

    private fun isOperatorQuery(q: String): Boolean {
        val opTriggers = listOf(
            "operator", "operan", "operand", "aritmatika", "modulo", "pangkat",
            "floor division", "bagi bulat", "perbandingan", "relasional",
            "presedensi", "is vs ==", "== vs is", "beda / dan //", "beda / dengan //",
            "// dan /", "/ dan //", "operator logika", "and or not", "sisa bagi",
            "simbol %", "operator penugasan", "assignment operator"
        )
        return opTriggers.any { q.contains(it) } || (q.contains("bagi") && q.contains("//")) || (q.contains("sisa") && q.contains("%"))
    }

    private fun handleOperatorsAndOperands(q: String): String {
        return """
            🎯 **Operator & Operan dalam Python 3.11:**

            Dalam pemrograman komputer:
            - 🔹 **Operan (*Operand*):** Adalah nilai, data, atau variabel yang sedang dioperasikan/diproses.
            - 🔸 **Operator:** Adalah simbol instruksi khusus yang memberi tahu komputer operasi apa yang harus dilakukan terhadap operan.
            
            *Contoh:* Pada ekspresi `hasil = a + b`, nilai `a` dan `b` adalah **Operan**, sedangkan tanda `+` dan `=` adalah **Operator**.

            🔬 **Klasifikasi Operator Resmi Python:**

            1️⃣ **Operator Aritmatika:**
            - `+` (Penjumlahan) & `-` (Pengurangan)
            - `*` (Perkalian) & `**` (Pangkat/Eksponen, misal: `2 ** 3 = 8`)
            - `/` (**Pembagian Float**): *Selalu* menghasilkan desimal pecahan (`10 / 2 = 5.0`)
            - `//` (**Floor Division**): Pembagian bulat dibulatkan ke bawah (`17 // 5 = 3`)
            - `%` (**Modulo**): Sisa pembagian (`17 % 5 = 2`)

            2️⃣ **Operator Perbandingan (Menghasilkan True/False):**
            - `==` (Sama dengan nilai) & `!=` (Tidak sama dengan)
            - `>`, `<`, `>=`, `<=`

            3️⃣ **Operator Logika:**
            - `and`: Bernilai `True` hanya jika **kedua** kondisi benar.
            - `or`: Bernilai `True` jika **salah satu** kondisi benar.
            - `not`: Membalikkan status kebenaran (`not True` -> `False`).

            4️⃣ **Operator Identitas (`is` vs `==`):**
            - `==` membandingkan **kesamaan nilai** (*equality*).
            - `is` membandingkan apakah dua variabel menunjuk ke **alamat memori yang persis sama** (`id(a) == id(b)`).

            5️⃣ **Presedensi Operator (Urutan Eksekusi):**
            Tanda Kurung `()` ➔ Pangkat `**` ➔ Perkalian/Pembagian/Modulo/Floor `*`, `/`, `//`, `%` ➔ Penjumlahan/Pengurangan `+`, `-` ➔ Perbandingan ➔ Logika `not`, `and`, `or`.

            💻 **Contoh Kode Verifikasi Python:**
            ```python
            a = 19
            b = 4

            # 1. Perbedaan Tiga Macam Pembagian
            print("Pembagian Float (/):", a / b)   # 4.75
            print("Floor Division (//):", a // b)  # 4
            print("Modulo Sisa Bagi (%):", a % b)  # 3 (karena 19 = 4 * 4 + 3)
            print("Pangkat (**):", b ** 3)         # 64 (4 * 4 * 4)

            # 2. Operator Logika Terpadu
            nilai_tugas = 85
            absensi = 90
            lulus = (nilai_tugas >= 75) and (absensi >= 80)
            print("Status Kelulusan Siswa:", lulus) # True

            # 3. Pembuktian '==' vs 'is' di Memori
            x = [1, 2, 3]
            y = [1, 2, 3]
            print("Apakah nilai x == y?", x == y)  # True (Isinya identik)
            print("Apakah objek memori x is y?", x is y) # False (Slot RAM berbeda!)
            ```

            💡 **Tips Guru Hermawan:**
            - Modulo `% 2 == 0` adalah cara standar paling cepat untuk mendeteksi bilangan genap!
            - Jangan pernah gunakan tanda `=` tunggal dalam kondisi perbandingan `if` — selalu gunakan `==` untuk memeriksa kesamaan.
        """.trimIndent()
    }

    private fun isInputOutputQuery(q: String): Boolean {
        val ioTriggers = listOf(
            "input", "output", "print", "f-string", "format string", "parameter sep",
            "parameter end", "baca input", "input()", "mencetak", "fungsi print", "fungsi input"
        )
        return ioTriggers.any { q.contains(it) } || (q.contains("cetak") && q.contains("layar"))
    }

    private fun handleInputOutput(q: String): String {
        return """
            🎯 **Input dan Output (I/O) pada Python 3.11:**

            Komunikasi data antara program dan pengguna terminal diatur oleh dua fungsi bawaan utama: `print()` untuk keluaran (output) dan `input()` untuk masukan (input).

            🔬 **Kaidah Resmi Spesifikasi Python:**

            1️⃣ **Fungsi `print(*objects, sep=' ', end='\n')`:**
            - `sep` (*separator*): Mengatur karakter pemisah antar argumen (default adalah spasi tunggal `' '`).
            - `end`: Mengatur karakter di akhir cetakan (default adalah ganti baris baru `'\n'`).

            2️⃣ **Modern f-string (Formatted String Literals):**
            - Sintaks modern tercepat dan paling bersih (direkomendasikan sejak Python 3.6+).
            - Awali string dengan huruf `f`, lalu masukkan variabel atau ekspresi matematika langsung di dalam kurung kurawal `{variabel}`.
            - Format presisi angka desimal: `{nilai:.2f}` (membulatkan tampilan ke 2 digit di belakang koma).

            3️⃣ **Fungsi `input(prompt)`:**
            - Menghentikan sementara jalannya program untuk menunggu masukan teks dari keyboard pengguna.
            - ⚠️ **ATURAN MUTLAK PYTHON:** Hasil dari fungsi `input()` **SELALU bertipe data `str` (string/teks)**!
            - Jika kamu ingin mengolah masukan sebagai angka (misal untuk penjumlahan), kamu **wajib melakukan Type Casting** dengan `int(input())` atau `float(input())`.

            💻 **Contoh Kode Verifikasi Python:**
            ```python
            # 1. Mengatur Parameter sep dan end
            print("SMAN", "1", "Kepanjen", sep="-") # Output: SMAN-1-Kepanjen
            print("Sedang memproses data", end="...")
            print(" Selesai!") # Output: Sedang memproses data... Selesai!

            # 2. Modern f-string dengan kalkulasi dan pembulatan
            nama = "Fajar Nugraha"
            nilai_murni = 87.6666
            print(f"Siswa: {nama} | Rata-rata Ujian: {nilai_murni:.2f} | KKM: {nilai_murni >= 75}")

            # 3. Input dengan konversi tipe eksplisit (Type Casting)
            # teks_usia = input("Masukkan umur: ")      # Tipe: str (contoh: "16")
            # usia_angka = int(teks_usia)              # Tipe: int (16)
            # tahun_lahir = 2026 - usia_angka
            # print(f"Tahun estimasi lahir: {tahun_lahir}")
            ```

            💡 **Tips Guru Hermawan:**
            Hindari menggabungkan teks dan angka menggunakan tanda tambah `print("Nilai: " + 90)` karena akan memicu `TypeError`. Gunakan **f-string `print(f"Nilai: {90}")`** yang otomatis mengonversi data ke teks dengan aman!
        """.trimIndent()
    }

    private fun isBranchingQuery(q: String): Boolean {
        val branchTriggers = listOf(
            "percabangan", "kondisi", "if else", "if-else", "elif", "nested if",
            "ternary", "seleksi kondisi", "percabangan bersarang", "aturan if"
        )
        return branchTriggers.any { q.contains(it) } || (q.contains("if") && (q.contains("cara") || q.contains("bagaimana") || q.contains("contoh") || q.contains("sintaks")))
    }

    private fun handleBranchingConditionals(q: String): String {
        return """
            🎯 **Percabangan & Seleksi Kondisi (if, elif, else) di Python 3.11:**

            Percabangan memungkinkan program mengambil keputusan cerdas: mengeksekusi blok instruksi tertentu hanya jika syarat/kondisi terpenuhi (bernilai `True`).

            🔬 **Kaidah Resmi & Struktur Sintaks Python:**

            1️⃣ **Titik Dua (`:`) dan Indentasi 4 Spasi (PEP 8):**
            - Setiap baris `if`, `elif`, dan `else` **WAJIB diakhiri tanda titik dua `:`**.
            - Baris di dalam blok keputusan **WAJIB menjorok ke dalam (indentasi)**. Jika indentasi hilang atau tidak konsisten, Python melempar `IndentationError`.

            2️⃣ **Evaluasi Sekuensial (Atas ke Bawah):**
            - Python memeriksa kondisi secara berurutan. Saat satu kondisi bernilai `True`, blok kodenya dieksekusi dan seluruh `elif` serta `else` di bawahnya langsung dilewati.
            - Gunakan `elif` (singkatan dari *else if*), bukan `else if` terpisah.

            3️⃣ **Percabangan Bersarang (*Nested if*):**
            - Struktur `if` yang berada di dalam blok `if` lainnya untuk memvalidasi kondisi berlapis.

            4️⃣ **Ternary Operator (Conditional Expression 1 Baris):**
            - Bentuk ringkas untuk penugasan nilai bersyarat: `hasil = nilai_a if kondisi else nilai_b`.

            💻 **Contoh Kode Verifikasi Python:**
            ```python
            skor_siswa = 84

            # 1. Struktur Standar if - elif - else
            if skor_siswa >= 90:
                predikat = "A (Amat Baik)"
            elif skor_siswa >= 80:
                predikat = "B (Baik)"
            elif skor_siswa >= 70:
                predikat = "C (Cukup)"
            else:
                predikat = "D (Perlu Bimbingan)"

            print(f"Hasil Evaluasi Siswa: {predikat}")

            # 2. Percabangan Bersarang (Nested if)
            kehadiran = 92
            status_siswa = "Aktif"

            if status_siswa == "Aktif":
                if skor_siswa >= 75 and kehadiran >= 85:
                    print("Status: LULUS SELEKSI INFORMATIKA")
                else:
                    print("Status: REMIDI MATERI TERTENTU")

            # 3. Ternary Operator (One-Liner)
            keterangan = "Tuntas KKM" if skor_siswa >= 75 else "Belum Tuntas"
            print("Keterangan Singkat:", keterangan)
            ```

            💡 **Tips Guru Hermawan:**
            Urutkan batasan kondisimu dari yang paling ketat/spesifik ke yang paling longgar (misal: 90 dulu, baru 80, baru 70). Jika dibalik `if skor >= 70:`, maka nilai 95 akan langsung terjaring di kondisi 70 dan predikat A tidak pernah tercapai!
        """.trimIndent()
    }

    private fun isLoopingQuery(q: String): Boolean {
        val loopTriggers = listOf(
            "perulangan", "looping", "loop", "for loop", "while loop", "fungsi range",
            "range(", "break", "continue", "infinite loop", "iterasi", "kontrol loop", "pernyataan pass"
        )
        return loopTriggers.any { q.contains(it) } || (q.contains("while") && q.contains("for")) || (q.contains("ulang") && q.contains("kode"))
    }

    private fun handleLoopsIteration(q: String): String {
        return """
            🎯 **Struktur Perulangan (*Looping*) & Iterasi di Python 3.11:**

            Perulangan digunakan untuk mengeksekusi sekumpulan instruksi berkali-kali secara otomatis tanpa perlu menduplikasi baris kode.

            🔬 **Dua Jenis Perulangan Utama di Python:**

            1️⃣ **`for` Loop (Iterasi Berbasis Urutan/Koleksi):**
            - Paling sering digunakan saat jumlah putaran sudah diketahui atau ketika mengolah elemen koleksi (List, Tuple, Set, Dict, String).
            - **Fungsi `range(start, stop[, step])`:**
              * Menghasilkan urutan angka integer.
              * ⚠️ **INGAT SIFAT EKSKLUSIF NILAI `stop`:** `range(1, 5)` menghasilkan angka `1, 2, 3, 4` (angka 5 TIDAK diikutsertakan!).
              * Jika hanya 1 argumen `range(3)`, start otomatis dari `0` ➔ `0, 1, 2`.

            2️⃣ **`while` Loop (Perulangan Berbasis Kondisi):**
            - Mengeksekusi blok kode berulang kali **selama kondisinya bernilai `True`**.
            - ⚠️ **BAHAYA INFINITE LOOP:** Pastikan ada pernyataan penambah (*counter/increment*) seperti `i += 1` agar kondisi pada akhirnya menjadi `False`.

            3️⃣ **Pernyataan Kontrol Alur Loop:**
            - `break`: Menghentikan dan keluar seketika dari perulangan secara paksa.
            - `continue`: Melewati sisa baris kode pada putaran saat ini dan langsung melompat ke putaran berikutnya.
            - `pass`: Pernyataan kosong (*placeholder*) agar sintaks tidak error saat blok belum selesai ditulis.
            - Klausa `else` pada loop: Dijalankan jika loop selesai secara normal (bukan karena dipaksa berhenti oleh `break`).

            💻 **Contoh Kode Verifikasi Python:**
            ```python
            # 1. for loop dengan range()
            print("--- Menghitung 1 sampai 5 ---")
            for angka in range(1, 6):
                print(f"Langkah ke-{angka}")

            # 2. Mengiterasi List dengan fungsi enumerate()
            siswa_lab = ["Andi", "Bella", "Candra"]
            for no_absen, nama in enumerate(siswa_lab, start=1):
                print(f"Absen {no_absen}: {nama}")

            # 3. while loop dengan break dan continue
            penghitung = 0
            while penghitung < 10:
                penghitung += 1
                if penghitung == 4:
                    continue  # Lewati angka 4
                if penghitung == 8:
                    break     # Berhenti ketika mencapai 8
                print("Angka aktif:", penghitung)
            ```

            💡 **Tips Guru Hermawan:**
            Saat mengiterasi struktur data (List/Tuple/Dict), selalu utamakan menggunakan **`for item in data:`** daripada `while`. Cara ini lebih alami (Pythonic), lebih cepat dieksekusi oleh CPython, dan 100% bebas dari risiko program *freeze* akibat infinite loop!
        """.trimIndent()
    }

    private fun isVariablesQuery(q: String): Boolean {
        val varTriggers = listOf(
            "variabel", "variable", "identifier", "penamaan", "dynamic typing",
            "reassign", "snake_case", "nama variabel", "aturan variabel", "id()"
        )
        return varTriggers.any { q.contains(it) }
    }

    private fun handleVariablesAndNaming(q: String): String {
        return """
            🎯 **Variabel & Kaidah Penamaan (*Identifiers*) di Python 3.11:**

            Variabel adalah nama referensi (pointer berlabel) yang menunjuk ke lokasi objek data yang disimpan di dalam memori RAM komputer.

            🔬 **Kaidah Resmi & Sifat Variabel di Python:**

            1️⃣ **Dynamic Typing:**
            - Python **tidak memerlukan deklarasi tipe eksplisit** (seperti `int x = 10;` di C/Java).
            - Tipe variabel ditentukan secara dinamis pada saat program dijalankan (*runtime*) berdasarkan nilai objek yang ditunjuknya.
            - Mendukung *Re-assignment*: Sebuah variabel bisa menunjuk ke tipe integer, lalu kemudian diubah menunjuk ke tipe string.

            2️⃣ **Aturan Baku Penamaan Variabel (Identifier Rules):**
            - ✅ **Wajib diawali** dengan huruf (`a-z`, `A-Z`) atau garis bawah (`_`).
            - ❌ **DILARANG diawali angka**: `1nilai` akan memicu `SyntaxError: invalid decimal literal`.
            - ❌ **DILARANG menggunakan spasi** atau simbol khusus seperti `!`, `@`, `#`, `\${'$'}`, `%`, `-`.
            - ⚠️ **Case-Sensitive (Sensitif Huruf Besar/Kecil)**: `total_skor`, `Total_Skor`, dan `TOTAL_SKOR` adalah 3 variabel yang terpisah di memori.
            - ❌ **DILARANG menggunakan Kata Kunci Cadangan (*Reserved Keywords*)**: Jangan menamai variabel dengan `for`, `while`, `if`, `else`, `def`, `class`, `import`, `in`, `is`, `True`, `False`, dsb.

            3️⃣ **Standar Gaya Penulisan Resmi PEP 8:**
            - Gunakan format **`snake_case`**: semua huruf kecil dan dipisahkan garis bawah jika lebih dari satu kata (contoh: `nilai_ujian_informatika`, `status_kehadiran`).

            💻 **Contoh Kode Verifikasi Python:**
            ```python
            # 1. Deklarasi Variabel Beragam Tipe
            nama_sekolah = "SMAN 1 Kepanjen" # str
            tahun_berdiri = 1968              # int
            akreditasi_nilai = 96.5           # float
            status_negeri = True              # bool

            # 2. Dynamic Re-assignment & Cek Alamat Memori id()
            skor = 100
            print("Alamat Memori Awal:", id(skor))

            skor = "Sempurna"  # Sah di Python!
            print("Alamat Memori Baru:", id(skor))

            # 3. Multiple Assignment (Penugasan Sekaligus)
            x, y, z = 10, 20, 30
            print(f"Koordinat: x={x}, y={y}, z={z}")
            ```

            💡 **Tips Guru Hermawan:**
            Variabel adalah dokumentasi pertama dari kodemu. Gunakan nama yang mencerminkan maknanya secara spesifik: pilih `daftar_hadir_siswa` daripada hanya huruf `d` atau `data1`.
        """.trimIndent()
    }

    private fun isDataTypesQuery(q: String): Boolean {
        val dtTriggers = listOf(
            "tipe data", "data type", "type data", "type casting", "casting",
            "konversi tipe", "int float", "truthy", "falsy", "fungsi type"
        )
        return dtTriggers.any { q.contains(it) } || (q.contains("type(") && q.contains("fungsi"))
    }

    private fun handleDataTypesAndCasting(q: String): String {
        return """
            🎯 **Tipe Data & Konversi Tipe (*Type Casting*) di Python 3.11:**

            Data di Python dikelompokkan menjadi **Tipe Primitif Tunggal** dan **Struktur Data Koleksi**.

            🔬 **Klasifikasi Tipe Data Resmi Python:**

            1️⃣ **Tipe Data Primitif (Skalar):**
            - `int`: Bilangan bulat positif, negatif, atau nol tanpa batas ukuran (*arbitrary-precision integers*).
            - `float`: Bilangan desimal pecahan berbasis standar IEEE 754.
            - `str`: Sekumpulan karakter berurutan (*string*) yang bersifat **Immutable**.
            - `bool`: Nilai logika kebenaran yang hanya memiliki dua status: `True` (bernilai 1) atau `False` (bernilai 0).

            2️⃣ **Struktur Data Koleksi (Koleksi Jamak):**
            - 📜 **List `[...]`**: Sekumpulan data berurutan, dapat diubah (*mutable*).
            - 🔒 **Tuple `(...)`**: Sekumpulan data berurutan konstan, tidak dapat diubah (*immutable*).
            - ⭕ **Set `{...}`**: Sekumpulan elemen unik tanpa urutan (*unordered*).
            - 📖 **Dictionary `{k: v}`**: Pasangan kunci-nilai berindeks hash (*key-value mapping*).

            3️⃣ **Type Casting (Konversi Tipe Eksplisit):**
            - `int("25")` ➔ Menghasilkan integer `25`.
            - `float(10)` ➔ Menghasilkan float `10.0`.
            - `str(100)` ➔ Menghasilkan teks `"100"`.
            - `bool(x)` ➔ Mengevaluasi nilai kebenaran.
            - `list((1, 2))` atau `tuple([1, 2])` ➔ Konversi antar koleksi.

            4️⃣ **Kaidah Resmi Truthy vs Falsy di Python:**
            Setiap nilai di Python dapat dievaluasi dalam konteks Boolean:
            - **Nilai FALSY (Evaluasi ke `False`):**
              `0`, `0.0`, `""` (string kosong), `[]` (list kosong), `()` (tuple kosong), `set()` (set kosong), `{}` (dict kosong), `None`, dan `False`.
            - **Nilai TRUTHY (Evaluasi ke `True`):**
              Semua nilai angka bukan nol, string yang memiliki karakter, dan koleksi yang berisi elemen.

            💻 **Contoh Kode Verifikasi Python:**
            ```python
            # 1. Pengecekan Tipe dengan type()
            skor = 98
            print("Tipe data skor:", type(skor)) # <class 'int'>

            # 2. Type Casting
            input_pengguna = "75"
            nilai_angka = int(input_pengguna)
            rata_rata = float(nilai_angka)
            print(f"Angka: {nilai_angka} ({type(nilai_angka)}), Desimal: {rata_rata}")

            # 3. Uji Kebenaran Truthy & Falsy
            data_siswa = []
            if not data_siswa:
                print("List masih kosong (Falsy)!")
            ```

            💡 **Tips Guru Hermawan:**
            Ingat: `{}` tanpa isi adalah **Dictionary kosong**, BUKAN Set kosong! Jika ingin membuat Set kosong, gunakan sintaks konstruktor **`set()`**.
        """.trimIndent()
    }

    private fun isFunctionsQuery(q: String): Boolean {
        val funcTriggers = listOf(
            "fungsi", "function", "def ", "parameter", "argumen", "return",
            "scope", "variabel lokal", "variabel global", "modular"
        )
        return funcTriggers.any { q.contains(it) } || (q.contains("cara buat fungsi") || q.contains("apa itu def"))
    }

    private fun handleFunctionsAndScope(q: String): String {
        return """
            🎯 **Fungsi & Ruang Lingkup (*Scope*) Variabel di Python 3.11:**

            Fungsi adalah blok kode terstruktur yang diberi nama, dirancang untuk menyelesaikan satu tugas spesifik, dan dapat dipanggil berulang kali (*reusable*).

            🔬 **Kaidah Resmi Fungsi Python:**

            1️⃣ **Definisi dengan Kata Kunci `def`:**
            - Dideklarasikan dengan `def nama_fungsi(parameter):`.
            - Setiap baris isi fungsi wajib diindentasi 4 spasi.

            2️⃣ **Parameter vs Argumen:**
            - **Parameter:** Variabel penampung yang didefinisikan pada deklarasi `def`.
            - **Argumen:** Nilai data nyata yang dikirim saat fungsi dipanggil.

            3️⃣ **Perbedaan `return` vs `print()`:**
            - **`return`:** Mengembalikan nilai hasil komputasi ke pemanggil sehingga hasilnya dapat ditampung ke variabel atau diolah lagi.
            - **`print()`:** Hanya menampilkan teks ke terminal console. Fungsi yang tidak memiliki pernyataan `return` secara otomatis mengembalikan nilai **`None`**.

            4️⃣ **Default Argument (Nilai Bawaan):**
            - Parameter dapat diberi nilai default: `def hitung(x, bobot=0.5):`.
            - Parameter default harus selalu diletakkan **setelah** parameter wajib.

            5️⃣ **Ruang Lingkup Variabel (*Variable Scope*):**
            - **Variabel Lokal:** Dibuat di dalam fungsi, hanya hidup saat fungsi berjalan, dan tidak dapat diakses dari luar fungsi.
            - **Variabel Global:** Dibuat di tingkat utama program di luar fungsi, dapat dibaca oleh seluruh fungsi.

            💻 **Contoh Kode Verifikasi Python:**
            ```python
            # 1. Definisi Fungsi dengan Default Argument dan Docstring
            def hitung_nilai_akhir(tugas, ujian, bobot_ujian=0.6):
                # Menghitung nilai berbobot siswa SMAN 1 Kepanjen
                bobot_tugas = 1 - bobot_ujian
                nilai_akhir = (tugas * bobot_tugas) + (ujian * bobot_ujian)
                return nilai_akhir

            # 2. Pemanggilan Fungsi & Penampungan Nilai Kembalian
            hasil_andi = hitung_nilai_akhir(80, 90)
            print(f"Nilai Akhir Andi: {hasil_andi:.1f}") # Output: 86.0

            # 3. Bukti Scope Variabel Lokal
            def uji_lingkup():
                pesan_rahasia = "Data Terlindungi"
                return pesan_rahasia

            print(uji_lingkup())
            # print(pesan_rahasia) -> Mem引发 NameError: name 'pesan_rahasia' is not defined
            ```

            💡 **Tips Guru Hermawan:**
            Setiap fungsi sebaiknya hanya bertanggung jawab atas **satu tugas spesifik** (prinsip *Single Responsibility*). Jika fungsimu mulai terlalu panjang dan rumit, pecahlah menjadi fungsi-fungsi pembantu yang lebih kecil!
        """.trimIndent()
    }

    private fun isGeneralPython(q: String): Boolean {
        val basicTriggers = listOf(
            "apa itu python", "kenapa python", "sejarah python", "kelebihan python", "python itu apa",
            "kenapa koding", "manfaat koding", "kenapa harus koding", "computational thinking",
            "berpikir komputasional", "apa itu koding", "bahasa pemrograman", "kenapa belajar python"
        )
        return basicTriggers.any { q.contains(it) }
    }

    private fun handleGeneralPythonQuery(q: String): String {
        return when {
            q.contains("python") && (q.contains("apa") || q.contains("kenapa") || q.contains("sejarah") || q.contains("kelebihan")) -> {
                """
                    🐍 **Mengenal Bahasa Pemrograman Python:**
                    
                    Python adalah bahasa pemrograman tingkat tinggi (*high-level language*) yang diciptakan oleh **Guido van Rossum** pada tahun 1991.
                    
                    🌟 **Mengapa Python Sangat Populer di Seluruh Dunia?**
                    1. **Sintaks Bersih & Mudah Dibaca:** Menggunakan indentasi dan kata-kata bahasa Inggris sederhana tanpa kurung kurawal `{}` yang rumit.
                    2. **Multi-Platform & Serbaguna:** Digunakan luas dalam bidang **Artificial Intelligence (AI)**, Data Science, Web Development, hingga Otomasi Robotika.
                    3. **Koleksi Struktur Data Bawaan yang Kuat:** Memiliki List, Tuple, Set, dan Dictionary secara native tanpa perlu instalasi pustaka tambahan.
                    4. **Ekosistem Raksasa:** Didukung ribuan library canggih seperti NumPy, Pandas, TensorFlow, dan PyTorch.
                    
                    💻 **Contoh Kesederhanaan Python:**
                    ```python
                    # Di Python, mencetak pesan hanya 1 baris:
                    print("Halo Siswa Informatika SMAN 1 Kepanjen!")
                    ```
                    
                    💡 **Tips Guru Hermawan:**
                    Fokuslah memahami logika berpikirnya terlebih dahulu. Di era kecerdasan buatan, pemahaman dasar komputasi adalah fondasi paling berharga!
                """.trimIndent()
            }

            else -> {
                """
                    💡 **Fondasi Berpikir Komputasional & Koding:**
                    
                    Belajar informatika bukan sekadar menghafal sintaks bahasa pemrograman, melainkan melatih **Berpikir Komputasional (*Computational Thinking*)**:
                    1. **Dekomposisi:** Memecah masalah besar menjadi bagian-bagian kecil yang dapat dikelola.
                    2. **Pengenalan Pola:** Menemukan keteraturan atau kesamaan alur logika antar masalah.
                    3. **Abstraksi:** Fokus pada inti permasalahan dan mengabaikan detail yang tidak esensial.
                    4. **Algoritma:** Menyusun urutan langkah terstruktur dan terukur untuk menyelesaikan masalah.
                    
                    Ingin mencoba membuat program sederhana atau ada konsep dasar lain yang ingin kamu tanyakan? 😊
                """.trimIndent()
            }
        }
    }

    private fun diagnoseExecutionError(error: String, code: String?): String {
        val errTrimmed = error.trim()

        val matchingError = PythonDataLabKnowledgeBase.ERROR_KNOWLEDGE.firstOrNull { err ->
            errTrimmed.contains(err.errorType.substringBefore(":"))
        }

        return if (matchingError != null) {
            """
                ❌ **Analisis Error Eksekusi Siswa (SMAN 1 Kepanjen):**
                **${matchingError.errorType}**
                
                🔬 **Penyebab Resmi Menurut Spesifikasi CPython 3.11:**
                ${matchingError.rootCause}
                
                ${matchingError.verifiableExplanation}
                
                🛠️ **Langkah Solusi Terverifikasi:**
                ${matchingError.solution}
                
                ${if (!code.isNullOrBlank()) "💻 **Kode yang Menghasilkan Error:**\n```python\n$code\n```" else ""}
                
                💡 **Tips Guru Hermawan:**
                Jadikan pesan error sebagai petunjuk navigasi memori Python. Periksa apakah tipe data yang Anda gunakan sudah sesuai sifat aslinya (*mutable* vs *immutable*).
            """.trimIndent()
        } else {
            """
                ❌ **Analisis Pesan Error Interpreter:**
                ```
                $errTrimmed
                ```
                
                🔬 **Penjelasan Guru:**
                Eksekusi kode terhenti karena terjadi kegagalan runtime. Periksa kembali:
                1. Apakah ada kurung siku `[]`, kurung bulat `()`, atau kurung kurawal `{}` yang belum tertutup?
                2. Apakah ada tipe data yang salah digunakan (misal mengubah elemen tuple atau mengindeks set)?
                3. Apakah ada indentasi atau titik dua `:` yang tertinggal?
                
                💡 **Silakan perbaiki kodenya di Playground lalu klik tombol Jalankan kembali!**
            """.trimIndent()
        }
    }

    // =========================================================================
    // CONTEXTUAL DATA STRUCTURE HANDLERS (SET, LIST, TUPLE, DICTIONARY & NESTED)
    // =========================================================================

    private fun isCrossStructureComparison(q: String, topic: String? = null): Boolean {
        // Disqualify specific intra-structure operations immediately
        if (q.contains("append") || q.contains("extend") || q.contains("discard") ||
            q.contains("unpack") || q.contains("comprehension") || q.contains("slicing") ||
            q.contains("dict.get") || q.contains("items()") || q.contains("kosong") ||
            q.contains("set{}") || q.contains("set()") || q.contains("kunci") || q.contains("key")) {
            return false
        }

        val keywords = listOf(
            "beda", "perbedaan", "komparasi", "bandingkan", "vs", "versus", "kapan pakai",
            "pilih mana", "kelebihan kekurangan", "perbandingan", "mana yang lebih", "kapan harus pakai",
            "matriks perbandingan"
        )
        val distinctStructures = setOfNotNull(
            if (q.contains("list")) "list" else null,
            if (q.contains("tuple")) "tuple" else null,
            if (q.contains("set") && !q.contains("setdefault")) "set" else null,
            if (q.contains("dict") || q.contains("dictionary") || q.contains("kamus")) "dict" else null
        )
        val structureCount = distinctStructures.size

        val hasComparisonKeyword = keywords.any { q.contains(it) } || q.contains("pilih") || q.contains("kapan")
        val isTopicComparison = topic?.lowercase()?.contains("perbandingan") == true || topic?.lowercase()?.contains("comparison") == true
        return (hasComparisonKeyword && structureCount >= 2) || (structureCount >= 3) || (isTopicComparison && hasComparisonKeyword)
    }

    private fun handleCrossStructureComparison(q: String, topic: String? = null): String {
        return """
            🎯 **Matriks Perbandingan & Panduan Pemilihan 4 Struktur Data Python 3.11:**

            Setiap struktur data di Python dirancang untuk karakteristik dan kasus penggunaan komputasi yang berbeda:

            | Karakteristik | List `[...]` | Tuple `(...)` | Set `{...}` | Dictionary `{k: v}` |
            |---|---|---|---|---|
            | **Mutabilitas** | **Mutable** (Bisa diedit/ditambah) | **Immutable** (Konstan/Read-only) | **Mutable** (Bisa ditambah/dihapus) | **Mutable** (Bisa ditambah/diedit) |
            | **Urutan (Ordering)** | **Ordered** (Indeks `[0]`) | **Ordered** (Indeks `[0]`) | **Unordered** (Tanpa indeks integer) | **Ordered** (Insertion-order 3.7+) |
            | **Duplikasi Data** | **Diizinkan** (Boleh ada item sama) | **Diizinkan** (Boleh ada item sama) | **Dieliminasi** (100% Unik) | Kunci **Wajib Unik** (Nilai boleh kembar) |
            | **Sintaks Akses** | `data[0]`, slicing `[start:stop]` | `data[0]`, slicing `[start:stop]` | Iterasi `for` atau `in`, `list(s)[0]` | Berbasis Kunci `data[kunci]` atau `.get()` |
            | **Pencarian (`in`)** | **O(n)** (Linear search - lambat) | **O(n)** (Linear search - lambat) | **O(1)** (Hash Table - super cepat!) | **O(1)** pada Key (Hash Table instan) |
            | **Kebutuhan Memori** | Lebih besar (over-allocation) | **Paling hemat memori** | Lebih besar (slot hash table) | Cukup besar (compact hash table) |

            🔬 **Panduan Memilih Struktur yang Tepat (*Decision Flowchart*):**
            1. **Gunakan List `[...]`** ketika kamu membutuhkan kumpulan data berurutan yang sering berubah, ditambah (`.append()`), disisipkan, atau diurutkan (`.sort()`), misal: antrean antrian, riwayat nilai siswa, log aktivitas.
            2. **Gunakan Tuple `(...)`** ketika data bersifat konstan dan tidak boleh diubah demi keamanan (*defensive programming*), koordinat GPS `(lat, long)`, konstanta warna RGB, atau sebagai Kunci Dictionary berpasangan `(x, y)` karena bersifat *hashable*.
            3. **Gunakan Set `{...}`** ketika kamu ingin membuang semua duplikat secara instan, melakukan operasi himpunan matematika (irisan ekskul, gabungan anggota, selisih), atau mengecek keanggotaan data skala jutaan secara seketika O(1).
            4. **Gunakan Dictionary `{k: v}`** ketika kamu memodelkan objek dunia nyata dengan label berpasangan (misal data profil siswa: NIS, Nama, Kelas) atau tabel pencarian (*lookup table*).

            💻 **Contoh Kode Konversi Antar Struktur Data:**
            ```python
            # 1. Konversi List ke Set untuk Menghapus Duplikat
            data_mentah = [10, 20, 20, 30, 10, 40]
            data_unik = list(set(data_mentah)) # [10, 20, 30, 40]

            # 2. Konversi List ke Tuple (Mengunci data agar aman)
            daftar_siswa = ["Bayu", "Citra"]
            kunci_aman = tuple(daftar_siswa) # ('Bayu', 'Citra')

            # 3. Menggabungkan dua List menjadi Dictionary dengan zip()
            kunci = ["nama", "kelas", "nilai"]
            nilai = ["Aisyah", "XI-A", 95]
            biodata = dict(zip(kunci, nilai)) # {'nama': 'Aisyah', 'kelas': 'XI-A', 'nilai': 95}
            ```

            💡 **Tips Guru Hermawan:**
            "Pilihlah struktur data berdasarkan cara data tersebut akan diakses dan seberapa sering data tersebut dimodifikasi. Pemilihan struktur data yang tepat adalah langkah awal algoritma yang efisien!"
        """.trimIndent()
    }

    private fun isNestedStructuresQuery(q: String, topic: String? = null): Boolean {
        val nestedTriggers = listOf(
            "nested", "bersarang", "list of dict", "dict of list", "list dalam list",
            "dict dalam dict", "matriks", "matrix", "2d", "2 dimensi", "multidimensi",
            "struktur bersarang", "nested structure", "tabel data", "json", "hierarkis", "chained index"
        )
        val isTopicNested = topic?.lowercase()?.contains("nested") == true || topic?.lowercase()?.contains("bersarang") == true
        return nestedTriggers.any { q.contains(it) } || isTopicNested
    }

    private fun handleNestedStructures(q: String, code: String?): String {
        return """
            🎯 **Struktur Data Bersarang (*Nested Data Structures*) di Python 3.11:**

            Struktur bersarang adalah kondisi di mana suatu struktur data (List, Tuple, Set, Dict) menyimpan struktur data lainnya sebagai elemen atau nilainya.

            🔬 **3 Pola Struktur Bersarang Paling Populer di Dunia Industri:**

            1️⃣ **List of Dictionaries (Tabel Database Record):**
            - Sangat umum untuk merepresentasikan kumpulan data tabel siswa, produk toko, atau hasil respons REST API JSON.
            - Akses: `data[indeks_baris]["nama_kolom"]`.

            2️⃣ **Dictionary of Lists (Pengelompokan Kategori / Nilai Siswa):**
            - Digunakan untuk memetakan satu kunci ke banyak nilai berurutan.
            - Akses: `data["kunci_kategori"][indeks_urutan]`.

            3️⃣ **2D List / Matriks (Tabel Angka Dua Dimensi):**
            - Representasi baris dan kolom: baris pertama `matriks[0]`, kolom kedua dari baris pertama `matriks[0][1]`.

            💻 **Contoh Kode Verifikasi Python 3.11:**
            ```python
            # 1. List of Dictionaries (Daftar Siswa SMAN 1 Kepanjen)
            kelas_xi = [
                {"nis": 101, "nama": "Aisyah", "nilai": [88, 92, 95]},
                {"nis": 102, "nama": "Budi", "nilai": [78, 85, 90]}
            ]

            # Mengakses nama siswa pertama
            print("Nama Siswa 1:", kelas_xi[0]["nama"]) # Aisyah

            # Mengakses nilai ujian ketiga siswa pertama
            print("Nilai ke-3 Aisyah:", kelas_xi[0]["nilai"][2]) # 95

            # 2. Dictionary of Dictionaries (Struktur Hierarkis / JSON)
            sekolah = {
                "nama": "SMAN 1 Kepanjen",
                "laboratorium": {
                    "lab_komputer_1": {"pc_tersedia": 36, "guru_pendamping": "Hermawan Hidayat, S.Kom."},
                    "lab_komputer_2": {"pc_tersedia": 32, "guru_pendamping": "Tim Lab"}
                }
            }

            guru_lab1 = sekolah["laboratorium"]["lab_komputer_1"]["guru_pendamping"]
            print("Pendamping Lab 1:", guru_lab1)
            ```

            💡 **Tips Guru Hermawan:**
            Saat mengakses struktur bersarang yang dalam, selalu gunakan metode defensif `.get()` pada Dictionary:
            `sekolah.get("laboratorium", {}).get("lab_komputer_1", {}).get("pc_tersedia", 0)`
            Trik ini mencegah program crash jika salah satu kunci di tengah belum terdaftar!
        """.trimIndent()
    }

    private fun isDictionaryQuery(q: String, topic: String? = null, code: String? = null): Boolean {
        val dictTriggers = listOf(
            "dict", "dictionary", "kamus", "key-value", "key value", "kunci", "pasangan",
            "get(", ".get", "popitem", "keys()", "values()", "items()",
            "keyerror", "hashable", "unhashable", "setdefault", "mapping", "update"
        )
        if (dictTriggers.any { q.contains(it) }) return true
        val isTopicDict = topic?.lowercase()?.contains("dict") == true || topic?.lowercase()?.contains("kamus") == true
        val hasCodeDict = code?.contains("{") == true && code.contains(":")
        return isTopicDict || hasCodeDict
    }

    private fun handleDictionaryQuestions(q: String, code: String?, topic: String? = null): String {
        return when {
            // Safe Access with .get() vs []
            q.contains("get") || q.contains("keyerror") || q.contains("ambil") || q.contains("akses") -> {
                """
                    🎯 **Akses Aman Dictionary: Perbedaan `dict[key]` vs `dict.get(key, default)`:**

                    🔬 **Spesifikasi Teknis CPython 3.11:**
                    - `dict[key]`: Mengasumsikan kunci pasti ada dalam Hash Table. Jika kunci tidak ditemukan, Python seketika melempar Exception **`KeyError`** dan menghentikan jalannya program.
                    - `dict.get(key, default=None)`: Mengambil nilai secara aman tanpa pernah melempar `KeyError`. Jika kunci tidak ada, fungsi akan mengembalikan nilai default (bawaan: `None` atau nilai cadangan yang kamu tentukan).

                    💻 **Contoh Kode Verifikasi:**
                    ```python
                    siswa = {"nis": 10501, "nama": "Citra Lestari", "kelas": "XI-B"}

                    # ❌ RISIKO TINGGI: Langsung kurung siku
                    # alamat = siswa["alamat"]  # Crash! KeyError: 'alamat'

                    # ✅ AMAN & DEFENSIVE: Menggunakan .get()
                    alamat = siswa.get("alamat", "Belum Tercatat")
                    print("Alamat:", alamat) # Output: Belum Tercatat

                    no_hp = siswa.get("no_hp") # Output: None
                    ```

                    💡 **Tips Guru Hermawan:**
                    Gunakan kurung siku `dict[key]` hanya jika kamu yakin 100% kunci tersebut ada (misal di dalam blok `if key in dict:`). Untuk input dinamis dari pengguna atau API, selalu gunakan `.get()`!
                """.trimIndent()
            }

            // Keys rules & hashability
            (q.contains("kunci") || q.contains("key")) && (q.contains("syarat") || q.contains("aturan") || q.contains("hashable") || q.contains("list") || q.contains("bisa")) -> {
                """
                    🎯 **Aturan Baku Kunci (Keys) pada Dictionary Python 3.11:**

                    🔬 **Kaidah Resmi Spesifikasi Python:**
                    1. **Kunci WAJIB Bersifat Hashable & Immutable:**
                       - ✅ Boleh: `str` ("nama"), `int` (101), `float` (3.14), `tuple` ("koordinat", 10, 20).
                       - ❌ **DILARANG: `list` `[1, 2]` dan `dict` `{}`** tidak bisa dijadikan kunci! Mencobanya akan menghasilkan `TypeError: unhashable type: 'list'`.
                    2. **Kunci WAJIB Unik:**
                       - Tidak boleh ada kunci kembar dalam satu dictionary. Jika kamu menulis kunci yang sama dua kali, nilai terakhir akan menimpa (*overwrite*) nilai pertama.
                    3. **Nilai (Values) Bebas Tanpa Syarat:**
                       - Nilai boleh berupa tipe data apa pun (int, string, list, set, bahkan dictionary lain) dan boleh duplikat.

                    💻 **Contoh Kode Verifikasi:**
                    ```python
                    # ✅ Sah: Kunci berupa String, Integer, dan Tuple
                    data_lab = {
                        "nama_lab": "Lab Komputer 1",
                        1: "Sesi Pagi",
                        (10, 20): "Koordinat Meja Server"
                    }

                    # ❌ KELIRU: List sebagai kunci
                    # jadwal = {[1, 2]: "Senin"} # TypeError: unhashable type: 'list'

                    # ✅ Penimpaan Kunci Duplikat:
                    skor = {"Matematika": 80, "Matematika": 95}
                    print(skor) # Output: {'Matematika': 95} (nilai 80 tertimpa!)
                    ```

                    💡 **Tips Guru Hermawan:**
                    Jika kamu butuh kunci majemuk gabungan dua data (misal NIS dan Tahun Ajaran), gunakan **Tuple `(nis, tahun)`** sebagai kunci dictionary!
                """.trimIndent()
            }

            // Adding & Updating
            q.contains("tambah") || q.contains("update") || q.contains("add") -> {
                """
                    🎯 **Cara Menambah & Memperbarui Data pada Dictionary Python:**

                    🔬 **Kaidah Baku Python 3.11:**
                    - ⚠️ **DILARANG MEMANGGIL `.add()`:** Dictionary **TIDAK MEMILIKI metode `.add()`**! Metode `.add()` hanya milik Set. Memanggil `dict.add()` akan memicu `AttributeError: 'dict' object has no attribute 'add'`.
                    - **Sintaks Penugasan Langsung:** `dict[kunci_baru] = nilai_baru`. Jika kunci sudah ada, nilainya diperbarui. Jika belum ada, pasangan baru otomatis dibuat.
                    - **Metode `.update({kunci: nilai})`:** Menambah atau memperbarui banyak pasangan kunci-nilai sekaligus dari dictionary lain.

                    💻 **Contoh Kode Verifikasi:**
                    ```python
                    profil = {"nama": "Dewi Sartika", "kelas": "XI-A"}

                    # 1. Menambah 1 data baru dengan kurung siku
                    profil["hobi"] = "Robotika"

                    # 2. Mengubah nilai yang sudah ada
                    profil["kelas"] = "XI-B"

                    # 3. Menambah banyak data sekaligus dengan .update()
                    profil.update({"nilai_rata": 91.5, "ekskul": "Informatika Club"})

                    print("Hasil Akhir Profil:", profil)
                    ```

                    💡 **Tips Guru Hermawan:**
                    Kamu juga bisa menggunakan `dict.setdefault(key, default)` jika ingin menambahkan kunci hanya jika kunci tersebut belum pernah ada sebelumnya.
                """.trimIndent()
            }

            // Deleting keys: del, pop, popitem, clear
            q.contains("hapus") || q.contains("del ") || q.contains("delete") || q.contains("pop") || q.contains("popitem") -> {
                """
                    🎯 **4 Cara Menghapus Data dari Dictionary di Python 3.11:**

                    1️⃣ **`dict.pop(key[, default])` (Paling Direkomendasikan):**
                    - Menghapus kunci dan **mengembalikan nilainya**.
                    - Jika diberi argumen kedua `default`, tidak akan crash meskipun kuncinya tidak ada: `val = d.pop('tidak_ada', None)`.
                    
                    2️⃣ **Pernyataan `del dict[key]`:**
                    - Menghapus pasangan secara langsung. Melempar `KeyError` jika kunci tidak ditemukan.
                    
                    3️⃣ **`dict.popitem()`:**
                    - Menghapus dan mengembalikan pasangan `(key, value)` terakhir yang dimasukkan (prinsip LIFO sejak Python 3.7+).
                    
                    4️⃣ **`dict.clear()`:**
                    - Mengosongkan seluruh isi dictionary tanpa menghapus variabelnya.

                    💻 **Contoh Kode Verifikasi:**
                    ```python
                    inventaris = {"pc": 36, "printer": 2, "proyektor": 1, "kabel_lan": 50}

                    # 1. Menghapus dengan .pop() dan mengambil nilainya
                    jml_printer = inventaris.pop("printer")
                    print("Printer dipindahkan:", jml_printer) # 2

                    # 2. Menghapus dengan del
                    del inventaris["kabel_lan"]

                    # 3. Popitem (menghapus item terakhir yang dimasukkan)
                    terakhir = inventaris.popitem()
                    print("Item terakhir:", terakhir) # ('proyektor', 1)

                    print("Sisa inventaris:", inventaris) # {'pc': 36}
                    ```

                    💡 **Tips Guru Hermawan:**
                    Gunakan selalu `d.pop(key, None)` untuk menghapus data yang keberadaannya belum pasti, sehingga kodemu aman dari `KeyError`!
                """.trimIndent()
            }

            // Looping & Iteration: keys, values, items
            q.contains("loop") || q.contains("iterasi") || q.contains("for") || q.contains("items") || q.contains("keys") || q.contains("values") -> {
                """
                    🎯 **Cara Looping & Iterasi Dictionary di Python 3.11:**

                    Dictionary memiliki 3 view object bawaan untuk perulangan `for`:

                    1️⃣ **Hanya Mengiterasi Kunci (*Keys*):**
                    - `for k in dict:` atau `for k in dict.keys():`
                    
                    2️⃣ **Hanya Mengiterasi Nilai (*Values*):**
                    - `for v in dict.values():`
                    
                    3️⃣ **Mengiterasi Kunci & Nilai Bersama-sama (*Items Unpacking*):**
                    - `for k, v in dict.items():` (Paling sering digunakan!)

                    💻 **Contoh Kode Verifikasi:**
                    ```python
                    nilai_siswa = {
                        "Aisyah": 95,
                        "Budi": 82,
                        "Citra": 88
                    }

                    # Cara 1: Hanya Kunci
                    print("--- Daftar Nama Siswa ---")
                    for nama in nilai_siswa:
                        print("Nama:", nama)

                    # Cara 2: Kunci dan Nilai sekaligus dengan .items()
                    print("\n--- Rapor Siswa SMAN 1 Kepanjen ---")
                    for nama, nilai in nilai_siswa.items():
                        status = "Tuntas" if nilai >= 75 else "Remidi"
                        print(f"Siswa: {nama:<8} | Nilai: {nilai} ({status})")
                    ```

                    💡 **Tips Guru Hermawan:**
                    Perhatikan unpacking `for k, v in dict.items():`! Ini adalah cara paling elegan (Pythonic) membaca seluruh data kamus tanpa perlu menulis `dict[k]` berulang-ulang.
                """.trimIndent()
            }

            // Merging dictionaries (Python 3.9+ | operator)
            q.contains("gabung") || q.contains("merge") || q.contains("|") -> {
                """
                    🎯 **Cara Menggabungkan Dua Dictionary di Python 3.11:**

                    Sejak Python 3.9+, Python memperkenalkan operator penggabungan baru yang sangat praktis:

                    1️⃣ **Operator Union `|` (Python 3.9+ Modern):**
                    - `hasil = dict1 | dict2`
                    - Menghasilkan dictionary baru. Jika ada kunci yang sama, nilai dari dictionary sebelah kanan (`dict2`) akan menang/menimpa nilai sebelah kiri.
                    
                    2️⃣ **In-Place Update `dict1.update(dict2)`:**
                    - Memodifikasi `dict1` secara langsung tanpa membuat objek baru.
                    
                    3️⃣ **Dictionary Unpacking `{**d1, **d2}`:**
                    - Metode kompatibel untuk versi Python lama.

                    💻 **Contoh Kode Verifikasi:**
                    ```python
                    profil_dasar = {"nama": "Bayu Pratama", "kelas": "XI-A", "skor": 80}
                    profil_tambahan = {"skor": 92, "ekskul": "Paskibra"}

                    # Menggabungkan dengan operator |
                    profil_lengkap = profil_dasar | profil_tambahan

                    print("Hasil Gabungan:", profil_lengkap)
                    # Output: {'nama': 'Bayu Pratama', 'kelas': 'XI-A', 'skor': 92, 'ekskul': 'Paskibra'}
                    # Catatan: skor menjadi 92 karena ditimpa profil_tambahan!
                    ```

                    💡 **Tips Guru Hermawan:**
                    Urutan operan sangat penting saat menggabungkan: `d1 | d2` berarti nilai pada `d2` akan menimpa kunci yang sama di `d1`!
                """.trimIndent()
            }

            // Sorting Dictionary
            q.contains("sort") || q.contains("urut") -> {
                """
                    🎯 **Cara Mengurutkan Dictionary di Python 3.11:**

                    Dictionary di Python 3.7+ menjamin urutan penambahan data (*Insertion-Order*). Untuk mengurutkannya secara terstruktur:

                    1️⃣ **Mengurutkan Berdasarkan Kunci (*Sort by Key*):**
                    - `dict(sorted(data.items()))`
                    
                    2️⃣ **Mengurutkan Berdasarkan Nilai (*Sort by Value*):**
                    - `dict(sorted(data.items(), key=lambda item: item[1], reverse=True))`

                    💻 **Contoh Kode Verifikasi:**
                    ```python
                    skor_peserta = {"Citra": 88, "Andi": 95, "Budi": 78, "Dewi": 92}

                    # 1. Urutkan berdasarkan Nama (Key alfabetis A-Z)
                    urut_nama = dict(sorted(skor_peserta.items()))
                    print("Urut Nama:", urut_nama)
                    # Output: {'Andi': 95, 'Budi': 78, 'Citra': 88, 'Dewi': 92}

                    # 2. Urutkan berdasarkan Skor Tertinggi (Value ranking)
                    urut_juara = dict(sorted(skor_peserta.items(), key=lambda x: x[1], reverse=True))
                    print("Ranking Juara:", urut_juara)
                    # Output: {'Andi': 95, 'Dewi': 92, 'Citra': 88, 'Budi': 78}
                    ```

                    💡 **Tips Guru Hermawan:**
                    `lambda x: x[1]` adalah fungsi anonim kecil yang memberitahu Python untuk melihat elemen indeks 1 dari pasangan `(key, value)`, yaitu nilai (*value*)-nya!
                """.trimIndent()
            }

            // Dictionary Comprehension
            q.contains("comprehension") || q.contains("komprehensi") -> {
                """
                    🎯 **Dictionary Comprehension di Python 3.11:**

                    Sintaks ringkas 1 baris untuk membangun dictionary baru dari iterable yang ada:
                    `{kunci_ekspresi: nilai_ekspresi for item in iterable if kondisi}`

                    💻 **Contoh Kode Verifikasi:**
                    ```python
                    daftar_siswa = ["Andi", "Budi", "Citra", "Dedi"]
                    nilai_mentah = [85, 92, 70, 88]

                    # 1. Membuat Dictionary dari dua list dengan zip
                    rapor = {nama: skor for nama, skor in zip(daftar_siswa, nilai_mentah)}

                    # 2. Memfilter hanya siswa yang lulus KKM (>= 75)
                    siswa_lulus = {k: v for k, v in rapor.items() if v >= 75}
                    print("Siswa Tuntas KKM:", siswa_lulus)
                    # Output: {'Andi': 85, 'Budi': 92, 'Dedi': 88}

                    # 3. Menukar Kunci dan Nilai (Invert dictionary)
                    tukar = {v: k for k, v in rapor.items()}
                    print("Nilai ke Nama:", tukar)
                    ```

                    💡 **Tips Guru Hermawan:**
                    Dictionary comprehension membuat kode pemrosesan data jauh lebih ringkas, elegan, dan berkinerja tinggi dibanding perulangan `for` konvensional.
                """.trimIndent()
            }

            // General Dictionary Mastery
            else -> {
                """
                    🎯 **Masteri Dictionary (Kamus Key-Value) Python 3.11:**

                    Dictionary memetakan pasangan unik **Kunci (Key)** ke **Nilai (Value)**.

                    🔬 **Spesifikasi Teknis Baku CPython 3.11:**
                    1. **Kunci (Key) Wajib Hashable & Unik**: String, Integer, Float, atau Tuple. List dan Dict TIDAK BISA dijadikan Kunci (`TypeError: unhashable type: 'list'`).
                    2. **Insertion-Order Terjaga**: Sejak Python 3.7+, urutan penambahan data dijamin dipertahankan di memori.
                    3. **Pencarian O(1) Instan**: Pencarian data berdasarkan kunci berjalan seketika (*constant time*) berkat algoritma Compact Hash Table.

                    💻 **Contoh Praktik Pemrograman:**
                    ```python
                    siswa = {
                        "nis": 10452,
                        "nama": "Aisyah Putri",
                        "kelas": "XI-A",
                        "nilai": [85, 90, 88]
                    }

                    # Akses aman
                    alamat = siswa.get("alamat", "Belum terdaftar")

                    # Penambahan data
                    siswa["predikat"] = "A"

                    # Looping pasangan
                    for kunci, nilai in siswa.items():
                        print(f"{kunci}: {nilai}")
                    ```

                    💡 **Tips Guru Hermawan:**
                    - Ingin membaca data aman? Gunakan `dict.get(key, default)`.
                    - Ingin menghapus aman? Gunakan `dict.pop(key, default)`.
                    - Butuh pasangan di loop? Gunakan `dict.items()`.
                """.trimIndent()
            }
        }
    }

    private fun isSetQuery(q: String, topic: String? = null, code: String? = null): Boolean {
        val setTriggers = listOf(
            "set", "himpunan", "venn", "irisan", "gabungan", "duplikat", "unik",
            "eliminasi duplikat", "discard", "union", "intersection", "difference",
            "symmetric", "subscriptable", "frozenset", "set()", "set{}", "himpunan kosong",
            "anggota set", "elemen set", "operasi himpunan"
        )
        if (setTriggers.any { q.contains(it) }) return true
        val isTopicSet = topic?.lowercase()?.contains("set") == true || topic?.lowercase()?.contains("himpunan") == true
        val hasCodeSet = code?.contains("set(") == true || (code?.contains("{") == true && !code.contains(":"))
        return isTopicSet || hasCodeSet
    }

    private fun handleSetQuestions(q: String, code: String?, topic: String? = null): String {
        return when {
            // Uniqueness and de-duplication
            q.contains("duplikat") || q.contains("unik") || q.contains("eliminasi") || q.contains("kembar") || q.contains("ganda") -> {
                """
                    🎯 **Karakteristik Unik & Eliminasi Duplikat Set di Python 3.11:**

                    🔬 **Spesifikasi Teknis CPython 3.11:**
                    - Set adalah koleksi data yang secara fundamental **hanya menyimpan elemen unik**.
                    - Setiap kali data baru dimasukkan, CPython menghitung nilai hash elemen tersebut dan membandingkannya via `__eq__`. Jika nilai tersebut sudah ada di tabel hash, data duplikat akan diabaikan tanpa melempar error.

                    💻 **Trik Tercepat Menghapus Duplikat dari List:**
                    ```python
                    data_siswa = ["Bayu", "Citra", "Bayu", "Dewi", "Citra", "Bayu"]

                    # 1. Konversi ke Set untuk membuang duplikat seketika
                    set_unik = set(data_siswa)
                    print("Set Unik:", set_unik) # {'Bayu', 'Citra', 'Dewi'}

                    # 2. Kembalikan ke List jika membutuhkan urutan indeks kembali
                    siswa_bersih = list(set(data_siswa))
                    print("Daftar Bersih:", siswa_bersih)
                    ```

                    💡 **Tips Guru Hermawan:**
                    Konversi `list(set(data))` adalah operasi O(n) tercepat di Python untuk membersihkan jutaan rekaman data yang kembar.
                """.trimIndent()
            }

            // Empty set trap {}
            q.contains("kosong") || q.contains("empty") || q.contains("{}") -> {
                """
                    🎯 **Jebakan Sintaks Set Kosong di Python:**

                    🔬 **Kaidah Resmi Python:**
                    - ⚠️ **`{}` BUKANLAH SET KOSONG!** Tanda `{}` kosong dicadangkan oleh Python untuk membuat **Dictionary kosong**.
                    - Untuk membuat Set kosong, kamu **WAJIB menggunakan konstruktor fungsi `set()`**!

                    💻 **Pembuktian di Terminal Python:**
                    ```python
                    objek_a = {}
                    print("Tipe objek_a:", type(objek_a)) # <class 'dict'>

                    objek_b = set()
                    print("Tipe objek_b:", type(objek_b)) # <class 'set'>

                    # Menambahkan data ke set kosong
                    objek_b.add("Informatika")
                    print(objek_b) # {'Informatika'}
                    ```

                    💡 **Tips Guru Hermawan:**
                    Ingat rumus ini: `{}` adalah Dictionary kosong, sedangkan `set()` adalah Set kosong!
                """.trimIndent()
            }

            // Unordered & Non-subscriptable
            q.contains("indeks") || q.contains("index") || q.contains("set[0]") || q.contains("subscriptable") || q.contains("urutan") -> {
                """
                    🎯 **Mengapa Set Tidak Mendukung Indeks (`set[0]`)?**

                    🔬 **Penjelasan Model Memori CPython 3.11:**
                    - Set diimplementasikan menggunakan arsitektur **Open Addressing Hash Table**, bukan array sekuensial linear.
                    - Posisi elemen di memori ditentukan oleh nilai hash elemen tersebut modulo ukuran tabel, sehingga tidak ada konsep urutan ke-0, ke-1, atau ke-2.
                    - Mencoba mengakses `set[0]` akan memicu error: **`TypeError: 'set' object is not subscriptable`**.

                    💻 **Cara Mengakses Elemen Set yang Benar:**
                    ```python
                    warna_rgb = {"Merah", "Hijau", "Biru"}

                    # ❌ AKAN ERROR:
                    # print(warna_rgb[0]) # TypeError: 'set' object is not subscriptable

                    # ✅ SOLUSI 1: Iterasi dengan for loop
                    for w in warna_rgb:
                        print("Warna:", w)

                    # ✅ SOLUSI 2: Pengecekan keanggotaan langsung O(1)
                    if "Merah" in warna_rgb:
                        print("Warna Merah tersedia!")

                    # ✅ SOLUSI 3: Konversi sementara ke List jika perlu indeks spesifik
                    warna_pertama = list(warna_rgb)[0]
                    ```

                    💡 **Tips Guru Hermawan:**
                    Jika urutan posisi elemen dan indeks `[i]` sangat penting bagi kebutuhan programmu, gunakan **List `[...]`** atau **Tuple `(...)`**, bukan Set!
                """.trimIndent()
            }

            // List in set / unhashable
            (q.contains("list") && (q.contains("dalam") || q.contains("bisa") || q.contains("unhashable"))) || q.contains("unhashable") -> {
                """
                    🎯 **Mengapa List Tidak Bisa Dimasukkan ke dalam Set?**

                    🔬 **Kaidah Integritas Hash Table:**
                    - Semua elemen di dalam Set **WAJIB bersifat Hashable dan Immutable** (nilainya tidak boleh berubah sepanjang masa hidupnya di memori).
                    - List bersifat *Mutable* (dapat ditambah atau diedit kapan saja). Jika isi list berubah, nilai hash-nya juga akan berubah dan merusak penempatan bucket di Hash Table Set.
                    - Memasukkan list ke dalam set `{[1, 2]}` akan menghasilkan: **`TypeError: unhashable type: 'list'`**.

                    💻 **Solusi Kode Terverifikasi:**
                    ```python
                    # ❌ KELIRU:
                    # kumpulan = {[1, 2], [3, 4]} # TypeError: unhashable type: 'list'

                    # ✅ BENAR: Ubah List menjadi Tuple (karena Tuple immutable & hashable)
                    kumpulan_aman = {(1, 2), (3, 4)}
                    print(kumpulan_aman) # {(1, 2), (3, 4)}
                    ```

                    💡 **Tips Guru Hermawan:**
                    Gunakan **Tuple `(1, 2)`** jika kamu membutuhkan pasangan angka (seperti koordinat titik) untuk disimpan di dalam Set!
                """.trimIndent()
            }

            // Venn operations (union, intersection, difference, symmetric)
            q.contains("venn") || q.contains("irisan") || q.contains("gabungan") || q.contains("selisih") ||
            q.contains("union") || q.contains("intersection") || q.contains("difference") || q.contains("|") || q.contains("&") -> {
                """
                    🎯 **Operasi Diagram Venn & Himpunan Matematika pada Set Python:**

                    Python menyediakan operator simbol dan metode bawaan untuk operasi relasi:

                    | Operasi | Operator | Metode Bawaan | Makna Matematika |
                    |---|---|---|---|
                    | **Gabungan (Union)** | `s1 \| s2` | `s1.union(s2)` | Ada di himpunan A atau B |
                    | **Irisan (Intersection)** | `s1 & s2` | `s1.intersection(s2)` | Ada di himpunan A DAN B (anggota bersama) |
                    | **Selisih (Difference)** | `s1 - s2` | `s1.difference(s2)` | Ada di A tapi BUKAN anggota B |
                    | **Symmetric Difference** | `s1 ^ s2` | `s1.symmetric_difference(s2)` | Hanya ikut salah satu (bukan keduanya) |

                    💻 **Contoh Kode Kasus Siswa Ekskul SMAN 1 Kepanjen:**
                    ```python
                    osis = {"Andi", "Budi", "Citra"}
                    pramuka = {"Citra", "Dewi", "Eko"}

                    # 1. Union: Seluruh siswa yang ikut OSIS atau Pramuka
                    print("Gabungan (|):", osis | pramuka)
                    # Output: {'Andi', 'Budi', 'Citra', 'Dewi', 'Eko'}

                    # 2. Intersection: Siswa yang aktif di KEDUA ekskul
                    print("Irisan (&):", osis & pramuka)
                    # Output: {'Citra'}

                    # 3. Difference: Anggota OSIS yang TIDAK ikut Pramuka
                    print("Selisih (-):", osis - pramuka)
                    # Output: {'Andi', 'Budi'}

                    # 4. Symmetric Difference: Siswa yang hanya ikut satu ekskul saja
                    print("Beda Simetris (^):", osis ^ pramuka)
                    # Output: {'Andi', 'Budi', 'Dewi', 'Eko'}
                    ```

                    💡 **Tips Guru Hermawan:**
                    Operasi Venn pada Set dieksekusi secepat kilat berkat hash table, sangat cocok untuk analisis data filter keanggotaan!
                """.trimIndent()
            }

            // Methods: add, update, remove, discard, pop
            q.contains("remove") || q.contains("discard") || q.contains("add") || q.contains("tambah") || q.contains("pop") -> {
                """
                    🎯 **Metode Manipulasi Set di Python 3.11:**

                    1️⃣ **Menambah Elemen:**
                    - `set.add(elem)`: Menambah 1 elemen skalar. (⚠️ Dilarang pakai `.append()`!).
                    - `set.update(iterable)`: Menambah banyak elemen sekaligus dari list/set lain.

                    2️⃣ **Perbedaan Krusial `.remove()` vs `.discard()`:**
                    - `set.remove(elem)`: Menghapus elemen. **Jika elemen TIDAK ADA, Python MELEMPAR `KeyError`**!
                    - `set.discard(elem)`: Menghapus elemen secara **AMAN**. Jika elemen tidak ada, Python diam saja tanpa pernah error!

                    3️⃣ **`set.pop()`:**
                    - Menghapus dan mengembalikan elemen acak (karena set tidak berurutan).

                    💻 **Contoh Kode Verifikasi:**
                    ```python
                    himpunan = {"A", "B", "C"}

                    # 1. Tambah elemen
                    himpunan.add("D")

                    # 2. Hapus aman dengan discard (Anti-Crash)
                    himpunan.discard("Z") # Tidak error meskipun "Z" tidak ada!

                    # 3. Hapus dengan remove (Harus ada datanya)
                    himpunan.remove("A")

                    print("Hasil Himpunan:", himpunan) # {'B', 'C', 'D'}
                    ```

                    💡 **Tips Guru Hermawan:**
                    Selalu utamakan menggunakan **`.discard()`** daripada `.remove()` agar programmu tidak berhenti tiba-tiba (*crash*) saat elemen yang ingin dihapus tidak ada!
                """.trimIndent()
            }

            // General Set Mastery
            else -> {
                """
                    🎯 **Masteri Set (Himpunan Unik) di Python 3.11:**

                    Set adalah koleksi data tanpa urutan (*unordered*) yang menjamin setiap anggotanya **100% UNIK**.

                    🔬 **Kaidah Resmi Spesifikasi Python:**
                    - Sintaks literal: `{item1, item2, ...}` atau fungsi `set(iterable)`.
                    - Set kosong wajib dibuat dengan `set()`, bukan `{}`.
                    - Elemen Set **WAJIB bersifat hashable & immutable** (int, str, float, tuple). List TIDAK BISA dimasukkan ke dalam Set.
                    - Pencarian keanggotaan `'x in my_set'` berjalan instan **O(1)**.

                    💻 **Contoh Kode Verifikasi:**
                    ```python
                    # 1. Pembuatan dan eliminasi otomatis
                    nilai_unik = {80, 90, 80, 100, 90}
                    print("Nilai Unik:", nilai_unik) # {80, 90, 100}

                    # 2. Pengecekan Cepat O(1)
                    if 100 in nilai_unik:
                        print("Nilai sempurna ditemukan!")
                    ```

                    💡 **Tips Guru Hermawan:**
                    Jika kamu punya 100.000 data dan perlu sering mengecek keberadaan data tertentu, gunakan **Set**, bukan List! Set bekerja seketika lewat sistem Hash.
                """.trimIndent()
            }
        }
    }

    private fun isTupleQuery(q: String, topic: String? = null, code: String? = null): Boolean {
        val tupleTriggers = listOf(
            "tuple", "tupple", "immutable", "t[0]", "unpacking", "konstan", "read-only",
            "trailing comma", "koma tuple", "(1,)", "tipe tuple", "swap", "tukar variabel",
            "kunci tuple", "1 elemen", "satu elemen"
        )
        if (tupleTriggers.any { q.contains(it) }) return true
        val isTopicTuple = topic?.lowercase()?.contains("tuple") == true
        val hasCodeTuple = code?.contains("(") == true && code.contains(",") && !code.contains("print(") && !code.contains("def ")
        return isTopicTuple || hasCodeTuple
    }

    private fun handleTupleQuestions(q: String, code: String?, topic: String? = null): String {
        return when {
            // Single-element tuple trap
            q.contains("1 elemen") || q.contains("satu elemen") || q.contains("koma") || q.contains("trailing comma") || q.contains("(1)") -> {
                """
                    🎯 **Jebakan Tuple 1 Elemen di Python (*Trailing Comma*):**

                    🔬 **Mengapa `(42)` Bukan Tuple?**
                    - Dalam matematika dan bahasa pemrograman, tanda kurung bulat `()` digunakan untuk mengelompokkan prioritas ekspresi matematika, contoh: `(2 + 3) * 4`.
                    - Oleh karena itu, jika kamu hanya menulis `x = (42)`, Python menganggapnya sebagai bilangan integer biasa di dalam kurung!
                    - **ATURAN MUTLAK PYTHON:** Untuk membuat Tuple yang hanya berisi 1 elemen, kamu **WAJIB menambahkan tanda koma di akhir: `(42,)`** atau `42,`.

                    💻 **Pembuktian Tipe Data di Python:**
                    ```python
                    bukan_tuple = (42)
                    print(type(bukan_tuple)) # <class 'int'> -> BUKAN TUPLE!

                    tuple_asli = (42,)
                    print(type(tuple_asli))  # <class 'tuple'> -> SAH TUPLE!

                    # Bisa juga tanpa kurung asalkan ada koma
                    tuple_singkat = 42,
                    print(type(tuple_singkat)) # <class 'tuple'>
                    ```

                    💡 **Tips Guru Hermawan:**
                    Ingat baik-baik: Tanda koma `,`-lah yang mendefinisikan sebuah Tuple di Python, bukan tanda kurungnya!
                """.trimIndent()
            }

            // Tuple Unpacking & Swapping
            q.contains("unpack") || q.contains("bongkar") || q.contains("tukar") || q.contains("swap") -> {
                """
                    🎯 **Tuple Unpacking & Penukaran Nilai (*Variable Swapping*) di Python:**

                    🔬 **Mekanisme Tuple Unpacking:**
                    Tuple memungkinkan kita membongkar seluruh elemennya langsung ke beberapa variabel penampung dalam satu baris instruksi yang rapi.

                    1️⃣ **Unpacking Standar:** Jumlah variabel di kiri harus persis sama dengan jumlah elemen tuple di kanan.
                    2️⃣ **Extended Unpacking dengan Asterisk (`*rest`):** Menampung sisa elemen ke dalam list.
                    3️⃣ **Menukar Variabel Tanpa Variabel Sementara:** Di bahasa lain seperti C/Java, menukar nilai butuh variabel bantuan `temp`. Di Python, cukup: `a, b = b, a`!

                    💻 **Contoh Kode Verifikasi:**
                    ```python
                    # 1. Unpacking Koordinat GPS SMAN 1 Kepanjen
                    lokasi = (-8.1345, 112.5712, "SMAN 1 Kepanjen")
                    latitude, longitude, nama_tempat = lokasi

                    print(f"Tempat: {nama_tempat} di Lat: {latitude}, Long: {longitude}")

                    # 2. Menukar Nilai Dua Variabel Seketika
                    a = 10
                    b = 20
                    a, b = b, a
                    print(f"Setelah ditukar: a={a}, b={b}") # a=20, b=10

                    # 3. Extended Unpacking (*sisa)
                    juara = ("Andi", "Budi", "Citra", "Dewi", "Eko")
                    emas, perak, perunggu, *peserta_lain = juara
                    print("Peserta Lainnya:", peserta_lain) # ['Dewi', 'Eko']
                    ```

                    💡 **Tips Guru Hermawan:**
                    Tuple unpacking membuat fungsimu bisa mengembalikan beberapa nilai sekaligus dengan bersih: `def hitung(): return hasil_a, hasil_b`.
                """.trimIndent()
            }

            // Immutability & Modification workarounds
            q.contains("immutable") || q.contains("ubah") || q.contains("edit") || q.contains("ganti") || q.contains("tambah") || q.contains("konstan") -> {
                """
                    🎯 **Karakteristik Immutability Tuple & Solusi Modifikasinya:**

                    Tuple bersifat **Immutable** (nilainya disegel konstan dan tidak dapat diubah setelah diciptakan di memori).

                    🔬 **Mengapa Python Menetapkan Tuple Immutable?**
                    1. **Integritas & Keamanan Data (*Defensive Programming*):** Melindungi data penting dari modifikasi tak sengaja (misal: konstanta sistem, tanggal lahir, ID unik).
                    2. **Efisiensi Alokasi Memori:** CPython mengalokasikan memori tepat sesuai ukuran tanpa buffer cadangan (*no over-allocation*), sehingga lebih cepat diciptakan dibanding List.
                    3. **Hashable:** Karena isinya tidak bisa berubah, Tuple dapat dijadikan Kunci Dictionary atau elemen Set.

                    💻 **Solusi Jika Ingin Mengubah Elemen Tuple:**
                    ```python
                    koordinat = (10, 20)

                    # ❌ AKAN ERROR:
                    # koordinat[0] = 99 # TypeError: 'tuple' object does not support item assignment

                    # ✅ SOLUSI 1: Konversi Sementara ke List lalu Kembalikan
                    temp = list(koordinat)
                    temp[0] = 99
                    koordinat = tuple(temp) # (99, 20)

                    # ✅ SOLUSI 2: Konkatenasi Tuple (Membuat Tuple Baru di Memori)
                    koordinat_baru = (100,) + koordinat[1:] # (100, 20)
                    ```

                    💡 **Tips Guru Hermawan:**
                    Jika data Anda memang membutuhkan operasi penambahan/pengubahan terus menerus, gunakan **List `[...]`** sejak awal.
                """.trimIndent()
            }

            // Hashability & Dict Keys
            q.contains("hashable") || q.contains("hash") || q.contains("key") || q.contains("kunci") -> {
                """
                    🎯 **Mengapa Tuple Bisa Menjadi Kunci Dictionary / Elemen Set?**

                    🔬 **Konsep Hashability CPython 3.11:**
                    - Hash Table (struktur dasar Set dan Dictionary) membutuhkan alamat bucket memori yang konstan melalui fungsi `hash(obj)`.
                    - Karena Tuple bersifat immutable, nilai `hash(tuple)` dijamin tidak akan pernah berubah sepanjang jalannya program.
                    - ⚠️ **SYARAT PENTING:** Semua elemen di dalam Tuple tersebut juga **wajib bersifat hashable**! Tuple yang membungkus List `(1, [2, 3])` tetap tidak bisa dijadikan kunci dict (`TypeError: unhashable type: 'list'`).

                    💻 **Contoh Kode Verifikasi:**
                    ```python
                    # ✅ Sah: Tuple sebagai Kunci Kamus (Peta Grid Koordinat Lab)
                    tata_ruang_lab = {
                        (0, 0): "Meja Guru Hermawan",
                        (0, 1): "PC Siswa 01",
                        (0, 2): "PC Siswa 02"
                    }

                    print("Posisi (0, 0):", tata_ruang_lab[(0, 0)])

                    # ❌ KELIRU: Tuple yang berisi List mutable
                    # peta_salah = {(1, [2, 3]): "Gagal"} # TypeError: unhashable type: 'list'
                    ```

                    💡 **Tips Guru Hermawan:**
                    Gunakan Tuple untuk membuat *Compound Keys* (kunci gabungan) seperti pasangan `(nomor_kelas, nomor_absen)` di Dictionary!
                """.trimIndent()
            }

            // Built-in Methods: count, index
            q.contains("metode") || q.contains("method") || q.contains("append") || q.contains("pop") -> {
                """
                    🎯 **Metode Bawaan Tuple di Python 3.11:**

                    Karena bersifat Immutable, Tuple **hanya memiliki 2 metode bawaan**:
                    1. **`tuple.count(x)`**: Menghitung berapa kali nilai `x` muncul dalam tuple.
                    2. **`tuple.index(x)`**: Mengembalikan indeks kemunculan pertama dari nilai `x` (melempar `ValueError` jika tidak ditemukan).

                    ⚠️ **Tuple TIDAK MEMILIKI:**
                    - `.append()`, `.extend()`, `.insert()`
                    - `.remove()`, `.pop()`, `.clear()`
                    - `.sort()`, `.reverse()`

                    💻 **Contoh Kode Verifikasi:**
                    ```python
                    nilai_ujian = (80, 90, 85, 90, 100, 90)

                    # 1. Menghitung frekuensi angka 90
                    kemunculan_90 = nilai_ujian.count(90)
                    print("Berapa kali nilai 90 muncul?", kemunculan_90) # 3

                    # 2. Mencari indeks nilai 100
                    posisi_100 = nilai_ujian.index(100)
                    print("Indeks nilai 100:", posisi_100) # 4
                    ```

                    💡 **Tips Guru Hermawan:**
                    Jika kamu butuh mengurutkan tuple, gunakan fungsi bawaan **`sorted(my_tuple)`** yang mengembalikan List terurut baru!
                """.trimIndent()
            }

            // General Tuple Mastery
            else -> {
                """
                    🎯 **Masteri Tuple (Koleksi Konstan) di Python 3.11:**

                    Tuple adalah sekumpulan data berurutan (*ordered*) yang bersifat **Immutable** (konstan dan tidak dapat diubah setelah diciptakan).

                    🔬 **Spesifikasi Teknis Baku CPython 3.11:**
                    - Sintaks: `(item1, item2, ...)` atau trailing comma `(item1,)` untuk 1 elemen.
                    - Mengizinkan elemen duplikat dan tipe data heterogen (campuran int, str, float).
                    - Didukung indeks integer positif `tuple[0]`, indeks negatif `tuple[-1]`, dan slicing `[start:stop:step]`.
                    - Jauh lebih hemat alokasi memori RAM dan lebih cepat diciptakan dibanding List.

                    💻 **Contoh Kode Verifikasi:**
                    ```python
                    koordinat_kepanjen = (-8.1345, 112.5712)
                    print("Latitude:", koordinat_kepanjen[0])
                    print("Longitude:", koordinat_kepanjen[1])

                    # Tuple unpacking
                    lat, lon = koordinat_kepanjen
                    print(f"Posisi: {lat}, {lon}")
                    ```

                    💡 **Tips Guru Hermawan:**
                    Gunakan Tuple untuk menyimpan data yang memang sejak awal dirancang tidak boleh diutak-atik (seperti konstanta konfigurasi atau koordinat fisik).
                """.trimIndent()
            }
        }
    }

    private fun isListQuery(q: String, topic: String? = null, code: String? = null): Boolean {
        val listTriggers = listOf(
            "list", "append", "extend", "insert", "pop", "remove", "sort",
            "comprehension", "slicing", "slice", "indeks", "index out of range", "daftar", "array"
        )
        if (listTriggers.any { q.contains(it) }) return true
        val isTopicList = topic?.lowercase()?.contains("list") == true || topic?.lowercase()?.contains("daftar") == true
        val hasCodeList = code?.contains("[") == true && code.contains("]")
        return isTopicList || hasCodeList
    }

    private fun handleListQuestions(q: String, code: String?, topic: String? = null): String {
        return when {
            // Append vs Extend vs Insert vs +
            q.contains("append") || q.contains("extend") || q.contains("insert") || (q.contains("tambah") && q.contains("list")) -> {
                """
                    🎯 **Perbedaan Menambah Elemen: `append()` vs `extend()` vs `insert()` pada List:**

                    🔬 **Spesifikasi Baku Python 3.11:**
                    1. **`list.append(x)` (Amortized O(1)):**
                       - Memasukkan objek `x` apa adanya ke **paling akhir** list sebagai **1 elemen tunggal**.
                       - Jika kamu memasukkan list `[3, 4]`, list tersebut akan bersarang menjadi sublist di dalam list utama!
                    2. **`list.extend(iterable)` (O(k)):**
                       - Membongkar (*unpack*) koleksi `iterable` dan memasukkan setiap elemennya satu per satu ke akhir list utama.
                    3. **`list.insert(index, x)` (O(n)):**
                       - Menyisipkan elemen `x` pada posisi indeks tertentu. Elemen di sebelah kanannya otomatis digeser ke belakang.
                    4. **Operator `+` (Konkatenasi):**
                       - Menggabungkan dua list dan **mengembalikan list baru** di memori tanpa memodifikasi list asli.

                    💻 **Contoh Kode Verifikasi:**
                    ```python
                    angka1 = [1, 2]
                    angka1.append([3, 4])
                    print("Hasil append:", angka1)  # [1, 2, [3, 4]] -> [3, 4] bersarang!

                    angka2 = [1, 2]
                    angka2.extend([3, 4])
                    print("Hasil extend:", angka2)  # [1, 2, 3, 4] -> menyatu datar!

                    angka3 = [10, 30]
                    angka3.insert(1, 20)            # Sisip di indeks 1
                    print("Hasil insert:", angka3)  # [10, 20, 30]
                    ```

                    💡 **Tips Guru Hermawan:**
                    Gunakan `.append()` untuk menambah data satu per satu, dan gunakan `.extend()` jika kamu ingin menggabungkan daftar siswa dari dua kelas yang berbeda!
                """.trimIndent()
            }

            // Pop vs Remove vs Del vs Clear
            q.contains("pop") || q.contains("remove") || q.contains("hapus") || q.contains("del ") || q.contains("clear") -> {
                """
                    🎯 **Perbedaan Menghapus Elemen: `pop()` vs `remove()` vs `del` pada List:**

                    | Metode / Sintaks | Basis Penghapusan | Nilai Kembalian (*Return*) | Jika Data Tidak Ada |
                    |---|---|---|---|
                    | **`list.pop([i])`** | Berdasarkan **Indeks integer** (default: akhir `-1`) | **Mengembalikan nilai** yang dihapus | `IndexError` |
                    | **`list.remove(x)`** | Berdasarkan **Nilai objek** (kemunculan pertama) | Mengembalikan `None` | `ValueError` |
                    | **`del list[i]`** | Berdasarkan **Indeks / Slicing** | Tidak ada kembalian | `IndexError` |
                    | **`list.clear()`** | Mengosongkan **seluruh isi list** | Mengembalikan `None` | Aman |

                    💻 **Contoh Kode Verifikasi:**
                    ```python
                    siswa = ["Andi", "Budi", "Citra", "Budi", "Dewi"]

                    # 1. Menghapus dengan pop (mengambil elemen terakhir)
                    terakhir = siswa.pop()
                    print("Siswa yang dipop:", terakhir) # Dewi
                    print("Siswa tersisa:", siswa)       # ['Andi', 'Budi', 'Citra', 'Budi']

                    # 2. Menghapus dengan remove (hanya menghapus kemunculan 'Budi' yang pertama!)
                    siswa.remove("Budi")
                    print("Setelah remove Budi:", siswa) # ['Andi', 'Citra', 'Budi']

                    # 3. Menghapus dengan del pada indeks 0
                    del siswa[0]
                    print("Setelah del indeks 0:", siswa) # ['Citra', 'Budi']
                    ```

                    💡 **Tips Guru Hermawan:**
                    Jika kamu butuh mengetahui data apa yang baru saja kamu hapus (misal untuk fitur undo), gunakan **`.pop()`**!
                """.trimIndent()
            }

            // List Comprehension
            q.contains("comprehension") || q.contains("komprehensi") -> {
                """
                    🎯 **List Comprehension di Python 3.11:**

                    List Comprehension adalah cara modern, ringkas, dan elegan untuk membuat list baru dari iterasi yang sudah ada dalam satu baris instruksi.

                    🔬 **Rumus Baku Sintaks:**
                    `[ekspresi for item in iterable if kondisi]`

                    💻 **Contoh Kode Verifikasi:**
                    ```python
                    angka = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

                    # 1. Kuadrat angka genap saja
                    kuadrat_genap = [x ** 2 for x in angka if x % 2 == 0]
                    print("Kuadrat Genap:", kuadrat_genap) # [4, 16, 36, 64, 100]

                    # 2. Membersihkan spasi pada nama siswa
                    nama_kotor = ["  andi ", "BUDI  ", " citra"]
                    nama_bersih = [nama.strip().capitalize() for nama in nama_kotor]
                    print("Nama Rapi:", nama_bersih) # ['Andi', 'Budi', 'Citra']
                    ```

                    💡 **Tips Guru Hermawan:**
                    List comprehension dieksekusi lebih cepat pada CPython dibanding loop `for` biasa yang memanggil `.append()` berkali-kali!
                """.trimIndent()
            }

            // Sorting & Reversing
            q.contains("sort") || q.contains("urut") || q.contains("reverse") -> {
                """
                    🎯 **Pengurutan List: `list.sort()` vs `sorted()` di Python 3.11:**

                    🔬 **Perbedaan Utama:**
                    - **`list.sort(key=None, reverse=False)`:**
                      * Mengubah dan mengurutkan list asli secara langsung di memori (*in-place*).
                      * Mengembalikan nilai `None` (jangan ditulis `x = list.sort()` karena `x` akan bernilai `None`!).
                    - **`sorted(iterable, key=None, reverse=False)`:**
                      * Fungsi bawaan independen yang **membuat dan mengembalikan list baru yang terurut**.
                      * List asli tetap utuh tidak berubah.

                    💻 **Contoh Kode Verifikasi:**
                    ```python
                    skor = [85, 92, 78, 95, 88]

                    # 1. Menggunakan sorted() (List asli aman)
                    skor_terurut = sorted(skor)
                    print("List Baru Terurut:", skor_terurut) # [78, 85, 88, 92, 95]
                    print("List Asli Tetap:", skor)           # [85, 92, 78, 95, 88]

                    # 2. Mengurutkan ranking tertinggi (Descending)
                    skor.sort(reverse=True)
                    print("List Asli Dimodifikasi:", skor)    # [95, 92, 88, 85, 78]
                    ```

                    💡 **Tips Guru Hermawan:**
                    Algoritma pengurutan Python menggunakan **Timsort** (kombinasi Merge Sort dan Insertion Sort) dengan kompleksitas O(n log n) yang sangat stabil dan cepat.
                """.trimIndent()
            }

            // Copy & Mutability
            q.contains("copy") || q.contains("pointer") || q.contains("memori") || q.contains("mutable") -> {
                """
                    🎯 **Mutabilitas List & Bahaya Penugasan `b = a` di Memori:**

                    🔬 **Penjelasan Model Pointer RAM Python:**
                    - List adalah objek mutable yang disimpan di heap memory. Variabel hanya menyimpan alamat pointer memori.
                    - Saat kamu menulis `b = a`, kamu **TIDAK MENDUPLIKASI** isi list, melainkan hanya membuat nama baru yang menunjuk ke alamat RAM yang persis sama! Jika `b` diubah, isi `a` juga ikut berubah!

                    💻 **Contoh Pembuktian & Solusi Salinan Aman:**
                    ```python
                    # ❌ JEBAKAN ALIAS (Pointer Sama):
                    a = [1, 2, 3]
                    b = a
                    b.append(99)
                    print("Nilai a ikut berubah!", a) # [1, 2, 3, 99]

                    # ✅ SOLUSI SHALLOW COPY (Alamat Baru Terpisah):
                    asli = [10, 20, 30]
                    salinan = asli.copy()  # atau asli[:]
                    salinan.append(999)
                    print("Asli tetap aman:", asli)        # [10, 20, 30]
                    print("Salinan berubah:", salinan)     # [10, 20, 30, 999]
                    ```

                    💡 **Tips Guru Hermawan:**
                    Gunakan selalu `.copy()` atau slicing `data[:]` jika ingin membuat salinan list independen!
                """.trimIndent()
            }

            // General List Mastery
            else -> {
                """
                    🎯 **Masteri List (Daftar Dinamis) di Python 3.11:**

                    List adalah struktur data berurutan (*ordered*) yang bersifat **Mutable** (elemennya dapat ditambah, disisipkan, diubah, dan dihapus kapan saja).

                    🔬 **Spesifikasi Teknis Baku CPython 3.11:**
                    - Sintaks: `[item1, item2, ...]` atau fungsi konstruktor `list()`.
                    - Zero-indexed integer: Indeks positif `0` s/d `len-1`, indeks negatif `-1` (elemen terakhir).
                    - Mendukung elemen duplikat dan tipe data heterogen.
                    - Mengalokasikan array pointer objek dinamis dengan alokasi cadangan proporsional (*over-allocation*) untuk menjamin efisiensi penambahan `.append()` berjalan amortized O(1).

                    💻 **Contoh Kode Verifikasi:**
                    ```python
                    daftar_siswa = ["Bayu", "Citra", "Dedi"]
                    daftar_siswa.append("Eko")
                    daftar_siswa[0] = "Aisyah" # Mengubah elemen pertama

                    for no, nama in enumerate(daftar_siswa, start=1):
                        print(f"{no}. {nama}")
                    ```

                    💡 **Tips Guru Hermawan:**
                    List adalah struktur data paling serbaguna di Python. Gunakan List setiap kali kamu membutuhkan daftar data berurutan yang sering berubah.
                """.trimIndent()
            }
        }
    }

    private fun isSlicingQuery(q: String): Boolean {
        val triggers = listOf("slicing", "slice", "potong", "start stop step", "[::-1]", "membalik list")
        return triggers.any { q.contains(it) }
    }

    private fun handleSlicingAndIndexing(q: String): String {
        return """
            🎯 **Rumus Baku Slicing Python: `koleksi[start : stop : step]`**

            Slicing berlaku untuk **List**, **Tuple**, dan **String**.

            🔬 **Ketentuan Resmi Python 3.11:**
            - `start`: Indeks awal (bersifat **inklusif** - elemen ini ikut terambil).
            - `stop`: Indeks akhir (bersifat **eksklusif** - berhenti tepat sebelum indeks ini).
            - `step`: Interval langkah lompatan (default = 1). Jika negatif, melompat mundur dari kanan ke kiri.

            💻 **Contoh Kode Verifikasi:**
            ```python
            data = [10, 20, 30, 40, 50, 60]

            print("data[1:4]:", data[1:4])   # [20, 30, 40] (indeks 1, 2, 3)
            print("data[:3]:", data[:3])     # [10, 20, 30] (dari awal sampai sebelum 3)
            print("data[3:]:", data[3:])     # [40, 50, 60] (dari indeks 3 sampai akhir)
            print("data[::2]:", data[::2])   # [10, 30, 50] (melompat 2 langkah)
            print("data[::-1]:", data[::-1]) # [60, 50, 40, 30, 20, 10] (dibalik sempurna!)
            ```

            💡 **Tips Guru Hermawan:**
            Slicing selalu mengembalikan objek koleksi **baru** (*shallow copy*) tanpa merusak atau mengubah data aslinya.
        """.trimIndent()
    }

    private fun isBigOQuery(q: String): Boolean {
        val triggers = listOf("big-o", "big o", "kompleksitas", "efisiensi", "o(1)", "o(n)", "kecepatan pencarian")
        return triggers.any { q.contains(it) }
    }

    private fun handleBigOComplexity(q: String): String {
        return """
            ⚡ **Analisis Efisiensi Waktu (Big-O Complexity) Python:**

            Big-O mengukur seberapa banyak operasi yang dibutuhkan komputer saat jumlah data (n) bertambah besar.

            | Operasi Komputasi | List `[...]` | Tuple `(...)` | Set `{...}` | Dictionary `{k: v}` |
            |---|---|---|---|---|
            | **Akses via Indeks/Kunci** | O(1) cepat | O(1) cepat | Tidak ada | **O(1)** instan |
            | **Pengecekan Keanggotaan (`in`)** | O(n) linear search | O(n) linear search | **O(1)** hash table | **O(1)** pada key |
            | **Menambah Data** | O(1) amortized append | N/A (Immutable) | **O(1)** add | **O(1)** insert key |
            | **Menghapus Data** | O(n) pergeseran elemen | N/A | **O(1)** discard/pop | **O(1)** del/pop |

            💡 **Tips Guru Hermawan:**
            Jika Anda memiliki 1.000.000 data siswa dan sering mengecek apakah seorang siswa terdaftar (`if nis in data`), gunakan **Set** atau **Dictionary**, bukan List! Set bekerja seketika (O(1)) lewat sistem hash bucket.
        """.trimIndent()
    }

    private fun handleContextualFallback(question: String, topicTitle: String?): String {
        val topicContextNote = if (!topicTitle.isNullOrBlank()) {
            "Saat ini kamu sedang membuka modul materi: **$topicTitle**.\n"
        } else ""

        return """
            🎯 **Pertanyaan Menarik: "$question"**
            $topicContextNote
            Sebagai AI Tutor Informatika SMAN 1 Kepanjen, saya siap membantumu menguasai konsep pemrograman dan 4 Struktur Data Python secara tuntas.

            💡 **Eksplorasi yang Bisa Kamu Tanyakan:**
            1. 📜 **List:** Cara menambah (`append` vs `extend`), menghapus (`pop` vs `remove`), slicing, atau list comprehension.
            2. 🔒 **Tuple:** Alasan immutability, jebakan koma `(1,)`, cara unpacking, atau penggunaan tuple sebagai kunci dictionary.
            3. ⭕ **Set:** Cara menghapus duplikat otomatis, diagram Venn (`|`, `&`, `-`, `^`), atau mengapa set tidak memiliki indeks.
            4. 📖 **Dictionary:** Pasangan Key-Value, akses aman dengan `.get()`, aturan kunci hashable, looping `.items()`, atau menggabungkan dictionary.
            5. 🧬 **Struktur Bersarang & Perbandingan:** Kapan harus memilih List vs Tuple vs Set vs Dict, atau analisis efisiensi Big-O.

            Silakan tuliskan pertanyaanmu atau klik salah satu tombol topik saran di atas! 😊
        """.trimIndent()
    }
}
