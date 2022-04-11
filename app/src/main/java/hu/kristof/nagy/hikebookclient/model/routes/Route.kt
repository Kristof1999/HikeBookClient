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

import android.os.Parcelable
import hu.kristof.nagy.hikebookclient.model.Point
import kotlinx.parcelize.Parcelize
import org.osmdroid.views.overlay.Polyline

@Parcelize
open class Route(
    open val routeName: String,
    open val points: List<Point>,
    open val description: String
    ) : Parcelable {

    fun toPolyline(): Polyline {
        val polyline = Polyline()
        polyline.setPoints(points.map {
            it.toGeoPoint()
        })
        return polyline
    }
}