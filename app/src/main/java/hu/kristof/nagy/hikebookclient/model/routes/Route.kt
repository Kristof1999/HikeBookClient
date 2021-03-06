/*
 * Copyright 2019, The Android Open Source Project
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
 *
 */

// based on:
// https://github.com/google-developer-training/android-kotlin-fundamentals-apps/tree/master/MarsRealEstateNetwork

package hu.kristof.nagy.hikebookclient.model.routes

import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.User
import hu.kristof.nagy.hikebookclient.util.Constants
import org.osmdroid.views.overlay.Polyline

/**
 * A Class representing a route: points with a name and a description.
 */
sealed class Route(
    open val routeName: String,
    open val points: List<Point>,
    open val description: String
    ) {

    /**
     * A Route representing a route that belongs to an user.
     */
    data class UserRoute(
        val userName: String,
        override val routeName: String,
        override val points: List<Point>,
        override val description: String
    ) : Route(routeName, points, description) {
        init {
            User.checkName(userName)
            checkRouteName(routeName)
            checkPointSize(points)
        }
    }

    /**
     * A Route representing a route that belongs to a group.
     */
    data class GroupRoute(
        val groupName: String,
        override val routeName: String,
        override val points: List<Point>,
        override val description: String
    ) : Route(routeName, points, description) {
        init {
            checkGroupName(groupName)
            checkRouteName(routeName)
            checkPointSize(points)
        }

        companion object {
            fun checkGroupName(groupName: String) {
                if (groupName.isEmpty())
                    throw IllegalArgumentException("A n??v nem lehet ??res.")
                if (groupName.contains("/"))
                    throw IllegalArgumentException("A n??v nem tartalmazhat / jelet.")
            }
        }
    }

    data class GroupHikeRoute(
        val groupHikeName: String,
        override val routeName: String,
        override val points: List<Point>,
        override val description: String
    ) : Route(routeName, points, description) {
        init {
            checkGroupHikeName(groupHikeName)
            checkRouteName(routeName)
            checkPointSize(points)
        }

        companion object {
            fun checkGroupHikeName(groupHikeName: String) {
                if (groupHikeName.isEmpty())
                    throw IllegalArgumentException("A csoportos t??ra neve nem lehet ??res.")
                if (groupHikeName.contains("/"))
                    throw IllegalArgumentException("A csoportos t??ra n??v nem tartalmazhat / jelet.")
            }
        }
    }


    init {
        routeName?.let { checkRouteName(it) }
        points?.let { checkPointSize(it) }
    }

    fun toPolyline(): Polyline {
        val polyline = Polyline()
        polyline.setPoints(points.map {
            it.toGeoPoint()
        })
        return polyline
    }

    /**
     * Computes the average speed in km/h on this route on the given time interval
     */
    fun computeAvgSpeed(startTime: Long, finishTime: Long): Double {
        val distance: Double = toPolyline().distance - 2 * Constants.GEOFENCE_RADIUS_IN_METERS
        val distanceInKm: Double = distance / 1000
        val timeInMillis: Long = finishTime - startTime
        val millisecondsInSecond = 1000
        val millisecondsInHour = millisecondsInSecond * 60 * 60
        val timeInHour = timeInMillis / ( millisecondsInHour )
        return distanceInKm / timeInHour
    }

    /**
     * @throws IllegalArgumentException if the route name does not pass the checks
     */
    protected fun checkRouteName(routeName: String) {
        if (routeName.isEmpty())
            throw IllegalArgumentException("Az ??tvonal n??v nem lehet ??res.")
        if (routeName.contains("/"))
            throw IllegalArgumentException("Az ??tvonal n??v nem tartalmazhat / jelet.")
    }

    /**
     * @throws IllegalArgumentException if points does not pass the checks
     */
    protected fun checkPointSize(points: List<Point>) {
        if (points.size < 2)
            throw IllegalArgumentException("Az ??tvonalnak legal??bb 2 pontb??l kell ??llnia.")
    }
}