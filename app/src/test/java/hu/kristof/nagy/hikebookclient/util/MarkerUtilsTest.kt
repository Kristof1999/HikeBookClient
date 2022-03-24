package hu.kristof.nagy.hikebookclient.util

import android.graphics.drawable.Drawable
import hu.kristof.nagy.hikebookclient.model.MarkerType
import hu.kristof.nagy.hikebookclient.model.MyMarker
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class MarkerUtilsTest {
    @Mock
    private lateinit var markerIcon: Drawable

    private lateinit var markers: ArrayList<MyMarker>
    private lateinit var polylines: ArrayList<Polyline>

    @Before
    fun setUp() {
        markers = ArrayList()
        polylines = ArrayList()
        markerIcon = Mockito.mock(Drawable::class.java)
    }

    @Test
    fun onDeleteOne() {
        val markerTitle = "marker"
        val marker = mock<Marker>()
        markers.add(MyMarker(marker, MarkerType.NEW, markerTitle))

        MarkerUtils.onDeleteLogicHandler(marker, markerIcon, markers, polylines)

        assertEquals(0, markers.size)
    }


    @Test
    fun onDeletePolyline() {
        val marker1 = mock<Marker>()
        val marker2 = mock<Marker>()
        markers.add(MyMarker(marker1, MarkerType.SET, ""))
        markers.add(MyMarker(marker2, MarkerType.NEW, ""))
        val polyline = Polyline()
        polyline.setPoints(listOf(GeoPoint(0.0, 0.0), GeoPoint(1.0, 1.0)))
        polylines.add(polyline)

        MarkerUtils.onDeleteLogicHandler(marker2, markerIcon, markers, polylines)

        assertEquals(0, polylines.size)
    }

    @Test
    fun onDeleteType() {
        val marker1 = mock<Marker>()
        val myMarker1 = MyMarker(marker1, MarkerType.CASTLE, "")
        val marker2 = mock<Marker>()
        val myMarker2 = MyMarker(marker2, MarkerType.SET, "")
        val marker3 = mock<Marker>()
        val myMarker3 = MyMarker(marker3, MarkerType.NEW, "")
        markers.add(myMarker1)
        markers.add(myMarker2)
        markers.add(myMarker3)
        val polyline1 = Polyline()
        polyline1.setPoints(listOf(GeoPoint(0.0, 0.0), GeoPoint(1.0, 1.0)))
        polylines.add(polyline1)
        val polyline2 = Polyline()
        polyline2.setPoints(listOf(GeoPoint(0.0, 0.0), GeoPoint(1.0, 1.0)))
        polylines.add(polyline2)

        MarkerUtils.onDeleteLogicHandler(marker3, markerIcon, markers, polylines)

        assertEquals(MarkerType.NEW, markers[1].type)

        MarkerUtils.onDeleteLogicHandler(marker2, markerIcon, markers, polylines)

        assertEquals(MarkerType.CASTLE, markers[0].type)
    }
}