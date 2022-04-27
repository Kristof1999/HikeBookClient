package hu.kristof.nagy.hikebookclient.viewmodel.livedata

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import hu.kristof.nagy.hikebookclient.data.IGroupHikeRepository
import hu.kristof.nagy.hikebookclient.data.routes.IUserRouteRepository
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.view.mymap.MarkerType
import hu.kristof.nagy.hikebookclient.viewModel.grouphike.GroupHikeDetailViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class LiveDataTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private val groupHikeName = "groupHike"
    private val routeName = "routeName"
    private val points = listOf(
        Point(0.0, 0.0, MarkerType.NEW, ""),
        Point(0.0, 0.0, MarkerType.NEW, "")
    )
    private val description = ""
    private val groupHikeRoute = Route.GroupHikeRoute(groupHikeName, routeName, points, description)
    private val response = ServerResponseResult(true, null, groupHikeRoute)
    private val groupHikeRepository = mock<IGroupHikeRepository> {
        onBlocking { loadRoute(groupHikeName) } doReturn response
    }
    private val userRouteRepository = mock<IUserRouteRepository> {
        onBlocking {
            createUserRouteForLoggedInUser(routeName, points, description)
        } doReturn flowOf(ResponseResult.Success(true))
    }
    private lateinit var viewModel: GroupHikeDetailViewModel

    @Before
    fun setUp() {
        viewModel = GroupHikeDetailViewModel(groupHikeRepository, userRouteRepository)
    }

    @Test
    fun `route loaded`() {
        viewModel.loadRoute(groupHikeName)

        val res = viewModel.route.getOrAwaitValue()

        assertEquals(true, res.isSuccess)
        assertEquals(groupHikeRoute, res.successResult!!)
    }

    @Test
    fun `add to my map, route has not loaded`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        viewModel.addToMyMap(context)

        val res = viewModel.addToMyMapRes.getOrAwaitValue()

        assertEquals(
            ResponseResult.Error<Boolean>("Kérem, várjon."),
            res
        )
    }

    @Test
    fun `add to my map, route loaded`() {
        viewModel.loadRoute(groupHikeName)
        val context = ApplicationProvider.getApplicationContext<Context>()
        viewModel.addToMyMap(context)

        val res = viewModel.addToMyMapRes.getOrAwaitValue()

        assertEquals(
            ResponseResult.Success(true),
            res
        )
    }
}