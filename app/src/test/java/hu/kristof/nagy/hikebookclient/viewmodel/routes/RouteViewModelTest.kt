package hu.kristof.nagy.hikebookclient.viewmodel.routes

import android.content.res.Resources
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import hu.kristof.nagy.hikebookclient.HikeBookApp
import hu.kristof.nagy.hikebookclient.model.MyMarker
import hu.kristof.nagy.hikebookclient.model.MyPolyline
import hu.kristof.nagy.hikebookclient.view.mymap.MarkerType
import hu.kristof.nagy.hikebookclient.viewModel.routes.RouteViewModel
import junit.framework.Assert.assertEquals
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline

@RunWith(AndroidJUnit4::class)
class RouteViewModelTest {
    private lateinit var overlays: MutableList<Overlay>

    private lateinit var viewModel: RouteViewModel

    private lateinit var resources: Resources

    @Before
    internal fun setUp() {
        viewModel = RouteViewModel()
        overlays = ArrayList()
        resources = ApplicationProvider.getApplicationContext<HikeBookApp>().resources
    }

    /**
     * Tests if the first marker was
     * added to the appropriate containers.
     */
    @Test
    fun `first marker add`() {
        val newMarker = mock<Marker>()
        val newMarkerType = MarkerType.NEW
        val newMarkerTitle = ""

        viewModel.onSingleTap(
            newMarker,
            GeoPoint(0.0, 0.0),
            resources,
            overlays
        )

        val myNewMarker = MyMarker(newMarker, newMarkerType, newMarkerTitle)
        assertThat(viewModel.myMarkers, hasItem(myNewMarker))
        assertThat(overlays, hasItem(newMarker))
        assertEquals(viewModel.myPolylines.size, 0)
    }

    /**
     * Tests if the polyline was
     * added to the appropriate container, and
     * that it connects the last 2 markers.
     */
    @Test
    fun `test polyline add and connect`() {
        val markerPoint = GeoPoint(0.0, 0.0)
        val marker = mock<Marker> {
            on { position } doReturn markerPoint
        }
        val newMarkerPoint = GeoPoint(1.0, 1.0)
        val newMarker = mock<Marker> {
            on { position } doReturn newMarkerPoint
        }

        viewModel.onSingleTap(
            marker,
            markerPoint,
            resources,
            overlays
        )
        viewModel.onSingleTap(
            newMarker,
            newMarkerPoint,
            resources,
            overlays
        )

        assertThat(viewModel.myPolylines.first().points[0], equalTo(markerPoint))
        assertThat(viewModel.myPolylines.first().points[1], equalTo(newMarkerPoint))
    }

    /**
     * Tests if the previous marker's type has changed
     * if it's type was NEW.
     */
    @Test
    fun `prev marker type changed`() {
        val p = GeoPoint(0.0, 0.0)
        val marker1 = mock<Marker> {
            on { position } doReturn p
        }
        val marker1Type = MarkerType.CASTLE
        val marker2 = mock<Marker> {
            on { position } doReturn p
        }
        val marker3 = mock<Marker> {
            on { position } doReturn p
        }

        viewModel.markerType = marker1Type
        viewModel.onSingleTap(
            marker1,
            p, resources,
            overlays
        )
        viewModel.markerType = MarkerType.NEW
        viewModel.onSingleTap(
            marker2,
            p, resources,
            overlays
        )
        viewModel.onSingleTap(
            marker3,
            p, resources,
            overlays
        )

        assertThat(viewModel.myMarkers[0].type, equalTo(marker1Type))
        assertThat(viewModel.myMarkers[1].type, equalTo(MarkerType.SET))
    }

    @Test
    fun `delete marker`() {
        val markerTitle = "marker"
        val marker = mock<Marker>()
        viewModel.myMarkers.add(MyMarker(marker, MarkerType.NEW, markerTitle))

        viewModel.onDelete(resources, marker)

        assertEquals(0, viewModel.myMarkers.size)
    }

    @Test
    fun `delete polyline`() {
        val marker1 = mock<Marker>()
        val marker2 = mock<Marker>()
        viewModel.myMarkers.add(MyMarker(marker1, MarkerType.SET, ""))
        viewModel.myMarkers.add(MyMarker(marker2, MarkerType.NEW, ""))
        val polyline = Polyline()
        polyline.setPoints(listOf(GeoPoint(0.0, 0.0), GeoPoint(1.0, 1.0)))
        viewModel.myPolylines.add(MyPolyline.from(polyline))

        viewModel.onDelete(resources, marker2)

        assertEquals(0, viewModel.myPolylines.size)
    }

    @Test
    fun `marker type changed after delete`() {
        val marker1 = mock<Marker>()
        val myMarker1 = MyMarker(marker1, MarkerType.CASTLE, "")
        val marker2 = mock<Marker>()
        val myMarker2 = MyMarker(marker2, MarkerType.SET, "")
        val marker3 = mock<Marker>()
        val myMarker3 = MyMarker(marker3, MarkerType.NEW, "")
        viewModel.myMarkers.add(myMarker1)
        viewModel.myMarkers.add(myMarker2)
        viewModel.myMarkers.add(myMarker3)
        val polyline1 = Polyline()
        polyline1.setPoints(listOf(GeoPoint(0.0, 0.0), GeoPoint(1.0, 1.0)))
        viewModel.myPolylines.add(MyPolyline.from(polyline1))
        val polyline2 = Polyline()
        polyline2.setPoints(listOf(GeoPoint(0.0, 0.0), GeoPoint(1.0, 1.0)))
        viewModel.myPolylines.add(MyPolyline.from(polyline2))

        viewModel.onDelete(resources, marker3)

        assertEquals(MarkerType.NEW, viewModel.myMarkers[1].type)

        viewModel.onDelete(resources, marker2)

        assertEquals(MarkerType.CASTLE, viewModel.myMarkers[0].type)
    }
}