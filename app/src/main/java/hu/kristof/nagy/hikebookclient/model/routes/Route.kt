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
import org.osmdroid.views.overlay.Polyline

/**
 * A Class representing a route: points with a name and a description.
 */
open class Route(
    open val routeName: String,
    open val points: List<Point>,
    open val description: String
    ) {

    fun toPolyline(): Polyline {
        val polyline = Polyline()
        polyline.setPoints(points.map {
            it.toGeoPoint()
        })
        return polyline
    }

    fun getDistance() = toPolyline().distance

    /**
     * @throws IllegalArgumentException if the route name does not pass the checks
     */
    protected fun checkRouteName(routeName: String) {
        if (routeName.isEmpty())
            throw IllegalArgumentException("Az útvonal név nem lehet üres.")
        if (routeName.contains("/"))
            throw IllegalArgumentException("Az útvonal név nem tartalmazhat / jelet.")
    }

    /**
     * @throws IllegalArgumentException if points does not pass the checks
     */
    protected fun checkPointSize(points: List<Point>) {
        if (points.size < 2)
            throw IllegalArgumentException("Az útvonalnak legalább 2 pontból kell állnia.")
    }
}