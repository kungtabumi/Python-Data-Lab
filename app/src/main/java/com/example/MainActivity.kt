package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.navigation.AppNavHost
import com.example.navigation.NavigationTab
import com.example.navigation.Screen
import com.example.ui.components.SoftLimePatternBackground
import com.example.ui.theme.LimeSoft
import com.example.ui.theme.LimeSoftContainer
import com.example.ui.theme.LimeSoftDark
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.defaultMinSize
import com.example.ui.theme.LimeSoftOn
import com.example.ui.theme.PythonBlue
import com.example.ui.theme.PythonDataLabTheme
import com.example.ui.theme.PythonYellow
import com.example.ui.theme.SleekNavyBg
import com.example.ui.theme.SleekNavyBorder
import com.example.ui.theme.SleekNavyBorderSubtle
import com.example.ui.theme.SleekNavyCard
import com.example.ui.theme.SleekNavySurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.PythonLabViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: PythonLabViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PythonDataLabTheme {
                PythonDataLabApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun PythonDataLabApp(viewModel: PythonLabViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Determine whether to show the bottom navigation bar (hide on lesson detail screen)
    val isLessonDetail = currentDestination?.route?.startsWith("lesson/") == true

    SoftLimePatternBackground(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            bottomBar = {
                if (!isLessonDetail) {
                    SleekBottomNavBar(
                        currentRoute = currentDestination?.route,
                        onNavigateToTab = { tab ->
                            if (tab != NavigationTab.CODE) {
                                viewModel.clearPlaygroundOrigin()
                            }
                            if (tab != NavigationTab.AI) {
                                viewModel.clearAiTutorOrigin()
                            }
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AppNavHost(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun SleekBottomNavBar(
    currentRoute: String?,
    onNavigateToTab: (NavigationTab) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = SleekNavySurface,
        shadowElevation = 12.dp,
        border = BorderStroke(1.dp, SleekNavyBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationTab.values().forEach { tab ->
                val isSelected = when (tab) {
                    NavigationTab.HOME -> currentRoute == Screen.Home.route
                    NavigationTab.MATERI -> currentRoute?.startsWith("modules") == true
                    NavigationTab.LAB -> currentRoute?.startsWith("lab") == true
                    NavigationTab.CODE -> currentRoute == Screen.Playground.route
                    NavigationTab.QUIZ -> currentRoute == Screen.Quiz.route
                    NavigationTab.AI -> currentRoute == Screen.AiTutor.route
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigateToTab(tab) }
                        .padding(vertical = 4.dp, horizontal = 2.dp)
                        .testTag("nav_tab_${tab.name.lowercase()}")
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) {
                                    LimeSoftContainer
                                } else {
                                    Color.Transparent
                                }
                            )
                            .then(
                                if (isSelected) {
                                    Modifier.border(
                                        1.dp,
                                        LimeSoft.copy(alpha = 0.6f),
                                        RoundedCornerShape(10.dp)
                                    )
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                             imageVector = tab.icon,
                             contentDescription = tab.label,
                             tint = if (isSelected) LimeSoftDark else TextMuted,
                             modifier = Modifier.size(19.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = tab.label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) LimeSoftDark else TextMuted,
                            fontSize = 9.5.sp
                        )
                    )
                }
            }
        }
    }
}
