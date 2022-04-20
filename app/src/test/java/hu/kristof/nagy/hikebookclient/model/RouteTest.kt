package hu.kristof.nagy.hikebookclient.model

import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.view.mymap.MarkerType
import org.junit.Assert.assertThrows
import org.junit.Test

class RouteTest {
    private val points = listOf(
        Point(0.0, 0.0, MarkerType.SET, ""),
        Point(1.0, 1.0, MarkerType.NEW, "")
    )

    @Test
    fun `route name empty`() {
        assertThrows(IllegalArgumentException::class.java) {
            Route("", points, "")
        }
    }

    @Test
    fun `illegal route name`() {
        assertThrows(IllegalArgumentException::class.java) {
            Route("/", points, "")
        }
    }

    @Test
    fun `point size`() {
        assertThrows(IllegalArgumentException::class.java) {
            Route("route name", listOf(
                Point(0.0, 0.0, MarkerType.SET, "")
            ), "")
        }
    }
}