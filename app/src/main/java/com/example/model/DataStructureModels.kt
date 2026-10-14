package com.example.model

enum class DataStructureType(
    val title: String,
    val subtitle: String,
    val iconName: String,
    val syntaxExample: String,
    val isMutable: Boolean,
    val isOrdered: Boolean,
    val allowsDuplicates: Boolean,
    val indexingType: String
) {
    LIST(
        title = "List",
        subtitle = "Terurut, dapat diubah, mengizinkan duplikat",
        iconName = "view_list",
        syntaxExample = "buah = [\"Apel\", \"Jeruk\", \"Mangga\"]",
        isMutable = true,
        isOrdered = true,
        allowsDuplicates = true,
        indexingType = "Integer Index [0, 1, 2...]"
    ),
    TUPLE(
        title = "Tuple",
        subtitle = "Terurut, IMMUTABLE (tetap), mengizinkan duplikat",
        iconName = "lock",
        syntaxExample = "koordinat = (10, 20, 30)",
        isMutable = false,
        isOrdered = true,
        allowsDuplicates = true,
        indexingType = "Integer Index [0, 1, 2...]"
    ),
    SET(
        title = "Set",
        subtitle = "Elemen UNIK, tidak terurut, himpunan matematika",
        iconName = "bubble_chart",
        syntaxExample = "angka = {1, 2, 3, 4}",
        isMutable = true,
        isOrdered = false,
        allowsDuplicates = false,
        indexingType = "Tidak Menggunakan Index"
    ),
    DICTIONARY(
        title = "Dictionary",
        subtitle = "Pasangan Key-Value unik, akses sangat cepat",
        iconName = "menu_book",
        syntaxExample = "siswa = {\"nama\": \"Andi\", \"nilai\": 90}",
        isMutable = true,
        isOrdered = true, // Python 3.7+ preserves insertion order
        allowsDuplicates = false, // Keys must be unique
        indexingType = "Key Lookup [key]"
    ),
    NESTED(
        title = "Nested Structures",
        subtitle = "Struktur data bersarang multi-dimensi",
        iconName = "account_tree",
        syntaxExample = "data = {\"nilai\": [80, 90, 85], \"koor\": (10, 20)}",
        isMutable = true,
        isOrdered = true,
        allowsDuplicates = true,
        indexingType = "Multi-level Chained Access"
    ),
    COMPARISON(
        title = "Komparasi Data",
        subtitle = "Perbandingan lengkap 4 struktur data Python",
        iconName = "compare_arrows",
        syntaxExample = "List vs Tuple vs Set vs Dictionary",
        isMutable = true,
        isOrdered = true,
        allowsDuplicates = true,
        indexingType = "Matrix Analysis"
    );

    val displayName: String get() = title
}

data class TopicLesson(
    val id: String,
    val structureType: DataStructureType,
    val title: String,
    val orderNumber: Int,
    val summary: String,
    val explanation: String,
    val codeExample: String,
    val expectedOutput: String,
    val visualizationType: VisualizationType,
    val initialDataState: List<String> = emptyList(),
    val actionCommand: String = "",
    val animationDescription: String = "",
    val proTips: List<String> = emptyList()
)

enum class VisualizationType {
    LIST_INDEX,
    LIST_NEGATIVE_INDEX,
    LIST_SLICING,
    LIST_MUTATION, // append, insert, remove, pop, sort, reverse
    TUPLE_LOCKED,
    TUPLE_UNPACKING,
    TUPLE_IMMUTABLE_ERROR,
    SET_DUPLICATE_ELIMINATION,
    SET_VENN_UNION,
    SET_VENN_INTERSECTION,
    SET_VENN_DIFFERENCE,
    SET_VENN_SYMMETRIC_DIFF,
    DICT_KEY_VALUE_TABLE,
    DICT_KEY_LOOKUP,
    DICT_MUTATION,
    NESTED_TREE_PATH,
    COMPARISON_TABLE
}

data class QuizQuestion(
    val id: String,
    val structureType: DataStructureType,
    val question: String,
    val codeSnippet: String? = null,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String,
    val xpReward: Int = 50,
    val difficulty: String = "Medium", // Easy, Medium, Hard
    val hint: String = ""
)

data class AchievementBadge(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val requiredXp: Int = 0,
    val category: String,
    val isUnlocked: Boolean = false
)

data class ChatMessage(
    val id: String,
    val sender: String,
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
