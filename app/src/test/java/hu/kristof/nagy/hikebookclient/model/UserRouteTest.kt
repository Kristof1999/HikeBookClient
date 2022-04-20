package hu.kristof.nagy.hikebookclient.model

import hu.kristof.nagy.hikebookclient.model.routes.UserRoute
import hu.kristof.nagy.hikebookclient.view.mymap.MarkerType
import org.junit.Assert.assertThrows
import org.junit.Test

class UserRouteTest {
    private val points = listOf(
        Point(0.0, 0.0, MarkerType.SET, ""),
        Point(1.0, 1.0, MarkerType.NEW, "")
    )

    @Test
    fun `route name empty`() {
        assertThrows(IllegalArgumentException::class.java) {
            UserRoute("user", "", points, "")
        }
    }

    @Test
    fun `illegal route name`() {
        assertThrows(IllegalArgumentException::class.java) {
            UserRoute("user", "/", points, "")
        }
    }

    @Test
    fun `point size`() {
        assertThrows(IllegalArgumentException::class.java) {
            UserRoute(
                "user", "route name", listOf(
                    Point(0.0, 0.0, MarkerType.SET, "")
                ), ""
            )
        }
    }

    @Test
    fun `user name empty`() {
        assertThrows(IllegalArgumentException::class.java) {
            UserRoute("", "route name", points, "")
        }
    }

    @Test
    fun `illegal user name`() {
        assertThrows(IllegalArgumentException::class.java) {
            UserRoute("/", "route name", points, "")
        }
    }
}