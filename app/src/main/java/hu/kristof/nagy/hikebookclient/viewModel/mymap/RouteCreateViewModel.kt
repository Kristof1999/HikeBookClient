/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// based on:
// https://developer.android.com/topic/libraries/architecture/datastore

package hu.kristof.nagy.hikebookclient.viewModel.mymap

import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.IRouteRepository
import hu.kristof.nagy.hikebookclient.model.MarkerType
import hu.kristof.nagy.hikebookclient.model.MyMarker
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.Route
import hu.kristof.nagy.hikebookclient.util.MapUtils
import hu.kristof.nagy.hikebookclient.util.MarkerUtils
import hu.kristof.nagy.hikebookclient.util.RouteUtils
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline
import javax.inject.Inject

@HiltViewModel
class RouteCreateViewModel @Inject constructor(
    private val repository: IRouteRepository
    ) : ViewModel() {
    private val markers = ArrayList<MyMarker>()
    private val polylines = ArrayList<Polyline>()

    private var _routeCreateRes = MutableLiveData<Boolean>()
    /**
     * Result of route creation attempt.
     */
    val routeCreateRes: LiveData<Boolean>
        get() = _routeCreateRes

    var markerType: MarkerType = MarkerType.NEW
    /**
     * Single use title. After usage for one marker, it will be reset to empty string.
     */
    var markerTitle: String = ""

    /**
     * Creates the route for the logged in user.
     * @param routeName name of the created route
     * @throws IllegalArgumentException if the route has an illegal name, or it has less than 2 points
     */
    fun onRouteCreate(routeName: String) {
        val points: List<Point> = markers.map {
            Point.from(it)
        }
        val route = Route(routeName, points)
        RouteUtils.checkRoute(route)
        viewModelScope.launch {
            repository.createRoute(route)
                .collect { res ->
                    _routeCreateRes.value = res
                }
        }
    }

    fun onSingleTap(
        newMarker: Marker,
        p: GeoPoint?,
        markerIcon: Drawable,
        setMarkerIcon: Drawable,
        overlays: MutableList<Overlay>
    ) {
        MapUtils.onSingleTap(
            newMarker, markerType, markerTitle, p!!, markerIcon, setMarkerIcon, overlays, markers, polylines
        )
        markerTitle = ""
    }

    fun onMarkerDragEnd(marker: Marker) =
        MarkerUtils.onMarkerDragEnd(marker, markers.map {it.marker} as ArrayList<Marker>, polylines)

    fun onMarkerDragStart(marker: Marker) =
        MarkerUtils.onMarkerDragStart(marker, markers.map {it.marker} as ArrayList<Marker>, polylines)

    fun onDelete(
        marker: Marker,
        markerIcon: Drawable
    ): Boolean = MarkerUtils.onDelete(marker, markerIcon, markers, polylines)
}