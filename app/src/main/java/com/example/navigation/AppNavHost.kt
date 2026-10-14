package com.example.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.data.CurriculumData
import com.example.model.DataStructureType
import com.example.ui.screens.AiTutorScreen
import com.example.ui.screens.CodePlaygroundScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InteractiveLabScreen
import com.example.ui.screens.LessonDetailScreen
import com.example.ui.screens.LessonListScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.viewmodel.PythonLabViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: PythonLabViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier.fillMaxSize(),
        enterTransition = {
            fadeIn(animationSpec = tween(220))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(180))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(220))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(180))
        }
    ) {
        // 1. Home Dashboard
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToStructure = { type ->
                    viewModel.setSelectedModuleFilter(type)
                    navController.navigate(Screen.Modules.route)
                },
                onNavigateToPlayground = {
                    viewModel.clearPlaygroundOrigin()
                    navController.navigate(Screen.Playground.route)
                },
                onNavigateToLab = {
                    viewModel.setActiveLabStructure(DataStructureType.LIST)
                    navController.navigate(Screen.InteractiveLab.route)
                },
                onNavigateToQuiz = {
                    navController.navigate(Screen.Quiz.route)
                },
                onNavigateToAi = {
                    viewModel.clearAiTutorOrigin()
                    navController.navigate(Screen.AiTutor.route)
                }
            )
        }

        // 2. Learning Modules (Lesson List) with filter parameter
        composable(
            route = Screen.Modules.route,
            arguments = listOf(
                navArgument("filter") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val filterParam = backStackEntry.arguments?.getString("filter")
            val initialFilter = filterParam?.let {
                runCatching { DataStructureType.valueOf(it) }.getOrNull()
            }

            LessonListScreen(
                viewModel = viewModel,
                initialFilter = initialFilter,
                onBack = {
                    val popped = navController.popBackStack()
                    if (!popped) {
                        navController.navigate(Screen.Home.route)
                    }
                },
                onSelectLesson = { lesson ->
                    navController.navigate(Screen.LessonDetail.createRoute(lesson.id))
                }
            )
        }

        // 3. Lesson Detail
        composable(
            route = Screen.LessonDetail.route,
            arguments = listOf(
                navArgument("lessonId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
            val lesson = CurriculumData.allLessons.find { it.id == lessonId }

            if (lesson != null) {
                LessonDetailScreen(
                    lesson = lesson,
                    viewModel = viewModel,
                    onBack = {
                        navController.popBackStack()
                    },
                    onOpenInPlayground = { code ->
                        viewModel.updateCode(code)
                        viewModel.setPlaygroundOrigin("lesson", lesson.title)
                        navController.navigate(Screen.Playground.route)
                    }
                )
            }
        }

        // 4. Interactive Lab with initial tab parameter
        composable(
            route = Screen.InteractiveLab.route,
            arguments = listOf(
                navArgument("initialTab") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val initialTabParam = backStackEntry.arguments?.getString("initialTab")
            val initialTab = initialTabParam?.let {
                runCatching { DataStructureType.valueOf(it) }.getOrNull()
            } ?: DataStructureType.LIST

            InteractiveLabScreen(
                viewModel = viewModel,
                initialTab = initialTab,
                onBack = {
                    val popped = navController.popBackStack()
                    if (!popped) {
                        navController.navigate(Screen.Home.route)
                    }
                }
            )
        }

        // 5. Code Playground
        composable(Screen.Playground.route) {
            CodePlaygroundScreen(
                viewModel = viewModel,
                onBack = {
                    val origin = viewModel.playgroundOrigin.value
                    viewModel.clearPlaygroundOrigin()
                    val popped = navController.popBackStack()
                    if (!popped) {
                        if (origin.sourceScreen == "lab") {
                            navController.navigate(Screen.InteractiveLab.route)
                        } else if (origin.sourceScreen == "lesson") {
                            navController.navigate(Screen.Modules.route)
                        } else {
                            navController.navigate(Screen.Home.route)
                        }
                    }
                },
                onAskAiAboutError = { errorPrompt ->
                    viewModel.setAiTutorOrigin("playground", "Code Playground")
                    navController.navigate(Screen.AiTutor.route)
                    viewModel.sendAiQuestion(errorPrompt, attachCurrentCode = true)
                }
            )
        }

        // 6. Quiz Screen
        composable(Screen.Quiz.route) {
            QuizScreen(
                viewModel = viewModel,
                onBack = {
                    val popped = navController.popBackStack()
                    if (!popped) {
                        navController.navigate(Screen.Home.route)
                    }
                }
            )
        }

        // 7. AI Tutor Screen
        composable(Screen.AiTutor.route) {
            AiTutorScreen(
                viewModel = viewModel,
                onBack = {
                    val origin = viewModel.aiTutorOrigin.value
                    viewModel.clearAiTutorOrigin()
                    val popped = navController.popBackStack()
                    if (!popped) {
                        if (origin.sourceScreen == "playground") {
                            navController.navigate(Screen.Playground.route)
                        } else {
                            navController.navigate(Screen.Home.route)
                        }
                    }
                }
            )
        }
    }
}
