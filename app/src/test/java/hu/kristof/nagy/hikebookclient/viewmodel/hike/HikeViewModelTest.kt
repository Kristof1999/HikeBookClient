package hu.kristof.nagy.hikebookclient.viewmodel.hike

import hu.kristof.nagy.hikebookclient.data.repository.routes.IUserRouteRepository
import hu.kristof.nagy.hikebookclient.di.Service
import hu.kristof.nagy.hikebookclient.util.Constants
import hu.kristof.nagy.hikebookclient.viewModel.hike.HikeViewModel
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock
import org.osmdroid.util.GeoPoint

class HikeViewModelTest {

    private val service = mock(Service::class.java)
    private val userRouteRepository = mock(IUserRouteRepository::class.java)
    private val viewModel = HikeViewModel(service, userRouteRepository)

    // Hungary -> lat range: 45-48, lon range: 16-22

    private val radius = Constants.GEOFENCE_RADIUS_IN_METERS // 100m
    private val center = Constants.START_POINT // lat: 47.2954, lon: 19.227

    // used: https://en.wikipedia.org/wiki/Geographic_coordinate_system#Length_of_a_degree
    // 1 lat ~ 110.6km = 110 600m, 1 lon ~ 111.3 km = 111 300m ->
    // 0.001 lat ~ 110.6m, 0.001lon ~ 110.3m

    @Test
    fun `is point in circle, very far`() {
        val veryFarPoint = GeoPoint(47.2954 - 1, 19.227 - 1)

        val res = viewModel.isPointInCircle(veryFarPoint, center, radius)

        assertFalse(res)
    }

    @Test
    fun `is point in circle, 200m`() {
        val farPoint = GeoPoint(47.2954 - 0.002, 19.227)

        val res = viewModel.isPointInCircle(farPoint, center, radius)

        assertFalse(res)
    }

    @Test
    fun `is point in circle, 50m`() {
        val closePoint = GeoPoint(47.2954 - 0.0005, 19.227)

        val res = viewModel.isPointInCircle(closePoint, center, radius)

        assertTrue(res)
    }

    @Test
    fun `is point in circle, 5m`() {
        val closePoint = GeoPoint(47.2954 - 0.00005, 19.227)

        val res = viewModel.isPointInCircle(closePoint, center, radius)

        assertTrue(res)
    }
}