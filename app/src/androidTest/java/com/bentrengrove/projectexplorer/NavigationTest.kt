package com.bentrengrove.projectexplorer

import android.app.Instrumentation
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.apollographql.apollo.test.espresso.ApolloIdlingResource
import com.bentrengrove.projectexplorer.repositories.RepositoriesFragment
import dagger.hilt.android.components.ApplicationComponent
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {
    @Before
    fun setup() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val app = instrumentation.targetContext.applicationContext as MyApplication
        val idlingResource: IdlingResource = ApolloIdlingResource.create("ApolloIdlingResource", app.apolloClient)
        IdlingRegistry.getInstance().register(idlingResource)
    }

    @Test
    fun testNavigateToProjects() {
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.nav_graph)

        val reposScenario = launchFragmentInContainer<RepositoriesFragment>(themeResId = R.style.AppTheme)
        reposScenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        assertThat(navController.currentDestination?.id, Matchers.equalTo(R.id.projectsFragment))
    }
}