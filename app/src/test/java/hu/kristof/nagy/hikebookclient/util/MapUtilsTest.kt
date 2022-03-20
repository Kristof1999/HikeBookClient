// based on:
// https://developer.android.com/training/testing/local-tests
// http://hamcrest.org/JavaHamcrest/tutorial

package hu.kristof.nagy.hikebookclient.util

import android.graphics.drawable.Drawable
import hu.kristof.nagy.hikebookclient.model.MarkerType
import hu.kristof.nagy.hikebookclient.model.MyMarker
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline

@RunWith(MockitoJUnitRunner::class)
class MapUtilsTest {
    @Mock
    private lateinit var markerIcon: Drawable

    @Mock
    private lateinit var setMarkerIcon: Drawable

    private lateinit var markers: ArrayList<MyMarker>
    private lateinit var polylines: ArrayList<Polyline>
    private lateinit var overlays: MutableList<Overlay>

    @Before
    fun setUp() {
        markers = ArrayList()
        polylines = ArrayList()
        overlays = ArrayList()
        markerIcon = mock(Drawable::class.java)
        setMarkerIcon = mock(Drawable::class.java)
    }

    /**
     * Tests if the first marker was
     * added to the appropriate containers.
     */
    @Test
    fun testOneAdd() {
        val newMarker = mock<Marker>()
        val newMarkerType = MarkerType.NEW
        val newMarkerTitle = ""

        MapUtils.onSingleTap(
            newMarker, newMarkerType, newMarkerTitle,
            GeoPoint(0.0, 0.0),
            markerIcon, setMarkerIcon,
            overlays, markers, polylines
        )

        val myNewMarker = MyMarker(newMarker, newMarkerType, newMarkerTitle)
        assertThat(markers, hasItem(myNewMarker))
        assertThat(overlays, hasItem(newMarker))
        assertEquals(polylines.size, 0)
    }

    /**
     * Tests if the polyline was
     * added to the appropriate container, and
     * that it connects the last 2 markers.
     */
    @Test
    fun testPolyline() {
        val markerPoint = GeoPoint(0.0, 0.0)
        val marker = mock<Marker>() {
            on { position } doReturn markerPoint
        }
        val markerType = MarkerType.NEW
        val markerTitle = ""
        val newMarkerPoint = GeoPoint(1.0, 1.0)
        val newMarker = mock<Marker>() {
            on { position } doReturn newMarkerPoint
        }
        val newMarkerType = MarkerType.NEW
        val newMarkerTitle = ""

        MapUtils.onSingleTap(
            marker, markerType, markerTitle,
            markerPoint,
            markerIcon, setMarkerIcon,
            overlays, markers, polylines
        )
        MapUtils.onSingleTap(
            newMarker, newMarkerType, newMarkerTitle,
            newMarkerPoint,
            markerIcon, setMarkerIcon,
            overlays, markers, polylines
        )

        assertThat(polylines.first().actualPoints[0], equalTo(markerPoint))
        assertThat(polylines.first().actualPoints[1], equalTo(newMarkerPoint))
    }

    /**
     * Tests if the previous marker's type has changed
     * if it's type was NEW.
     */
    @Test
    fun testChangePrev() {
        val p = GeoPoint(0.0, 0.0)
        val marker1 = mock<Marker>() {
            on { position } doReturn p
        }
        val marker1Type = MarkerType.CASTLE
        val marker2 = mock<Marker>() {
            on { position } doReturn p
        }
        val marker2Type = MarkerType.NEW
        val marker3 = mock<Marker>() {
            on { position } doReturn p
        }
        val marker3Type = MarkerType.NEW

        MapUtils.onSingleTap(
            marker1, marker1Type, "",
            p, markerIcon, setMarkerIcon,
            overlays, markers, polylines
        )
        MapUtils.onSingleTap(
            marker2, marker2Type, "",
            p, markerIcon, setMarkerIcon,
            overlays, markers, polylines
        )
        MapUtils.onSingleTap(
            marker3, marker3Type, "",
            p, markerIcon, setMarkerIcon,
            overlays, markers, polylines
        )

        assertThat(markers[0].type, equalTo(marker1Type))
        assertThat(markers[1].type, equalTo(MarkerType.SET))
    }
}