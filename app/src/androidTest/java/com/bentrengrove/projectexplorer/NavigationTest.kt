package com.bentrengrove.projectexplorer

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
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.test.espresso.ApolloIdlingResource
import com.bentrengrove.projectexplorer.repositories.RepositoriesFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NavigationTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @Inject lateinit var apolloClient: ApolloClient

    @Before
    fun setup() {
        hiltRule.inject()
        val idlingResource: IdlingResource = ApolloIdlingResource.create("ApolloIdlingResource", apolloClient)
        IdlingRegistry.getInstance().register(idlingResource)
    }

    @Test
    fun testNavigateToProjects() {
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.nav_graph)

        launchFragmentInHiltContainer<RepositoriesFragment>(themeResId = R.style.AppTheme) {
            Navigation.setViewNavController(requireView(), navController)
        }

        onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        assertThat(navController.currentDestination?.id, Matchers.equalTo(R.id.projectsFragment))
    }
}