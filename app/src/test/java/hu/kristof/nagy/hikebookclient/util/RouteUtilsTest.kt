//package hu.kristof.nagy.hikebookclient.util
//
//import hu.kristof.nagy.hikebookclient.view.mymap.MarkerType
//import hu.kristof.nagy.hikebookclient.model.Point
//import org.junit.Assert.assertThrows
//import org.junit.Test
//
//class RouteUtilsTest {
//    private val p1 = Point(0.0, 0.0, MarkerType.NEW, "")
//    private val p2 = Point(0.0, 1.0, MarkerType.NEW, "")
//
//    @Test
//    fun testEmptyName() {
//        val route = UserRoute("", "", listOf(p1, p2), "")
//        assertThrows(IllegalArgumentException::class.java) {
//            RouteUtils.checkRoute(route.routeName, route.points)
//        }
//    }
//
//    @Test
//    fun testIllegalName() {
//        val route = UserRoute("", "/", listOf(p1, p2), "")
//        assertThrows(IllegalArgumentException::class.java) {
//            RouteUtils.checkRoute(route.routeName, route.points)
//        }
//    }
//
//    @Test
//    fun testRouteLength() {
//        var route = UserRoute("", "name", listOf(), "")
//        assertThrows(IllegalArgumentException::class.java) {
//            RouteUtils.checkRoute(route.routeName, route.points)
//        }
//
//        route = UserRoute("","name", listOf(p1), "")
//        assertThrows(IllegalArgumentException::class.java) {
//            RouteUtils.checkRoute(route.routeName, route.points)
//        }
//    }
//}