package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.engine.LocalPythonInterpreter
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Python DataLab", appName)
    }

    @Test
    fun `test python interpreter list and sum`() = runBlocking {
        val interpreter = LocalPythonInterpreter()
        val code = """
            nilai = [80, 90, 100]
            nilai.append(70)
            print("Total:", sum(nilai))
            print("Max:", max(nilai))
        """.trimIndent()

        val result = interpreter.execute(code)
        assertNull(result.error)
        assertTrue(result.stdout.contains("Total: 340"))
        assertTrue(result.stdout.contains("Max: 100"))
    }

    @Test
    fun `test python interpreter tuple immutability`() = runBlocking {
        val interpreter = LocalPythonInterpreter()
        val code = """
            t = (1, 2, 3)
            t[0] = 99
        """.trimIndent()

        val result = interpreter.execute(code)
        assertTrue(result.error != null && result.error!!.contains("TypeError"))
    }

    @Test
    fun `test python interpreter set operations`() = runBlocking {
        val interpreter = LocalPythonInterpreter()
        val code = """
            a = {1, 2, 3}
            b = {3, 4, 5}
            print("Irisan:", a & b)
        """.trimIndent()

        val result = interpreter.execute(code)
        assertNull(result.error)
        assertTrue(result.stdout.contains("{3}"))
    }

    @Test
    fun `test in-lab coding execution for dictionary and list mutation`() = runBlocking {
        val interpreter = LocalPythonInterpreter()
        val code = """
            buah = ["Apel", "Jeruk", "Mangga"]
            buah.append("Durian")
            print("Buah:", buah)

            siswa = {"nama": "Aisyah", "kelas": "XI IPA 1"}
            siswa["status"] = "Aktif"
            print("Siswa:", siswa["nama"])
        """.trimIndent()

        val result = interpreter.execute(code)
        assertNull(result.error)
        assertTrue(result.stdout.contains("Durian"))
        assertTrue(result.stdout.contains("Aisyah"))
        val buahVar = result.variables["buah"]
        assertTrue(buahVar is List<*>)
        assertEquals(4, (buahVar as List<*>).size)
    }

    @Test
    fun `test ai tutor contextual response to greetings`() {
        val tutorService = com.example.ai.GeminiTutorService()

        val greetingResp1 = tutorService.getOfflineEducationalResponse("Halo", null)
        assertTrue(greetingResp1.contains("Selamat datang di AI Tutor Python DataLab"))
        assertTrue(greetingResp1.contains("SMAN 1 Kepanjen"))

        val greetingResp2 = tutorService.getOfflineEducationalResponse("Selamat pagi pak", null)
        assertTrue(greetingResp2.contains("Selamat datang di AI Tutor Python DataLab"))
    }

    @Test
    fun `test ai tutor contextual response to identity and general python`() {
        val tutorService = com.example.ai.GeminiTutorService()

        val identityResp = tutorService.getOfflineEducationalResponse("siapa kamu?", null)
        assertTrue(identityResp.contains("Hermawan Hidayat, S.Kom."))
        assertTrue(identityResp.contains("SMAN 1 Kepanjen"))

        val variableResp = tutorService.getOfflineEducationalResponse("apa itu variabel?", null)
        assertTrue(variableResp.contains("kotak penyimpanan berlabel") || variableResp.contains("Variabel"))

        val motivationResp = tutorService.getOfflineEducationalResponse("belajar koding susah banget", null)
        assertTrue(motivationResp.contains("Jangan Menyerah") || motivationResp.contains("Kiat Praktis"))

        val thanksResp = tutorService.getOfflineEducationalResponse("terima kasih tutor!", null)
        assertTrue(thanksResp.contains("Sama-sama"))
    }

    @Test
    fun `test ai tutor response to python fundamentals`() {
        val tutorService = com.example.ai.GeminiTutorService()

        // 1. Operators & Operands
        val opResp = tutorService.getOfflineEducationalResponse("apa itu operator dan operan dalam python?", null)
        assertTrue(opResp.contains("Operan (*Operand*)"))
        assertTrue(opResp.contains("Floor Division"))
        assertTrue(opResp.contains("Modulo"))

        // 2. Input and Output
        val ioResp = tutorService.getOfflineEducationalResponse("bagaimana cara input dan print f-string?", null)
        assertTrue(ioResp.contains("Input dan Output"))
        assertTrue(ioResp.contains("f-string"))
        assertTrue(ioResp.contains("Type Casting"))

        // 3. Branching
        val branchResp = tutorService.getOfflineEducationalResponse("jelaskan percabangan if elif else!", null)
        assertTrue(branchResp.contains("Percabangan & Seleksi Kondisi"))
        assertTrue(branchResp.contains("if"))
        assertTrue(branchResp.contains("elif"))
        assertTrue(branchResp.contains("Indentasi"))

        // 4. Loops & Iteration
        val loopResp = tutorService.getOfflineEducationalResponse("bagaimana cara kerja perulangan for loop dan while loop?", null)
        assertTrue(loopResp.contains("Perulangan (*Looping*)"))
        assertTrue(loopResp.contains("range(start, stop"))
        assertTrue(loopResp.contains("break"))

        // 5. Functions & Scope
        val funcResp = tutorService.getOfflineEducationalResponse("bagaimana cara membuat fungsi def dan return di python?", null)
        assertTrue(funcResp.contains("Fungsi & Ruang Lingkup"))
        assertTrue(funcResp.contains("def"))
        assertTrue(funcResp.contains("return"))

        // 6. Data Types & Type Casting
        val dtResp = tutorService.getOfflineEducationalResponse("apa saja tipe data dasar dan bagaimana type casting?", null)
        assertTrue(dtResp.contains("Tipe Data & Konversi Tipe"))
        assertTrue(dtResp.contains("Truthy vs Falsy"))
    }

    @Test
    fun `test ai tutor error diagnosis for fundamental errors`() {
        val tutorService = com.example.ai.GeminiTutorService()

        // ZeroDivisionError
        val zeroDivResp = tutorService.getOfflineEducationalResponse(
            question = "Kenapa kodingan saya error?",
            code = "x = 10 / 0",
            executionError = "ZeroDivisionError: division by zero"
        )
        assertTrue(zeroDivResp.contains("ZeroDivisionError"))
        assertTrue(zeroDivResp.contains("angka nol"))

        // NameError
        val nameErrResp = tutorService.getOfflineEducationalResponse(
            question = "Tolong perbaiki error ini!",
            code = "print(total_skor)",
            executionError = "NameError: name 'total_skor' is not defined"
        )
        assertTrue(nameErrResp.contains("NameError"))
        assertTrue(nameErrResp.contains("belum terdaftar"))

        // IndentationError
        val indentErrResp = tutorService.getOfflineEducationalResponse(
            question = "Apa yang salah dengan indentasi kode ini?",
            code = "if x > 0:\nprint(x)",
            executionError = "IndentationError: expected an indented block"
        )
        assertTrue(indentErrResp.contains("IndentationError"))
        assertTrue(indentErrResp.contains("indentasi"))

        // SyntaxError
        val syntaxErrResp = tutorService.getOfflineEducationalResponse(
            question = "Error syntax ini kenapa?",
            code = "if x == 10",
            executionError = "SyntaxError: invalid syntax"
        )
        assertTrue(syntaxErrResp.contains("SyntaxError"))
        assertTrue(syntaxErrResp.contains("parser"))
    }
}
