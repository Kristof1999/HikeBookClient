package hu.kristof.nagy.hikebookclient.model

import org.osmdroid.views.overlay.Marker

data class MyMarker(val marker: Marker, val type: MarkerType, val title: String)
