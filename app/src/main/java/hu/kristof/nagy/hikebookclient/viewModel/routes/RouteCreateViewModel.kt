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

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.routes.GroupRouteRepository
import hu.kristof.nagy.hikebookclient.data.routes.UserRouteRepository
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.RouteType
import hu.kristof.nagy.hikebookclient.util.handleIllegalStateAndArgument
import hu.kristof.nagy.hikebookclient.util.handleOffline
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A RouteViewModel that helps with saving the created route.
 */
@HiltViewModel
class RouteCreateViewModel @Inject constructor(
    private val userRouteRepository: UserRouteRepository,
    private val groupRouteRepository: GroupRouteRepository
    ) : RouteViewModel() {

    private val _routeCreateRes = MutableLiveData<ResponseResult<Boolean>>()
    val routeCreateRes: LiveData<ResponseResult<Boolean>>
        get() = _routeCreateRes

    fun onRouteCreate(
        routeType: RouteType,
        groupName: String?,
        context: Context
    ) {
        val points: List<Point> = myMarkers.map {
            Point.from(it)
        }

        viewModelScope.launch {
            handleIllegalStateAndArgument(_routeCreateRes) {
                handleOffline(_routeCreateRes, context) {
                    when (routeType) {
                        RouteType.USER -> onUserRouteCreate(routeName, hikeDescription, points)
                        RouteType.GROUP ->
                            onGroupRouteCreate(routeName, hikeDescription, points, groupName!!)
                    }
                }
            }
        }
    }

    private suspend fun onUserRouteCreate(
        routeName: String,
        hikeDescription: String,
        points: List<Point>
    ) {
        userRouteRepository.createUserRouteForLoggedInUser(routeName, points, hikeDescription)
            .collect { res ->
                _routeCreateRes.value = res
            }
    }

    private suspend fun onGroupRouteCreate(
        routeName: String,
        hikeDescription: String,
        points: List<Point>,
        groupName: String
    ) {
        _routeCreateRes.value = groupRouteRepository.createGroupRoute(
            groupName, routeName, points, hikeDescription
        )
    }
}