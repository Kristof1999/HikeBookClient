package hu.kristof.nagy.hikebookclient.util

import hu.kristof.nagy.hikebookclient.model.MarkerType
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.Route
import org.junit.Assert.assertThrows
import org.junit.Test

class RouteUtilsTest {
    private val p1 = Point(0.0, 0.0, MarkerType.NEW, "")
    private val p2 = Point(0.0, 1.0, MarkerType.NEW, "")

    @Test
    fun testEmptyName() {
        val route = Route("", listOf(p1, p2))
        assertThrows(IllegalArgumentException::class.java) {
            RouteUtils.checkRoute(route)
        }
    }

    @Test
    fun testIllegalName() {
        val route = Route("/", listOf(p1, p2))
        assertThrows(IllegalArgumentException::class.java) {
            RouteUtils.checkRoute(route)
        }
    }

    @Test
    fun testRouteLength() {
        var route = Route("name", listOf())
        assertThrows(IllegalArgumentException::class.java) {
            RouteUtils.checkRoute(route)
        }

        route = Route("name", listOf(p1))
        assertThrows(IllegalArgumentException::class.java) {
            RouteUtils.checkRoute(route)
        }
    }
}