package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.model.DataStructureType
import com.example.navigation.NavigationTab
import com.example.navigation.Screen
import com.example.ui.viewmodel.PythonLabViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NavigationFlowTest {

    private lateinit var viewModel: PythonLabViewModel

    @Before
    fun setup() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        viewModel = PythonLabViewModel(app)
    }

    @Test
    fun testActiveLabStructurePersistence() {
        // Default should be LIST
        assertEquals(DataStructureType.LIST, viewModel.activeLabStructure.value)

        // Switch to SET (e.g. user was in Set Lab)
        viewModel.setActiveLabStructure(DataStructureType.SET)
        assertEquals(DataStructureType.SET, viewModel.activeLabStructure.value)

        // Switch to DICTIONARY
        viewModel.setActiveLabStructure(DataStructureType.DICTIONARY)
        assertEquals(DataStructureType.DICTIONARY, viewModel.activeLabStructure.value)
    }

    @Test
    fun testPlaygroundOriginTrackingAndReturnToLab() {
        // Initially empty
        assertNull(viewModel.playgroundOrigin.value.sourceScreen)
        assertNull(viewModel.playgroundOrigin.value.sourceTitle)

        // When user in Lab clicks "Buka & Jalankan di Code Playground"
        viewModel.setActiveLabStructure(DataStructureType.SET)
        viewModel.setPlaygroundOrigin("lab", "Lab Set (Himpunan)")

        assertEquals("lab", viewModel.playgroundOrigin.value.sourceScreen)
        assertEquals("Lab Set (Himpunan)", viewModel.playgroundOrigin.value.sourceTitle)

        // When user clicks back to Lab or banner
        viewModel.clearPlaygroundOrigin()
        assertNull(viewModel.playgroundOrigin.value.sourceScreen)
        assertNull(viewModel.playgroundOrigin.value.sourceTitle)

        // Lab structure state is still preserved as SET
        assertEquals(DataStructureType.SET, viewModel.activeLabStructure.value)
    }

    @Test
    fun testPlaygroundOriginTrackingFromLessonDetail() {
        // When user in LessonDetail clicks "Buka di Code Playground"
        viewModel.setPlaygroundOrigin("lesson", "Modul 1: Pengenalan List")
        assertEquals("lesson", viewModel.playgroundOrigin.value.sourceScreen)
        assertEquals("Modul 1: Pengenalan List", viewModel.playgroundOrigin.value.sourceTitle)

        // Clear upon return
        viewModel.clearPlaygroundOrigin()
        assertNull(viewModel.playgroundOrigin.value.sourceScreen)
    }

    @Test
    fun testAiTutorOriginTrackingFromPlayground() {
        // When user clicks "Tanya AI tentang Error" in Playground
        viewModel.setAiTutorOrigin("playground", "Code Playground")
        assertEquals("playground", viewModel.aiTutorOrigin.value.sourceScreen)
        assertEquals("Code Playground", viewModel.aiTutorOrigin.value.sourceTitle)

        // Clear upon return
        viewModel.clearAiTutorOrigin()
        assertNull(viewModel.aiTutorOrigin.value.sourceScreen)
    }

    @Test
    fun testSelectedModuleFilterState() {
        assertNull(viewModel.selectedModuleFilter.value)

        viewModel.setSelectedModuleFilter(DataStructureType.TUPLE)
        assertEquals(DataStructureType.TUPLE, viewModel.selectedModuleFilter.value)

        viewModel.setSelectedModuleFilter(null)
        assertNull(viewModel.selectedModuleFilter.value)
    }

    @Test
    fun testNavRoutesAndTabsIntegrity() {
        assertEquals("home", Screen.Home.route)
        assertEquals("modules?filter={filter}", Screen.Modules.route)
        assertEquals("modules", Screen.Modules.createRoute(null))
        assertEquals("modules?filter=LIST", Screen.Modules.createRoute("LIST"))
        assertEquals("lab?initialTab={initialTab}", Screen.InteractiveLab.route)
        assertEquals("lab", Screen.InteractiveLab.createRoute(null))
        assertEquals("lab?initialTab=SET", Screen.InteractiveLab.createRoute("SET"))
        assertEquals("playground", Screen.Playground.route)
        assertEquals("quiz", Screen.Quiz.route)
        assertEquals("ai_tutor", Screen.AiTutor.route)

        assertEquals("home", NavigationTab.HOME.route)
        assertEquals("modules", NavigationTab.MATERI.route)
        assertEquals("lab", NavigationTab.LAB.route)
        assertEquals("playground", NavigationTab.CODE.route)
        assertEquals("quiz", NavigationTab.QUIZ.route)
        assertEquals("ai_tutor", NavigationTab.AI.route)
    }
}
