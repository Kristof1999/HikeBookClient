package hu.kristof.nagy.hikebookclient.util

import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.view.routes.MarkerType

fun getMarkerIcon(type: MarkerType, resources: Resources): Drawable = when(type) {
    MarkerType.NEW -> ResourcesCompat.getDrawable(resources, R.drawable.marker_image, null)!!
    MarkerType.CASTLE -> ResourcesCompat.getDrawable(resources, R.drawable.castle_image, null)!!
    MarkerType.LOOKOUT -> ResourcesCompat.getDrawable(resources, R.drawable.landscape_image, null)!!
    MarkerType.TEXT -> ResourcesCompat.getDrawable(resources, R.drawable.text_marker, null)!!
    MarkerType.SET -> ResourcesCompat.getDrawable(resources, R.drawable.set_marker_image, null)!!
}