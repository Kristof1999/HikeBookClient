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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.di.Service
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.Route
import hu.kristof.nagy.hikebookclient.util.Constants
import hu.kristof.nagy.hikebookclient.util.MapHelper
import hu.kristof.nagy.hikebookclient.util.MarkerUtils
import hu.kristof.nagy.hikebookclient.util.RouteUtils
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline
import javax.inject.Inject

@HiltViewModel
class RouteCreateViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val service: Service
    ) : ViewModel() {
    private val markers = ArrayList<Marker>()
    private val polylines = ArrayList<Polyline>()

    private var _routeCreateRes = MutableLiveData<Boolean>()
    val routeCreateRes: LiveData<Boolean>
        get() = _routeCreateRes

    fun onRouteCreate(routeName: String) {
        val points: List<Point> = markers.map {
            Point.from(it)
        }
        RouteUtils.checkRoute(Route(routeName, points))
        viewModelScope.launch {
            dataStore.data.map { preferences ->
                preferences[Constants.DATA_STORE_USER_NAME]
            }.collect { userName ->
                _routeCreateRes.value = service.createRoute(userName!!, routeName, points)
            }
        }
    }

    fun onSingleTap(
        newMarker: Marker,
        p: GeoPoint?,
        markerIcon: Drawable,
        setMarkerIcon: Drawable,
        overlays: MutableList<Overlay>
    ) = MapHelper.onSingleTap(
        newMarker, p, markerIcon, setMarkerIcon, overlays, markers, polylines
    )

    fun onMarkerDragEnd(marker: Marker) =
        MarkerUtils.onMarkerDragEnd(marker, markers, polylines)

    fun onMarkerDragStart(marker: Marker) =
        MarkerUtils.onMarkerDragStart(marker, markers, polylines)

    fun onDelete(
        marker: Marker,
        markerIcon: Drawable
    ): Boolean = MarkerUtils.onDelete(marker, markerIcon, markers, polylines)
}