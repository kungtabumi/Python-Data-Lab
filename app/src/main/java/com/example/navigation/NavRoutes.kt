package com.example.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Science
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Screen destination definitions for Compose Navigation.
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Modules : Screen("modules?filter={filter}") {
        fun createRoute(filter: String? = null): String {
            return if (filter != null) "modules?filter=$filter" else "modules"
        }
    }
    object LessonDetail : Screen("lesson/{lessonId}") {
        fun createRoute(lessonId: String): String = "lesson/$lessonId"
    }
    object InteractiveLab : Screen("lab?initialTab={initialTab}") {
        fun createRoute(initialTab: String? = null): String {
            return if (initialTab != null) "lab?initialTab=$initialTab" else "lab"
        }
    }
    object Playground : Screen("playground")
    object Quiz : Screen("quiz")
    object AiTutor : Screen("ai_tutor")
}

/**
 * Bottom navigation tabs mapping to top-level routes.
 */
enum class NavigationTab(val route: String, val label: String, val icon: ImageVector) {
    HOME(Screen.Home.route, "Beranda", Icons.Default.Home),
    MATERI("modules", "Materi", Icons.Default.MenuBook),
    LAB("lab", "Lab", Icons.Default.Science),
    CODE(Screen.Playground.route, "Code", Icons.Default.Code),
    QUIZ(Screen.Quiz.route, "Kuis", Icons.Default.Quiz),
    AI(Screen.AiTutor.route, "AI Tutor", Icons.Default.AutoAwesome)
}
