package hu.kristof.nagy.hikebookclient.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserRoute(
    val userName: String,
    override var routeName: String,
    override var points: List<Point>,
    override var description: String
) : Route(routeName, points, description), Parcelable
