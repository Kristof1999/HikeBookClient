package hu.kristof.nagy.hikebookclient.model

// TODO: derive classes: Date and Time, and use these in the dialog fragments
data class DateTime(
    val year: Int, val month: Int, val dayOfMonth: Int,
    val hourOfDay: Int, val minute: Int
)