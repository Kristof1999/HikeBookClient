package hu.kristof.nagy.hikebookclient.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DateTime(
    val year: Int, val month: Int, val dayOfMonth: Int,
    val hourOfDay: Int, val minute: Int
) : Parcelable {
    override fun toString(): String {
        return "$year-$month-$dayOfMonth, $hourOfDay:$minute";
    }
}