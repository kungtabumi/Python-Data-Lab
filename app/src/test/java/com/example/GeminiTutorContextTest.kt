package com.example

import com.example.ai.GeminiTutorService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GeminiTutorContextTest {

    private lateinit var tutorService: GeminiTutorService

    @Before
    fun setUp() {
        tutorService = GeminiTutorService()
    }

    @Test
    fun testEducationalQueriesNotInterceptedByFastSocialChitChat() {
        // Questions about data structures should NEVER be intercepted as pure social chit chat
        val questions = listOf(
            "Apa perbedaan list dan tuple?",
            "Bagaimana cara membuat set kosong di Python?",
            "Kenapa dict.get() lebih aman dibanding dict[key]?",
            "Bagaimana cara looping dictionary dengan items()?",
            "Mengapa tuple immutable dan apa keuntungannya?",
            "Apa beda append dan extend pada list?",
            "Apakah list bisa jadi elemen set?",
            "Contoh struktur data bersarang list of dictionary"
        )

        for (q in questions) {
            val isSocial = tutorService.isPureSocialChitChat(q)
            assertFalse("Query '$q' should not be considered pure social chit-chat", isSocial)
            val isDirectMatch = tutorService.isDirectGroundTruthMatch(q, null, null)
            assertFalse("Query '$q' should not be intercepted as a fixed template", isDirectMatch)
        }
    }

    @Test
    fun testSetQueriesRelevanceAndCompleteness() = runBlocking {
        val questions = listOf(
            "Bagaimana cara kerja set di Python dan operasi apa saja yang ada?",
            "Kenapa set{} bukan set kosong tapi dict?",
            "Apa beda remove() dan discard() pada set?",
            "Bisa tidak set memiliki indeks seperti set[0]?",
            "Jelaskan operasi himpunan union intersection difference pada set Python"
        )

        for (q in questions) {
            val response = tutorService.getOfflineEducationalResponse(
                question = q,
                code = null,
                executionError = null,
                executionStdout = null,
                topicTitle = "Set (Himpunan Unik)"
            )

            assertTrue("Response for '$q' must not be blank", response.isNotBlank())
            assertTrue("Response for '$q' must mention set concepts", response.lowercase().contains("set"))

            // Verify code block completeness (no truncated code blocks)
            val backticks = response.split("```").size - 1
            assertEquals("Code blocks must be properly closed in '$q'", 0, backticks % 2)

            // Verify educational tone
            assertTrue("Response must have pedagogical structure", response.contains("Guru") || response.contains("Python"))
        }
    }

    @Test
    fun testListQueriesRelevanceAndCompleteness() = runBlocking {
        val questions = listOf(
            "Apa perbedaan append() dan extend() pada list?",
            "Bagaimana cara kerja list comprehension?",
            "Bagaimana sintaks slicing list data[start:stop:step]?",
            "Apa beda remove() dan pop() pada list?",
            "Bagaimana cara kerja list bersarang atau 2D matriks?"
        )

        for (q in questions) {
            val response = tutorService.getOfflineEducationalResponse(
                question = q,
                code = null,
                executionError = null,
                executionStdout = null,
                topicTitle = "List (Daftar Dinamis)"
            )

            assertTrue("Response for '$q' must not be blank", response.isNotBlank())
            assertTrue("Response for '$q' must mention list concepts", response.lowercase().contains("list"))

            val backticks = response.split("```").size - 1
            assertEquals("Code blocks must be properly closed in '$q'", 0, backticks % 2)
        }
    }

    @Test
    fun testTupleQueriesRelevanceAndCompleteness() = runBlocking {
        val questions = listOf(
            "Mengapa tuple bersifat immutable dan kapan kita harus menggunakannya?",
            "Kenapa tuple 1 elemen butuh koma (1,)?",
            "Bagaimana cara tuple unpacking di Python?",
            "Kenapa tuple bisa jadi kunci dictionary tapi list tidak bisa?"
        )

        for (q in questions) {
            val response = tutorService.getOfflineEducationalResponse(
                question = q,
                code = null,
                executionError = null,
                executionStdout = null,
                topicTitle = "Tuple (Data Konstan)"
            )

            assertTrue("Response for '$q' must not be blank", response.isNotBlank())
            assertTrue("Response for '$q' must address tuple concepts", response.lowercase().contains("tuple"))

            val backticks = response.split("```").size - 1
            assertEquals("Code blocks must be properly closed in '$q'", 0, backticks % 2)
        }
    }

    @Test
    fun testDictionaryQueriesRelevanceAndCompleteness() = runBlocking {
        val questions = listOf(
            "Bagaimana cara kerja dictionary di Python dan apa itu key value?",
            "Kenapa pakai dict.get() dibanding dict[key]?",
            "Bagaimana cara looping dictionary dengan keys values dan items?",
            "Mengapa key dictionary harus immutable atau hashable?",
            "Bagaimana cara menggabungkan dua dictionary di Python 3.9+?"
        )

        for (q in questions) {
            val response = tutorService.getOfflineEducationalResponse(
                question = q,
                code = null,
                executionError = null,
                executionStdout = null,
                topicTitle = "Dictionary (Pemetaan Key-Value)"
            )

            assertTrue("Response for '$q' must not be blank", response.isNotBlank())
            assertTrue("Response for '$q' must address dictionary concepts", response.lowercase().contains("dict"))

            val backticks = response.split("```").size - 1
            assertEquals("Code blocks must be properly closed in '$q'", 0, backticks % 2)
        }
    }

    @Test
    fun testComparisonMatrixAndDecisionFlowchart() = runBlocking {
        val response = tutorService.getOfflineEducationalResponse(
            question = "Apa beda List, Tuple, Set, dan Dictionary?",
            code = null,
            executionError = null,
            executionStdout = null,
            topicTitle = null
        )

        assertTrue(response.contains("List `[...]`"))
        assertTrue(response.contains("Tuple `(...)`"))
        assertTrue(response.contains("Set `{...}`"))
        assertTrue(response.contains("Dictionary `{k: v}`"))
        assertTrue(response.contains("Mutable"))
        assertTrue(response.contains("Immutable"))

        val backticks = response.split("```").size - 1
        assertEquals("Code blocks must be properly closed", 0, backticks % 2)
    }

    @Test
    fun testContextualTopicDisambiguation() = runBlocking {
        // When in Dictionary topic, a question like "bagaimana cara menambah data?" should return dictionary guidance
        val dictResponse = tutorService.getOfflineEducationalResponse(
            question = "bagaimana cara menambah data baru?",
            code = null,
            executionError = null,
            executionStdout = null,
            topicTitle = "Dictionary (Pemetaan Key-Value)"
        )
        assertTrue("Contextual dict response must mention dict concepts", dictResponse.lowercase().contains("dict"))

        // When in Set topic, a question like "bagaimana cara menambah data?" should return set guidance
        val setResponse = tutorService.getOfflineEducationalResponse(
            question = "bagaimana cara menambah data baru?",
            code = null,
            executionError = null,
            executionStdout = null,
            topicTitle = "Set (Himpunan Unik)"
        )
        assertTrue("Contextual set response must mention set concepts", setResponse.lowercase().contains("set"))

        // When in Tuple topic, a question like "bagaimana cara mengubah nilainya?" should explain immutability
        val tupleResponse = tutorService.getOfflineEducationalResponse(
            question = "bagaimana cara mengubah nilainya?",
            code = null,
            executionError = null,
            executionStdout = null,
            topicTitle = "Tuple (Data Konstan)"
        )
        assertTrue("Contextual tuple response must mention tuple concepts", tupleResponse.lowercase().contains("tuple"))

        // When in List topic, a question like "bagaimana cara menambah data?" should explain append/extend/insert
        val listResponse = tutorService.getOfflineEducationalResponse(
            question = "bagaimana cara menambah data baru?",
            code = null,
            executionError = null,
            executionStdout = null,
            topicTitle = "List (Daftar Dinamis)"
        )
        assertTrue("Contextual list response must mention list concepts", listResponse.lowercase().contains("list"))
    }

    @Test
    fun testTutorStreamingAccumulatorDoesNotTruncate() = runBlocking {
        val accumulated = StringBuilder()
        val result = tutorService.askTutorStream(
            userMessage = "Jelaskan perbedaan mendasar antara List, Tuple, Set, dan Dictionary serta berikan contoh kode lengkapnya!",
            contextCode = null,
            conversationHistory = emptyList(),
            lastExecutionError = null,
            lastExecutionStdout = null,
            activeTopicTitle = "Struktur Data",
            onChunkReceived = { chunk ->
                accumulated.append(chunk)
            }
        )

        assertTrue("Result should be non-empty", result.isNotBlank())
        assertEquals("Accumulated text must match result", accumulated.toString(), result)

        val backticks = result.split("```").size - 1
        assertEquals("All code blocks must be closed properly", 0, backticks % 2)
    }
}
