package hu.kristof.nagy.hikebookclient.model

import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.view.mymap.MarkerType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.util.*

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

    @Test
    fun `compute average speed`() {
        val start = Calendar.getInstance().apply {
            clear()
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 30)
        }.timeInMillis
        val finish = Calendar.getInstance().apply {
            clear()
            set(Calendar.HOUR_OF_DAY, 15)
            set(Calendar.MINUTE, 30)
        }.timeInMillis
        val route = Route("name", listOf(
            Point(0.0, 0.0, MarkerType.NEW, ""),
            Point(1.0, 1.0, MarkerType.NEW, "")
        ), "")
        // println(route.toPolyline().distance)
        // 157425.537108412 m ~ 157.425 km
        // 157.425 km / 5 h = 31.485 km/h

        val avgSpeed = route.computeAvgSpeed(start, finish)

        assertEquals(31.0, avgSpeed, 2.0)
    }
}