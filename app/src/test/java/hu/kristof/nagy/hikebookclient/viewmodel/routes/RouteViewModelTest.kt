package hu.kristof.nagy.hikebookclient.viewmodel.routes

import android.graphics.drawable.Drawable
import hu.kristof.nagy.hikebookclient.model.MyMarker
import hu.kristof.nagy.hikebookclient.view.mymap.MarkerType
import junit.framework.Assert.assertEquals
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline

@RunWith(MockitoJUnitRunner::class)
class RouteViewModelTest {
    @Mock
    private lateinit var markerIcon: Drawable

    @Mock
    private lateinit var setMarkerIcon: Drawable

    private lateinit var overlays: MutableList<Overlay>

    private lateinit var viewModel: DummyRouteViewModel

    @Before
    internal fun setUp() {
        viewModel = DummyRouteViewModel()
        overlays = ArrayList()
        markerIcon = Mockito.mock(Drawable::class.java)
        setMarkerIcon = Mockito.mock(Drawable::class.java)
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
            markerIcon, setMarkerIcon,
            overlays
        )

        val myNewMarker = MyMarker(newMarker, newMarkerType, newMarkerTitle)
        assertThat(viewModel.markers, hasItem(myNewMarker))
        assertThat(overlays, hasItem(newMarker))
        assertEquals(viewModel.polylines.size, 0)
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
            markerIcon, setMarkerIcon,
            overlays
        )
        viewModel.onSingleTap(
            newMarker,
            newMarkerPoint,
            markerIcon, setMarkerIcon,
            overlays
        )

        assertThat(viewModel.polylines.first().actualPoints[0], equalTo(markerPoint))
        assertThat(viewModel.polylines.first().actualPoints[1], equalTo(newMarkerPoint))
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
            p, markerIcon, setMarkerIcon,
            overlays
        )
        viewModel.markerType = MarkerType.NEW
        viewModel.onSingleTap(
            marker2,
            p, markerIcon, setMarkerIcon,
            overlays
        )
        viewModel.onSingleTap(
            marker3,
            p, markerIcon, setMarkerIcon,
            overlays
        )

        assertThat(viewModel.markers[0].type, equalTo(marker1Type))
        assertThat(viewModel.markers[1].type, equalTo(MarkerType.SET))
    }

    @Test
    fun `delete marker`() {
        val markerTitle = "marker"
        val marker = mock<Marker>()
        viewModel.markers.add(MyMarker(marker, MarkerType.NEW, markerTitle))

        viewModel.onDelete(markerIcon, marker)

        assertEquals(0, viewModel.markers.size)
    }

    @Test
    fun `delete polyline`() {
        val marker1 = mock<Marker>()
        val marker2 = mock<Marker>()
        viewModel.markers.add(MyMarker(marker1, MarkerType.SET, ""))
        viewModel.markers.add(MyMarker(marker2, MarkerType.NEW, ""))
        val polyline = Polyline()
        polyline.setPoints(listOf(GeoPoint(0.0, 0.0), GeoPoint(1.0, 1.0)))
        viewModel.polylines.add(polyline)

        viewModel.onDelete(markerIcon, marker2)

        assertEquals(0, viewModel.polylines.size)
    }

    @Test
    fun `marker type changed after delete`() {
        val marker1 = mock<Marker>()
        val myMarker1 = MyMarker(marker1, MarkerType.CASTLE, "")
        val marker2 = mock<Marker>()
        val myMarker2 = MyMarker(marker2, MarkerType.SET, "")
        val marker3 = mock<Marker>()
        val myMarker3 = MyMarker(marker3, MarkerType.NEW, "")
        viewModel.markers.add(myMarker1)
        viewModel.markers.add(myMarker2)
        viewModel.markers.add(myMarker3)
        val polyline1 = Polyline()
        polyline1.setPoints(listOf(GeoPoint(0.0, 0.0), GeoPoint(1.0, 1.0)))
        viewModel.polylines.add(polyline1)
        val polyline2 = Polyline()
        polyline2.setPoints(listOf(GeoPoint(0.0, 0.0), GeoPoint(1.0, 1.0)))
        viewModel.polylines.add(polyline2)

        viewModel.onDelete(markerIcon, marker3)

        assertEquals(MarkerType.NEW, viewModel.markers[1].type)

        viewModel.onDelete(markerIcon, marker2)

        assertEquals(MarkerType.CASTLE, viewModel.markers[0].type)
    }
}