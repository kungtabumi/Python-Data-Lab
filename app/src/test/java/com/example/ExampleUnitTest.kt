package com.example

import com.example.engine.LocalPythonInterpreter
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testDictDemoExecution() = runBlocking {
        val code = """
            # Eksperimen Data Siswa Dictionary (Key-Value)
            siswa = {
                "nis": "20241101",
                "nama": "Aisyah Putri",
                "jurusan": "Informatika",
                "nilai": [95, 90, 88]
            }
            siswa["nilai"].append(100)
            print("Nama Siswa:", siswa["nama"])
            print("Daftar Nilai:", siswa["nilai"])
            print("Rata-rata Nilai:", sum(siswa["nilai"]) / len(siswa["nilai"]))
            print("Semua Key:", list(siswa.keys()))
            print("Semua Value:", list(siswa.values()))
        """.trimIndent()

        val interpreter = LocalPythonInterpreter()
        val res = interpreter.execute(code)
        assertTrue(res.isSuccess)
        assertFalse(res.stdout.isEmpty())
        assertTrue(res.stdout.contains("Nama Siswa: Aisyah Putri"))
        assertTrue(res.stdout.contains("Daftar Nilai: [95, 90, 88, 100]"))
        assertTrue(res.stdout.contains("Semua Key: ['nis', 'nama', 'jurusan', 'nilai']"))
    }

    @Test
    fun testDictMethodsAndBuiltins() = runBlocking {
        val code = """
            d = {"a": 1, "b": 2, "c": 3}
            print("Keys:", list(d.keys()))
            print("Values:", tuple(d.values()))
            print("Has a:", "a" in d)
            print("Get:", d.get("b"))
            val = d.pop("c")
            print("Popped:", val)
            print("Len:", len(d))
        """.trimIndent()

        val interpreter = LocalPythonInterpreter()
        val res = interpreter.execute(code)
        assertTrue(res.isSuccess)
        assertTrue(res.stdout.contains("Keys: ['a', 'b', 'c']"))
        assertTrue(res.stdout.contains("Values: (1, 2, 3)"))
        assertTrue(res.stdout.contains("Has a: True"))
        assertTrue(res.stdout.contains("Popped: 3"))
        assertTrue(res.stdout.contains("Len: 2"))
    }

    @Test
    fun testNavigationRoutes() {
        val modulesRouteAll = com.example.navigation.Screen.Modules.createRoute(null)
        assertEquals("modules", modulesRouteAll)

        val modulesRouteList = com.example.navigation.Screen.Modules.createRoute("LIST")
        assertEquals("modules?filter=LIST", modulesRouteList)

        val lessonRoute = com.example.navigation.Screen.LessonDetail.createRoute("list_1")
        assertEquals("lesson/list_1", lessonRoute)

        val labRoute = com.example.navigation.Screen.InteractiveLab.createRoute("DICTIONARY")
        assertEquals("lab?initialTab=DICTIONARY", labRoute)
    }

    @Test
    fun testAchievementBadgesAndRewardCelebration() {
        val allBadges = com.example.data.CurriculumData.allAchievements
        assertTrue("Achievements should not be empty", allBadges.isNotEmpty())
        
        val listMasterBadge = allBadges.find { it.id == "b_list_master" }
        assertNotNull(listMasterBadge)
        assertEquals("List Master", listMasterBadge?.title)

        val celebration = com.example.model.RewardCelebrationEvent(
            badge = listMasterBadge,
            lessonTitle = "List Indexing",
            structureDisplayName = "List",
            structureEmoji = "📜",
            xpGained = 430,
            isModuleCompleted = true,
            totalLessonsInModule = 14,
            completedInModule = 14
        )

        assertEquals("List Master", celebration.badge?.title)
        assertTrue(celebration.isModuleCompleted)
        assertEquals(430, celebration.xpGained)
    }

    @Test
    fun testGeminiTutorServiceContextualUnderstanding_List() {
        val tutorService = com.example.ai.GeminiTutorService()
        
        // Append vs extend
        val appendResp = tutorService.getOfflineEducationalResponse("Apa beda append dan extend pada list?", null)
        assertTrue("Should explain append vs extend", appendResp.contains("append") && appendResp.contains("extend"))
        assertTrue("Should provide code verification", appendResp.contains("```python"))

        // List comprehension
        val compResp = tutorService.getOfflineEducationalResponse("Bagaimana cara membuat list comprehension?", null)
        assertTrue("Should explain list comprehension", compResp.contains("List Comprehension"))
        assertTrue("Should provide code verification", compResp.contains("```python"))

        // Pop vs remove
        val popResp = tutorService.getOfflineEducationalResponse("Apa perbedaan pop dan remove pada list?", null)
        assertTrue("Should explain pop vs remove", popResp.contains("pop") && popResp.contains("remove"))
    }

    @Test
    fun testGeminiTutorServiceContextualUnderstanding_Tuple() {
        val tutorService = com.example.ai.GeminiTutorService()

        // Single element tuple comma trap
        val commaResp = tutorService.getOfflineEducationalResponse("Kenapa tuple 1 elemen butuh koma?", null)
        assertTrue("Should explain single-element tuple trailing comma", commaResp.contains("1 Elemen") || commaResp.contains("Trailing Comma") || commaResp.contains("(42,)"))

        // Tuple unpacking
        val unpackResp = tutorService.getOfflineEducationalResponse("Bagaimana cara unpacking tuple dan swap variabel?", null)
        assertTrue("Should explain tuple unpacking", unpackResp.contains("Unpacking") || unpackResp.contains("membongkar"))

        // Immutability
        val immResp = tutorService.getOfflineEducationalResponse("Mengapa tuple tidak bisa diubah dan bagaimana solusinya?", null)
        assertTrue("Should explain immutability", immResp.contains("Immutable") || immResp.contains("TypeError"))
    }

    @Test
    fun testGeminiTutorServiceContextualUnderstanding_Set() {
        val tutorService = com.example.ai.GeminiTutorService()

        // Uniqueness
        val uniqueResp = tutorService.getOfflineEducationalResponse("Bagaimana set mengeliminasi duplikat?", null)
        assertTrue("Should explain uniqueness", uniqueResp.contains("Unik") || uniqueResp.contains("Duplikat"))

        // Empty set syntax trap
        val emptyResp = tutorService.getOfflineEducationalResponse("Bagaimana cara membuat set kosong dan kenapa bukan {}?", null)
        assertTrue("Should explain empty set trap", emptyResp.contains("set()") && emptyResp.contains("{}"))

        // Venn operations
        val vennResp = tutorService.getOfflineEducationalResponse("Bagaimana cara operasi irisan dan gabungan Venn di set?", null)
        assertTrue("Should explain Venn operations", vennResp.contains("Venn") || vennResp.contains("Irisan") || vennResp.contains("Union"))

        // Discard vs remove
        val discardResp = tutorService.getOfflineEducationalResponse("Apa beda remove dan discard pada set?", null)
        assertTrue("Should explain remove vs discard", discardResp.contains("discard") && discardResp.contains("remove"))
    }

    @Test
    fun testGeminiTutorServiceContextualUnderstanding_Dictionary() {
        val tutorService = com.example.ai.GeminiTutorService()

        // Safe access dict.get()
        val getResp = tutorService.getOfflineEducationalResponse("Kenapa harus pakai dict.get() daripada dict[key]?", null)
        assertTrue("Should explain dict.get vs []", getResp.contains(".get()") && getResp.contains("KeyError"))

        // Keys must be hashable
        val keyResp = tutorService.getOfflineEducationalResponse("Apakah list bisa jadi kunci dictionary?", null)
        assertTrue("Should explain keys must be hashable", keyResp.contains("Hashable") || keyResp.contains("unhashable"))

        // Looping dictionary
        val loopResp = tutorService.getOfflineEducationalResponse("Bagaimana cara looping key dan value dictionary bersamaan?", null)
        assertTrue("Should explain items() loop", loopResp.contains(".items()") || loopResp.contains("Looping"))

        // Merging dictionaries
        val mergeResp = tutorService.getOfflineEducationalResponse("Bagaimana cara menggabungkan dua dictionary?", null)
        assertTrue("Should explain merging dicts", mergeResp.contains("|") || mergeResp.contains("update"))
    }

    @Test
    fun testGeminiTutorServiceContextualUnderstanding_ComparisonAndNested() {
        val tutorService = com.example.ai.GeminiTutorService()

        // Comparison
        val compResp = tutorService.getOfflineEducationalResponse("Kapan harus pakai List dibanding Tuple, Set, dan Dictionary?", null)
        assertTrue("Should provide comparison matrix", compResp.contains("Matriks Perbandingan") || compResp.contains("List") && compResp.contains("Tuple"))

        // Nested structures
        val nestedResp = tutorService.getOfflineEducationalResponse("Bagaimana contoh struktur data bersarang list of dict?", null)
        assertTrue("Should explain nested structures", nestedResp.contains("Bersarang") || nestedResp.contains("List of Dictionaries"))
    }
}
