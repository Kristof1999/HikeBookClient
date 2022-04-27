package hu.kristof.nagy.hikebookclient.viewmodel.livedata

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import hu.kristof.nagy.hikebookclient.data.IGroupHikeRepository
import hu.kristof.nagy.hikebookclient.data.routes.IUserRouteRepository
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.view.mymap.MarkerType
import hu.kristof.nagy.hikebookclient.viewModel.grouphike.GroupHikeDetailViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LiveDataTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private val groupHikeName = "groupHike"
    private val groupHikeRoute = Route.GroupHikeRoute(
        groupHikeName, "routeName", listOf(
        Point(0.0, 0.0, MarkerType.NEW, ""),
        Point(0.0, 0.0, MarkerType.NEW, "")
    ), "")
    private val response = ServerResponseResult(true, null, groupHikeRoute)
    private val groupHikeRepository = mock<IGroupHikeRepository> {
        onBlocking { loadRoute(groupHikeName) } doReturn response
    }
    private val userRouteRepository  = mock<IUserRouteRepository>()
    private val viewModel = GroupHikeDetailViewModel(groupHikeRepository, userRouteRepository)

    @Test
    fun `route loaded`() {
        viewModel.loadRoute(groupHikeName)

        val res = viewModel.route.getOrAwaitValue()

        assertEquals(true, res.isSuccess)
        assertEquals(groupHikeRoute, res.successResult!!)
    }

    fun `general connect, route has not loaded`() {

    }

    fun `general connect, route loaded`() {

    }
}