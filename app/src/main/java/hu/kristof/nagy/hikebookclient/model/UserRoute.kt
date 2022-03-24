package hu.kristof.nagy.hikebookclient.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// TODO: try composition instead of inheritance
@Parcelize
data class UserRoute(
    val userName: String,
    override val routeName: String,
    override val points: List<Point>,
    override val description: String
) : Route(), Parcelable
