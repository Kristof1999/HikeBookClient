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

package hu.kristof.nagy.hikebookclient.viewModel.routes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.IUserRouteRepository
import hu.kristof.nagy.hikebookclient.model.MyMarker
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.util.RouteUtils
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.osmdroid.views.overlay.Polyline
import javax.inject.Inject

@HiltViewModel
class RouteCreateViewModel @Inject constructor(
    private val userRepository: IUserRouteRepository
    ) : RouteViewModel() {
    override val markers = ArrayList<MyMarker>()
    override val polylines = ArrayList<Polyline>()

    private var _routeCreateRes = MutableLiveData<Result<Boolean>>()
    /**
     * Result of route creation attempt.
     */
    val routeCreateRes: LiveData<Result<Boolean>>
        get() = _routeCreateRes

    /**
     * Creates the route for the logged in user.
     * @param routeName name of the created route
     * @throws IllegalArgumentException if the route has an illegal name, or it has less than 2 points
     */
    fun onRouteCreate(routeName: String, hikeDescription: String) {
        val points: List<Point> = markers.map {
            Point.from(it)
        }
        RouteUtils.checkRoute(routeName, points)
        viewModelScope.launch {
            userRepository.createUserRoute(routeName, points, hikeDescription)
                .collect { res ->
                    _routeCreateRes.value = res
                }
        }
    }
}