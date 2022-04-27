package hu.kristof.nagy.hikebookclient.viewmodel.livedata

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import hu.kristof.nagy.hikebookclient.data.GroupHikeRepository
import hu.kristof.nagy.hikebookclient.data.routes.UserRouteRepository
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.view.mymap.MarkerType
import hu.kristof.nagy.hikebookclient.viewModel.grouphike.GroupHikeDetailViewModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
class LiveDataTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var groupHikeRepository: GroupHikeRepository
    @Mock
    lateinit var userRouteRepository: UserRouteRepository

    private val viewModel = GroupHikeDetailViewModel(groupHikeRepository, userRouteRepository)
    private val groupHikeName = "groupHike"
    private val groupHikeRoute = Route.GroupHikeRoute(groupHikeName, "routeName", listOf(
        Point(0.0, 0.0, MarkerType.NEW, ""),
        Point(0.0, 0.0, MarkerType.NEW, "")
    ), "")

    @Before
    fun init() {
        val serverResponseResult = ServerResponseResult(true, null, groupHikeRoute)
        groupHikeRepository = mock<GroupHikeRepository>() {
            on { loadRoute(groupHikeName) } doReturn serverResponseResult
        }
        userRouteRepository = mock<UserRouteRepository>()
    }

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