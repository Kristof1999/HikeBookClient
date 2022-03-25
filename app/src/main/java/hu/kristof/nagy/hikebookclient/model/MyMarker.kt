package hu.kristof.nagy.hikebookclient.model

import hu.kristof.nagy.hikebookclient.view.mymap.MarkerType
import org.osmdroid.views.overlay.Marker

data class MyMarker(val marker: Marker, val type: MarkerType, val title: String)
